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
import com.m4rc310.utils.DateUtils;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Marcelo
 */
public class AdapterJTextDate extends AbstractComponetAdapter<JTextComponent, Date> {

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.DATE);
    }

    @Override
    public void buildComponent(Field field, Object container) {
        component = gui.getJTextField();
        if (!ac.text().isEmpty()) {
            component = gui.getJTextField("", ac.text());
        }

        component.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                change_(((JTextComponent) input).getText());
                return true;
            }
        });

    }

    private void change_(String text) {
        Date value;
        try {
            value = DateUtils.getDate(text);
            fireChangeListener(value);

            update(field, value);
            setValue(value);
            fireChangeListener(value);

        } catch (ParseException | RuntimeException e) {
            log(Level.SEVERE, "Não foi possivel converter o valor [{0}] para <{1}> Erro: {2}", text, ac.typeValue(), e.getMessage());
            fireError(e);
            fireChangeListener(null);
//            fireChangeTextListener(null);
        }
    }

    @Override
    public void setValue(Date value) {
        try {
            String dateText = DateUtils.dateToString(value, ac.dateFormat());
            component.setText(dateText);
        } catch (Exception e) {
//            clear();
//            e.printStackTrace();
            fireChangeListener(null);
//            fireChangeTextListener(null);
        }

    }

    @Override
    protected void fireChangeListener(Date value) {
        super.fireChangeListener(value);

        try {
            
        Amethod am = field.getDeclaredAnnotation(Amethod.class);
        String methodOnChangeValueObject = am.methodOnChangeValueObject();

        if (!methodOnChangeValueObject.isEmpty()) {
            componentsBuilder.getTargetsForMethodName(methodOnChangeValueObject).stream().forEach((tar) -> {
                try {
                    MethodUtils.declaredMethod(tar, methodOnChangeValueObject, Object.class).invoke(value);
                } catch (Exception e) {
                }
            });
        }

        } catch (Exception e) {
        }
    }

    @Override
    public Date getValueDefault(Field field) {
        return null;
    }

    @Override
    public void clear() {
        component.setText("");
        fireChangeListener(null);
//        fireChangeTextListener(null);
    }

}
