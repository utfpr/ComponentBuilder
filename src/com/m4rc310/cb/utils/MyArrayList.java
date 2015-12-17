/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.utils;

import br.edu.utfpr.cm.tsi.utils.LogServer;
import java.util.ArrayList;

/**
 *
 * @author Marcelo
 * @param <T>
 */
public class MyArrayList<T> extends ArrayList<T> {

    @Override
    public boolean add(T e) {

        LogServer.getInstance().debug(e, "ADD: {0}", e);

        return super.add(e);
    }

}
