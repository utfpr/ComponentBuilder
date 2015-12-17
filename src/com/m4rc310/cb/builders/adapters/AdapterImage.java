/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.Amethod;
import com.m4rc310.cb.annotations.EnumComponentType;
import java.awt.Cursor;
import java.lang.reflect.Field;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Marcelo
 */
public class AdapterImage extends AbstractComponetAdapter<JPanel,String> {
    
    private JLabel jLabelImage ;
    
    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.IMAGE);
    }
    
    @Override
    public void buildComponent(Field field, Object target) {
        component = gui.getJPanel(new MigLayout("insets 0, gap 0"));
        jLabelImage = gui.getJLabel("");
        component.add(jLabelImage, ac.layout());
        
        if (ac.showBorder()) {
            component.setBorder(BorderFactory.createTitledBorder(ac.text()));
        }
        
        if(field.isAnnotationPresent(Amethod.class)){
            Amethod am = field.getDeclaredAnnotation(Amethod.class);
            if(!am.methodOnActionListener().isEmpty()){
                jLabelImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }
    }

    
    

    @Override
    public void setValue(String icon) {
        gui.setIcon(jLabelImage, icon, 
                    component.getPreferredSize().width, 
                    component.getPreferredSize().height);
    }

    @Override
    public String getValueDefault(Field field) {
        return "";
    }

    @Override
    public void clear() {
        jLabelImage.setIcon(null);
    }
    
   
}
