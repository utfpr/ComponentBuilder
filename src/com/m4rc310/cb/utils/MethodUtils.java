/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author Marcelo
 */
public class MethodUtils {

    private Method method;
    private Object target;

    public static MethodUtils declaredMethod(Object target, String methodName, Class... types) {
        return new MethodUtils().declaredMethod_(target, methodName, types);
    }
    public static MethodUtils method(Object target, String methodName, Class... types) throws RuntimeException{
        return new MethodUtils().method_(target, methodName, types);
    }

    private MethodUtils declaredMethod_(Object target, String methodName, Class... types) {
        try {
            method = target.getClass().getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            this.target = target;
            return this;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    private MethodUtils method_(Object target, String methodName, Class... types) {
        try {
            method = target.getClass().getMethod(methodName, types);
            method.setAccessible(true);
            this.target = target;
            return this;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    
    
    public Object invoke(Object... args) throws RuntimeException{
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

}
