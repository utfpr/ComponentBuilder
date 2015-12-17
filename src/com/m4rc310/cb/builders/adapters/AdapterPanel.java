/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.EnumComponentType;
import java.awt.Color;
import java.lang.reflect.Field;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Marcelo
 */
public class AdapterPanel extends AbstractComponetAdapter<JPanel,Object> {
    
    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.PANEL);
    }
    
    @Override
    public void buildComponent(Field field, Object target) {
        component = gui.getJPanel(new MigLayout(ac.layoutContainer()));
        if (ac.showBorder()) {
            component.setBorder(BorderFactory.createTitledBorder(getString(ac.text())));
        }
        
        if(ac.background()!= -1){
            component.setBackground(new Color(ac.background()));
        }
    }
    
    @Override
    public Object getValueDefault(Field field) {
        return field.getName();
    }

    @Override
    public void setValue(Object value) {
    }

    @Override
    public void clear() {
    }

    
   
}
