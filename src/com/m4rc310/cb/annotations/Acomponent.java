/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.swing.JPasswordField;

/**
 *
 * @author Marcelo
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Acomponent {

    public static final int NONE = -1;
    public static final int CENTER = 0;
    public static final int TOP = 1;
    public static final int LEFT = 2;
    public static final int BOTTOM = 3;
    public static final int RIGHT = 4;

    public static final int TO_UPPER_CASE = 5;
    public static final int TO_LOWER_CASE = 6;

    EnumComponentType type() default EnumComponentType.NONE;

    EnumTypeOfValue typeValue() default EnumTypeOfValue.STRING;

    boolean toUpperCase() default false;

    boolean toLowerCase() default false;

    boolean showBorder() default false;

    boolean visibleTreeNode() default true;

    String layoutLabel() default "";

    String layoutContainer() default "";

    String layout() default "";

    String ref() default "";

    String text() default "";

    String label() default "";

    String name() default "";

    String tabFor() default "";

    String dateFormat() default "dd/MM/yyyy";

    String format() default "";

    String toolTipText() default "";
    
    String[] groups() default "";
    
    
    int horizontalAlignment() default NONE;

    int background() default -1;

    int foreground() default -1;

    int fontSize() default 12;

    int[] style() default 0;

    int w() default 20;

    int h() default 20;

    int toCase() default -1;

    int maxLength() default -1;

    boolean memorize() default false;

    boolean editable() default true;

    boolean internationalize() default true;

    boolean focusable() default false;

    char echoChar() default '-';

}
