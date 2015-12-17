/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.EnumComponentType;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import javax.swing.JCheckBox;

/**
 *
 * @author Marcelo
 */
public class AdapterJCheckBox extends AbstractComponetAdapter<JCheckBox, Boolean> {

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.CHECK_BOX);
    }

    @Override
    public void buildComponent(Field field, Object target) {
        component = gui.getJCheckBox(ac.text());
        component.addActionListener((ActionEvent e) -> {
            boolean value = ((JCheckBox) e.getSource()).isSelected();
            fireChangeListener(value);
        });
    }

    @Override
    public void setValue(Boolean value) {
        component.setSelected(value);
    }

    @Override
    public Boolean getValueDefault(Field field) {
        return false;
    }

    @Override
    public void clear() {
        
    }

}
