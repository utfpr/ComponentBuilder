/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import br.edu.utfpr.cm.tsi.utils.LogServer;
import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.Adialog;
import com.m4rc310.cb.annotations.Amethod;
import com.m4rc310.cb.annotations.EnumComponentType;
import static com.m4rc310.cb.builders.IComponentsBuilder1.PATH_CONFIGURATION;
import static com.m4rc310.cb.builders.IComponentsBuilder1.PATH_OF_DIALOGS_DEFAULT;
import com.m4rc310.cb.builders.adapters.AbstractComponetAdapter;
import com.m4rc310.cb.builders.adapters.AdapterTabPanel;
import com.m4rc310.cb.utils.MethodUtils;
import com.m4rc310.ui.gui.componentUtils.GuiUtils;
import com.m4rc310.ui.gui.componentUtils.impl.GuiUtilsImpl;
import com.m4rc310.ui.nfe.gui.actions.Action;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import net.miginfocom.swing.MigLayout;
import net.sf.trugger.scan.ClassScan;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Marcelo
 */
public class ComponentBuilder implements IComponentsBuilder1, Action.Listener {

    private JSONObject conf;
    private Object objectAnnotated;
    private JDialogDefalt dialog;

    private final Collection targets;
    private final Collection<Field> fields;

    private final Map<Integer, Object> containers;
    private final Map<Field, AbstractComponetAdapter> adapters;

    private GuiUtils gui;

    public ComponentBuilder() {
        this.targets = new ArrayList();
        this.fields = new ArrayList<>();
        this.adapters = new HashMap<>();
        this.containers = new HashMap<>();
        this.init();
    }

    public void addTargets(Object target) {
        if (!targets.contains(target)) {
            LogServer.getInstance().info(target,"Adicionando Target: [{0}] ~> {1}", target.hashCode(), target);
            targets.add(target);
        }
    }

    @Override
    public Map<Field, AbstractComponetAdapter> getAdapters() {
        return this.adapters;
    }

    @Override
    public JDialogDefalt getDialog() {
        return dialog;
    }

    public void processMethodsAnnotateds() {
        adapters.values().stream().forEach((adapter) -> {
            adapter.processMethodsAnnotateds();
        });
    }

    private void putContainer(int hash, Object container) {
        if (!containers.containsKey(hash)) {
            LogServer.getInstance().debug(null, "Adicionando Containner: [{0}] ~> {1}", hash, container);
            containers.put(hash, container);
        } else {
            LogServer.getInstance().warning(container, "Containner já foi registrado: [{0}] ~> {1}", hash, container);
        }
    }

    public List getComponents(String... refs) {
        List ret = new ArrayList();
        if (refs.length == 0) {
            adapters.values().stream().forEach((adapter) -> {
                ret.add(adapter.getComponent());
            });
        } else {
            adapters.keySet().stream().forEach((field) -> {
                Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
                for (String ref : refs) {
                    if (ref.equals(ac.ref())) {
                        ret.add(adapters.get(field).getComponent());
                    }
                }
            });
        }

        return ret;
    }

    public Object getComponentForName(String name) {
        for (Map.Entry<Field, AbstractComponetAdapter> entrySet : adapters.entrySet()) {
            Field field = entrySet.getKey();
            AbstractComponetAdapter adapter = entrySet.getValue();
            if (field.getName().equals(name)) {
                Object component = adapter.getComponent();
                return component;
            }
        }
        return null;
    }

    public Object getComponent(String ref) {
        for (Map.Entry<Field, AbstractComponetAdapter> entrySet : adapters.entrySet()) {
            Field field = entrySet.getKey();
            AbstractComponetAdapter adapter = entrySet.getValue();
            Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
            if (ref.equals(ac.ref())) {
                Object component = adapter.getComponent();
                return component;
            }
        }
        return null;
    }

    private void addField(Field field) {
        if (!fields.contains(field)) {
            Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
            LogServer.getInstance().debug(null, "Adicionando a Field: [{0}] ~> {1}", field.hashCode(), ac.ref());
            fields.add(field);

            AbstractComponetAdapter adapter = getComponentAdapter(ac);
            LogServer.getInstance().debug(null, "agregando um <adapter> [{0}] para ~> {1}", adapter, ac.ref());
            adapters.put(field, adapter);

        } else {
            LogServer.getInstance().debug(null, "Já contem a Field", field);
        }
    }

    public Field getField(String ref) {
        for (Field field : fields) {
            if (field.isAnnotationPresent(Acomponent.class)) {
                Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
                if (ac.ref().equalsIgnoreCase(ref)) {
                    return field;
                }
            }
        }

        throw new UnsupportedOperationException("Field not found!");
    }

    public List<AbstractComponetAdapter> getComponentAdapter(String... refs) {
        List<AbstractComponetAdapter> ret = new ArrayList<>();
        for (String ref : refs) {
            fields.stream().filter((field) -> (field.getName().equalsIgnoreCase(ref))).map((field) -> field.getDeclaredAnnotation(Acomponent.class)).forEach((ac) -> {
                ret.add(getComponentAdapter(ac));
            });
        }
        return ret;
    }

    public AbstractComponetAdapter getComponentAdapter(Acomponent ac) {
        for (Class in : ClassScan.findAll().assignableTo(AbstractComponetAdapter.class).in("com.m4rc310.cb.builders.adapters")) {
            if (in == AbstractComponetAdapter.class) {
                continue;
            }

            try {
                Constructor constructor = in.getDeclaredConstructor();
                constructor.setAccessible(true);
                AbstractComponetAdapter adapter = (AbstractComponetAdapter) constructor.newInstance();
                if (adapter.isComponentFor(ac)) {
                    return adapter;
                }
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                infoError(e);
            }
        }

        throw new UnsupportedOperationException("Não há um <AbstractComponetAdapter> registrado para Acomponent: " + ac.ref());
    }

    public static void showDialog(Object objectDialog, Object... args) {
        new Thread() {
            @Override
            public void run() {
                new ComponentBuilder()._showDialog(objectDialog, "", null, args);
            }
        }.start();
    }

    public static void showDialog(String ref, Object... args) {
        new ComponentBuilder()._showDialog(ref, null, args);
    }

    public static void showDialogRelativeTo(String ref, Object c, Object... args) {
        new ComponentBuilder()._showDialog(ref, c, args);
    }

    public void _showDialog(Object objAnnotated, String ref, Object relative, Object... args) {

        if (objAnnotated != null) {
            objectAnnotated = objAnnotated;
//            Class[] classArgs = new Class[args.length];
//            MethodUtils.method(objectAnnotated, "setValuesToSearch", classArgs).invoke(args[0]);
        } else {
            objectAnnotated = getNewInstanceObjectAnnotated(ref, args);
        }

        Class<? extends Object> objectAnnotatedClass = objectAnnotated.getClass();

        dialog = new JDialogDefalt();

        if (objectAnnotatedClass.isAnnotationPresent(Amethod.class)) {
            Amethod am = objectAnnotatedClass.getDeclaredAnnotation(Amethod.class);
            final String methodOnError = am.methodOnError();
            LogServer.getInstance().debug(null, "Adicionando Listener de erro: {0}", methodOnError);
            if (!methodOnError.isEmpty()) {
                dialog.addPropertyChangeListener("onError", (PropertyChangeEvent evt) -> {
                    getTargetsForMethodName(methodOnError).stream().forEach((tar) -> {
                        MethodUtils.declaredMethod(tar, methodOnError, String.class, String.class).invoke(evt.getPropertyName(), evt.getNewValue());
                    });
                });
            }
        }

        Adialog ad = objectAnnotatedClass.getDeclaredAnnotation(Adialog.class);

        String title = getString(ad.title(), ref);
        if (ad.debug()) {
            title = getString("title.title.mode.debug", title);
        }
        dialog.setTitle(title);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                MethodUtils.method(objectAnnotated, "setComponentsBuilder", ComponentBuilder.class).invoke(ComponentBuilder.this);
                MethodUtils.method(objectAnnotated, "setDialog", Dialog.class).invoke(dialog);
                MethodUtils.method(objectAnnotated, "setGui", GuiUtils.class).invoke(gui);
            }
        });

        dialog.setLayout(new MigLayout(ad.layoutDialog()));
        dialog.abiliteCloseOnESC();

        putContainer(objectAnnotated.hashCode(), dialog);

        addTargets(objectAnnotated);
        loadAllFields(objectAnnotated, objectAnnotated.getClass());

        buildAllComponents();
        printDialog();

        dialog.setFontSize(ad.fontSize());
        dialog.setModal(ad.modal());
        dialog.setResizable(ad.resizable());
        dialog.pack();

        dialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        dialog.setLocationRelativeTo((Component) relative);
        dialog.showWindow();
    }

    public void _showDialog(String ref, Object relative, Object... args) {
        _showDialog(null, ref, relative, args);
    }

    private void buildAllComponents() {
        for (Map.Entry<Field, AbstractComponetAdapter> entrySet : adapters.entrySet()) {
            Field field = entrySet.getKey();

            Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);

            AbstractComponetAdapter adapter = entrySet.getValue();
            Object target = getTargetForField(field);

            adapter.setComponentsBuilder(this);
            adapter.build(field, target);

            Adialog adialog = objectAnnotated.getClass().getDeclaredAnnotation(Adialog.class);

            if (adialog.debug()) {
                try {
                    Object component = adapter.getComponent();

                    StringBuilder sgroup = new StringBuilder();
                    for (String group : ac.groups()) {
                        sgroup.append("[ ");
                        sgroup.append(group);
                        sgroup.append(" ]");
                    }

                    String toolTipText = String.format("Name: %s - ToolTipText: %s - Groups: %s", ac.ref(), ac.toolTipText(), sgroup);
                    MethodUtils.method(component, "setToolTipText", String.class).invoke(toolTipText);
                } catch (Exception e) {
                    infoError(e);
                }
            } else {
                Object component = adapter.getComponent();
                MethodUtils.method(component, "setToolTipText", String.class).invoke(getString(ac.toolTipText()));

            }

            try {
                Object value = field.get(target);
                adapter.update(field, value);

                if (ac.type().equals(EnumComponentType.PANEL)) {

                    Object component = adapter.getComponent();

//                    Object[] components = ((JPanel) component).getComponents();
//                    
//                    for (Object c : components) {
//                        boolean bvalue = (boolean) MethodUtils.method(c, "isEnabled").invoke();
//                        mc.put(c, bvalue);
//                    }
                    PropertyChangeListener pcl = new PropertyChangeListener() {
                        Map<Object, Boolean> mc = new HashMap<>();

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            Object[] components = ((JPanel) component).getComponents();

                            if (evt.getPropertyName().equals("enabled")) {

                                boolean enable = (boolean) evt.getNewValue();

                                for (Object com : components) {
                                    if (!mc.containsKey(com)) {
                                        boolean bvalue = (boolean) MethodUtils.method(com, "isEnabled").invoke();
                                        mc.put(com, bvalue);
                                    }

                                    if (enable) {
                                        MethodUtils.method(com, "setEnabled", boolean.class).invoke(mc.get(com));
                                    } else {
                                        MethodUtils.method(com, "setEnabled", boolean.class).invoke(enable);
                                    }
                                }
                            }
                        }
                    };

                    MethodUtils.method(component, "addPropertyChangeListener", PropertyChangeListener.class).invoke(pcl);
                    putContainer(value.hashCode(), component);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                 infoError(e);
                throw new UnsupportedOperationException(e);
            }
        }
    }

    public Object getJTabbedPane(String name) {
        for (Map.Entry<Field, AbstractComponetAdapter> entrySet : adapters.entrySet()) {
            Field field = entrySet.getKey();
            AbstractComponetAdapter adapter = entrySet.getValue();
            if (adapter instanceof AdapterTabPanel) {
                Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
                if (ac.name().equals(name)) {
                    return adapter.getComponent();
                }
            }
        }
        throw new UnsupportedOperationException("JTabbedPane não encontrado: " + name);
    }

    private final Map<String, JLabel> mapLabels = new HashMap<>();

    private void printDialog() {

        for (Field field : fields) {
//        fields.stream().forEach((field) -> {
            Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
            AbstractComponetAdapter adapter = adapters.get(field);
            Object target = getTargetForField(field);
            Object containner = containers.get(target.hashCode());

            if (!ac.tabFor().isEmpty()) {
                Object jtp = getJTabbedPane(ac.tabFor());
                String text = getString(ac.text());
                MethodUtils.method(jtp, "addTab", String.class, Component.class).invoke(text, adapter.getComponent());
                continue;
            }

            if (!ac.label().isEmpty()) {
                JLabel jLabel = gui.getJLabel(ac.label(), adapter.getComponent());
                MethodUtils.method(containner, "add", Component.class, Object.class).invoke(jLabel, ac.layoutLabel());
                mapLabels.put(ac.ref(), jLabel);
            }

            MethodUtils.method(containner, "add", Component.class, Object.class).invoke(adapter.getComponent(), ac.layout());
        }
    }

    public void changeLabel(String ref, String newLabel) {
        try {
            JLabel label = mapLabels.get(ref);
            label.setText(newLabel);
        } catch (Exception e) {
            infoError(e);
        }
    }

    private void loadAllFields(Object object, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Acomponent.class)) {
                continue;
            }

            field.setAccessible(true);

            Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);

            if (field.getClass().getDeclaredFields().length > 0) {
                Object value;

                try {
                    value = field.get(object);
                    if (ac.type().equals(EnumComponentType.PANEL)) {
                        addTargets(value);
                        loadAllFields(value, field.getType());
                    }

                    mudarReferencia(field);
                    addField(field);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    infoError(e);
                    LogServer.getInstance().error(null, "O <field> [{0}] está nulo!\nDialog foi iniciado de forma incompleta com falhas!", field);
                }
            }
        }
    }

    private void mudarReferencia(Field field) {
        Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
        String ref = ac.ref();
        if (ref.isEmpty()) {
            ref = field.getName().toLowerCase();
        }

        int i = 0;
        for (Field f : fields) {
            Acomponent ac2 = f.getDeclaredAnnotation(Acomponent.class);
            if (ac2.ref().contains(ref)) {
                i++;
            }
        }

        if (i > 0) {
            ref = String.format("%s_%d", ref, i);
        }
        changeAnnotationValue(ac, "ref", ref);
    }

    @Override
    public Object getTargetForField(Field field) {
        Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
        for (Object target : targets) {
            for (Field declaredField : target.getClass().getDeclaredFields()) {
                Acomponent ac1 = declaredField.getDeclaredAnnotation(Acomponent.class);
                try {
                    if (ac.ref().equals(ac1.ref())) {
                        return target;
                    }
                } catch (Exception e) {
                    infoError(e);
                }
            }
        }
        throw new UnsupportedOperationException("Não há nenhum <target> para o <field> [" + ac.ref() + "].");
    }

    public Object changeAnnotationValue(Annotation annotation, String key, Object newValue) {
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            infoError(e);
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            infoError(e);
            throw new IllegalStateException(e);
        }
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key, newValue);

        LogServer.getInstance().debug(null, "O campo <{0}> foi alterado de [{1}] para [{2}]", key, oldValue, newValue);

        return oldValue;
    }

    private void init() {

        this.gui = new GuiUtilsImpl() {

            @Override
            public String getText(String text, Object... args) {
                return getString(text, args);
            }

        };

        String pathConfig = PATH_CONFIGURATION;
        try {
            File file = new File(pathConfig);

            if (file.createNewFile()) {
                conf = new JSONObject();
                conf.put("path_gui", PATH_OF_DIALOGS_DEFAULT);

                try (Writer writer = new FileWriter(file)) {
                    conf.writeJSONString(writer);
                    writer.flush();
                }
            }

            try (Reader reader = new FileReader(file)) {
                conf = (JSONObject) new JSONParser().parse(reader);
            }

        } catch (IOException | ParseException e) {
            infoError(e);
        }
    }

    public void log(Level level, String message, Object... args) {
//        message = MessageFormat.format(message, args);
//        Logger.getLogger(level.getName()).log(level, message);
    }

    private Object getNewInstanceObjectAnnotated(String ref, Object... args) {
        Object ret = null;
        try {
            for (Class in : ClassScan.findAll().annotatedWith(Adialog.class).recursively().in(conf.get("path_gui").toString(), "com.m4rc310.gui")) {
                Adialog ad = (Adialog) in.getDeclaredAnnotation(Adialog.class);
                if (ad.ref().equals(ref)) {
                    if (ret != null) {
                        throw new Exception(String.format("Há mais de uma classe referênciada como [%s]!", ref));
                    }

                    Class[] types = new Class[args.length];

                    Constructor constructor = null;
                    for (int i = 0; i < args.length; i++) {
                        types[i] = args[i].getClass();
                        Class type = args[i].getClass();
                        for (Class ai : type.getInterfaces()) {
                            try {
                                constructor = in.getDeclaredConstructor(ai);
                                break;
                            } catch (NoSuchMethodException | SecurityException e) {
                                infoError(e);
                            }
                        }
                    }

                    constructor = constructor == null ? in.getDeclaredConstructor(types) : constructor;

//                    Constructor constructor = in.getDeclaredConstructor(types);
                    constructor.setAccessible(true);
                    ret = constructor.newInstance(args);
                }
            }
            return ret;
        } catch (Exception e) {
            infoError(e);
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public void update(Object... targetsA) {
        if (targetsA.length == 0) {
            Collections.addAll(targets, targetsA);
        }

        for (Object tar : targetsA) {
            adapters.entrySet().stream().forEach((entrySet) -> {
                Field field = entrySet.getKey();
                AbstractComponetAdapter adapter = entrySet.getValue();
                if (getTargetForField(field) == tar) {
                    try {
                        Object value = field.get(tar);
                        adapter.update(field, value);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        infoError(e);
                    }
                }
            });
        }
    }

    @Override
    public GuiUtils getGui() {
        return gui;
    }

    @Override
    public String getString(String text, Object... args) {
        try {
            return (String) MethodUtils.declaredMethod(objectAnnotated, "getString", String.class, Object[].class).invoke(text, args);
        } catch (Exception e) {
            infoError(e);
            return text;
        }
    }

    @Override
    public List getTargetsForMethodName(String methodName) {
        Collection<Method> methods = new ArrayList<>();
        List ret = new ArrayList();

        if (methodName.isEmpty()) {
            return ret;
        }

        for (Object tar : getAllTargets()) {
            Class tarClass = tar.getClass();

            methods.addAll(Arrays.asList(tarClass.getDeclaredMethods()));
            methods.addAll(Arrays.asList(tarClass.getMethods()));

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    if (!ret.contains(tar)) {
                        ret.add(tar);
                        break;
                    }
                }
            }
        }

        return ret;
    }

    @Override
    public Collection getAllTargets() {
        return targets;
    }

    @Override
    public void clear(Object... targetsA) {
        if (targetsA.length == 0) {
            Collections.addAll(targets, targetsA);
        }

        for (Object tar : targetsA) {
            adapters.entrySet().stream().forEach((entrySet) -> {
                Field field = entrySet.getKey();
                AbstractComponetAdapter adapter = entrySet.getValue();
                if (getTargetForField(field) == tar) {
                    try {
                        adapter.clear();
                    } catch (Exception e) {
                        infoError(e);
                    }
                }
            });
        }
    }

    private void infoError(java.lang.Exception e) {
//        LogServer.getInstance().error(e);
    }

}
