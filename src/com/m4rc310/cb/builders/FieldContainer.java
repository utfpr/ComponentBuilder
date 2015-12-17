/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.builders.adapters.AbstractComponetAdapter;
import java.lang.reflect.Field;

/**
 *
 * @author Marcelo
 */
public class FieldContainer {

    private Field field;
    private Object target;
    private Object container;
    private Object component;
    private Acomponent acomponent;
    private AbstractComponetAdapter adapter;

    public FieldContainer(Field field, Object target, Object component) {
        this.field = field;
        this.target = target;
        this.component = component;
        loadAcomponent();
    }

    private void loadAcomponent() {
        acomponent = field.getDeclaredAnnotation(Acomponent.class);
    }

    public Field getField() {
        return field;
    }

    public Object getTarget() {
        return target;
    }

    public Object getComponent() {
        return component;
    }

    /**
     * @param field the field to set
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * @param component the component to set
     */
    public void setComponent(Object component) {
        this.component = component;
    }

    public Acomponent getAcomponent() {
        return acomponent;
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

    /**
     * @return the adapter
     */
    public AbstractComponetAdapter getAdapter() {
        return adapter;
    }

    /**
     * @param adapter the adapter to set
     */
    public void setAdapter(AbstractComponetAdapter adapter) {
        this.adapter = adapter;
    }
}
