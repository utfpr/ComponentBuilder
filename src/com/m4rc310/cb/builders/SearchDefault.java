/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import com.m4rc310.ui.nfe.gui.actions.Action;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ObjectOid;

/**
 *
 * @author Marcelo
 * @param <T>
 */
public class SearchDefault<T> implements ISearch<T> , Action.Listener{

    protected ODB odb; 
    protected T value;
    protected List<T> list;
    
    protected final Collection<ISearchListener> iSearchListeners;
//    private ISearchSelectListener iSearchSelectListener;
//    private ISearchStatusListener iSearchStatusListener;

    public SearchDefault(ODB odb) {
        this.list = new ArrayList<>();
        this.odb = odb;
        this.iSearchListeners = new ArrayList<>();
    }

    
    @Override
    public List<T> search(Object... args) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public T unique(Object arg) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Object[] desmember(T o) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

   @Override
    public void setTarget(Object target) {
    }

    @Override
    public void returnValue() {
        
        iSearchListeners.stream().forEach((isl) -> {
            isl.loadValue(value);
        });
        
        try {
            ObjectOid oid = odb.getObjectId(value);
            status("return object with oid: " + oid);
            isNew(false);
        } catch (Exception e) {
            isNew(true);
        }
    }
    
    protected void isNew(boolean isnew){
        iSearchListeners.stream().forEach((isl) -> {
           isl.newValue(isnew);
        });
//        if(iSearchListeners!=null){
//            iSearchListeners.newValue(isnew);
//        }
    }
    
    protected void status(String text){
        
        iSearchListeners.stream().forEach((isl) -> {
           isl.status(text);
        });
//        if(iSearchListeners!=null){
//            iSearchListeners.status(text);
//        }
    }
    

    @Override
    public void selectedValue(T value) {
        this.value = value;
        iSearchListeners.stream().forEach((isl) -> {
            isl.selected(value);
        });
    }

    @Override
    public void addISearchListener(ISearchListener isl) {
        if(!iSearchListeners.contains(isl)){
            iSearchListeners.add(isl);
        }
    }

//    @Override
//    public void setISearchSelectListener(ISearchSelectListener isl) {
//        this.iSearchSelectListener = isl;
//    }
    
    
    protected Object[] getObjects(Object... args){
        return args;
    }

    @Override
    public Collection<ISearchListener> getISearchListeners() {
        return iSearchListeners;
    }

    protected void cloneObject(Object source, Object target) {

        try {

            Class csource = source.getClass();
            Class ctarget = target.getClass();

            Collection<Field> sourceFields = new ArrayList<>();
            Collection<Field> targetFields = new ArrayList<>();

            while (csource != Object.class) {
                sourceFields.addAll(Arrays.asList(csource.getDeclaredFields()));
                csource = csource.getSuperclass();
            }

            while (ctarget != Object.class) {
                targetFields.addAll(Arrays.asList(ctarget.getDeclaredFields()));
                ctarget = ctarget.getSuperclass();
            }

            sourceFields.addAll(Arrays.asList(csource.getFields()));

//        targetFields.addAll(Arrays.asList(ctarget.getDeclaredFields()));
//        targetFields.addAll(Arrays.asList(ctarget.getFields()));
            for (Field sourceField : sourceFields) {
                for (Field targetField : targetFields) {
                    if (sourceField.getName().equals(targetField.getName())) {
                        sourceField.setAccessible(true);
                        targetField.setAccessible(true);

                        try {
                            Object _value = sourceField.get(source);
                            targetField.set(target, _value);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void setArguments(Object... args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
