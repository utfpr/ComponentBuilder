/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import br.edu.utfpr.cm.tsi.utils.LogServer;
import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.Amethod;
import com.m4rc310.cb.annotations.EnumComponentType;
import com.m4rc310.cb.utils.MethodUtils;
import com.m4rc310.cb.utils.MyArrayList;
import com.m4rc310.cb.utils.TableControl;
import com.m4rc310.ui.gui.utils.MTableModel3;
import com.m4rc310.ui.gui.utils.TableFormatterAdapter;
import static com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Marcelo
 */
public class AdapterJTable extends AbstractComponetAdapter<JScrollPane, List> {

    private JTable jTable;
    private MTableModel3 model;
    private TableFormatterAdapter format;
    private TableControl tableControl;

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.TABLE);
    }

    @Override
    public void buildComponent(Field field, Object target) {
        component = gui.getJScrollPane();
        component.setFocusable(false);
        jTable = gui.getJTable();

        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTable.setColumnSelectionAllowed(false);
        jTable.setRowSelectionAllowed(true);
        
        jTable.setUpdateSelectionOnSort(true);
                    

        model = new MTableModel3();

        DefaultTableCellRenderer defaltRenderer = new DefaultTableCellRenderer();
        defaltRenderer.setHorizontalAlignment(JLabel.LEFT);
        jTable.setDefaultRenderer(String.class, defaltRenderer);

        DefaultTableCellRenderer integerRenderer = new DefaultTableCellRenderer();
        integerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable.setDefaultRenderer(Integer.class, integerRenderer);
        jTable.setDefaultRenderer(Long.class, integerRenderer);

        jTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        jTable.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = jTable.getSelectedRow();
                if (selectedRow > -1) {
                    try {
                        Amethod am = field.getDeclaredAnnotation(Amethod.class);
                        String methodOnChangeValue = am.methodOnChangeValue();
                        Object value = model.getValue(selectedRow);
                        assertTrue(am.methodOnReturnSelectedValue().isEmpty(), "Metodo <methodOnReturnSelectedValue> não foi referenciado!");
                        List listTargets = componentsBuilder.getTargetsForMethodName(am.methodOnReturnSelectedValue());
                        listTargets.stream().forEach((tar) -> {
                            MethodUtils.declaredMethod(tar, am.methodOnReturnSelectedValue(), value.getClass()).invoke(value);
                        });
                    } catch (Exception ex) {
                        LogServer.getInstance().error(ex);
                    }
                }
            }
        });

        jTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {

            Amethod am = field.getDeclaredAnnotation(Amethod.class);
            String methodOnChangeValue = am.methodOnChangeValue();

            int selectedRow = jTable.getSelectedRow();
            if (selectedRow != -1) {
                Object value = model.getValue(selectedRow);

                if (tableControl != null) {
                    tableControl.getTableControlListener().valueSelected(value);
                }

                if (!methodOnChangeValue.isEmpty()) {
                    List listTargets = componentsBuilder.getTargetsForMethodName(methodOnChangeValue);
                    listTargets.stream().forEach((tar) -> {
                        try {
                            MethodUtils.declaredMethod(tar, methodOnChangeValue, value.getClass()).invoke(value);
                        } catch (Exception e) {
                            LogServer.getInstance().error(e);
                        }
                    });
                }
            }
        });

        component.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        component.getHorizontalScrollBar().setPreferredSize(new Dimension(10, 0));

        try {
            if (field.isAnnotationPresent(Amethod.class)) {
                Amethod am = field.getDeclaredAnnotation(Amethod.class);

                assertTrue(am.tableCollumnNames().length == 0, "informe valor para <tableCollumnNames>");
                assertTrue(am.tableCollumnWith().length == 0, "informe valor para <tableCollumnWith>");

                format = new TableFormatterAdapter() {
                    @Override
                    public Object[] desmemberObject(Object o) {
                        List targets = componentsBuilder.getTargetsForMethodName(am.methodOnDismemberObject());

                        Object[] desmember;
                        for (Object tar : targets) {
                            desmember = (Object[]) MethodUtils.declaredMethod(tar, am.methodOnDismemberObject(), o.getClass()).invoke(o);
                            return desmember;
                        }
                        return new Object[]{o};
                    }
                };

                int[] tableCollumnWith = am.tableCollumnWith();
                String[] tableCollumnNames = am.tableCollumnNames();

                for (int i = 0; i < tableCollumnNames.length; i++) {
                    format.addCollumn(getString(tableCollumnNames[i]), tableCollumnWith[i]);
                }

                model.setFormatter(format);
                jTable.setGridColor(new Color(0xDBDBDB));

                jTable.setModel(model);
                component.setViewportView(jTable);

                if (ac.w() > 20) {
                    jTable.setPreferredSize(new Dimension(ac.w() - 15, ac.h() - 17));
                    component.setPreferredSize(new Dimension(ac.w(), ac.h()));
                }

                model.resizeTable(jTable);
            }
        } catch (Exception e) {
            LogServer.getInstance().error(e);
        }
    }

    @Override
    public void update(Field field, List value) {
        Amethod am = field.getDeclaredAnnotation(Amethod.class);
        if (!am.methodReturnTableControl().isEmpty()) {
            String mrtc = am.methodReturnTableControl();
            List targets = componentsBuilder.getTargetsForMethodName(mrtc);
            tableControl = new TableControl() {
                @Override
                public void setObjectItem(Object item) {
                    int i = value.indexOf(item);
                    value.set(i, item);
                    update(field, value);
                    jTable.setRowSelectionInterval(i, i);
                }
            };

            targets.stream().forEach((tar) -> {
                try {
                    MethodUtils.declaredMethod(tar, mrtc, TableControl.class).invoke(tableControl);
                    LogServer.getInstance().info(tar, "retornando tableControl");
                } catch (Exception e) {
                    LogServer.getInstance().error(e);
                }
            });

        }

        super.update(field, value);
        model.setValues(value);
    }

    @Override
    public void setValue(List value) {
        jTable.removeAll();
        model.setValues(value);
    }

    @Override
    public List getValueDefault(Field field) {
        return new ArrayList() {
        };
    }

    @Override
    public void clear() {
        model.clear();
        jTable.removeAll();
    }

//    @Override
//    public void setValue(MyArrayList value) {
//        jTable.removeAll();
//        model.setValues(value);
//    }
//
//    @Override
//    public void update(Field field, MyArrayList value) {
//        super.update(field, value); 
//        model.setValues(value);
//    }
//    
//    
//
//    @Override
//    public MyArrayList getValueDefault(Field field) {
//        return new MyArrayList();
//    }
}
