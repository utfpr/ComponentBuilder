/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.annotations;

import com.m4rc310.cb.builders.ISearch;
import com.m4rc310.gui.DialogSearch;
import com.m4rc310.gui.ISearchDialog;
import java.awt.event.KeyEvent;
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
public @interface Aseach {

    String dialogRef() default "";

    Class<? extends ISearch> typeSearch() default ISearch.class;
    Class<? extends ISearchDialog> typeSearchDialog() default DialogSearch.class;

    String methodSearch() default "";

    String labelSearch() default "label.search";

    String textAnny() default "text.anny";
    
    String textSearching() default "label.searching";

    String textSearch() default "text.search";

    String textButtonCancel() default "text.cancel";

    String textButtonReturn() default "text.return";

    int background() default 0x71FFCB;

    int keyCode() default KeyEvent.VK_F5;
    
    boolean showSearchDialog() default true;
    
    String[] tableCollumnNames() default {};

    int[] tableCollumnWith() default {};
    
}
