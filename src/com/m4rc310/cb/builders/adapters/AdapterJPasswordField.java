/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.EnumComponentType;
import java.lang.reflect.Field;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Marcelo
 */
public class AdapterJPasswordField extends AbstractComponetAdapter<JPasswordField, char[]> {

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.PASSOWORD);
    }

    @Override
    public void buildComponent(Field field, Object container) {
        component = gui.getJPasswodField(ac.text());

        component.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                change();
            }

            private void change() {
                fireChangeListener(component.getPassword());
            }
        });

        if (ac.echoChar() != '-') {
            if (ac.echoChar() == '0') {
                component.setEchoChar((char) 0);
            } else {
                component.setEchoChar((char) ac.echoChar());
            }
        }
    }

    @Override
    public void setValue(char[] value) {
        component.setText(new String(value));
    }

    @Override
    public char[] getValueDefault(Field field) {
        return "".toCharArray();
    }

    @Override
    public void clear() {
        component.setText("");
    }

}
