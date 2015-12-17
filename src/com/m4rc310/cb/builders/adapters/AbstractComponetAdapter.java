/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import br.edu.utfpr.cm.tsi.utils.LogServer;
import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.Adialog;
import com.m4rc310.cb.annotations.Amethod;
import com.m4rc310.cb.annotations.Aseach;
import com.m4rc310.cb.builders.ComponentBuilder;
import com.m4rc310.cb.builders.IComponentsBuilder1;
import com.m4rc310.cb.builders.ISearch;
import com.m4rc310.cb.builders.StorageFieldContainer;
import com.m4rc310.cb.utils.MethodUtils;
import com.m4rc310.gui.SearchException;
import com.m4rc310.ui.gui.componentUtils.GuiUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Marcelo
 * @param <C>
 * @param <T>
 */
public abstract class AbstractComponetAdapter<C, T> {

    protected C component;
    protected GuiUtils gui;
    protected Acomponent ac;
    protected Field field;
    protected Adialog adialog;
    protected Object target;
    protected IComponentsBuilder1 componentsBuilder;
    protected StorageFieldContainer storageFieldContainer;
    private Object container;

    private ChangeListener<T> changeListener;
    private ChangeTextListener<T> changeTextListener;

    public AbstractComponetAdapter() {
        initListener();
    }

    public abstract boolean isComponentFor(Acomponent ac);

    public abstract void buildComponent(Field field, Object container);

    public abstract void setValue(T value);

    public abstract void clear();

    public void update(Field field, T value) {

        try {
            boolean isFocusOwner = (boolean) MethodUtils.method(component, "isFocusOwner").invoke();
            if (isFocusOwner) {
                fireChangeListener(value);
                return;
            }
            if (this.field == null) {
                return;
            }
            if (this.field.equals(field)) {
                if (value != null) {
                    setValue(value);
                }
            }
        } catch (Exception e) {
        }
    }

    public abstract T getValueDefault(Field field);

    public boolean containField(Field field) {
        return this.field.equals(field);
    }

    public StorageFieldContainer getStorageFieldContainer() {
        return storageFieldContainer;
    }

    public void build(Field field, Object container) {
        this.field = field;
        ac = field.getDeclaredAnnotation(Acomponent.class);
        buildComponent(field, container);
        try {
            T value = (T) field.get(container);
            if (value == null) {
                setValue(getValueDefault(field));
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {

        }

        processOthersActions();
    }

    public void setStorageFieldContainer(StorageFieldContainer storageFieldContainer) {
        this.storageFieldContainer = storageFieldContainer;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setComponentsBuilder(IComponentsBuilder1 componentsBuilder) {
        this.componentsBuilder = componentsBuilder;
        this.gui = componentsBuilder.getGui();
    }

    public void processMethodsAnnotateds() {

        if (!ac.format().isEmpty()) {
            FocusListener focusListener = new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Object tar = componentsBuilder.getTargetForField(field);
                        Object ret = field.get(tar);

                        MaskFormatter formatter = new MaskFormatter(ac.format());
                        String value = formatter.valueToString(ret);

                        MethodUtils.method(component, "setText", String.class).invoke(value);

                    } catch (IllegalAccessException | ParseException | RuntimeException ex) {
                    }
                }
            };
            MethodUtils.method(component, "addFocusListener", FocusListener.class).invoke(focusListener);
        }

        if (ac.memorize()) {
            String key = String.format("%s-%s", componentsBuilder.getTargetForField(field), ac.ref()).toLowerCase();
            Preferences p = Preferences.userNodeForPackage(getClass()).node(key);
            String text = p.get("text", "");

            if (!text.isEmpty()) {
                MethodUtils.method(component, "setText", String.class).invoke(text);
                try {
                    field.set(target, text);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    LogServer.getInstance().error(e);
                }

            }

            KeyListener keyListener = new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    try {
                        Object com = e.getSource();
                        String stext = (String) MethodUtils.method(com, "getText").invoke();
                        p.put("text", stext);
                        p.flush();
                    } catch (BackingStoreException ex) {
                        LogServer.getInstance().error(ex);
                    }

                }

            };

            MethodUtils.method(component, "addKeyListener", KeyListener.class).invoke(keyListener);
        }

        if (field.isAnnotationPresent(Amethod.class)) {
            Amethod am = field.getDeclaredAnnotation(Amethod.class);
            final String methodOnKeyListener = am.methodOnKeyListener();

            if (!methodOnKeyListener.isEmpty()) {
                KeyListener keyListener = new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        List targets = componentsBuilder.getTargetsForMethodName(methodOnKeyListener);
                        targets.stream().forEach((tar) -> {
                            try {
                                Object value = field.get(target);
                                MethodUtils.declaredMethod(tar, methodOnKeyListener, Object.class).invoke(value);
                            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException ex) {
                            }
                        });
                    }
                };

                MethodUtils.method(component, "addKeyListener", KeyListener.class).invoke(keyListener);
            }

            if (!am.methodOnInputVerify().isEmpty()) {
                final String methodOnInputVerify = am.methodOnInputVerify();

                List targets = componentsBuilder.getTargetsForMethodName(methodOnInputVerify);

                InputVerifier inputVerifier = new InputVerifier() {
                    @Override
                    public boolean verify(JComponent input) {
                        for (Object tar : targets) {
                            try {
                                if (target == null) {
                                    throw new IllegalArgumentException("target null");
                                }
                                Object value = field.get(target);
                                return (boolean) MethodUtils.declaredMethod(tar, methodOnInputVerify, value.getClass()).invoke(value);
                            } catch (IllegalArgumentException | IllegalAccessException | UnsupportedOperationException e) {
//                                fireError(getString("error.validacao.campo", ac.ref()), e.getMessage());
                                return false;
                            }
                        }
                        return true;
                    }
                };

                MethodUtils.method(component, "setInputVerifier", InputVerifier.class).invoke(inputVerifier);

            }

            if (!am.methodOnLostFocus().isEmpty()) {
                final String methodOnLostFocus = am.methodOnLostFocus();

                log(Level.INFO, "adicionando listener para metodo <addFocusListener> -> {0}", methodOnLostFocus);

                List targets = componentsBuilder.getTargetsForMethodName(methodOnLostFocus);

                FocusListener focusListener = new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        targets.stream().forEach((tar) -> {
                            try {
                                log(Level.INFO, "Executando -> {0}", methodOnLostFocus);
                                Object value = field.get(target);
                                MethodUtils.declaredMethod(tar, methodOnLostFocus, value.getClass()).invoke(value);
                            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException ex) {
                                log(Level.SEVERE, "Erro ao tentar executar o metodo: -> {0}\n{1}", methodOnLostFocus, ex.getMessage());
                            }
                        });
                    }
                };

//                new JTextField().addFocusListener(focusListener);
                MethodUtils.method(component, "addFocusListener", FocusListener.class).invoke(focusListener);
            }
        }
        if (field.isAnnotationPresent(Aseach.class)) {
            processSearch(field);
        }
    }

    private void fireError(String title, String message) {
        PropertyChangeListener[] listeners = componentsBuilder.getDialog().getPropertyChangeListeners("onError");
        for (PropertyChangeListener listener : listeners) {
            PropertyChangeEvent pce = new PropertyChangeEvent(listener, title, "", message);
            listener.propertyChange(pce);
        }
    }

    public void updateText() {
    }

    public C getComponent() {

        if (ac.fontSize() != 12) {
            Font font = (Font) MethodUtils.method(component, "getFont").invoke();
            Font newFont = new Font(font.getName(), font.getStyle(), ac.fontSize());
//            gui.setFont(newFont, (JComponent[]) component);
            MethodUtils.method(component, "setFont", Font.class).invoke(newFont);
        }

        try {
            PropertyChangeListener pce = (PropertyChangeEvent evt) -> {
                if ("editable".equals(evt.getPropertyName())) {
                    if (ac.focusable()) {
                        MethodUtils.method(component, "setFocusable", boolean.class).invoke(ac.editable());
                        MethodUtils.method(component, "setFocusCycleRoot", boolean.class).invoke(ac.editable());
                    }
                }
            };

            MethodUtils.method(component, "addPropertyChangeListener", PropertyChangeListener.class).invoke(pce);
            MethodUtils.method(component, "setEditable", boolean.class).invoke(ac.editable());

        } catch (Exception e) {

        }

        if (ac.style().length > 0) {
            Object font = MethodUtils.method(component, "getFont").invoke();
            font = MethodUtils.method(font, "deriveFont", float.class).invoke(ac.fontSize());
            MethodUtils.method(component, "setFont", Font.class).invoke(font);
        }

        return component;
    }

    protected void format(Object value) {
        if (!ac.format().isEmpty()) {
            try {
                MethodUtils.method(component, "setText", String.class).invoke(String.format(ac.format(), value));
            } catch (Exception e) {
                LogServer.getInstance().error(e);
            }
        }
    }

    private void initListener() {

        changeListener = (T value) -> {
            try {
                target = componentsBuilder.getTargetForField(field);
                field.set(target, value);

                if (field.isAnnotationPresent(Amethod.class)) {
                    Amethod am = field.getDeclaredAnnotation(Amethod.class);

                    String methodOnChangeValue = am.methodOnChangeValue();
                    String methodOnActionListener = am.methodOnActionListener();
                    String methodOnChangeValueObject = am.methodOnChangeValueObject();

                    if (!methodOnChangeValue.isEmpty()) {
                        componentsBuilder.getTargetsForMethodName(methodOnChangeValue).stream().forEach((tar) -> {
                            try {
                                MethodUtils.declaredMethod(tar, methodOnChangeValue, value.getClass()).invoke(value);
                            } catch (Exception e) {
                            }
                        });
                    }
                    if (!methodOnActionListener.isEmpty()) {
                        componentsBuilder.getTargetsForMethodName(methodOnActionListener).stream().forEach((tar) -> {
                            MethodUtils.declaredMethod(tar, methodOnActionListener).invoke();
                        });
                    }

//                    if (!methodOnChangeValueObject.isEmpty()) {
//                        componentsBuilder.getTargetsForMethodName(methodOnChangeValueObject).stream().forEach((tar) -> {
//                            MethodUtils.declaredMethod(tar, methodOnChangeValueObject, Object.class).invoke(value);
//                        });
//                    }
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                log(Level.SEVERE, "Erro Listener: {0}", e.getMessage());
            }
        };
    }

    protected void fireError(Throwable e) {
        LogServer.getInstance().error(e);
    }

    public void fireChangeTextListener(T value) {
        changeTextListener.change(value);
    }

    protected void fireChangeListener(T value) {
        changeListener.change(value);

    }

    protected String getString(String text, Object... args) {
        if (componentsBuilder != null) {
            return componentsBuilder.getString(text, args);
        }
        return text;
    }

    protected void assertTrue(boolean istrue, String text, Object... args) throws Exception {
        if (istrue) {
            throw new Exception(MessageFormat.format(text, args));
        }
    }

    protected void log(Level level, String text, Object... args) {
        LogServer.getInstance().debug(level, text, args);
    }

    private void processOthersActions() {
        try {
            if (ac.horizontalAlignment() != Acomponent.NONE) {
                MethodUtils.method(component, "setHorizontalAlignment", int.class).invoke(ac.horizontalAlignment());
            }

            if (ac.foreground() != -1) {
                Color color = new Color(ac.foreground());
                MethodUtils.method(component, "setForeground", Color.class).invoke(color);
            }

        } catch (Exception e) {
        }
    }

    private Object getTargetForField(Field field) {
        for (Object tar : storageFieldContainer.getTargets()) {
            for (Field f : tar.getClass().getDeclaredFields()) {
                if (f.isAnnotationPresent(Acomponent.class)) {
                    Acomponent fac = f.getDeclaredAnnotation(Acomponent.class);
                    if (ac.ref().equals(fac.ref())) {
                        return tar;
                    }
                }
            }
        }
        throw new UnsupportedOperationException("Não há nenhum objeto com o campo informado!");
    }

    /**
     * @return the container
     */
    public Object getContainer() {
        return container;
    }

    /**
     * @param container the container to set
     */
    public void setContainer(Object container) {
        this.container = container;
    }

    private void processSearch(Field field) {
        Aseach as = field.getDeclaredAnnotation(Aseach.class);

        if (as.background() != -1) {
            MethodUtils.method(component, "setBackground", Color.class).invoke(new Color(as.background()));
        }

        try {
            final Object searchDialog = as.typeSearchDialog().newInstance();

            ISearch iSearch = null;
            if (!as.methodSearch().isEmpty()) {
//                Object tar = componentsBuilder.getTargetForField(field);

                List tars = componentsBuilder.getTargetsForMethodName(as.methodSearch());

                for (Object tar : tars) {
                    iSearch = (ISearch) MethodUtils.declaredMethod(tar, as.methodSearch()).invoke();
                }

            }

            final Object isearch = as.methodSearch().isEmpty() ? as.typeSearch().newInstance() : iSearch;

            MethodUtils.method(searchDialog, "setAserch", Aseach.class).invoke(as);
            MethodUtils.method(searchDialog, "setISearch", ISearch.class).invoke(isearch);

//            MethodUtils.declaredMethod(searchDialog, "show").invoke();
            MouseListener mouseListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {

                        String text = (String) MethodUtils.method(e.getSource(), "getText").invoke();

                        Object tar = componentsBuilder.getTargetForField(field);
                        MethodUtils.method(isearch, "setTarget", Object.class).invoke(tar);
                        MethodUtils.method(searchDialog, "setAserch", Aseach.class).invoke(as);

//                        new JTextField().isEditable()
                        boolean isEnabled = (boolean) MethodUtils.method(component, "isEnabled").invoke();
                        if (isEnabled) {

                            if (as.showSearchDialog()) {
                                ComponentBuilder.showDialog(searchDialog, text);
                            }
                        }
//                        MethodUtils.method(searchDialog, "clearDialog").invoke();
                    }
                }
            };

            InputVerifier inputVerifier = new InputVerifier() {
                @Override
                public boolean verify(JComponent input) {

                    try {
                        Object text = ((JTextField) input).getText();
                        Object ret = MethodUtils.method(isearch, "unique", Object.class).invoke(text);

                        Object tar = componentsBuilder.getTargetForField(field);
                        MethodUtils.method(isearch, "setTarget", Object.class).invoke(tar);
                        MethodUtils.method(isearch, "selectedValue", ret.getClass()).invoke(ret);
                        MethodUtils.method(isearch, "returnValue").invoke();

                    } catch (Exception ex) {

                        if (ex instanceof NullPointerException) {
                            List iSearchListeners = (List) MethodUtils.method(isearch, "getISearchListeners").invoke();
                            for (Object isl : iSearchListeners) {
                                MethodUtils.method(isl, "onSearchError", String.class, String.class).invoke("", getString("error.search.returns.a.valid.value"));
                            }
                            return true;
                        }

                        Throwable cause = ex.getCause().getCause();
                        if (cause instanceof SearchException) {
                            List iSearchListeners = (List) MethodUtils.method(isearch, "getISearchListeners").invoke();
                            for (Object isl : iSearchListeners) {
                                MethodUtils.method(isl, "onSearchError", String.class, String.class).invoke("", cause.getMessage());
                            }
                        }
//                        fireError(ex);
                        return false;
                    }
                    return true;
                }
            };

            KeyListener keyListener = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == as.keyCode()) {

                        Object tar = componentsBuilder.getTargetForField(field);
                        MethodUtils.method(searchDialog, "setAserch", Aseach.class).invoke(as);
                        MethodUtils.method(isearch, "setTarget", Object.class).invoke(tar);

                        String text = (String) MethodUtils.method(e.getSource(), "getText").invoke();

                        boolean isEnabled = (boolean) MethodUtils.method(component, "isEnabled").invoke();
                        if (isEnabled) {
                            if (as.showSearchDialog()) {
                                ComponentBuilder.showDialog(searchDialog, text);
                            }
                        }

                    }
                }
            };

            MethodUtils.method(component, "addMouseListener", MouseListener.class).invoke(mouseListener);
            MethodUtils.method(component, "addKeyListener", KeyListener.class).invoke(keyListener);
            MethodUtils.method(component, "setInputVerifier", InputVerifier.class).invoke(inputVerifier);

        } catch (InstantiationException | IllegalAccessException e) {
            if (field.isAnnotationPresent(Amethod.class)) {
                Amethod am = field.getDeclaredAnnotation(Amethod.class);
                String methodOnError = am.methodOnError();
                if (!methodOnError.isEmpty()) {
                    componentsBuilder.getTargetsForMethodName(methodOnError).stream().forEach((tar) -> {
                        MethodUtils.declaredMethod(tar, methodOnError, e.getClass()).invoke(e);
                    });
                }
            }
        }

    }

    public interface ChangeListener<T> {

        void change(T value);
    }

    private interface ChangeTextListener<T> {

        void change(T value);
    }

    protected class LengthFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (ac.maxLength() != -1) {
                super.insertString(fb, offset, string, attr);
            } else if (string.length() <= ac.maxLength()) {
                super.insertString(fb, offset, string, attr);
            }
        }

    }

    protected class CaseFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

            switch (ac.toCase()) {
                case Acomponent.TO_LOWER_CASE:
                    string = string.toLowerCase();
                    break;
                case Acomponent.TO_UPPER_CASE:
                    string = string.toLowerCase();
                    break;
            }

            if (ac.maxLength() == -1) {
                super.insertString(fb, offset, string.toUpperCase(), attr);
            } else if (string.length() <= ac.maxLength()) {
                super.insertString(fb, offset, string.toUpperCase(), attr);
            }

        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

            switch (ac.toCase()) {
                case Acomponent.TO_LOWER_CASE:
                    text = text.toLowerCase();
                    break;
                case Acomponent.TO_UPPER_CASE:
                    text = text.toUpperCase();
                    break;
            }
            if (ac.maxLength() == -1) {
                super.replace(fb, offset, length, text, attrs);
            } else {

                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - ac.maxLength() - length;
                if (overLimit > 0) {
                    text = text.substring(0, text.length() - overLimit);
                }
                if (text.length() > 0) {
                    super.replace(fb, offset, length, text, attrs);
                }

            }

        }

    }

}
