/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BandDielectric.java
 *
 * Created on Jan 20, 2010, 8:46:20 PM
 */
package band;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.jdesktop.application.Action;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;

/**
 *
 * @author mbaker
 */
public class BandMaterialDielectric extends MaterialSelect {

   private Dielectric currentRecord;
   private boolean confirmed;
   private EvalPoint currentPoint = new EvalPoint();
   protected ObservableList<EvalPoint> listPoints;
   public static final String PROP_LISTDIELECTRIC = "listDielectric";

   /** Not used, requires Dielectric to be passed in.
   private BandMaterialDielectric(java.awt.Frame parent, boolean modal) {}

   /** Creates new form BandDielectric */
    public BandMaterialDielectric(java.awt.Frame parent, boolean modal, Material passCurrentRecord) {
      super(parent, modal);

      InputMap iMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
      iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
    getRootPane().getActionMap().put("escape", new AbstractAction() {

         @Override
         public void actionPerformed(ActionEvent e) {
            jButtonCancel_Click();
         }
      });

      getRootPane().getActionMap().put("enter", new AbstractAction() {

         @Override
         public void actionPerformed(ActionEvent e) {
            jButtonOK_Click();
         }
      });

      for (int i = 0; i < passCurrentRecord.getPoint().size(); i++) {
         if (passCurrentRecord.getPoint().get(i).getCharge() == 0) {
            passCurrentRecord.getPoint().remove(i);
            i--;
         }
      }

      currentRecord = (Dielectric) passCurrentRecord;
      listPoints = ObservableCollections.observableList(currentRecord.getPoint());

      setConfirmed(false);
      getRootPane().setDefaultButton(jButtonOK);

      initComponents();

      addWindowListener(new WindowAdapter() {

         @Override
         public void windowOpened(WindowEvent e) {
            jTextFieldThickness.requestFocusInWindow();
            jTextFieldThickness.selectAll();
         }
      });
   }

   public List<EvalPoint> getListPoints() {
      return listPoints;
   }
   
   @Override
   public boolean isConfirmed() {
      return confirmed;
   }

   @Override
   public void setConfirmed(boolean value) {
      confirmed = value;
   }

   @Override
   public void setCurrentRecord(Material m) {
      Dielectric oldDielectric = currentRecord;
      currentRecord = (Dielectric) m;
      propertyChangeSupport.firePropertyChange("currentRecord", oldDielectric, currentRecord);
   }

   @Override
   public Dielectric getCurrentRecord() {
      return currentRecord;
   }

   public void setCurrentPoint(EvalPoint passPoint) {
      EvalPoint oldPoint = currentPoint;
      currentPoint = passPoint;
      propertyChangeSupport.firePropertyChange("currentPoint", oldPoint, currentPoint);
   }

   public EvalPoint getCurrentPoint() {
      return currentPoint;
   }

   @Action
   public void addCurrentPoint() {
      EvalPoint addPoint = new EvalPoint(currentPoint);
      if (addPoint.getLocation() >= 0 && addPoint.getLocationNm() <= currentRecord.getThicknessNm()) {
         if (!this.jTextFieldChargeC.getText().isEmpty()) {
            addPoint.setCharge(Double.valueOf(jTextFieldChargeC.getText()));
            int index = -1;
            for (int i = 0; i < listPoints.size(); i++) {
               if (listPoints.get(i).getLocation() == addPoint.getLocation()) {
                  index = i;
                  break;
               }
            }
            if (index > -1) {
               listPoints.remove(index);
               listPoints.add(index, addPoint);
            } else {
               listPoints.add(addPoint);
            }
            Collections.sort(listPoints);
         } else {
            if (!this.jTextFieldChargeE.getText().isEmpty()) {
               addPoint.setElectronCharge(Double.valueOf(jTextFieldChargeE.getText()));
               int index = -1;
               for (int i = 0; i < listPoints.size(); i++) {
                  if (listPoints.get(i).getLocation() == addPoint.getLocation()) {
                     index = i;
                     break;
                  }
               }
               if (index > -1) {
                  listPoints.remove(index);
                  listPoints.add(index, addPoint);
               } else {
                  listPoints.add(addPoint);
               }
               Collections.sort(listPoints);
            } else {
               JOptionPane.showMessageDialog(rootPane, "Could not decipher the charge! Please use only numbers");
            }
         }

      } else {
         JOptionPane.showMessageDialog(rootPane, "The location must be within the thickness of the Oxide!");
      }
   }

   @Action
   public void deleteSelectedPoint() {
      if (jTableFixedCharge.getRowCount() > 0) {
         int n = JOptionPane.showConfirmDialog(null, "Delete the record permanently?", "Warning",
                 JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
         if (n == JOptionPane.YES_OPTION) {
            listPoints.remove(jTableFixedCharge.getSelectedRow());
         }
      }
   }

   @Action
   public void jButtonOK_Click() {

      if (jTextFieldName.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Name is a required field.");
         jTextFieldName.requestFocus();
         return;
      }
      if (jTextFieldElectronAffinity.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Electron Affinity is a required field.");
         jTextFieldElectronAffinity.requestFocus();
         return;
      }
      if (jTextFieldBandGap.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Band Gap is a required field.");
         jTextFieldBandGap.requestFocus();
         return;
      }
      if (this.jTextFieldDielectricConstant.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Dielectric Constant is a required field.");
         jTextFieldDielectricConstant.requestFocus();
         return;
      }
      try {
         double numValue = Functions.evaluateExpression(jTextFieldDielectricConstant.getText(), 'F', 0);
      } catch (Exception e) {
         JOptionPane.showMessageDialog(this, "Could not decipher the dielectric constant! Please check expression, use 'F' for electric field variable (V/cm).");
         jTextFieldBandGap.requestFocus();
         return;
      }
      if (this.jTextFieldThickness.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Thickness is a required field.");
         jTextFieldThickness.requestFocus();
         return;
      }

      if (currentRecord.getThickness() > 0) {
         setConfirmed(true);
         setVisible(false);
      } else {
         JOptionPane.showMessageDialog(rootPane, "The thickness must be bigger than 0!");
      }
   }

   @Action
   public void jButtonCancel_Click() {
      setConfirmed(false);
      setVisible(false);
   }

   @Action
   public void showColorChooser() {
      Color newColor = JColorChooser.showDialog(rootPane, "Choose a color...", jPanelPlotColor.getBackground());
      if (newColor != null) {
         jPanelPlotColor.setBackground(newColor);
      }
   }
   private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

   /**
    * Add PropertyChangeListener.
    *
    * @param listener
    */
   @Override
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.addPropertyChangeListener(listener);
   }

   /**
    * Remove PropertyChangeListener.
    *
    * @param listener
    */
   @Override
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.removePropertyChangeListener(listener);
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

        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabelName = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jLabelBandGap = new javax.swing.JLabel();
        jTextFieldBandGap = new javax.swing.JTextField();
        jLabelElectronAffinity = new javax.swing.JLabel();
        jTextFieldElectronAffinity = new javax.swing.JTextField();
        jLabelDielectricConstant = new javax.swing.JLabel();
        jTextFieldDielectricConstant = new javax.swing.JTextField();
        jButtonPlotColor = new javax.swing.JButton();
        jPanelPlotColor = new javax.swing.JPanel();
        jLabelThickness = new javax.swing.JLabel();
        jTextFieldThickness = new javax.swing.JTextField();
        jPanelFixedCharge = new javax.swing.JPanel();
        jLabelLocation = new javax.swing.JLabel();
        jTextFieldLocation = new javax.swing.JTextField();
        jLabelChargeC = new javax.swing.JLabel();
        jTextFieldChargeC = new javax.swing.JTextField();
        jLabelChargeE = new javax.swing.JLabel();
        jTextFieldChargeE = new javax.swing.JTextField();
        jButtonFixedChargeAdd = new javax.swing.JButton();
        jButtonFixedChargeDelete = new javax.swing.JButton();
        jScrollPaneFixedCharge = new javax.swing.JScrollPane();
        jTableFixedCharge = new javax.swing.JTable();
        jLabelNotes = new javax.swing.JLabel();
        jScrollPaneNotes = new javax.swing.JScrollPane();
        jTextAreaNotes = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandMaterialDielectric.class);
        setTitle(resourceMap.getString("BandMaterialDielectric.title")); // NOI18N
        setName("BandMaterialDielectric"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandMaterialDielectric.class, this);
        jButtonOK.setAction(actionMap.get("jButtonOK_Click")); // NOI18N
        jButtonOK.setText(resourceMap.getString("jButtonOK.text")); // NOI18N
        jButtonOK.setName("jButtonOK"); // NOI18N

        jButtonCancel.setAction(actionMap.get("jButtonCancel_Click")); // NOI18N
        jButtonCancel.setText(resourceMap.getString("jButtonCancel.text")); // NOI18N
        jButtonCancel.setName("jButtonCancel"); // NOI18N

        jLabelName.setText(resourceMap.getString("jLabelName.text")); // NOI18N
        jLabelName.setName("jLabelName"); // NOI18N

        jTextFieldName.setName("jTextFieldName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.name}"), jTextFieldName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelBandGap.setText(resourceMap.getString("jLabelBandGap.text")); // NOI18N
        jLabelBandGap.setName("jLabelBandGap"); // NOI18N

        jTextFieldBandGap.setName("jTextFieldBandGap"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.bandGap}"), jTextFieldBandGap, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelElectronAffinity.setText(resourceMap.getString("jLabelElectronAffinity.text")); // NOI18N
        jLabelElectronAffinity.setName("jLabelElectronAffinity"); // NOI18N

        jTextFieldElectronAffinity.setName("jTextFieldElectronAffinity"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.electronAffinity}"), jTextFieldElectronAffinity, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelDielectricConstant.setText(resourceMap.getString("jLabelDielectricConstant.text")); // NOI18N
        jLabelDielectricConstant.setName("jLabelDielectricConstant"); // NOI18N

        jTextFieldDielectricConstant.setName("jTextFieldDielectricConstant"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.dielectricConstantExpression}"), jTextFieldDielectricConstant, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jButtonPlotColor.setAction(actionMap.get("showColorChooser")); // NOI18N
        jButtonPlotColor.setText(resourceMap.getString("jButtonPlotColor.text")); // NOI18N
        jButtonPlotColor.setActionCommand(resourceMap.getString("jButtonPlotColor.actionCommand")); // NOI18N
        jButtonPlotColor.setName("jButtonPlotColor"); // NOI18N

        jPanelPlotColor.setName("jPanelPlotColor"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.fillColor}"), jPanelPlotColor, org.jdesktop.beansbinding.BeanProperty.create("background"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanelPlotColorLayout = new javax.swing.GroupLayout(jPanelPlotColor);
        jPanelPlotColor.setLayout(jPanelPlotColorLayout);
        jPanelPlotColorLayout.setHorizontalGroup(
            jPanelPlotColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 91, Short.MAX_VALUE)
        );
        jPanelPlotColorLayout.setVerticalGroup(
            jPanelPlotColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jLabelThickness.setText(resourceMap.getString("jLabelThickness.text")); // NOI18N
        jLabelThickness.setName("jLabelThickness"); // NOI18N

        jTextFieldThickness.setName("jTextFieldThickness"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.thicknessNm}"), jTextFieldThickness, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanelFixedCharge.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanelFixedCharge.border.title"))); // NOI18N
        jPanelFixedCharge.setName("jPanelFixedCharge"); // NOI18N

        jLabelLocation.setText(resourceMap.getString("jLabelLocation.text")); // NOI18N
        jLabelLocation.setName("jLabelLocation"); // NOI18N

        jTextFieldLocation.setName("jTextFieldLocation"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentPoint.locationNm}"), jTextFieldLocation, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelChargeC.setText(resourceMap.getString("jLabelChargeC.text")); // NOI18N
        jLabelChargeC.setName("jLabelChargeC"); // NOI18N

        jTextFieldChargeC.setName("jTextFieldChargeC"); // NOI18N
        jTextFieldChargeC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldChargeCFocusGained(evt);
            }
        });

        jLabelChargeE.setText(resourceMap.getString("jLabelChargeE.text")); // NOI18N
        jLabelChargeE.setName("jLabelChargeE"); // NOI18N

        jTextFieldChargeE.setName("jTextFieldChargeE"); // NOI18N
        jTextFieldChargeE.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldChargeEFocusGained(evt);
            }
        });

        jButtonFixedChargeAdd.setAction(actionMap.get("addCurrentPoint")); // NOI18N
        jButtonFixedChargeAdd.setText(resourceMap.getString("jButtonFixedChargeAdd.text")); // NOI18N
        jButtonFixedChargeAdd.setName("jButtonFixedChargeAdd"); // NOI18N

        jButtonFixedChargeDelete.setAction(actionMap.get("deleteSelectedPoint")); // NOI18N
        jButtonFixedChargeDelete.setText(resourceMap.getString("jButtonFixedChargeDelete.text")); // NOI18N
        jButtonFixedChargeDelete.setName("jButtonFixedChargeDelete"); // NOI18N

        jScrollPaneFixedCharge.setName("jScrollPaneFixedCharge"); // NOI18N

        jTableFixedCharge.setAutoCreateRowSorter(true);
        jTableFixedCharge.setName("jTableFixedCharge"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${listPoints}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableFixedCharge);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${locationNm}"));
        columnBinding.setColumnName("Location Nm");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${electronCharge}"));
        columnBinding.setColumnName("Electron Charge");
        columnBinding.setColumnClass(Double.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${charge}"));
        columnBinding.setColumnName("Charge");
        columnBinding.setColumnClass(Double.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPaneFixedCharge.setViewportView(jTableFixedCharge);
        jTableFixedCharge.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTableFixedCharge.columnModel.title0")); // NOI18N
        jTableFixedCharge.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTableFixedCharge.columnModel.title2")); // NOI18N
        jTableFixedCharge.getColumnModel().getColumn(1).setCellRenderer(null);
        jTableFixedCharge.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTableFixedCharge.columnModel.title1")); // NOI18N
        jTableFixedCharge.getColumnModel().getColumn(2).setCellRenderer(null);

        javax.swing.GroupLayout jPanelFixedChargeLayout = new javax.swing.GroupLayout(jPanelFixedCharge);
        jPanelFixedCharge.setLayout(jPanelFixedChargeLayout);
        jPanelFixedChargeLayout.setHorizontalGroup(
            jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFixedChargeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFixedChargeLayout.createSequentialGroup()
                        .addGroup(jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelChargeC)
                            .addComponent(jLabelLocation)
                            .addComponent(jLabelChargeE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldChargeC)
                            .addComponent(jTextFieldLocation)
                            .addComponent(jTextFieldChargeE, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelFixedChargeLayout.createSequentialGroup()
                        .addComponent(jButtonFixedChargeAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonFixedChargeDelete)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneFixedCharge, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelFixedChargeLayout.setVerticalGroup(
            jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFixedChargeLayout.createSequentialGroup()
                .addGroup(jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelLocation)
                    .addComponent(jTextFieldLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelChargeC)
                    .addComponent(jTextFieldChargeC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelChargeE)
                    .addComponent(jTextFieldChargeE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelFixedChargeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFixedChargeAdd)
                    .addComponent(jButtonFixedChargeDelete)))
            .addComponent(jScrollPaneFixedCharge, 0, 0, Short.MAX_VALUE)
        );

        jLabelNotes.setText(resourceMap.getString("jLabelNotes.text")); // NOI18N
        jLabelNotes.setName("jLabelNotes"); // NOI18N

        jScrollPaneNotes.setName("jScrollPaneNotes"); // NOI18N

        jTextAreaNotes.setColumns(20);
        jTextAreaNotes.setLineWrap(true);
        jTextAreaNotes.setRows(5);
        jTextAreaNotes.setName("jTextAreaNotes"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.notes}"), jTextAreaNotes, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPaneNotes.setViewportView(jTextAreaNotes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelFixedCharge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(jLabelBandGap))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelDielectricConstant))
                            .addComponent(jLabelThickness, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldDielectricConstant, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextFieldBandGap, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabelElectronAffinity)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldElectronAffinity, 0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanelPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jTextFieldThickness, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPaneNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                    .addComponent(jLabelNotes))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jLabelBandGap)
                    .addComponent(jTextFieldBandGap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelElectronAffinity)
                    .addComponent(jTextFieldElectronAffinity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPlotColor)
                    .addComponent(jButtonCancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelName)
                            .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDielectricConstant)
                            .addComponent(jTextFieldDielectricConstant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldThickness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelThickness)))
                    .addComponent(jPanelPlotColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelFixedCharge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelNotes)
                .addGap(6, 6, 6)
                .addComponent(jScrollPaneNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldChargeCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldChargeCFocusGained
       jTextFieldChargeE.setText("");
    }//GEN-LAST:event_jTextFieldChargeCFocusGained

    private void jTextFieldChargeEFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldChargeEFocusGained
       jTextFieldChargeC.setText("");
    }//GEN-LAST:event_jTextFieldChargeEFocusGained

   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
      java.awt.EventQueue.invokeLater(new Runnable() {

         public void run() {
            BandMaterialDielectric dialog = new BandMaterialDielectric(new javax.swing.JFrame(), true, new Dielectric());
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
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonFixedChargeAdd;
    private javax.swing.JButton jButtonFixedChargeDelete;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonPlotColor;
    private javax.swing.JLabel jLabelBandGap;
    private javax.swing.JLabel jLabelChargeC;
    private javax.swing.JLabel jLabelChargeE;
    private javax.swing.JLabel jLabelDielectricConstant;
    private javax.swing.JLabel jLabelElectronAffinity;
    private javax.swing.JLabel jLabelLocation;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelNotes;
    private javax.swing.JLabel jLabelThickness;
    private javax.swing.JPanel jPanelFixedCharge;
    private javax.swing.JPanel jPanelPlotColor;
    private javax.swing.JScrollPane jScrollPaneFixedCharge;
    private javax.swing.JScrollPane jScrollPaneNotes;
    private javax.swing.JTable jTableFixedCharge;
    private javax.swing.JTextArea jTextAreaNotes;
    private javax.swing.JTextField jTextFieldBandGap;
    private javax.swing.JTextField jTextFieldChargeC;
    private javax.swing.JTextField jTextFieldChargeE;
    private javax.swing.JTextField jTextFieldDielectricConstant;
    private javax.swing.JTextField jTextFieldElectronAffinity;
    private javax.swing.JTextField jTextFieldLocation;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldThickness;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
