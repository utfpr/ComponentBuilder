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
 */
public class ISearchAdapter<T> implements ISearchListener<T> {

    @Override
    public void loadValue(T value) {
    }

    @Override
    public void onSearchError(String title, String description) {
    }

    @Override
    public void selected(T value) {
    }

    @Override
    public void newValue(Boolean isNew) {
    }

    @Override
    public void status(String text) {
    }

    @Override
    public void newValueAddeded() {
    }

    @Override
    public void loadListValues(List<T> values) {
    }

    @Override
    public void resultEmpty(boolean empty) {
    }

}
