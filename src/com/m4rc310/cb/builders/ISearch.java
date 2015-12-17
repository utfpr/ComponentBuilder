/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Marcelo
 * @param <T>
 */
public interface ISearch<T> {
    
    List<T> search(Object... args);
    T unique(Object arg) throws Exception;
    Object[] desmember(T o);

    void setArguments(Object... args);
    
//    void setODB(ODB odb);

    void setTarget(Object target);
    void returnValue();
    void selectedValue(T value);

    Collection<ISearchListener> getISearchListeners() ;
    
    void addISearchListener(ISearchListener isl);
    
}
