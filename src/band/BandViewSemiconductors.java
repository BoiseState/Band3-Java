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

import java.awt.Color;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
//import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.*;

/**
 *
 * @author mbaker
 */
public class BandViewSemiconductors extends javax.swing.JDialog {

   protected ObservableList<Material> listSemiconductor = ObservableCollections.observableList(BandApp.getApplication().getListSemiconductor());
   public static final String PROP_LISTSEMICONDUCTOR = "listSemiconductor";

   public List<Material> getSemiconductors() {
      return listSemiconductor;
   }

//   @Action
   public void closeSemiconductorsBox() {
      dispose();
   }

   public void showSemiconductor() {
      BandSemiconductor semiconductorBox = null;
      JFrame mainFrame = BandApp.getApplication();
      semiconductorBox = new BandSemiconductor(mainFrame,true);
      semiconductorBox.setLocationRelativeTo(semiconductorBox);
   }


    /** Creates new form BandViewDielectrics */
    public BandViewSemiconductors(java.awt.Frame parent, boolean modal) {
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

        initComponents();
    }

   

//     @Action
    public void newRecord() {
       BandSemiconductor semiconductorBox = null;
       Semiconductor newSemiconductor = new Semiconductor();
       newSemiconductor.fillColor = Color.BLACK;

       JFrame mainFrame = BandApp.getApplication();
       semiconductorBox = new BandSemiconductor(mainFrame,true);
       semiconductorBox.setLocationRelativeTo(this);
       semiconductorBox.setCurrentRecord(newSemiconductor);
       semiconductorBox.setVisible(true);

       if (semiconductorBox.isConfirmed()) {
          listSemiconductor.add(newSemiconductor);
       }
       
       Collections.sort(listSemiconductor);
    }

//    @Action
    public void editRecord() {
       int selectedRow = jTableSemiconductors.getSelectedRow();

       BandSemiconductor semiconductorBox = null;

       JFrame mainFrame = BandApp.getApplication();
       semiconductorBox = new BandSemiconductor(mainFrame,true);
       semiconductorBox.setLocationRelativeTo(this);
       Semiconductor tempSemiconductor = (Semiconductor)listSemiconductor.get(selectedRow);
       semiconductorBox.setCurrentRecord((Semiconductor)listSemiconductor.get(selectedRow));
       semiconductorBox.setVisible(true);

       if (!semiconductorBox.isConfirmed()) {
          listSemiconductor.remove(selectedRow);
          listSemiconductor.add(selectedRow, tempSemiconductor);
       }
       
       Collections.sort(listSemiconductor);
    }

//    @Action
    public void deleteRecord() {
        int n = JOptionPane.showConfirmDialog(null, "Delete the records permanently?", "Warning",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
        if (n == JOptionPane.YES_OPTION) {
            int[] selected = jTableSemiconductors.getSelectedRows();
            List<Semiconductor> toRemove = new ArrayList<Semiconductor>(selected.length);
            for (int idx = 0; idx < selected.length; idx++) {
                Semiconductor g = (Semiconductor)listSemiconductor.get(jTableSemiconductors.convertRowIndexToModel(selected[idx]));
                toRemove.add(g);
            }
            listSemiconductor.removeAll(toRemove);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   // Generated using JFormDesigner Educational license - Madi Thompson (madithompson)
   private void initComponents() {
       ResourceBundle bundle = ResourceBundle.getBundle("band.resources.BandViewSemiconductors");
       jButtonAdd = new JButton();
       jButtonEdit = new JButton();
       jButtonDelete = new JButton();
       jButtonClose = new JButton();
       jScrollPane1 = new JScrollPane();
       jTableSemiconductors = new JTable();

       //======== this ========
       setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
       setTitle(bundle.getString("BandViewSemiconductors.title"));
       setName("BandViewSemiconductors");
       var contentPane = getContentPane();

       //---- jButtonAdd ----
       jButtonAdd.setText(bundle.getString("jButtonAdd.text"));
       jButtonAdd.setName("jButtonAdd");

       //---- jButtonEdit ----
       jButtonEdit.setText(bundle.getString("jButtonEdit.text"));
       jButtonEdit.setName("jButtonEdit");

       //---- jButtonDelete ----
       jButtonDelete.setText(bundle.getString("jButtonDelete.text"));
       jButtonDelete.setName("jButtonDelete");

       //---- jButtonClose ----
       jButtonClose.setText(bundle.getString("jButtonClose.text"));
       jButtonClose.setName("jButtonClose");

       //======== jScrollPane1 ========
       {
	   jScrollPane1.setName("jScrollPane1");

	   //---- jTableSemiconductors ----
	   jTableSemiconductors.setName("jTableSemiconductors");
	   jTableSemiconductors.addMouseListener(new MouseAdapter() {
	       @Override
	       public void mouseClicked(MouseEvent e) {
		   jTableSemiconductorsMouseClicked(e);
	       }
	   });
	   jScrollPane1.setViewportView(jTableSemiconductors);
       }

       GroupLayout contentPaneLayout = new GroupLayout(contentPane);
       contentPane.setLayout(contentPaneLayout);
       contentPaneLayout.setHorizontalGroup(
	   contentPaneLayout.createParallelGroup()
	       .addGroup(contentPaneLayout.createSequentialGroup()
		   .addContainerGap()
		   .addGroup(contentPaneLayout.createParallelGroup()
		       .addComponent(jButtonAdd)
		       .addComponent(jButtonEdit)
		       .addComponent(jButtonDelete)
		       .addComponent(jButtonClose))
		   .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		   .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
		   .addContainerGap())
       );
       contentPaneLayout.setVerticalGroup(
	   contentPaneLayout.createParallelGroup()
	       .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
		   .addContainerGap()
		   .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
		       .addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
		       .addGroup(contentPaneLayout.createSequentialGroup()
			   .addComponent(jButtonAdd)
			   .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			   .addComponent(jButtonEdit)
			   .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			   .addComponent(jButtonDelete)
			   .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
			   .addComponent(jButtonClose)))
		   .addContainerGap())
       );
       pack();
       setLocationRelativeTo(getOwner());

       //---- bindings ----
       bindingGroup = new BindingGroup();
       {
	   var binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
	       this, (ELProperty) ELProperty.create("${semiconductors}"), jTableSemiconductors);
	   binding.addColumnBinding(ELProperty.create("${name}"))
	       .setColumnName("Name")
	       .setColumnClass(String.class)
	       .setEditable(false);
	   binding.addColumnBinding(ELProperty.create("${bandGap}"))
	       .setColumnName("Band Gap")
	       .setColumnClass(Double.class)
	       .setEditable(false);
	   binding.addColumnBinding(ELProperty.create("${electronAffinity}"))
	       .setColumnName("Electron Affinity")
	       .setColumnClass(Double.class)
	       .setEditable(false);
	   binding.addColumnBinding(ELProperty.create("${dielectricConstant}"))
	       .setColumnName("Dielectric Constant")
	       .setColumnClass(Double.class)
	       .setEditable(false);
	   binding.addColumnBinding(ELProperty.create("${notes}"))
	       .setColumnName("Notes")
	       .setColumnClass(String.class)
	       .setEditable(false);
	   bindingGroup.addBinding(binding);
	   binding.bind();
       }
       bindingGroup.bind();
   }// </editor-fold>//GEN-END:initComponents

    private void jTableSemiconductorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSemiconductorsMouseClicked
       if (evt.getClickCount() == 2) {
          editRecord();
       }
    }//GEN-LAST:event_jTableSemiconductorsMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BandViewSemiconductors dialog = new BandViewSemiconductors(new javax.swing.JFrame(), true);
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
   // Generated using JFormDesigner Educational license - Madi Thompson (madithompson)
   private JButton jButtonAdd;
   private JButton jButtonEdit;
   private JButton jButtonDelete;
   private JButton jButtonClose;
   private JScrollPane jScrollPane1;
   private JTable jTableSemiconductors;
   private BindingGroup bindingGroup;
   // End of variables declaration//GEN-END:variables
}
