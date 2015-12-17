/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.EnumComponentType;
import java.lang.reflect.Field;
import javax.swing.JTabbedPane;

/**
 *
 * @author Marcelo
 */
public class AdapterTabPanel extends AbstractComponetAdapter<JTabbedPane,Object> {
    
    private JTabbedPane jTabbedPane;

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.TABBED_PANE);
    }

    @Override
    public void buildComponent(Field field, Object target) {
//        component = gui.getJPanel(new MigLayout("insets 0, gap 0"));
        component = gui.getJTabbedPane();
//        component.add(jTabbedPane, "w 100%,h 100%");
        
//        jTabbedPane.addTab("test", new JPanel());
        
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
