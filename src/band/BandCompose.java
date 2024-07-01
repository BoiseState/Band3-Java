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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.jdesktop.application.Action;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;

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
          metalBox.setAlwaysOnTop(true);

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
             dielectricBox.setAlwaysOnTop(true);

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
                semiconductorBox.setAlwaysOnTop(true);

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
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jButtonDone = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonMoveUp = new javax.swing.JButton();
        jButtonMoveDown = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jLabelStructure = new javax.swing.JLabel();
        jScrollPaneStructure = new javax.swing.JScrollPane();
        jTableStructure = new javax.swing.JTable();
        jLabelMetals = new javax.swing.JLabel();
        jScrollPaneMetals = new javax.swing.JScrollPane();
        jTableMetals = new javax.swing.JTable();
        jLabelDielectrics = new javax.swing.JLabel();
        jScrollPaneDielectrics = new javax.swing.JScrollPane();
        jTableDielectrics = new javax.swing.JTable();
        jLabelSemiconductors = new javax.swing.JLabel();
        jScrollPaneSemiconductors = new javax.swing.JScrollPane();
        jTableSemiconductors = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandCompose.class);
        setTitle(resourceMap.getString("BandCompose.title")); // NOI18N
        setName("BandCompose"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandCompose.class, this);
        jButtonDone.setAction(actionMap.get("closeComposeBox")); // NOI18N
        jButtonDone.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonDone.setText(resourceMap.getString("jButtonDone.text")); // NOI18N
        jButtonDone.setName("jButtonDone"); // NOI18N

        jButtonAdd.setAction(actionMap.get("jButtonAdd_Click")); // NOI18N
        jButtonAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonAdd.setText(resourceMap.getString("jButtonAdd.text")); // NOI18N
        jButtonAdd.setName("jButtonAdd"); // NOI18N

        jButtonMoveUp.setAction(actionMap.get("jButtonMoveUp_Click")); // NOI18N
        jButtonMoveUp.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonMoveUp.setText(resourceMap.getString("jButtonMoveUp.text")); // NOI18N
        jButtonMoveUp.setName("jButtonMoveUp"); // NOI18N

        jButtonMoveDown.setAction(actionMap.get("jButtonMoveDown_Click")); // NOI18N
        jButtonMoveDown.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonMoveDown.setText(resourceMap.getString("jButtonMoveDown.text")); // NOI18N
        jButtonMoveDown.setName("jButtonMoveDown"); // NOI18N

        jButtonEdit.setAction(actionMap.get("jButtonEdit_Click")); // NOI18N
        jButtonEdit.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonEdit.setText(resourceMap.getString("jButtonEdit.text")); // NOI18N
        jButtonEdit.setName("jButtonEdit"); // NOI18N

        jButtonDelete.setAction(actionMap.get("jButtonDelete_Click")); // NOI18N
        jButtonDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonDelete.setText(resourceMap.getString("jButtonDelete.text")); // NOI18N
        jButtonDelete.setName("jButtonDelete"); // NOI18N

        jLabelStructure.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelStructure.setText(resourceMap.getString("jLabelStructure.text")); // NOI18N
        jLabelStructure.setName("jLabelStructure"); // NOI18N

        jScrollPaneStructure.setBackground(resourceMap.getColor("jScrollPaneStructure.background")); // NOI18N
        jScrollPaneStructure.setBorder(null);
        jScrollPaneStructure.setName("jScrollPaneStructure"); // NOI18N

        jTableStructure.setBackground(resourceMap.getColor("jTableStructure.background")); // NOI18N
        jTableStructure.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTableStructure.setFillsViewportHeight(true);
        jTableStructure.setName("jTableStructure"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${structure}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableStructure);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${thicknessNm}"));
        columnBinding.setColumnName("Thickness Nm");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notes}"));
        columnBinding.setColumnName("Notes");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jTableStructure.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableStructureMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableStructureMouseReleased(evt);
            }
        });
        jScrollPaneStructure.setViewportView(jTableStructure);

        jLabelMetals.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelMetals.setText(resourceMap.getString("jLabelMetals.text")); // NOI18N
        jLabelMetals.setName("jLabelMetals"); // NOI18N

        jScrollPaneMetals.setName("jScrollPaneMetals"); // NOI18N

        jTableMetals.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTableMetals.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTableMetals.setName("jTableMetals"); // NOI18N
        jTableMetals.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metals}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableMetals);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
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
        jTableMetals.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMetalsMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableMetalsMouseReleased(evt);
            }
        });
        jTableMetals.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableMetalsKeyPressed(evt);
            }
        });
        jScrollPaneMetals.setViewportView(jTableMetals);
        jTableMetals.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableMetals.columnModel.title0")); // NOI18N
        jTableMetals.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableMetals.columnModel.title1")); // NOI18N
        jTableMetals.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableMetals.columnModel.title2")); // NOI18N

        jLabelDielectrics.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelDielectrics.setText(resourceMap.getString("jLabelDielectrics.text")); // NOI18N
        jLabelDielectrics.setName("jLabelDielectrics"); // NOI18N

        jScrollPaneDielectrics.setName("jScrollPaneDielectrics"); // NOI18N

        jTableDielectrics.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTableDielectrics.setName("jTableDielectrics"); // NOI18N
        jTableDielectrics.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${dielectrics}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableDielectrics);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
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
        jTableDielectrics.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableDielectricsMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableDielectricsMouseReleased(evt);
            }
        });
        jTableDielectrics.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableDielectricsKeyPressed(evt);
            }
        });
        jScrollPaneDielectrics.setViewportView(jTableDielectrics);
        jTableDielectrics.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title0")); // NOI18N
        jTableDielectrics.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title1")); // NOI18N
        jTableDielectrics.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title2")); // NOI18N
        jTableDielectrics.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title3")); // NOI18N
        jTableDielectrics.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTableDielectrics.columnModel.title4")); // NOI18N

        jLabelSemiconductors.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelSemiconductors.setText(resourceMap.getString("jLabelSemiconductors.text")); // NOI18N
        jLabelSemiconductors.setName("jLabelSemiconductors"); // NOI18N

        jScrollPaneSemiconductors.setName("jScrollPaneSemiconductors"); // NOI18N

        jTableSemiconductors.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTableSemiconductors.setName("jTableSemiconductors"); // NOI18N
        jTableSemiconductors.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${semiconductors}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableSemiconductors);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
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
        jTableSemiconductors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableSemiconductorsMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableSemiconductorsMouseReleased(evt);
            }
        });
        jTableSemiconductors.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableSemiconductorsKeyPressed(evt);
            }
        });
        jScrollPaneSemiconductors.setViewportView(jTableSemiconductors);
        jTableSemiconductors.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableSemiconductors.columnModel.title0")); // NOI18N
        jTableSemiconductors.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableSemiconductors.columnModel.title1")); // NOI18N
        jTableSemiconductors.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableSemiconductors.columnModel.title2")); // NOI18N
        jTableSemiconductors.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTableSemiconductors.columnModel.title3")); // NOI18N
        jTableSemiconductors.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTableSemiconductors.columnModel.title4")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonDone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonMoveUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonMoveDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelStructure)
                    .addComponent(jScrollPaneStructure, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneDielectrics, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                    .addComponent(jLabelMetals)
                    .addComponent(jLabelDielectrics)
                    .addComponent(jScrollPaneMetals, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                    .addComponent(jLabelSemiconductors)
                    .addComponent(jScrollPaneSemiconductors, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelStructure)
                            .addComponent(jLabelMetals))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPaneMetals, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelDielectrics)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPaneDielectrics, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelSemiconductors)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPaneSemiconductors, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
                            .addComponent(jScrollPaneStructure, 0, 0, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonDone)
                        .addGap(58, 58, 58)
                        .addComponent(jButtonAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonMoveUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonMoveDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDelete)))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
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
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDone;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonMoveDown;
    private javax.swing.JButton jButtonMoveUp;
    private javax.swing.JLabel jLabelDielectrics;
    private javax.swing.JLabel jLabelMetals;
    private javax.swing.JLabel jLabelSemiconductors;
    private javax.swing.JLabel jLabelStructure;
    private javax.swing.JScrollPane jScrollPaneDielectrics;
    private javax.swing.JScrollPane jScrollPaneMetals;
    private javax.swing.JScrollPane jScrollPaneSemiconductors;
    private javax.swing.JScrollPane jScrollPaneStructure;
    private javax.swing.JTable jTableDielectrics;
    private javax.swing.JTable jTableMetals;
    private javax.swing.JTable jTableSemiconductors;
    private javax.swing.JTable jTableStructure;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

   BandMaterialMetal metalBox;
   BandMaterialDielectric dielectricBox;
   BandMaterialSemiconductor semiconductorBox;
}
