/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.gui;

import br.edu.utfpr.cm.tsi.utils.B;
import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.Adialog;
import com.m4rc310.cb.annotations.Amethod;
import com.m4rc310.cb.annotations.Aseach;
import com.m4rc310.cb.annotations.EnumComponentType;
import com.m4rc310.cb.builders.ISearch;
import com.m4rc310.cb.utils.DialogObject;
import com.m4rc310.ui.nfe.gui.actions.Action;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Marcelo
 */
@Adialog(ref = "search", layoutDialog = "insets 5, gap 0", modal = true, debug = true)
public class DialogSearch extends DialogObject implements ISearchDialog {

    @Acomponent(type = EnumComponentType.PANEL, showBorder = true, layoutContainer = "insets 5, gap 0", layout = "wrap")
    private final PanelTop panelTop;
    private ISearch iSearch;
    private Aseach aseach;

    @Amethod(methodOnDismemberObject = "desmember", methodOnChangeValue = "selectedValue")
    @Acomponent(type = EnumComponentType.TABLE, layout = "split,span,wrap, w 100%, h 100%, hmin 140")
    private List results;

    @Acomponent(type = EnumComponentType.BUTTON, layout = "split,span,R")
    private final ActionListener actionListenerCancel;
    @Acomponent(type = EnumComponentType.BUTTON)
    private final ActionListener actionListenerOK;

    private Object selectedValue;

    public DialogSearch(String value) {
        this();
        panelTop.setDescription(value);
    }

    public DialogSearch() {
        this.panelTop = new PanelTop();
        this.results = new ArrayList();
        this.actionListenerCancel = getActionListenerCancel();
        this.actionListenerOK = getActionListenerOK();
        
    }

    @Override
    protected void init(Dialog dialog) {
        addTarget(iSearch, this);
        update(this);
//        format();
    }

    private void selectedObject(Object value) {
        this.selectedValue = value;
        
    }

    @Override
    public void setISearch(ISearch iSearch) {
        this.iSearch = iSearch;
    }

    @Override
    public void setAserch(Aseach aseach) {
        this.aseach = aseach;
        format();
    }

    private void format() {
//        Class panelTopClass = panelTop.getClass();
        try {

            List<Field> fields = new ArrayList<>();

            fields.addAll(Arrays.asList(panelTop.getClass().getDeclaredFields()));
            fields.addAll(Arrays.asList(getClass().getDeclaredFields()));

            fields.stream().filter((field) -> !(!field.isAnnotationPresent(Acomponent.class))).forEach((field) -> {
                Acomponent ac = field.getDeclaredAnnotation(Acomponent.class);
                if (field.getName().equals("results")) {
                    Amethod am = field.getDeclaredAnnotation(Amethod.class);
                    changeAnnotationValue(am, "tableCollumnNames", aseach.tableCollumnNames());
                    changeAnnotationValue(am, "tableCollumnWith", aseach.tableCollumnWith());
                }
                if (field.getName().equals("actionListenerCancel")) {
                    changeAnnotationValue(ac, "text", aseach.textButtonCancel());
                }
                if (field.getName().equals("actionListenerOK")) {
                    changeAnnotationValue(ac, "text", aseach.textButtonReturn());
                }
                if (field.getName().equals("anny")) {
                    changeAnnotationValue(ac, "text", aseach.textAnny());
                }
                if (field.getName().equals("description")) {
                    changeAnnotationValue(ac, "label", aseach.labelSearch());
                }
                if (field.getName().equals("actionListenerSearch")) {
                    changeAnnotationValue(ac, "text", aseach.textSearch());
                }
            });
        } catch (Exception e) {
        }
    }

    private ActionListener getActionListenerCancel() {
        return (ActionEvent ae) -> {
            dialog.dispose();
        };
    }

    private ActionListener getActionListenerOK() {
        return (ActionEvent ae) -> {
            iSearch.returnValue();
            dialog.dispose();
        };
    }

    @Override
    public String getString(String text, Object... args) {
        return B.getString(text, args);
    }

    @Override
    public void setValuesToSearch(String value) {
        panelTop.setDescription(value);
    }

    @Override
    public void clearDialog() {
        results = new ArrayList();
        update(this);
    }

    private class PanelTop implements Action.Listener {

        @Acomponent(type = EnumComponentType.TEXT_FIELD, layout = "growx,wmin 200")
        private String description;

        @Acomponent(type = EnumComponentType.CHECK_BOX)
        private boolean anny;

        @Acomponent(type = EnumComponentType.BUTTON)
        private final ActionListener actionListenerSearch;

        public PanelTop() {
            this.actionListenerSearch = getActionListenerSearch();
        }

        private ActionListener getActionListenerSearch() {
            return (ActionEvent ae) -> {
                results = iSearch.search(description, anny);
                update(DialogSearch.this);
            };
        }

        public void setDescription(String description) {
            this.description = description;

            if (!description.isEmpty()) {
                results = iSearch.search(description, anny);
            }
        }
    }
}
