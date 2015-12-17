/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.gui;

import com.m4rc310.cb.annotations.Aseach;
import com.m4rc310.cb.builders.ISearch;

/**
 *
 * @author Marcelo
 */
public interface ISearchDialog {
    void setISearch(ISearch iSearch);
    void setAserch(Aseach aseach);
    
    
    void setValuesToSearch(String value);
    
    //void show();
    void clearDialog();
}
