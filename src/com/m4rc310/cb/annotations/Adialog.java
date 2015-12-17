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

/**
 *
 * @author Marcelo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adialog {

    String ref();

    String title() default "";

    String layoutDialog() default "";
    
    int key() default 0;

    int fontSize() default 12;

    boolean modal() default false;

    boolean resizable() default true;

    boolean debug() default false;

}
