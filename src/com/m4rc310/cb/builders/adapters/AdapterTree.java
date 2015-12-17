/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.m4rc310.cb.builders.adapters;

import com.m4rc310.cb.annotations.Acomponent;
import com.m4rc310.cb.annotations.EnumComponentType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 *
 * @author Marcelo
 */
public class AdapterTree extends AbstractComponetAdapter<JScrollPane, List> {

    private JTree jtree;
    private TreeModel model;
    private DefaultMutableTreeNode root;

    public AdapterTree() {

    }

    @Override
    public boolean isComponentFor(Acomponent ac) {
        return ac.type().equals(EnumComponentType.JTREE);
    }

    @Override
    public void buildComponent(Field field, Object target) {

        try {
            Object object = field.get(target);
            component = gui.getJScrollPane();

            root = new DefaultMutableTreeNode("root");
            jtree = new JTree(root);
            
            jtree.setRootVisible(ac.visibleTreeNode());
            
            component.setViewportView(jtree);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clear() {
        component.removeAll();
    }

    @Override
    public void setValue(List value) {
        root.removeAllChildren();
        processHierarchy(value);
    }

    @Override
    public void update(Field field, List value) {
        root.removeAllChildren();
        processHierarchy(value);
    }

    @Override
    public List getValueDefault(Field field) {
        return new ArrayList();
    }

    private DefaultMutableTreeNode processHierarchy(List list) {
//        DefaultMutableTreeNode node = new DefaultMutableTreeNode(list.get(0));
        DefaultMutableTreeNode child;
        for (Object nodeSpecifier : list) {
            if (nodeSpecifier instanceof List) {
                child = processHierarchy((List) nodeSpecifier);
            } else {
                child = new DefaultMutableTreeNode(nodeSpecifier);
                root.add(child);
            }
        }

        for (Object nodeSpecifier : list) {
        }
        return root;
    }

}
