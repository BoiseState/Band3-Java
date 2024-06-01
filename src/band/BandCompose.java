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
import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.*;

/**
 *
 * @author mbaker
 */
public class BandCompose extends javax.swing.JDialog {
   protected ObservableList<Material> listSemiconductor = ObservableCollections.observableList(BandApp.getApplication().getListSemiconductor());
   public static final String PROP_LISTSEMICONDUCTOR = "listSemiconductor";

   public List<Material> getSemiconductors() {
      return listSemiconductor;
   }

   protected ObservableList<Material> listMetal = ObservableCollections.observableList(BandApp.getApplication().getListMetal());
   public static final String PROP_LISTMETAL = "listMetal";

   public List<Material> getMetals() {
      return listMetal;
   }

   protected ObservableList<Material> listDielectric = ObservableCollections.observableList(BandApp.getApplication().getListDielectric());
   public static final String PROP_LISTDIELECTRIC = "listDielectric";

   public List<Material> getDielectrics() {
      return listDielectric;
   }

   protected ObservableList<Material> structure = ObservableCollections.observableList(BandApp.getApplication().getStructure().clone().getMaterialList());
   public static final String PROP_STRUCTURE = "structure";

   public List<Material> getStructure() {
      return structure;
   }
   
   private Structure theRealThing = BandApp.getApplication().getStructure();

    /** Creates new form BandCompose */
    public BandCompose(java.awt.Frame parent, boolean modal) {
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
        getRootPane().setDefaultButton(jButtonDone);
    }

    @Action
    public void closeComposeBox() {
       for (int i = 0; i < structure.size(); i++) {
          if (i == 0) {
             if (structure.get(i) instanceof Dielectric) {
                JOptionPane.showMessageDialog(this, "An Oxide cannot be a starting material!");
                return;
             }
             if (structure.get(i) instanceof Semiconductor) {
                JOptionPane.showMessageDialog(this, "A semiconductor cannot be a starting material!");
                return;
             }
          }
          else {
             if (structure.get(i) instanceof Semiconductor &&
                     structure.size() - 1 > i) {
                JOptionPane.showMessageDialog(this, "A semiconductor can only be an end material!");
                return;
             }
             if (structure.get(i) instanceof Metal &&
                     structure.get(i-1) instanceof Metal) {
                JOptionPane.showMessageDialog(this, "Sorry, this program does not support two or more metals next to each other!");
                return;
             }
             if (structure.size() - 1 == i && structure.get(i) instanceof Dielectric) {
                JOptionPane.showMessageDialog(this, "An oxide cannot be an end material!");
                return;
             }
          }
       }
       dispose();
    }

    @Action
    public void jButtonEdit_Click() throws Exception {
       JFrame mainFrame = BandApp.getApplication().getMainFrame();
       int selectedRow = jTableStructure.getSelectedRow();
       if (selectedRow >= 0) {
          Material tempMaterial = structure.get(selectedRow);
          if (tempMaterial instanceof Metal) {
             metalBox = new BandMaterialMetal(mainFrame, true);
             metalBox.setLocationRelativeTo(mainFrame);

             Metal tempMetal = (Metal)tempMaterial.clone();

             metalBox.setCurrentRecord(tempMetal);

             BandApp.getApplication().show(metalBox);

             if (metalBox.isConfirmed()) {
                getStructure().remove(selectedRow);
                theRealThing.removeLayer(selectedRow);
                getStructure().add(selectedRow, tempMetal);
                theRealThing.addLayer(selectedRow,tempMetal);
                jTableStructure.addRowSelectionInterval(selectedRow, selectedRow);
                BandApp.getApplication().setChanged(true);
             }
          }
          else {
             if (tempMaterial instanceof Dielectric) {
                Dielectric tempDielectric = (Dielectric)tempMaterial.clone();

                dielectricBox = new BandMaterialDielectric(mainFrame, true, tempDielectric);
                dielectricBox.setLocationRelativeTo(mainFrame);

                dielectricBox.setCurrentRecord(tempDielectric);

                BandApp.getApplication().show(dielectricBox);

                if (dielectricBox.isConfirmed()) {
                   getStructure().remove(selectedRow);
                   theRealThing.removeLayer(selectedRow);
                   getStructure().add(selectedRow, tempDielectric);
                   theRealThing.addLayer(selectedRow,tempDielectric);
                   jTableStructure.addRowSelectionInterval(selectedRow, selectedRow);
                   BandApp.getApplication().setChanged(true);
                }
             }
             else {
                if (tempMaterial instanceof Semiconductor) {
                   semiconductorBox = new BandMaterialSemiconductor(mainFrame, true);
                   semiconductorBox.setLocationRelativeTo(mainFrame);

                   Semiconductor tempSemiconductor = (Semiconductor)tempMaterial.clone();

                   semiconductorBox.setCurrentRecord(tempSemiconductor);

                   BandApp.getApplication().show(semiconductorBox);

                   if (semiconductorBox.isConfirmed()) {
                      getStructure().remove(selectedRow);
                      theRealThing.removeLayer(selectedRow);
                      getStructure().add(selectedRow, tempSemiconductor);
                      theRealThing.addLayer(selectedRow,tempSemiconductor);
                      jTableStructure.addRowSelectionInterval(selectedRow, selectedRow);
                      BandApp.getApplication().setChanged(true);
                   }
                }
             }
          }
       }
    }

    @Action
    public void jButtonAdd_Click() {
       JFrame mainFrame = BandApp.getApplication().getMainFrame();
       if (jTableMetals.getSelectedRow() >= 0) {
          metalBox = new BandMaterialMetal(mainFrame, true);
          metalBox.setLocationRelativeTo(mainFrame);

          Metal tempMetal = (Metal)listMetal.get(jTableMetals.getSelectedRow()).clone();

          metalBox.setCurrentRecord(tempMetal);

          BandApp.getApplication().show(metalBox);

          if (metalBox.isConfirmed()) {
             getStructure().add(tempMetal);
             theRealThing.addLayer(tempMetal);
             jTableMetals.clearSelection();
             jTableStructure.addRowSelectionInterval(jTableStructure.getRowCount()-1, jTableStructure.getRowCount()-1);
             setButtonsForAdd(false);
             BandApp.getApplication().setChanged(true);
          }
       }
       else {
          if (jTableDielectrics.getSelectedRow() >= 0) {
             Dielectric tempDielectric = (Dielectric)listDielectric.get(jTableDielectrics.getSelectedRow()).clone();
             dielectricBox = new BandMaterialDielectric(mainFrame, true, tempDielectric);
             dielectricBox.setLocationRelativeTo(mainFrame);                          

             BandApp.getApplication().show(dielectricBox);

             if (dielectricBox.isConfirmed()) {
                getStructure().add(tempDielectric);
                theRealThing.addLayer(tempDielectric);
                jTableDielectrics.clearSelection();
                jTableStructure.addRowSelectionInterval(jTableStructure.getRowCount()-1, jTableStructure.getRowCount()-1);
                setButtonsForAdd(false);
                BandApp.getApplication().setChanged(true);
             }
          }
          else {
             if (jTableSemiconductors.getSelectedRow() >= 0) {
                semiconductorBox = new BandMaterialSemiconductor(mainFrame, true);
                semiconductorBox.setLocationRelativeTo(mainFrame);

                Semiconductor tempSemiconductor = (Semiconductor)listSemiconductor.get(jTableSemiconductors.getSelectedRow()).clone();

                semiconductorBox.setCurrentRecord(tempSemiconductor);

                BandApp.getApplication().show(semiconductorBox);

                if (semiconductorBox.isConfirmed()) {
                   getStructure().add(tempSemiconductor);
                   theRealThing.addLayer(tempSemiconductor);
                   jTableSemiconductors.clearSelection();
                   jTableStructure.addRowSelectionInterval(jTableStructure.getRowCount()-1, jTableStructure.getRowCount()-1);
                   setButtonsForAdd(false);
                   BandApp.getApplication().setChanged(true);
                }
             }
          }
       }       
    }

    private void setButtonsForAdd(boolean setAdd) {
       jButtonAdd.setEnabled(setAdd);
       jButtonMoveUp.setEnabled(!setAdd);
       jButtonMoveDown.setEnabled(!setAdd);
       jButtonEdit.setEnabled(!setAdd);
       jButtonDelete.setEnabled(!setAdd);
    }

    @Action
    public void jButtonMoveUp_Click() {
       Integer selRow = jTableStructure.getSelectedRow();

       if (selRow > 0) {
          Material tempMaterial = structure.get(selRow);
          structure.remove(tempMaterial);
          structure.add(selRow-1,tempMaterial);
          theRealThing.moveLayerUp(selRow);
          jTableStructure.addRowSelectionInterval(selRow-1, selRow-1);
          BandApp.getApplication().setChanged(true);
       }
    }

    @Action
    public void jButtonMoveDown_Click() {
       Integer selRow = jTableStructure.getSelectedRow();

       if (selRow >= 0 && selRow+1 < jTableStructure.getRowCount()) {
          Material tempMaterial = structure.get(selRow);
          structure.remove(tempMaterial);
          structure.add(selRow+1,tempMaterial);
          theRealThing.moveLayerDown(selRow);
          jTableStructure.addRowSelectionInterval(selRow+1, selRow+1);
          BandApp.getApplication().setChanged(true);
       }
    }

    @Action
    public void jButtonDelete_Click() {
       if (jTableStructure.getRowCount() > 0) {
          int n = JOptionPane.showConfirmDialog(null, "Delete the record permanently?", "Warning",
                  JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
          if (n == JOptionPane.YES_OPTION) {
              int selRow = jTableStructure.getSelectedRow();
             structure.remove(selRow);
             theRealThing.removeLayer(selRow);
             BandApp.getApplication().setChanged(true);
             if (jTableStructure.getRowCount() > 0) {
                jTableStructure.addRowSelectionInterval(jTableStructure.getRowCount()-1, jTableStructure.getRowCount()-1);
             }
          }
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
	ResourceBundle bundle = ResourceBundle.getBundle("band.resources.BandCompose");
	jButtonDone = new JButton();
	jButtonAdd = new JButton();
	jButtonMoveUp = new JButton();
	jButtonMoveDown = new JButton();
	jButtonEdit = new JButton();
	jButtonDelete = new JButton();
	jLabelStructure = new JLabel();
	jScrollPaneStructure = new JScrollPane();
	jTableStructure = new JTable();
	jLabelMetals = new JLabel();
	jScrollPaneMetals = new JScrollPane();
	jTableMetals = new JTable();
	jLabelDielectrics = new JLabel();
	jScrollPaneDielectrics = new JScrollPane();
	jTableDielectrics = new JTable();
	jLabelSemiconductors = new JLabel();
	jScrollPaneSemiconductors = new JScrollPane();
	jTableSemiconductors = new JTable();

	//======== this ========
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	setTitle(bundle.getString("BandCompose.title"));
	setName("BandCompose");
	var contentPane = getContentPane();

	//---- jButtonDone ----
	jButtonDone.setText(bundle.getString("jButtonDone.text"));
	jButtonDone.setName("jButtonDone");

	//---- jButtonAdd ----
	jButtonAdd.setText(bundle.getString("jButtonAdd.text"));
	jButtonAdd.setName("jButtonAdd");

	//---- jButtonMoveUp ----
	jButtonMoveUp.setText(bundle.getString("jButtonMoveUp.text"));
	jButtonMoveUp.setName("jButtonMoveUp");

	//---- jButtonMoveDown ----
	jButtonMoveDown.setText(bundle.getString("jButtonMoveDown.text"));
	jButtonMoveDown.setName("jButtonMoveDown");

	//---- jButtonEdit ----
	jButtonEdit.setText(bundle.getString("jButtonEdit.text"));
	jButtonEdit.setName("jButtonEdit");

	//---- jButtonDelete ----
	jButtonDelete.setText(bundle.getString("jButtonDelete.text"));
	jButtonDelete.setName("jButtonDelete");

	//---- jLabelStructure ----
	jLabelStructure.setText(bundle.getString("jLabelStructure.text"));
	jLabelStructure.setName("jLabelStructure");

	//======== jScrollPaneStructure ========
	{
	    jScrollPaneStructure.setBackground(Color.white);
	    jScrollPaneStructure.setBorder(null);
	    jScrollPaneStructure.setName("jScrollPaneStructure");

	    //---- jTableStructure ----
	    jTableStructure.setBackground(Color.white);
	    jTableStructure.setFillsViewportHeight(true);
	    jTableStructure.setName("jTableStructure");
	    jTableStructure.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
		    jTableStructureMouseClicked(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		    jTableStructureMouseReleased(e);
		}
	    });
	    jScrollPaneStructure.setViewportView(jTableStructure);
	}

	//---- jLabelMetals ----
	jLabelMetals.setText(bundle.getString("jLabelMetals.text"));
	jLabelMetals.setName("jLabelMetals");

	//======== jScrollPaneMetals ========
	{
	    jScrollPaneMetals.setName("jScrollPaneMetals");

	    //---- jTableMetals ----
	    jTableMetals.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    jTableMetals.setName("jTableMetals");
	    jTableMetals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
	jLabelDielectrics.setText(bundle.getString("jLabelDielectrics.text"));
	jLabelDielectrics.setName("jLabelDielectrics");

	//======== jScrollPaneDielectrics ========
	{
	    jScrollPaneDielectrics.setName("jScrollPaneDielectrics");

	    //---- jTableDielectrics ----
	    jTableDielectrics.setName("jTableDielectrics");
	    jTableDielectrics.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
	jLabelSemiconductors.setText(bundle.getString("jLabelSemiconductors.text"));
	jLabelSemiconductors.setName("jLabelSemiconductors");

	//======== jScrollPaneSemiconductors ========
	{
	    jScrollPaneSemiconductors.setName("jScrollPaneSemiconductors");

	    //---- jTableSemiconductors ----
	    jTableSemiconductors.setName("jTableSemiconductors");
	    jTableSemiconductors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

	GroupLayout contentPaneLayout = new GroupLayout(contentPane);
	contentPane.setLayout(contentPaneLayout);
	contentPaneLayout.setHorizontalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
			.addComponent(jButtonDone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(jButtonAdd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(jButtonMoveUp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(jButtonMoveDown, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(jButtonEdit, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(jButtonDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addComponent(jLabelStructure)
			.addComponent(jScrollPaneStructure, GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addComponent(jScrollPaneDielectrics, GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
			.addComponent(jLabelMetals)
			.addComponent(jLabelDielectrics)
			.addComponent(jScrollPaneMetals, GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
			.addComponent(jLabelSemiconductors)
			.addComponent(jScrollPaneSemiconductors, GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
		    .addContainerGap())
	);
	contentPaneLayout.setVerticalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelStructure)
				.addComponent(jLabelMetals))
			    .addGap(5, 5, 5)
			    .addGroup(contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addComponent(jScrollPaneMetals, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addComponent(jLabelDielectrics)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(jScrollPaneDielectrics, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addComponent(jLabelSemiconductors)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(jScrollPaneSemiconductors, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
				.addComponent(jScrollPaneStructure, 0, 0, Short.MAX_VALUE)))
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addComponent(jButtonDone)
			    .addGap(58, 58, 58)
			    .addComponent(jButtonAdd)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jButtonMoveUp)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jButtonMoveDown)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jButtonEdit)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jButtonDelete)))
		    .addContainerGap())
	);
	pack();
	setLocationRelativeTo(getOwner());

	//---- bindings ----
	bindingGroup = new BindingGroup();
	{
	    var binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
		this, (ELProperty) ELProperty.create("${structure}"), jTableStructure);
	    binding.addColumnBinding(ELProperty.create("${name}"))
		.setColumnName("Name")
		.setColumnClass(String.class);
	    binding.addColumnBinding(ELProperty.create("${thicknessNm}"))
		.setColumnName("Thickness Nm")
		.setColumnClass(Double.class);
	    binding.addColumnBinding(ELProperty.create("${notes}"))
		.setColumnName("Notes")
		.setColumnClass(String.class);
	    bindingGroup.addBinding(binding);
	    binding.bind();
	}
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
       jTableSemiconductors.clearSelection();
       jTableDielectrics.clearSelection();
       jTableStructure.clearSelection();
       setButtonsForAdd(true);
    }//GEN-LAST:event_jTableMetalsMouseReleased

    private void jTableDielectricsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDielectricsMouseReleased
       jTableMetals.clearSelection();
       jTableSemiconductors.clearSelection();
       jTableStructure.clearSelection();
       setButtonsForAdd(true);
    }//GEN-LAST:event_jTableDielectricsMouseReleased

    private void jTableSemiconductorsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSemiconductorsMouseReleased
       jTableMetals.clearSelection();
       jTableDielectrics.clearSelection();
       jTableStructure.clearSelection();
       setButtonsForAdd(true);
    }//GEN-LAST:event_jTableSemiconductorsMouseReleased

    private void jTableStructureMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableStructureMouseReleased
       jTableMetals.clearSelection();
       jTableDielectrics.clearSelection();
       jTableSemiconductors.clearSelection();
       setButtonsForAdd(false);
    }//GEN-LAST:event_jTableStructureMouseReleased

    private void jTableMetalsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMetalsMouseClicked
       if (evt.getClickCount() == 2) {
          jButtonAdd_Click();
       }
    }//GEN-LAST:event_jTableMetalsMouseClicked

    private void jTableDielectricsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDielectricsMouseClicked
       if (evt.getClickCount() == 2) {
          jButtonAdd_Click();
       }
    }//GEN-LAST:event_jTableDielectricsMouseClicked

    private void jTableSemiconductorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSemiconductorsMouseClicked
       if (evt.getClickCount() == 2) {
          jButtonAdd_Click();
       }
    }//GEN-LAST:event_jTableSemiconductorsMouseClicked

    private void jTableStructureMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableStructureMouseClicked
       if (evt.getClickCount() == 2) {
          try {
             jButtonEdit_Click();
          }
          catch (Exception e) {}
       }
    }//GEN-LAST:event_jTableStructureMouseClicked

    String searchField;
    long saved_ms=0;
    final long TIMETOWAIT = 1000;
    private void tableKeyPressed(java.awt.event.KeyEvent evt,ObservableList list,javax.swing.JTable tbl){
        
    if (!Character.isLetter(evt.getKeyChar()))
        return;
    
        //concat the search string until it is re-set by the timer
    if (System.currentTimeMillis() - saved_ms < TIMETOWAIT) {
        searchField = searchField + evt.getKeyChar();
    }
    else {
    searchField = "" + evt.getKeyChar();
    }
    //do search;
    
    for (int i = 0; i < list.size(); i++){
        Class type = list.get(i).getClass();
        String tmp;
        if (type == Metal.class)
         tmp =  ((Metal)list.get(i)).getName();
        else if (type == Semiconductor.class)
            tmp =  ((Semiconductor)list.get(i)).getName();
        else
            tmp = tmp =  ((Dielectric)list.get(i)).getName();
        
        if (tmp.toLowerCase().startsWith(searchField.toLowerCase())){
            tbl.setRowSelectionInterval(i, i);
            tbl.scrollRectToVisible(tbl.getCellRect(i, 0, rootPaneCheckingEnabled));
            break;
        }
    }
    saved_ms = System.currentTimeMillis();

    }
    private void jTableMetalsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableMetalsKeyPressed
        tableKeyPressed(evt,listMetal,jTableMetals);
    }//GEN-LAST:event_jTableMetalsKeyPressed

    private void jTableDielectricsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableDielectricsKeyPressed
        tableKeyPressed(evt,listDielectric,jTableDielectrics);
    }//GEN-LAST:event_jTableDielectricsKeyPressed

    private void jTableSemiconductorsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableSemiconductorsKeyPressed
        tableKeyPressed(evt,listSemiconductor,jTableSemiconductors);
    }//GEN-LAST:event_jTableSemiconductorsKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BandCompose dialog = new BandCompose(new javax.swing.JFrame(), true);
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
    private JButton jButtonDone;
    private JButton jButtonAdd;
    private JButton jButtonMoveUp;
    private JButton jButtonMoveDown;
    private JButton jButtonEdit;
    private JButton jButtonDelete;
    private JLabel jLabelStructure;
    private JScrollPane jScrollPaneStructure;
    private JTable jTableStructure;
    private JLabel jLabelMetals;
    private JScrollPane jScrollPaneMetals;
    private JTable jTableMetals;
    private JLabel jLabelDielectrics;
    private JScrollPane jScrollPaneDielectrics;
    private JTable jTableDielectrics;
    private JLabel jLabelSemiconductors;
    private JScrollPane jScrollPaneSemiconductors;
    private JTable jTableSemiconductors;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

   BandMaterialMetal metalBox;
   BandMaterialDielectric dielectricBox;
   BandMaterialSemiconductor semiconductorBox;
}
