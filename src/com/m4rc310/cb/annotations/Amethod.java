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
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Amethod {

    String methodOnDismemberObject() default "";

//    String methodOnComboBoxRendererMethod() default "";
    String methodOnReturnSelectedValue() default "";

    String methodOnActionListener() default "";

    String methodOnInputVerify() default "";

    String methodOnKeyListener() default "";

    String methodOnChangeValue() default "";

    String methodOnChangeValueObject() default "";

    String methodOnChangeValueComponent() default "";

    String methodOnLostFocus() default "";

    String methodOnGrabFocus() default "";

    String methodOnError() default "";

    String[] tableCollumnNames() default {};

    int[] tableCollumnWith() default {};

    String methodOnProgressInfo() default "";

    String  methodReturnTableControl() default "";
}
