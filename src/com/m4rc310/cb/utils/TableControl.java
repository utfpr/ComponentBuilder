/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.utils;

/**
 *
 * @author Marcelo
 */
public class TableControl {

    private TableControlListener tableControlListener;

    public TableControl() {
        this.tableControlListener = new TableControlAdapter();
    }

    public void setTableControlListener(TableControlListener tableControlListener) {
        this.tableControlListener = tableControlListener;
    }

    public TableControlListener getTableControlListener() {
        return tableControlListener;
    }

    public void setObjectItem(Object item){}
    
    

    public interface TableControlListener<T> {
        void valueSelected(T value);
    }
}
