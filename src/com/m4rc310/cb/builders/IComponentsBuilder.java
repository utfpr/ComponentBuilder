/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import java.lang.annotation.Annotation;

/**
 *
 * @author Marcelo
 */
public interface IComponentsBuilder {
    
    public static final String PATH_OF_DIALOGS_DEFAULT = "com.m4rc310.gui";
    
    void drawDialog(Object objectAnnotated);
    
    void drawPanel(Object panel, Class objectAnnotated, Object... args);
    
    void update(Object... target);
    
    
    void setPathDialogAnnotated(String pathDialogAnnotated);
    
    void showDialog(String ref, Object... args);
    
    
    Object changeAnnotationValue(Annotation annotation, String key, Object newValue);

    String getString(String text, Object... args);
}
