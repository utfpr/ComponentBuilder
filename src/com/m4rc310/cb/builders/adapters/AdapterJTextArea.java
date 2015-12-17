/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.Amethod;
import com.m4rc310.cb.annotations.EnumComponentType;
import com.m4rc310.cb.utils.MethodUtils;
import java.awt.KeyboardFocusManager;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author Marcelo
 */
public class AdapterJTextArea extends AbstractComponetAdapter<JScrollPane, Object> {

    private JTextArea textArea;

    @Override
    public void buildComponent(Field field, Object container) {
        component = gui.getJScrollPane();

        textArea = gui.getJTextArea();
        if (!ac.text().isEmpty()) {
            textArea = gui.getJTextArea(ac.text());
        }

        CaseFilter caseFilter = new CaseFilter();
        ((AbstractDocument) textArea.getDocument()).setDocumentFilter(caseFilter);
        
        
        
        textArea.getDocument().addDocumentListener(new DocumentListener() {

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

                try {
                    final String ftext = textArea.getText();
                    Amethod am = field.getDeclaredAnnotation(Amethod.class);
                    if (!am.methodOnChangeValueObject().isEmpty()) {
                        componentsBuilder.getTargetsForMethodName(am.methodOnChangeValueObject()).stream().forEach((tar) -> {
                            MethodUtils.declaredMethod(tar, am.methodOnChangeValueObject(), Object.class).invoke(ftext);
                        });
                    }
                } catch (Exception e) {
                }

                change_(textArea.getText());

            }
        });

        textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERS‌​AL_KEYS, null);
        textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERS‌​AL_KEYS, null);

        component.setViewportView(textArea);
    }

    private void change_(String text) {
        Object value;
        try {

            switch (ac.typeValue()) {
                case BIG_DECIMAL:
                    text = text.replace(".", "");
                    text = text.replace(",", ".");
                    value = new BigDecimal(text);
                    break;
                case LONG:
                    value = Long.parseLong(text);
                    break;
                case INT:
                    value = Integer.parseInt(text);
                    break;
                case STRING:
                default:
                    value = text;
            }

            fireChangeListener(value);

        } catch (Exception e) {
//            log(Level.SEVERE, "Não foi possivel converter o valor [{0}] para <{1}> Erro: {2}", text, ac.typeValue(), e.getMessage());
            fireError(e);
        }
    }

    @Override
    public Object getValueDefault(Field field) {
        try {
            Object tar = componentsBuilder.getTargetForField(field);
            Object ret = field.get(tar);

            return ret == null ? "" : ret;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return "";
        }

    }

    @Override
    public void setValue(Object value) {
        try {
            textArea.setText(String.valueOf(value));
        } catch (Exception e) {
            textArea.setText("");
        }
    }

    @Override
    public void clear() {
        if (!textArea.isFocusOwner()) {
            textArea.setText("");
        }
    }

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.TEXT_AREA);
    }
}
