/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.Amethod;
import com.m4rc310.cb.annotations.EnumComponentType;
import com.m4rc310.cb.utils.MethodUtils;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

/**
 *
 * @author Marcelo
 */
public class AdapterJComboBoxOld extends AbstractComponetAdapter<JComboBox, List> {

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return false;
//        return ac.type().equals(EnumComponentType.COMBO_BOX);
    }

    @Override
    public void buildComponent(Field field, Object target) {
        component = gui.getJComboBox("");

        if (field.isAnnotationPresent(Amethod.class)) {
            Amethod am = field.getDeclaredAnnotation(Amethod.class);
            if (!am.methodOnDismemberObject().isEmpty()) {
                component.setRenderer((JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) -> {
                    try {
                        value = MethodUtils.declaredMethod(target, am.methodOnDismemberObject(), value.getClass()).invoke(value);
//                        return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    } catch (Exception e) {
                    }
                    return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                });
            }

            component.addActionListener((ActionEvent e) -> {
                if (am.methodOnReturnSelectedValue().isEmpty()) {
                    return;
                }

                storageFieldContainer.getTargets(field).stream().forEach((tar) -> {
                    try {
                        Object value = component.getSelectedItem();
                        
                        if (value != null) {
                            MethodUtils.declaredMethod(tar, am.methodOnReturnSelectedValue(), value.getClass()).invoke(value);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

            });
        }

    }

    @Override
    public void update(Field field, List value) {
    }
    
    
    

    @Override
    public void setValue(List value) {
        component.removeAllItems();
        value.stream().forEach((item) -> {
            component.addItem(item);
        });
    }

    @Override
    public List getValueDefault(Field field) {
        return new ArrayList();
    }

    @Override
    public void clear() {
        component.setSelectedItem(null);
    }
}
