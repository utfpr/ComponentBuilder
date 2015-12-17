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
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author Marcelo
 */
public class AdapterProgressBar extends AbstractComponetAdapter<JProgressBar, Integer> {

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.PROGRESS_BAR);
    }

    @Override
    public void buildComponent(Field field, Object target) {
        if (field.isAnnotationPresent(Amethod.class)) {
            Amethod am = field.getDeclaredAnnotation(Amethod.class);
            
            if(!am.methodOnProgressInfo().isEmpty()){
                List list = componentsBuilder.getTargetsForMethodName(am.methodOnProgressInfo());
                for (Object tar : list) {
                    Integer[] info = (Integer[]) MethodUtils.declaredMethod(tar, am.methodOnProgressInfo()).invoke();
                    component = new JProgressBar(info[0], info[1]);
                }
            }else{
                component = new JProgressBar(0,100);
            }
            
//            int[] info = am.methodOnProgressInfo();
        } else {
            component = new JProgressBar();
        }
    }

    @Override
    public void clear() {
        component.setValue(0);
    }

    @Override
    public void setValue(Integer value) {
        component.setValue(value);
    }

    @Override
    public void update(Field field, Integer value) {
        if (field.isAnnotationPresent(Amethod.class)) {
            Amethod am = field.getDeclaredAnnotation(Amethod.class);
            if(!am.methodOnProgressInfo().isEmpty()){
                List list = componentsBuilder.getTargetsForMethodName(am.methodOnProgressInfo());
                for (Object tar : list) {
                    Integer[] info = (Integer[]) MethodUtils.declaredMethod(tar, am.methodOnProgressInfo()).invoke();
                    component.setMinimum(info[0]);
                    component.setMaximum(info[1]);
                }
            }
            
            
        }
        
        component.setStringPainted(true);

        component.setIndeterminate(value == -1);

        component.setValue(value);
    }

    @Override
    public Integer getValueDefault(Field field) {
        return 0;
    }

}
