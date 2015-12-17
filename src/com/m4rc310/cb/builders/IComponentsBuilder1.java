/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import com.m4rc310.cb.builders.adapters.AbstractComponetAdapter;
import com.m4rc310.ui.gui.componentUtils.GuiUtils;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marcelo
 */
public interface IComponentsBuilder1 {
    
    public static final String PATH_CONFIGURATION = "conf.json";
    public static final String PATH_OF_DIALOGS_DEFAULT = "com.m4rc310.gui";
    
//    void drawDialog(Object objectAnnotated);
//    
//    void drawPanel(Object panel, Class objectAnnotated, Object... args);
//    
    void update(Object... target);
    void clear(Object... target);
//    
//    
//    void setPathDialogAnnotated(String pathDialogAnnotated);
//    
//    void showDialog(String ref, Object... args);
//    void addTarget(Object target);
//    void putContainer(int hash,Object container);
//    
//    Object getContainerForTarget(Object target) ;
//    Object getTargetForField(Field field);
//    Object changeAnnotationValue(Annotation annotation, String key, Object newValue) ;
    
    List getTargetsForMethodName(String methodName);
    
    Map<Field, AbstractComponetAdapter> getAdapters();
    
    
    JDialogDefalt getDialog();
    Collection<?> getAllTargets();
    Object getTargetForField(Field field);
//    
    String getString(String text, Object... args);
    
    GuiUtils getGui();
//    
//    
//    Object changeAnnotationValue(Annotation annotation, String key, Object newValue);
//
//    String getString(String text, Object... args);
}
