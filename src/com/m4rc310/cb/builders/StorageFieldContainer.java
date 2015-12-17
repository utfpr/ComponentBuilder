/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import com.m4rc310.cb.builders.adapters.AbstractComponetAdapter;
import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.EnumComponentType;
import com.m4rc310.ui.gui.componentUtils.GuiUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.trugger.scan.ClassScan;

/**
 *
 * @author Marcelo
 */
public class StorageFieldContainer {

    private final String PATH_COMPONENTS_ADAPTERS = AbstractComponetAdapter.class.getPackage().getName();
    private final List<FieldContainer> fieldsContainers;
    private final List<AbstractComponetAdapter> listAbstractComponetAdapters;
    private final Collection targets;
    private ComponentBuilder componentsBuilder;
    private GuiUtils gui;

    public StorageFieldContainer() {
        this.fieldsContainers = new ArrayList<>();
        this.listAbstractComponetAdapters = new ArrayList<>();
        this.targets = new ArrayList();
    }

    StorageFieldContainer(ComponentBuilder componentsBuilder) {
        this();
        this.componentsBuilder = componentsBuilder;
        init();
    }

    public void update(Object... targets) {
        
        for (Object target : targets) {
            for (AbstractComponetAdapter adapter : listAbstractComponetAdapters) {
                for (Field field : target.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(Acomponent.class)) {
                        field.setAccessible(true);
                        try {
                            Object value = field.get(target);
                            adapter.update(field, value);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            log(Level.WARNING, e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public List<FieldContainer> getFieldsContainers() {
        return fieldsContainers;
    }

    private void processTargets(Class classTarget, Object target) {

        for (Field field : classTarget.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Acomponent.class)) {
                
                
                try {
                    Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
                    target = getTarget(field);
                    Object value = field.get(target);
                    if (ac.type().equals(EnumComponentType.PANEL)) {
                        if (value != null) {
                            target = value;
                            if (!targets.contains(target)) {
                                targets.add(target);
                            }
                        }
                    } else {
                        for (AbstractComponetAdapter lc : listAbstractComponetAdapters) {
                            if (lc.isComponentFor(ac)) {
                                lc.update(field, value);
                                lc.setTarget(target);
                            }
                        }
                    }
                    Class type = field.getType();
                    processTargets(type, target);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log(Level.SEVERE, e.getCause().getMessage());
                }
            }
        }
    }

    public Collection getTargets() {
        return targets;
    }

    public void setValueInTarget(Acomponent ac, Object value) {
        for (Object target : targets) {
            try {
                for (Field field : target.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(Acomponent.class)) {
                        Acomponent fac = field.getDeclaredAnnotation(Acomponent.class);
                        if (fac.ref().equals(ac.ref())) {
                            field.setAccessible(true);
                            field.set(target, value);
                            return;
                        }
                    }
                }
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                log(Level.SEVERE, e.getCause().getMessage());
            }
        }
    }

    private void init() {

        log(Level.INFO, "preparando repositório de adaptadores...");

        for (Class clasz : ClassScan.findAll().assignableTo(AbstractComponetAdapter.class).recursively().in(PATH_COMPONENTS_ADAPTERS)) {
            if (clasz == AbstractComponetAdapter.class) {
                continue;
            }
            try {
                Object instance = clasz.newInstance();
                log(Level.INFO, "nova instancia {0}", instance.toString());
                listAbstractComponetAdapters.add((AbstractComponetAdapter) instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(StorageFieldContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private AbstractComponetAdapter getAbstractComponetAdapter(Acomponent ac) {
        for (Class clasz : ClassScan.findAll().assignableTo(AbstractComponetAdapter.class).recursively().in(PATH_COMPONENTS_ADAPTERS)) {
            if (clasz == AbstractComponetAdapter.class) {
                continue;
            }
            try {
                AbstractComponetAdapter adapter = (AbstractComponetAdapter) clasz.newInstance();
                if (adapter.isComponentFor(ac)) {
                    adapter.setStorageFieldContainer(this);
//                    adapter.setComponentsBuilder(componentsBuilder);
                    return adapter;
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(StorageFieldContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        throw new UnsupportedOperationException("ComponentAdapter not found!");
    }

    public boolean containFieldContainer(Acomponent ac) {
        return fieldsContainers.stream().map((fc) -> fc.getField()).map((field) -> field.getDeclaredAnnotation(Acomponent.class)).anyMatch((lac) -> (lac.ref().equals(ac.ref())));
    }

    public void addFieldContainer(FieldContainer fieldContainer) {
        Acomponent ac = fieldContainer.getField().getDeclaredAnnotation(Acomponent.class);
        String ref = "";
        if (ac.ref().isEmpty()) {
            ref = fieldContainer.getField().getName().toLowerCase();
            changeAnnotationValue(ac, "ref", ref);
        }

        int i = 0;
        for (FieldContainer fc : fieldsContainers) {
            Acomponent lfc = fc.getField().getDeclaredAnnotation(Acomponent.class);
            String localRef = lfc.ref();
            if (localRef.startsWith(ref)) {
                i++;
            }
        }

        if (i > 0) {
            ref = String.format("%s%d", ac.ref(), i);
            changeAnnotationValue(ac, "ref", ref);
        }
        log(Level.INFO, "<FielContainer>[{0}] adicionado!", ref);
        
        fillComponent(ac, fieldContainer);
        fieldsContainers.add(fieldContainer);
    }

    public void fillComponent(Acomponent ac, FieldContainer container) {
        AbstractComponetAdapter adapter = getAbstractComponetAdapter(ac);
        adapter.build(container.getField(), container.getTarget());

        container.setComponent(adapter.getComponent());
        container.setAdapter(adapter);
        listAbstractComponetAdapters.add(adapter);
        if (!targets.contains(container.getTarget())) {
            targets.add(container.getTarget());
        }

        processTargets(container.getTarget().getClass(), container.getTarget());

    }

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

        log(Level.OFF, "Mudando valor de <ref> -> [{0}] para [{1}]", oldValue, newValue);

        return oldValue;
    }

    private void log(Level level, String text, Object... args) {
        Logger.getLogger(getClass().getName()).log(level, MessageFormat.format(text, args));
    }

    public Object getTarget(Field field) {
        Acomponent a1 = field.getDeclaredAnnotation(Acomponent.class);
        for (Object target : getTargets()) {
            for (Field f : target.getClass().getDeclaredFields()) {
                Acomponent a2 = f.getDeclaredAnnotation(Acomponent.class);
            }
        }
        
        
        
        
        throw new UnsupportedOperationException("Target não encontrado para o <field> [" + field +"]");
    }
    
    public List getTargets(Field field) {
        List ret = new ArrayList();
        for (Object target : getTargets()) {
            for (Field f : target.getClass().getDeclaredFields()) {
                if(f.getName().equals(field.getName())){
                    ret.add(target);
                }
            }
        }
        return ret;
    }
}
