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
import com.m4rc310.cb.annotations.EnumComponentType;
import com.m4rc310.cb.annotations.EnumTypeOfValue;
import com.m4rc310.cb.utils.DialogObject;
import com.m4rc310.utils.DateUtils;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 *
 * @author Marcelo
 */
@Adialog(ref = "test2", title = "TESTE", debug = true)
public class DialogTest2 extends DialogObject{
    
    @Amethod(methodOnChangeValue = "processNascimento")
    @Acomponent(type = EnumComponentType.TEXT_FIELD, layout = "w 200", label = "label.ola", layoutLabel = "split, span")
    private String nome = "Marcelo";
    
    @Amethod(methodOnChangeValue = "processaDataNascimento")
    @Acomponent(type = EnumComponentType.DATE, layout = "w 200, wrap", label = "label.data.aniversario", dateFormat = "dd/MM/yyyy", 
            horizontalAlignment = Acomponent.CENTER)
    private Date dataAniversario;
    
    
    
    @Acomponent(type = EnumComponentType.PANEL, layoutContainer = "insets 4, gap 0", showBorder = true)
    private  Panel panel;

    public DialogTest2() {
        this.panel = new Panel();
    }
    
    
    @Override
    protected void init(Dialog dialog) {
        
    }
    
    private void processNascimento(String value){
        panel.setNome(value);
        update(panel);
    }
    
    private void processaDataNascimento(Date data){
        try {
            Long idade = DateUtils.getIdade(data);
            panel.setIdade(idade);
            update(panel);
        } catch (Exception e) {
        }
    }
    
    private class Panel{
        @Acomponent(type = EnumComponentType.TEXT_FIELD, layout = "w 180")
        private String nome = "Marcelo";
        
        @Acomponent(type = EnumComponentType.TEXT_FIELD, typeValue = EnumTypeOfValue.LONG,  layout = "w 80")
        private Long idade;
        
        
        @Acomponent(type = EnumComponentType.CHECK_BOX, text = "text.bloqueado")
        private boolean bloqueado;
        
        @Acomponent(type = EnumComponentType.BUTTON, text = "text.verificar")
        private final ActionListener verificar;

        public Panel() {
            verificar = (ActionEvent e) -> {
            }; 
        }
        
        

        public void setNome(String nome) {
            this.nome = nome;
//            update(this);
        }

        public void setIdade(Long idade) {
            this.idade = idade;
        }

    }

    
}
