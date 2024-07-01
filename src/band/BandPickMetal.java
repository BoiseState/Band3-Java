/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BandViewDielectrics.java
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
public class BandPickMetal extends MaterialPick {

   protected ObservableList<Material> listMetal = ObservableCollections.observableList(BandApp.getApplication().getListMetal());
   public static final String PROP_LISTMETAL = "listMetal";
   private Metal newMetal;
   private boolean confirmed;
   private double thickness = 0;

    /** Creates new form BandViewDielectrics */
    public BandPickMetal(java.awt.Frame parent, boolean modal) {
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

    public List<Material> getMetals() {
       return listMetal;
    }

    @Action
    public void closeMetalBox() {
       dispose();
    }

    @Override
    public Material getNewMaterial() {
       return newMetal;
    }
    
    @Override
    public boolean isConfirmed() {
       return confirmed;
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

    @Action
    public void addRecord() {
       int selectedRow = jTableMetals.getSelectedRow();
       newMetal = (Metal)listMetal.get(selectedRow).clone();

       if (thickness > 0) {
           newMetal.setThicknessNm(thickness);
           thickness = 0;
           confirmed = true;
       }
       else {
           JFrame mainFrame = BandApp.getApplication().getMainFrame();
           BandMaterialMetal metalBox = new BandMaterialMetal(mainFrame,true);
           metalBox.setCurrentRecord(newMetal);
           metalBox.setAutoRequestFocus(true);
//           metalBox.setAlwaysOnTop(true);
           metalBox.setVisible(true);

           if (metalBox.isConfirmed()) {
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
        jScrollPaneMetals = new javax.swing.JScrollPane();
        jTableMetals = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandPickMetal.class);
        setTitle(resourceMap.getString("BandPickMetal.title")); // NOI18N
        setName("BandPickMetal"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandPickMetal.class, this);
        jButtonAdd.setAction(actionMap.get("addRecord")); // NOI18N
        jButtonAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonAdd.setText(resourceMap.getString("jButtonAdd.text")); // NOI18N
        jButtonAdd.setName("jButtonAdd"); // NOI18N

        jButtonClose.setAction(actionMap.get("closeMetalBox")); // NOI18N
        jButtonClose.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonClose.setText(resourceMap.getString("jButtonClose.text")); // NOI18N
        jButtonClose.setName("jButtonClose"); // NOI18N

        jScrollPaneMetals.setName("jScrollPaneMetals"); // NOI18N

        jTableMetals.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTableMetals.setName("jTableMetals"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metals}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableMetals);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${workFunction}"));
        columnBinding.setColumnName("Work Function");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notes}"));
        columnBinding.setColumnName("Notes");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPaneMetals.setViewportView(jTableMetals);
        jTableMetals.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableMetals.columnModel.title0")); // NOI18N
        jTableMetals.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableMetals.columnModel.title1")); // NOI18N
        jTableMetals.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableMetals.columnModel.title2")); // NOI18N

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
                .addComponent(jScrollPaneMetals, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneMetals, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 181, Short.MAX_VALUE)
                        .addComponent(jButtonClose)))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BandPickMetal dialog = new BandPickMetal(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPaneMetals;
    private javax.swing.JTable jTableMetals;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
