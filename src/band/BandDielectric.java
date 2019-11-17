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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.jdesktop.application.Action;

/**
 *
 * @author mbaker
 */
public class BandDielectric extends javax.swing.JDialog {

   private Dielectric currentRecord;
   private boolean confirmed;

   /** Creates new form BandDielectric */
   public BandDielectric(java.awt.Frame parent, boolean modal) {
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

      setConfirmed(false);
      getRootPane().setDefaultButton(jButtonOK);

      initComponents();
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
        jLabelDielectricConstant = new javax.swing.JLabel();
        jTextFieldDielectricConstant = new javax.swing.JTextField();
        jLabelBandGap = new javax.swing.JLabel();
        jTextFieldBandGap = new javax.swing.JTextField();
        jLabelElectronAffinity = new javax.swing.JLabel();
        jTextFieldElectronAffinity = new javax.swing.JTextField();
        jButtonPlotColor = new javax.swing.JButton();
        jPanelPlotColor = new javax.swing.JPanel();
        jLabelNotes = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaNotes = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandDielectric.class);
        setTitle(resourceMap.getString("BandDielectric.title")); // NOI18N
        setName("BandDielectric"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandDielectric.class, this);
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

        jLabelDielectricConstant.setText(resourceMap.getString("jLabelDielectricConstant.text")); // NOI18N
        jLabelDielectricConstant.setName("jLabelDielectricConstant"); // NOI18N

        jTextFieldDielectricConstant.setName("jTextFieldDielectricConstant"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.dielectricConstantExpression}"), jTextFieldDielectricConstant, org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        jLabelNotes.setText(resourceMap.getString("jLabelNotes.text")); // NOI18N
        jLabelNotes.setName("jLabelNotes"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextAreaNotes.setColumns(20);
        jTextAreaNotes.setLineWrap(true);
        jTextAreaNotes.setRows(5);
        jTextAreaNotes.setWrapStyleWord(true);
        jTextAreaNotes.setName("jTextAreaNotes"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.notes}"), jTextAreaNotes, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextAreaNotes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonCancel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabelBandGap))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabelName)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabelDielectricConstant)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldDielectricConstant, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextFieldBandGap, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabelElectronAffinity)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldElectronAffinity, 0, 0, Short.MAX_VALUE))))
                            .addComponent(jLabelNotes))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanelPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(19, 19, 19))))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldName)
                    .addComponent(jTextFieldDielectricConstant)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelName)
                        .addComponent(jLabelDielectricConstant)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelNotes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
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
            BandDielectric dialog = new BandDielectric(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonPlotColor;
    private javax.swing.JLabel jLabelBandGap;
    private javax.swing.JLabel jLabelDielectricConstant;
    private javax.swing.JLabel jLabelElectronAffinity;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelNotes;
    private javax.swing.JPanel jPanelPlotColor;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaNotes;
    private javax.swing.JTextField jTextFieldBandGap;
    private javax.swing.JTextField jTextFieldDielectricConstant;
    private javax.swing.JTextField jTextFieldElectronAffinity;
    private javax.swing.JTextField jTextFieldName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

   public void setCurrentRecord(Dielectric passDielectric) {
      Dielectric oldDielectric = currentRecord;
      currentRecord = passDielectric;
      propertyChangeSupport.firePropertyChange("currentRecord", oldDielectric, currentRecord);
   }

   public Dielectric getCurrentRecord() {
      return currentRecord;
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

      setConfirmed(true);
      this.setVisible(false);
   }

   @Action
   public void jButtonCancel_Click() {
      setConfirmed(false);
      this.setVisible(false);
   }

   public boolean isConfirmed() {
      return confirmed;
   }

   public void setConfirmed(boolean value) {
      confirmed = value;
   }

   @Action
   public void showColorChooser() {
      Color newColor = JColorChooser.showDialog(rootPane, "Choose a color...", Color.yellow);
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
}
