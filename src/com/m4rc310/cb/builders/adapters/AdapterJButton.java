/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import br.edu.utfpr.cm.tsi.utils.LogServer;
import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.EnumComponentType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Marcelo
 */
public class AdapterJButton extends AbstractComponetAdapter<JButton, ActionListener> {

    private final ActionListener actionListener;

    private ActionListener value;

    public AdapterJButton() {
        this.actionListener = (ActionEvent e) -> {
            LogServer.getInstance().warning(e.getSource(), "Action is Empty");
        };
    }

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.BUTTON);
    }

    @Override
    public void buildComponent(Field field, Object target) {
        component = gui.getJButton("");
        component.setText(getString(ac.text()));

        try {
            value = (ActionListener) field.get(target);
            value = value == null ? getValueDefault(field) : value;
            component.addActionListener((ActionEvent ae) -> {
                value.actionPerformed(ae);
            });
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LogServer.getInstance().error(e);
        }
    }

    @Override
    public void setValue(ActionListener value) {
        this.value = value;
    }

    @Override
    public void update(Field field, ActionListener value) {
        removeAllActionListeners();
        super.update(field, value);
        this.value = value;

        if (value != null) {
            component.addActionListener(value);
        }

        component.setEnabled(value != null);
    }

    private void removeAllActionListeners() {
        for (ActionListener al : component.getActionListeners()) {
            component.removeActionListener(al);
        }
    }

    @Override
    public ActionListener getValueDefault(Field field) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireChangeListener(this);
            }
        };
    }

    @Override
    public void clear() {
    }
}
