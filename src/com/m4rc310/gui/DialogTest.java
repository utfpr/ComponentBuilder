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
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marcelo
 */
@Adialog(ref = "test", title = "TESTE", debug = true)
public class DialogTest extends DialogObject {
    
//    @Acomponent(type = EnumComponentType.IMAGE, layout = "w 130, h 130, wrap", showBorder = true)
    private String foto = "http://1.bp.blogspot.com/-7C82RxxkeQs/UT6nM8-YTuI/AAAAAAAAjvU/gJpW9sGGGV4/s1600/gostosas-do-facebook-017.jpg";

    @Amethod(methodOnChangeValue = "mudouNome")
    @Acomponent(type = EnumComponentType.TEXT_FIELD, layout = "w 200", label = "nome")
    private String nome = "Marcelo";
//    @Acomponent(type = EnumComponentType.TEXT_FIELD, typeValue = EnumTypeOfValue.BIG_DECIMAL, layout = "w 200", label = "valor")
    private BigDecimal valor;
//    @Acomponent(type = EnumComponentType.PASSOWORD, layout = "w 120")
    private char[] senha;
    
//    @Amethod(methodOnDismemberObject = "desmembrar", 
//            methodOnReturnSelectedValue = "setSexo",
//            tableCollumnNames = "text.sexo",tableCollumnWith = {20})
//    @Acomponent(type = EnumComponentType.TABLE)
    private List<String> sexo;
    
//    @Acomponent(type = EnumComponentType.CHECK_BOX, text = "text.bloqueado")
    private boolean bloqueado;
    
    @Amethod(methodOnActionListener = "verificarNome")
    @Acomponent(type = EnumComponentType.BUTTON, text = "OK", layout = "wrap")
    private ActionListener actionListenerOK;

    @Acomponent(type = EnumComponentType.PANEL, layout = "split,span")
    private final Panel panel;

    public DialogTest() {
        this.panel = new Panel();
        sexo = new ArrayList<>();
        sexo.add("M");
        sexo.add("F");
    }

    private String desmembrar(String sexo){
        return sexo + " - ";
    }
    
    private void setSexo(String sexo){
    }
    
    
    @Override
    public String getString(String text, Object... args) {
        return B.getString(text, args);
    }

    private void verificarNome() {
        panel.setSobrenome(nome);
        nome = "+++++";
        
        
        nome = senha + "";
        
        update(this);
        update(panel);
    }

    @Override
    protected void init(Dialog dialog) {
    }

    private class PanelNome {

        @Acomponent(type = EnumComponentType.TEXT_FIELD, label = "nome", text = "Nome", layout = "w 200")
        private String nome;
        @Acomponent(type = EnumComponentType.TEXT_FIELD, layout = "w 200")
        private String sobrenome;
        @Acomponent(type = EnumComponentType.PANEL, showBorder = true)
        private Panel pane;

        public PanelNome() {
            pane = new Panel();
        }
    }

    private class Panel {

        @Amethod(methodOnChangeValue = "mudouNome")
        @Acomponent(type = EnumComponentType.TEXT_FIELD, layout = "w 200", label = "nome")
        private String nome = "Marcelo";

        @Acomponent(type = EnumComponentType.TEXT_FIELD, layout = "w 200", label = "sobrenome")
        private String sobrenome = "Lopes da Silva";

        @Acomponent(type = EnumComponentType.BUTTON, text = "OK")
        private ActionListener actionListenerOK;

        public Panel() {
        }

        public void setSobrenome(String sobrenome) {
            this.sobrenome = sobrenome;
        }
        private void mudouNome(String nome) {
            this.sobrenome = nome;
            update(this);
        }

    }

}
