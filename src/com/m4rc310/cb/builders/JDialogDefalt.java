/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders;

import com.m4rc310.ui.gui.componentUtils.GuiUtils;
import com.m4rc310.ui.gui.componentUtils.impl.MJDialog;
import java.awt.Font;

/**
 *
 * @author Marcelo
 */
public class JDialogDefalt extends MJDialog {
    
    public GuiUtils getGuiUtils() {
        return gui;
    }
    
    public void setFontSize(int size){
        Font f = getFont();
        if(f == null){
            f = new Font(Font.SANS_SERIF, Font.TRUETYPE_FONT, size);
        }else{
            f = new Font(f.getFamily(), f.getStyle(), size);
        }
        gui.setFontToAllComponentsContainer(this, f);
    }
    
}
