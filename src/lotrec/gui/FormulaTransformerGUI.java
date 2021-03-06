/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FormulaTransformerGUI.java
 *
 * Created on 22 avr. 2009, 14:10:34
 */
package lotrec.gui;

import gi.transformers.Connector;
import gi.transformers.FormulaTransformer;
import gi.transformers.LoTRECToTWB;
import gi.transformers.PriorityInfixToPrefix;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import lotrec.dataStructure.Logic;

/**
 *
 * @author said
 */
public class FormulaTransformerGUI extends javax.swing.JPanel {

    private PriorityInfixToPrefix twbToLotrec;
    private FormulaTransformer lotrecToTWB;

    /** Creates new form FormulaTransformerGUI */
    public FormulaTransformerGUI() {
        initComponents();
    }

    public void init(Logic logic) {
        ArrayList<Connector> connList = new ArrayList<Connector>();
        for (lotrec.dataStructure.expression.Connector conn : logic.getConnectors()) {
            connList.add(new Connector(conn.getName(), conn.getArity(), conn.getOutString(), conn.getPriority()));
        }
//        connList.add(new Connector("not", 1, "~_", 3));
//        connList.add(new Connector("nec", 1, "box_", 3));
//        connList.add(new Connector("pos", 1, "dia_", 3));
//        connList.add(new Connector("and", 2, "_&_", 2));
//        connList.add(new Connector("or", 2, "_v_", 2));
//        connList.add(new Connector("imp", 2, "_->_", 1));
//        connList.add(new Connector("equiv", 2, "_<->_", 0));
        twbToLotrec = new PriorityInfixToPrefix(connList);
        lotrecToTWB = new LoTRECToTWB(connList);
        try {
            twbToLotrec.compile();
            lotrecToTWB.compile();
        } catch (Exception ex) {
            System.out.println("Error while transforming from infix to prefix:\n " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Grammar compilation error:", JOptionPane.ERROR_MESSAGE);
        }
        Object[][] data = new Object[connList.size()][4];
        for (Connector c : connList) {
            data[connList.indexOf(c)][0] = c.getName();
            data[connList.indexOf(c)][1] = c.getArity();
            data[connList.indexOf(c)][2] = c.getOutput();
            data[connList.indexOf(c)][3] = c.getPriority();
        }
        tblConnectors.setModel(new javax.swing.table.DefaultTableModel(
                data,
                new String[]{
                    "Prefix name", "Arity", "Infix form", "Priority"
                }));
        tblConnectors.setEnabled(false);
    }

    public String toPrefix(String formulaInfixCode) {
        String formulaCode = null;
        try {
            formulaCode = twbToLotrec.transform(formulaInfixCode);
        } catch (Exception ex) {
            System.out.println("Error: while transforming from infix to prefix:\n " + ex.getMessage());
        }
        return formulaCode;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblConnectors = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        infixFormula = new javax.swing.JTextArea();
        toPrefix = new javax.swing.JButton();
        toInfix = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        prefixFormula = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        tblConnectors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Prefix name", "Arity", "Infix form", "Priority"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblConnectors);

        jLabel1.setText("Infix formula:");

        infixFormula.setColumns(20);
        infixFormula.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        infixFormula.setRows(5);
        jScrollPane1.setViewportView(infixFormula);

        toPrefix.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gi/transformers/images/arrow-down.PNG"))); // NOI18N
        toPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toPrefixActionPerformed(evt);
            }
        });

        toInfix.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gi/transformers/images/arrow-up.PNG"))); // NOI18N
        toInfix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toInfixActionPerformed(evt);
            }
        });

        jLabel2.setText("Prefix formula:");

        prefixFormula.setColumns(20);
        prefixFormula.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        prefixFormula.setRows(5);
        jScrollPane2.setViewportView(prefixFormula);

        jLabel3.setText("Connectors:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(104, 104, 104)
                                        .addComponent(toPrefix)
                                        .addGap(18, 18, 18)
                                        .addComponent(toInfix))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)))
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(toPrefix)
                            .addComponent(toInfix))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 444, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void toPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toPrefixActionPerformed
        try {
            prefixFormula.setText(twbToLotrec.transform(infixFormula.getText()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Interpreter error:", JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_toPrefixActionPerformed

    private void toInfixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toInfixActionPerformed
        try {
            infixFormula.setText(lotrecToTWB.transform(prefixFormula.getText()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Interpreter error:", JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_toInfixActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea infixFormula;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea prefixFormula;
    private javax.swing.JTable tblConnectors;
    private javax.swing.JButton toInfix;
    private javax.swing.JButton toPrefix;
    // End of variables declaration//GEN-END:variables
}
