/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import java.util.List;

/**
 *
 * @author Marcelo
 * @param <T>
 */
public interface ISearchListener<T> {

    public void loadListValues(List<T> values);
    
    public void loadValue(T value);

    public void onSearchError(String title, String description);

    public void selected(T value);

    public void newValue(Boolean isNew);

    public void status(String text);
    
    public void resultEmpty(boolean  empty);
    
    void newValueAddeded();
    
}
