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
import java.awt.Component;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author Marcelo
 */
public class AdapterJComboBox extends AbstractComponetAdapter<JComboBox, List> {

    private DefaultComboBoxModel model;
//    private DefaultListCellRenderer renderer;

    @Override
    public boolean isComponentFor(Acomponent ac) {
//        return false;
        return ac.type().equals(EnumComponentType.COMBO_BOX);
    }

    @Override
    public void buildComponent(Field field, Object container) {
        model = new DefaultComboBoxModel();
        this.component = new JComboBox(model);
        component.setEditable(false);
        
        component.setModel(model);

        if (field.isAnnotationPresent(Amethod.class)) {
            return;
        }

        Amethod am = field.getDeclaredAnnotation(Amethod.class);
        if (!am.methodOnDismemberObject().isEmpty()) {
            
//            component.setIgnoreRepaint(true);
            
            component.setRenderer((JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) -> {
                try {
                    Object tar = componentsBuilder.getTargetForField(field);
                    value = MethodUtils.method(tar, am.methodOnDismemberObject(), value.getClass()).invoke(value);
                    return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                } catch (Exception e) {
                    return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });

        }
        
    }

    @Override
    public void update(Field field, List value) {
        super.update(field, value);
    }

    @Override
    public void setValue(List list) {

        model.removeAllElements();

        list.stream().forEach((value) -> {
            model.addElement(value);
        });

//        model.setSelectedItem("");
    }

    @Override
    public void clear() {
    }

    @Override
    public List getValueDefault(Field field) {
        return new ArrayList();
    }

}
