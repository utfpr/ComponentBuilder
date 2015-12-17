/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.utils;

import br.edu.utfpr.cm.tsi.utils.LogServer;
import com.m4rc310.ui.nfe.gui.actions.DialogMethods;
import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.builders.ComponentBuilder;
import com.m4rc310.cb.builders.adapters.AbstractComponetAdapter;
import com.m4rc310.ui.gui.componentUtils.GuiUtils;
import com.m4rc310.ui.nfe.gui.actions.Action;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import mtfn.MetaphonePtBr;

/**
 *
 * @author Marcelo
 */
public abstract class DialogObject implements Action.Listener, DialogMethods {

    protected ComponentBuilder componentsBuilder;
    protected Dialog dialog;
    protected GuiUtils gui;

    protected abstract void init(Dialog dialog);

//    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        init(dialog);
        componentsBuilder.processMethodsAnnotateds();
    }

    public void setGui(GuiUtils gui) {
        this.gui = gui;
    }

    private final Map<Object, Boolean> cacheComponents = new HashMap<>();

    @Override
    public void editableAll(boolean editable) {
        componentsBuilder.getComponents().stream().forEach((component) -> {
            try {
                MethodUtils.method(component, "setFocusable", boolean.class).invoke(editable);
                MethodUtils.method(component, "setEditable", boolean.class).invoke(editable);
            } catch (Exception e) {
            }
        });
    }

    @Override
    public void enableGroup(boolean enable, String... groups) {
        
        for (Map.Entry<Field, AbstractComponetAdapter> entrySet : componentsBuilder.getAdapters().entrySet()) {
            Field field = entrySet.getKey();
            AbstractComponetAdapter adapter = entrySet.getValue();
            Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
            for (String gA : groups) {
                for (String gB : ac.groups()) {
                    if(gA.equals(gB)){
                        enable(enable, ac.ref());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void editableGroup(boolean enable, String... groups) {
        for (Map.Entry<Field, AbstractComponetAdapter> entrySet : componentsBuilder.getAdapters().entrySet()) {
            Field field = entrySet.getKey();
            AbstractComponetAdapter adapter = entrySet.getValue();
            Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
            for (String gA : groups) {
                for (String gB : ac.groups()) {
                    if(gA.equals(gB)){
                        editable(enable, ac.ref());
                        LogServer.getInstance().debug(ac, "change property <editable> for: {0}", gB);
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void enableAll(boolean enable) {
        if (!enable) {
            componentsBuilder.getComponents().stream().map((component) -> {
                boolean isEnable = (boolean) MethodUtils.method(component, "isEnabled").invoke();
                cacheComponents.put(component, isEnable);
                return component;
            }).forEach((component) -> {
                MethodUtils.method(component, "setEnabled", boolean.class).invoke(enable);
            });
        } else {
            componentsBuilder.getComponents().stream().map((component) -> {
                MethodUtils.method(component, "setEnabled", boolean.class).invoke(cacheComponents.get(component));
                return component;
            }).forEach((component) -> {
                cacheComponents.remove(component);
            });
        }
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass, String fieldName) {
        Field field = componentsBuilder.getField(fieldName);

        Annotation ac = field.getDeclaredAnnotation(annotationClass);

        return (A) ac;

//        throw new UnsupportedOperationException();
    }

    @Override
    public void changeText(String fieldName, String text) {
        text = getString(text);
        Acomponent ac = getAnnotation(Acomponent.class, fieldName);
        changeAnnotationValue(ac, "text", text);
        Object com = componentsBuilder.getComponent(fieldName);
        MethodUtils.method(com, "setText", String.class).invoke(ac.text());
    }

    @Override
    public void clear() {
        componentsBuilder.getAdapters().values().stream().forEach((adapter) -> {
            adapter.clear();
        });
    }

    @Override
    public void clearComponents(String ref) {
        componentsBuilder.getAdapters().entrySet().stream().forEach((entrySet) -> {
            Field key = entrySet.getKey();
            AbstractComponetAdapter value = entrySet.getValue();

            if (key.getName().equalsIgnoreCase(ref)) {
                value.clear();
            }
        });
    }

    @Override
    public void setButtonDefault(String ref) {
        Object component = componentsBuilder.getComponent(ref);
        JRootPane root = (JRootPane) MethodUtils.method(dialog, "getRootPane").invoke();
        MethodUtils.method(root, "setDefaultButton", JButton.class).invoke(component);
    }

    @Override
    public void grabFocus(String ref) {
        Object comp = componentsBuilder.getComponent(ref);
        

//            while (!(boolean)MethodUtils.method(comp, "isFocusOwner").invoke()) {                
                MethodUtils.method(comp, "requestFocusInWindow").invoke();
                MethodUtils.method(comp, "grabFocus").invoke();
//                System.out.println("===");
//            }
//        }
        
    }

    public void setForeground(String ref, int color) {
        Object comp = componentsBuilder.getComponent(ref);

        new JLabel().setForeground(Color.red);
        MethodUtils.method(comp, "setForeground", Color.class).invoke(new Color(color));

    }

    @Override
    public void selectedTab(String name, int index) {
        Object component = componentsBuilder.getJTabbedPane(name);
        //new JTabbedPane().setSelectedIndex(index);
        MethodUtils.method(component, "setSelectedIndex", int.class).invoke(index);
    }

    @Override
    public void enabledTab(String name, int index, boolean enable) {
        Object component = componentsBuilder.getJTabbedPane(name);

        MethodUtils.method(component, "setEnabledAt", int.class, boolean.class).invoke(index, enable);
    }

    @Override
    public void enable(boolean enable, String... fieldNames) {
        
        for (String ref : fieldNames) {
            Object component = componentsBuilder.getComponent(ref);
            if (component != null) {
                MethodUtils.method(component, "setEnabled", boolean.class).invoke(enable);
            }
        }
    }

    @Override
    public void editable(boolean editable, String... fieldNames) {
        for (String ref : fieldNames) {
            Object component = componentsBuilder.getComponent(ref);
            if (component != null) {
                MethodUtils.method(component, "setEditable", boolean.class).invoke(editable);
            }
        }
    }

    @Override
    public void visible(boolean editable, String... fieldNames) {
        for (String ref : fieldNames) {
            Object component = componentsBuilder.getComponent(ref);
            if (component != null) {
                MethodUtils.method(component, "setVisible", boolean.class).invoke(editable);
            }
        }
    }

    @Override
    public void setActionListener(String ref, ActionListener actionListener) {

        Field field = componentsBuilder.getField(ref);
        try {
            Object tar = componentsBuilder.getTargetForField(field);
            field.set(tar, actionListener);
            update(tar);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LogServer.getInstance().error(e);
        }
    }

    public void setComponentsBuilder(ComponentBuilder componentsBuilder) {
        this.componentsBuilder = componentsBuilder;
        addTarget(componentsBuilder);
    }

    protected void addTarget(Object... targets) {
        for (Object target : targets) {
            this.componentsBuilder.addTargets(target);
            if(target instanceof Action){
                Action ac = (Action) target;
                ac.setDialog(dialog);
            }
        }
    }

    @Override
    public void updateField(String... fieldName) {
        for (String fn : fieldName) {
            Field field = componentsBuilder.getField(fn);
            Object tar = componentsBuilder.getTargetForField(field);
            try {
                Object value = field.get(tar);
                List<AbstractComponetAdapter> adapters = componentsBuilder.getComponentAdapter(fn);
                adapters.stream().forEach((adapter) -> {
                    adapter.update(field, value);
                });
            } catch (IllegalArgumentException | IllegalAccessException e) {
            }
        }
    }

    @Override
    public void clearComponentsIn(Object... targets) {
        this.componentsBuilder.clear(targets);
    }

    @Override
    public void update(Object... targets) {
        this.componentsBuilder.update(targets);
    }

    public String getString(String text, Object... args) {
        return text;
    }

    @Override
    public void cloneObject(Object source, Object target) {

        try {

            Class csource = source.getClass();
            Class ctarget = target.getClass();

            Collection<Field> sourceFields = new ArrayList<>();
            Collection<Field> targetFields = new ArrayList<>();

            while (csource != Object.class) {
                sourceFields.addAll(Arrays.asList(csource.getDeclaredFields()));
                csource = csource.getSuperclass();
            }

            while (ctarget != Object.class) {
                targetFields.addAll(Arrays.asList(ctarget.getDeclaredFields()));
                ctarget = ctarget.getSuperclass();
            }

            sourceFields.addAll(Arrays.asList(csource.getFields()));

//        targetFields.addAll(Arrays.asList(ctarget.getDeclaredFields()));
//        targetFields.addAll(Arrays.asList(ctarget.getFields()));
            for (Field sourceField : sourceFields) {
                for (Field targetField : targetFields) {
                    if (sourceField.getName().equals(targetField.getName())) {
                        sourceField.setAccessible(true);
                        targetField.setAccessible(true);

                        try {
                            Object value = sourceField.get(source);
                            targetField.set(target, value);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

    }
    
    @Override
    public void showPassword(String ref, boolean show){
        Object com = componentsBuilder.getComponent(ref);
        
        char echoCharDefault = new JPasswordField().getEchoChar();
        
        
        if(show){
            MethodUtils.method(com, "setEchoChar", char.class).invoke((char)0);
        }else{
            MethodUtils.method(com, "setEchoChar", char.class).invoke(echoCharDefault);
        }
    }
    
    @Override
    public void changeLabel(String ref, String label) {
        componentsBuilder.changeLabel(ref, label);
    }

    @Override
    public Object changeAnnotationValue(Annotation annotation, String key, Object newValue) {
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key, newValue);
        return oldValue;
    }

    @Override
    public void dispose() {
        dialog.dispose();
    }

    public ActionListener getActionListener(int index) {
        return (ActionEvent ae) -> {

        };
    }

    public static String toPhonetic(String value) {
        value = new MetaphonePtBr(value).toString();
        return value;
    }
}
