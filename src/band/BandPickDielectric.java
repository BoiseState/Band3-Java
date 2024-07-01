/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BandViewDielectric.java
 *
 * Created on Jan 21, 2010, 9:13:51 PM
 */

package band;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.jdesktop.application.Action;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;

/**
 *
 * @author mbaker
 */
public class BandPickDielectric extends MaterialPick {

    protected ObservableList<Material> listDielectric = ObservableCollections.observableList(BandApp.getApplication().getListDielectric());
    public static final String PROP_LISTDIELECTRIC = "listDielectric";
    private Dielectric newDielectric;
    private boolean confirmed;
    private double thickness;

    /** Creates new form BandViewDielectric */
    public BandPickDielectric(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        InputMap iMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

        getRootPane().getActionMap().put("escape", new AbstractAction() {
           @Override
           public void actionPerformed(ActionEvent e) {
              dispose();
           }
        });

        getRootPane().getActionMap().put("enter", new AbstractAction() {
           @Override
           public void actionPerformed(ActionEvent e) {
              dispose();
           }
        });

        getRootPane().setDefaultButton(jButtonClose);

        confirmed = false;

        initComponents();
    }

    public void setReplace(double thickness) {
        this.thickness = thickness;
        if (thickness > 0) {
           jButtonAdd.setText("Replace");
        }
        else {
            jButtonAdd.setText("Add");
        }
    }

    public List<Material> getDielectrics() {
       return listDielectric;
    }
    
    @Action
    public void closeDielectricsBox() {
       dispose();
    }

    @Override
    public Material getNewMaterial() {
       return newDielectric;
    }

    @Override
    public boolean isConfirmed() {
       return confirmed;
    }

    @Action
    public void addRecord() {
       int selectedRow = jTableDielectrics.getSelectedRow();
       newDielectric = (Dielectric)listDielectric.get(selectedRow).clone();

       if (thickness > 0) {
           newDielectric.setThicknessNm(thickness);
           thickness = 0;
           confirmed = true;
       }
       else {
           JFrame mainFrame = BandApp.getApplication().getMainFrame();
           BandMaterialDielectric dielectricBox = new BandMaterialDielectric(mainFrame,true,newDielectric);
           dielectricBox.setAlwaysOnTop(true);
           dielectricBox.setVisible(true);

           if (dielectricBox.isConfirmed()) {
              confirmed = true;
           }
       }
       dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jButtonAdd = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jScrollPaneDielectrics = new javax.swing.JScrollPane();
        jTableDielectrics = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandPickDielectric.class);
        setTitle(resourceMap.getString("BandPickDielectric.title")); // NOI18N
        setName("BandPickDielectric"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandPickDielectric.class, this);
        jButtonAdd.setAction(actionMap.get("addRecord")); // NOI18N
        jButtonAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonAdd.setText(resourceMap.getString("jButtonAdd.text")); // NOI18N
        jButtonAdd.setName("jButtonAdd"); // NOI18N
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonClose.setAction(actionMap.get("closeDielectricsBox")); // NOI18N
        jButtonClose.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonClose.setText(resourceMap.getString("jButtonClose.text")); // NOI18N
        jButtonClose.setName("jButtonClose"); // NOI18N

        jScrollPaneDielectrics.setName("jScrollPaneDielectrics"); // NOI18N

        jTableDielectrics.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTableDielectrics.setName("jTableDielectrics"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${dielectrics}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableDielectrics);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${dielectricConstant}"));
        columnBinding.setColumnName("Dielectric Constant");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${bandGap}"));
        columnBinding.setColumnName("Band Gap");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${electronAffinity}"));
        columnBinding.setColumnName("Electron Affinity");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notes}"));
        columnBinding.setColumnName("Notes");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPaneDielectrics.setViewportView(jTableDielectrics);
        jTableDielectrics.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title0")); // NOI18N
        jTableDielectrics.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title1")); // NOI18N
        jTableDielectrics.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title2")); // NOI18N
        jTableDielectrics.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title3")); // NOI18N
        jTableDielectrics.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title4")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAdd)
                    .addComponent(jButtonClose))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneDielectrics, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneDielectrics, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 181, Short.MAX_VALUE)
                        .addComponent(jButtonClose)))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jButtonAddActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BandPickDielectric dialog = new BandPickDielectric(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JScrollPane jScrollPaneDielectrics;
    private javax.swing.JTable jTableDielectrics;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
