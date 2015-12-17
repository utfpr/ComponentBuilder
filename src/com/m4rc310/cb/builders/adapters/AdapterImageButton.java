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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author Marcelo
 */
public class AdapterImageButton extends AbstractComponetAdapter<JButton, String> {

    private JLabel jLabelImage;

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.IMAGE_BUTTON);
    }

    @Override
    public void buildComponent(Field field, Object target) {
        component = gui.getJButton("");

        if (field.isAnnotationPresent(Amethod.class)) {
            Amethod am = field.getDeclaredAnnotation(Amethod.class);
            component.addActionListener((ActionEvent e) -> {
                String methodOnActionListener = am.methodOnActionListener();
                componentsBuilder.getTargetsForMethodName(methodOnActionListener).stream().forEach((tar) -> {
                    MethodUtils.declaredMethod(tar, methodOnActionListener).invoke();
                });
            });
        }
        
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void setValue(String icon) {
        gui.setIcon(component, icon, ac.w(), ac.h());

        component.setMargin(new Insets(0, 0, 0, 0));
        component.setBorder(null);
        component.setOpaque(false);
        component.setContentAreaFilled(false);
        component.setBorderPainted(false);

        component.setPreferredSize(new Dimension(ac.w(), ac.h()));

    }

    @Override
    public String getValueDefault(Field field) {
        return "";
    }

    @Override
    public void clear() {
//        component.setIcon(null);
    }

}
