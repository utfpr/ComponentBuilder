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
import java.awt.event.ContainerAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Marcelo
 */
public class AdapterJTextComponent extends AbstractComponetAdapter<JTextComponent, Object> {

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.TEXT_FIELD);
    }

    @Override
    public void buildComponent(Field field, Object container) {
        component = gui.getJTextField();

//        component.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                
//                if("editable".equals(evt.getPropertyName())){
//                    JTextComponent comp = (JTextComponent) evt.getSource();
//                    
////                    comp.setFocusable(false);
//                }
//            }
//        });

        

//        component.getParent().addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                System.out.println(evt.getPropertyName());
//            }
//        });

        component.addFocusListener(new FocusAdapter() {
                
            @Override
            public void focusGained(FocusEvent e) {
                JTextComponent jtc = (JTextComponent) e.getComponent();
                if (!jtc.isEditable()) {
                    
                }
            }

        });

        CaseFilter caseFilter = new CaseFilter();
        ((AbstractDocument) component.getDocument()).setDocumentFilter(caseFilter);
//        ((AbstractDocument) component.getDocument()).addDocumentListener(null);
        if (!ac.text().isEmpty()) {
            component = gui.getJTextField("", ac.text());
        }

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
                try {
                    change_(component.getText());
                    final String ftext = component.getText();
                    Amethod am = field.getDeclaredAnnotation(Amethod.class);
                    if (!am.methodOnChangeValueObject().isEmpty()) {
                        componentsBuilder.getTargetsForMethodName(am.methodOnChangeValueObject()).stream().forEach((tar) -> {
                            MethodUtils.declaredMethod(tar, am.methodOnChangeValueObject(), Object.class).invoke(ftext);
                        });
                    }
                } catch (Exception e) {
//                    LogServer.getInstance().error(e);
                }

            }
        });
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
//            fireError(e);
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
            component.setText(String.valueOf(value));
//            format();
        } catch (Exception e) {
            component.setText("");
        }
    }

    @Override
    public void clear() {
        try {
            if (!component.isFocusOwner()) {
                component.setText("");
            }

        } catch (Exception e) {
        }
    }

}
