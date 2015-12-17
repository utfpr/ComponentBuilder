/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.EnumComponentType;
import java.lang.reflect.Field;
import javax.swing.JLabel;

/**
 *
 * @author Marcelo
 */
public class AdapterNone extends AbstractComponetAdapter<JLabel, Object> {

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.NONE);
    }

    @Override
    public void buildComponent(Field field, Object target) {

        component = gui.getJLabel(ac.internationalize() ? getString(ac.text()) : ac.text());
        
    }

    @Override
    public Object getValueDefault(Field field) {
        return ac.text();
    }

    @Override
    public void setValue(Object value) {
        component.setText(ac.internationalize() ? getString(value + "") : value + "");
//        component.setText(value + "");
    }

    @Override
    public void clear() {
    }

}
