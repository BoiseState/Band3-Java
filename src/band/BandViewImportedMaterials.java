/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BandCompose.java
 *
 * Created on Jan 20, 2010, 7:59:27 PM
 */

package band;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
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
public class BandViewImportedMaterials extends javax.swing.JDialog {
   protected ObservableList<Material> listSemiconductor;
   public static final String PROP_LISTSEMICONDUCTOR = "listSemiconductor";

   public List<Material> getSemiconductors() {
      return listSemiconductor;
   }

   protected ObservableList<Material> listMetal;
   public static final String PROP_LISTMETAL = "listMetal";

   public List<Material> getMetals() {
      return listMetal;
   }

   protected ObservableList<Material> listDielectric;
   public static final String PROP_LISTDIELECTRIC = "listDielectric";

   public List<Material> getDielectrics() {
      return listDielectric;
   }

    /** Creates new form BandCompose */
    public BandViewImportedMaterials(java.awt.Frame parent, boolean modal,
            List<Material> importedMaterials) {
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
        listDielectric      = ObservableCollections.observableList(
                extractMaterialList(importedMaterials, Dielectric.class));
        Collections.sort(listDielectric);
        listMetal           = ObservableCollections.observableList(
                extractMaterialList(importedMaterials, Metal.class));
        Collections.sort(listMetal);
        listSemiconductor   = ObservableCollections.observableList(
                extractMaterialList(importedMaterials, Semiconductor.class));
        Collections.sort(listSemiconductor);
       
        initComponents();
        getRootPane().setDefaultButton(jButtonCancel);
    }
    
    private List<Material> extractMaterialList(List<Material> listMaterials, Class<?> type) {
        List<Material> retList = new LinkedList<Material>();
        for(Material m : listMaterials) {
           if(m.getClass() == type) {
               retList.add(m);
           }
        }
        
        return retList;
    }

//    @Action
    public void close() {
       dispose();
    }
    
//    @Action
    public void selectAll() {
        jTableMetals.selectAll();
        jTableDielectrics.selectAll();
        jTableSemiconductors.selectAll();
    }
    
//    @Action
    public void selectNone() {
        jTableMetals.clearSelection();
        jTableDielectrics.clearSelection();
        jTableSemiconductors.clearSelection();
    }
    
//    @Action
    public void doImport() {
        // Figure out which rows got selected
        int[] selectedMetals = jTableMetals.getSelectedRows();
        int[] selectedDielectrics = jTableDielectrics.getSelectedRows();
        int[] selectedSemiconductors = jTableSemiconductors.getSelectedRows();
        
        // Add the corresponding material into our library
        for(int i=0; i<selectedMetals.length; i++) {
            BandApp.getApplication()
                    .getListMetal().add(listMetal.get(i));
        }
        for(int i=0; i<selectedDielectrics.length; i++) {
            BandApp.getApplication()
                    .getListDielectric().add(listDielectric.get(i));
        }
        for(int i=0; i<selectedSemiconductors.length; i++) {
            BandApp.getApplication()
                    .getListSemiconductor().add(listSemiconductor.get(i));
        }
        
        // Sort the libraries
        Collections.sort(BandApp.getApplication().getListMetal());
        Collections.sort(BandApp.getApplication().getListDielectric());
        Collections.sort(BandApp.getApplication().getListSemiconductor());
        
        close();
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
	ResourceBundle bundle = ResourceBundle.getBundle("band.resources.BandViewImportedMaterials");
	jButtonSelectAll = new JButton();
	jButtonSelectNone = new JButton();
	jLabelStructure = new JLabel();
	jLabelMetals = new JLabel();
	jScrollPaneMetals = new JScrollPane();
	jTableMetals = new JTable();
	jLabelDielectrics = new JLabel();
	jScrollPaneDielectrics = new JScrollPane();
	jTableDielectrics = new JTable();
	jLabelSemiconductors = new JLabel();
	jScrollPaneSemiconductors = new JScrollPane();
	jTableSemiconductors = new JTable();
	jButtonCancel = new JButton();
	jButtonImport = new JButton();

	//======== this ========
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	setName("BandCompose");
	var contentPane = getContentPane();

	//---- jButtonSelectAll ----
	jButtonSelectAll.setText(bundle.getString("jButtonSelectAll.text"));
	jButtonSelectAll.setName("jButtonSelectAll");

	//---- jButtonSelectNone ----
	jButtonSelectNone.setText(bundle.getString("jButtonSelectNone.text"));
	jButtonSelectNone.setName("jButtonSelectNone");

	//---- jLabelStructure ----
	jLabelStructure.setName("jLabelStructure");

	//---- jLabelMetals ----
	jLabelMetals.setName("jLabelMetals");

	//======== jScrollPaneMetals ========
	{
	    jScrollPaneMetals.setName("jScrollPaneMetals");

	    //---- jTableMetals ----
	    jTableMetals.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    jTableMetals.setName("jTableMetals");
	    jTableMetals.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    jTableMetals.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
		    jTableMetalsMouseClicked(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		    jTableMetalsMouseReleased(e);
		}
	    });
	    jTableMetals.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
		    jTableMetalsKeyPressed(e);
		}
	    });
	    jScrollPaneMetals.setViewportView(jTableMetals);
	}

	//---- jLabelDielectrics ----
	jLabelDielectrics.setName("jLabelDielectrics");

	//======== jScrollPaneDielectrics ========
	{
	    jScrollPaneDielectrics.setName("jScrollPaneDielectrics");

	    //---- jTableDielectrics ----
	    jTableDielectrics.setName("jTableDielectrics");
	    jTableDielectrics.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    jTableDielectrics.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
		    jTableDielectricsMouseClicked(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		    jTableDielectricsMouseReleased(e);
		}
	    });
	    jTableDielectrics.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
		    jTableDielectricsKeyPressed(e);
		}
	    });
	    jScrollPaneDielectrics.setViewportView(jTableDielectrics);
	}

	//---- jLabelSemiconductors ----
	jLabelSemiconductors.setName("jLabelSemiconductors");

	//======== jScrollPaneSemiconductors ========
	{
	    jScrollPaneSemiconductors.setName("jScrollPaneSemiconductors");

	    //---- jTableSemiconductors ----
	    jTableSemiconductors.setName("jTableSemiconductors");
	    jTableSemiconductors.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    jTableSemiconductors.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
		    jTableSemiconductorsMouseClicked(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		    jTableSemiconductorsMouseReleased(e);
		}
	    });
	    jTableSemiconductors.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
		    jTableSemiconductorsKeyPressed(e);
		}
	    });
	    jScrollPaneSemiconductors.setViewportView(jTableSemiconductors);
	}

	//---- jButtonCancel ----
	jButtonCancel.setText(bundle.getString("jButtonCancel.text"));
	jButtonCancel.setName("jButtonCancel");

	//---- jButtonImport ----
	jButtonImport.setText(bundle.getString("jButtonImport.text"));
	jButtonImport.setName("jButtonImport");

	GroupLayout contentPaneLayout = new GroupLayout(contentPane);
	contentPane.setLayout(contentPaneLayout);
	contentPaneLayout.setHorizontalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addGroup(contentPaneLayout.createParallelGroup()
				.addComponent(jButtonSelectAll, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
				.addComponent(jButtonSelectNone, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
				    .addComponent(jLabelStructure)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 624, Short.MAX_VALUE)
				    .addGroup(contentPaneLayout.createParallelGroup()
					.addComponent(jLabelMetals)
					.addComponent(jLabelDielectrics)
					.addComponent(jLabelSemiconductors)))
				.addComponent(jScrollPaneSemiconductors, GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
				.addComponent(jScrollPaneMetals, GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
				.addComponent(jScrollPaneDielectrics, GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)))
			.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
			    .addComponent(jButtonImport)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jButtonCancel)))
		    .addContainerGap())
	);
	contentPaneLayout.setVerticalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(jLabelStructure)
			.addComponent(jLabelMetals))
		    .addGap(5, 5, 5)
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addComponent(jScrollPaneMetals, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			    .addComponent(jLabelDielectrics)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jScrollPaneDielectrics, GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			    .addComponent(jLabelSemiconductors)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jScrollPaneSemiconductors, GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addComponent(jButtonSelectAll)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jButtonSelectNone)))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(jButtonCancel)
			.addComponent(jButtonImport))
		    .addContainerGap())
	);
	pack();
	setLocationRelativeTo(getOwner());

	//---- bindings ----
	bindingGroup = new BindingGroup();
	{
	    var binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
		this, (ELProperty) ELProperty.create("${metals}"), jTableMetals);
	    binding.addColumnBinding(ELProperty.create("${name}"))
		.setColumnName("Name")
		.setColumnClass(String.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${workFunction}"))
		.setColumnName("Work Function")
		.setColumnClass(Double.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${notes}"))
		.setColumnName("Notes")
		.setColumnClass(String.class)
		.setEditable(false);
	    bindingGroup.addBinding(binding);
	    binding.bind();
	}
	{
	    var binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
		this, (ELProperty) ELProperty.create("${dielectrics}"), jTableDielectrics);
	    binding.addColumnBinding(ELProperty.create("${name}"))
		.setColumnName("Name")
		.setColumnClass(String.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${dielectricConstant}"))
		.setColumnName("Dielectric Constant")
		.setColumnClass(Double.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${bandGap}"))
		.setColumnName("Band Gap")
		.setColumnClass(Double.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${electronAffinity}"))
		.setColumnName("Electron Affinity")
		.setColumnClass(Double.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${notes}"))
		.setColumnName("Notes")
		.setColumnClass(String.class)
		.setEditable(false);
	    bindingGroup.addBinding(binding);
	    binding.bind();
	}
	{
	    var binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
		this, (ELProperty) ELProperty.create("${semiconductors}"), jTableSemiconductors);
	    binding.addColumnBinding(ELProperty.create("${name}"))
		.setColumnName("Name")
		.setColumnClass(String.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${dielectricConstant}"))
		.setColumnName("Dielectric Constant")
		.setColumnClass(Double.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${bandGap}"))
		.setColumnName("Band Gap")
		.setColumnClass(Double.class)
		.setEditable(false);
	    binding.addColumnBinding(ELProperty.create("${electronAffinity}"))
		.setColumnName("Electron Affinity")
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

    private void jTableMetalsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMetalsMouseReleased

    }//GEN-LAST:event_jTableMetalsMouseReleased

    private void jTableDielectricsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDielectricsMouseReleased

    }//GEN-LAST:event_jTableDielectricsMouseReleased

    private void jTableSemiconductorsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSemiconductorsMouseReleased

    }//GEN-LAST:event_jTableSemiconductorsMouseReleased

    private void jTableMetalsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMetalsMouseClicked

    }//GEN-LAST:event_jTableMetalsMouseClicked

    private void jTableDielectricsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDielectricsMouseClicked

    }//GEN-LAST:event_jTableDielectricsMouseClicked

    private void jTableSemiconductorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSemiconductorsMouseClicked

    }//GEN-LAST:event_jTableSemiconductorsMouseClicked


    private void jTableMetalsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableMetalsKeyPressed

    }//GEN-LAST:event_jTableMetalsKeyPressed

    private void jTableDielectricsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableDielectricsKeyPressed
       
    }//GEN-LAST:event_jTableDielectricsKeyPressed

    private void jTableSemiconductorsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableSemiconductorsKeyPressed

    }//GEN-LAST:event_jTableSemiconductorsKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Generated using JFormDesigner Educational license - Madi Thompson (madithompson)
    private JButton jButtonSelectAll;
    private JButton jButtonSelectNone;
    private JLabel jLabelStructure;
    private JLabel jLabelMetals;
    private JScrollPane jScrollPaneMetals;
    private JTable jTableMetals;
    private JLabel jLabelDielectrics;
    private JScrollPane jScrollPaneDielectrics;
    private JTable jTableDielectrics;
    private JLabel jLabelSemiconductors;
    private JScrollPane jScrollPaneSemiconductors;
    private JTable jTableSemiconductors;
    private JButton jButtonCancel;
    private JButton jButtonImport;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
