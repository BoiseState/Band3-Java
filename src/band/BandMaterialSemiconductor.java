/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BandSemiconductor.java
 *
 * Created on Jan 20, 2010, 8:46:20 PM
 */

package band;

import java.awt.Color;
import java.awt.Font;
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
public class BandMaterialSemiconductor extends MaterialSelect {

   private Semiconductor currentRecord;
   private boolean confirmed;

    /** Creates new form BandSemiconductor */
    public BandMaterialSemiconductor(java.awt.Frame parent, boolean modal) {
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

    @Override
    public void setCurrentRecord(Material m) {
       Semiconductor oldSemiconductor = currentRecord;
       currentRecord = (Semiconductor) m;
       propertyChangeSupport.firePropertyChange("currentRecord", oldSemiconductor, currentRecord);
    }

    @Override
    public Semiconductor getCurrentRecord() {
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
      if (this.jTextFieldDielectricConstant.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Dielectric Constant is a required field.");
         jTextFieldDielectricConstant.requestFocus();
         return;
      }
      if (jTextFieldBandGap.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Band Gap is a required field.");
         jTextFieldBandGap.requestFocus();
         return;
      }
      try {
         double numValue = Functions.evaluateExpression(jTextFieldBandGap.getText(), 'T', 300);
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this, "Could not decipher the Band Gap! Please check expression, use 'T' for temperature variable.");
         jTextFieldBandGap.requestFocus();
         return;
      }

      if (jTextFieldIntrinsicCarrierConcentration.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Intrinsic Carrier Concentration is a required field.");
         jTextFieldIntrinsicCarrierConcentration.requestFocus();
         return;
      }
      try {
         double numValue = Functions.evaluateExpression(jTextFieldIntrinsicCarrierConcentration.getText(), 'T', 300);
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,"Could not decipher the Intrinsic Carrier Concentration! Please check expression, use 'T' for temperature variable.");
         jTextFieldIntrinsicCarrierConcentration.requestFocus();
         return;
      }

      if (jTextFieldDopantConcentration.getText().isEmpty()) {
         JOptionPane.showMessageDialog(this, "Dopant Concentration is a required field.");
         jTextFieldDopantConcentration.requestFocus();
         return;
      }

       if (currentRecord.getDopantConcentration() > currentRecord.getIntrinsicCarrierConcentration() * 100) {
          setConfirmed(true);
          this.setVisible(false);
       }
       else {
          JOptionPane.showMessageDialog(rootPane, "The doping concentration must be much greater than the intrinsic carrier concentration!");
       }
    }

    @Action
    public void jButtonCancel_Click() {
       setConfirmed(false);
       this.setVisible(false);
    }

    @Override
    public boolean isConfirmed() {
       return confirmed;
    }

    @Override
    public void setConfirmed(boolean value) {
       confirmed = value;
    }

    @Action
    public void showColorChooser() {
       Color newColor = JColorChooser.showDialog(rootPane, "Choose a color...", jPanelPlotColor.getBackground() );
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

        buttonGroup1 = new javax.swing.ButtonGroup();
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
        jLabelNotes = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaNotes = new javax.swing.JTextArea();
        jLabelIntrinsicCarrierConcentration = new javax.swing.JLabel();
        jTextFieldIntrinsicCarrierConcentration = new javax.swing.JTextField();
        jLabelDopantConcentration = new javax.swing.JLabel();
        jTextFieldDopantConcentration = new javax.swing.JTextField();
        jRadioButtonNType = new javax.swing.JRadioButton();
        jRadioButtonPType = new javax.swing.JRadioButton();
        jPanelPlotColor = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandMaterialSemiconductor.class);
        setTitle(resourceMap.getString("BandMaterialSemiconductor.title")); // NOI18N
        setName("BandMaterialSemiconductor"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandMaterialSemiconductor.class, this);
        jButtonOK.setAction(actionMap.get("jButtonOK_Click")); // NOI18N
        jButtonOK.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonOK.setText(resourceMap.getString("jButtonOK.text")); // NOI18N
        jButtonOK.setName("jButtonOK"); // NOI18N
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setAction(actionMap.get("jButtonCancel_Click")); // NOI18N
        jButtonCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonCancel.setText(resourceMap.getString("jButtonCancel.text")); // NOI18N
        jButtonCancel.setName("jButtonCancel"); // NOI18N

        jLabelName.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelName.setText(resourceMap.getString("jLabelName.text")); // NOI18N
        jLabelName.setName("jLabelName"); // NOI18N

        jTextFieldName.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldName.setName("jTextFieldName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.name}"), jTextFieldName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelDielectricConstant.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelDielectricConstant.setText(resourceMap.getString("jLabelDielectricConstant.text")); // NOI18N
        jLabelDielectricConstant.setName("jLabelDielectricConstant"); // NOI18N

        jTextFieldDielectricConstant.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldDielectricConstant.setName("jTextFieldDielectricConstant"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.dielectricConstant}"), jTextFieldDielectricConstant, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelBandGap.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelBandGap.setText(resourceMap.getString("jLabelBandGap.text")); // NOI18N
        jLabelBandGap.setName("jLabelBandGap"); // NOI18N

        jTextFieldBandGap.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldBandGap.setName("jTextFieldBandGap"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.bandGapExpression}"), jTextFieldBandGap, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelElectronAffinity.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelElectronAffinity.setText(resourceMap.getString("jLabelElectronAffinity.text")); // NOI18N
        jLabelElectronAffinity.setName("jLabelElectronAffinity"); // NOI18N

        jTextFieldElectronAffinity.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldElectronAffinity.setName("jTextFieldElectronAffinity"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.electronAffinity}"), jTextFieldElectronAffinity, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jButtonPlotColor.setAction(actionMap.get("showColorChooser")); // NOI18N
        jButtonPlotColor.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonPlotColor.setText(resourceMap.getString("jButtonPlotColor.text")); // NOI18N
        jButtonPlotColor.setActionCommand(resourceMap.getString("jButtonPlotColor.actionCommand")); // NOI18N
        jButtonPlotColor.setName("jButtonPlotColor"); // NOI18N

        jLabelNotes.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelNotes.setText(resourceMap.getString("jLabelNotes.text")); // NOI18N
        jLabelNotes.setName("jLabelNotes"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextAreaNotes.setColumns(20);
        jTextAreaNotes.setRows(5);
        jTextAreaNotes.setName("jTextAreaNotes"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.notes}"), jTextAreaNotes, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextAreaNotes);

        jLabelIntrinsicCarrierConcentration.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelIntrinsicCarrierConcentration.setText(resourceMap.getString("jLabelIntrinsicCarrierConcentration.text")); // NOI18N
        jLabelIntrinsicCarrierConcentration.setName("jLabelIntrinsicCarrierConcentration"); // NOI18N

        jTextFieldIntrinsicCarrierConcentration.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldIntrinsicCarrierConcentration.setName("jTextFieldIntrinsicCarrierConcentration"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.intrinsicCarrierConcentrationExpression}"), jTextFieldIntrinsicCarrierConcentration, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelDopantConcentration.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelDopantConcentration.setText(resourceMap.getString("jLabelDopantConcentration.text")); // NOI18N
        jLabelDopantConcentration.setName("jLabelDopantConcentration"); // NOI18N

        jTextFieldDopantConcentration.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldDopantConcentration.setName("jTextFieldDopantConcentration"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.dopantConcentration}"), jTextFieldDopantConcentration, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        buttonGroup1.add(jRadioButtonNType);
        jRadioButtonNType.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jRadioButtonNType.setText(resourceMap.getString("jRadioButtonNType.text")); // NOI18N
        jRadioButtonNType.setName("jRadioButtonNType"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.NType}"), jRadioButtonNType, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        buttonGroup1.add(jRadioButtonPType);
        jRadioButtonPType.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jRadioButtonPType.setText(resourceMap.getString("jRadioButtonPType.text")); // NOI18N
        jRadioButtonPType.setName("jRadioButtonPType"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.PType}"), jRadioButtonPType, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jPanelPlotColor.setName("jPanelPlotColor"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.fillColor}"), jPanelPlotColor, org.jdesktop.beansbinding.BeanProperty.create("background"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanelPlotColorLayout = new javax.swing.GroupLayout(jPanelPlotColor);
        jPanelPlotColor.setLayout(jPanelPlotColorLayout);
        jPanelPlotColorLayout.setHorizontalGroup(
            jPanelPlotColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 105, Short.MAX_VALUE)
        );
        jPanelPlotColorLayout.setVerticalGroup(
            jPanelPlotColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 59, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabelNotes, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButtonCancel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabelBandGap)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldBandGap)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabelElectronAffinity)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldElectronAffinity, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabelName)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jRadioButtonPType)
                                        .addComponent(jRadioButtonNType))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabelDopantConcentration)
                                        .addComponent(jLabelIntrinsicCarrierConcentration))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTextFieldIntrinsicCarrierConcentration)
                                        .addComponent(jTextFieldDopantConcentration, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabelDielectricConstant)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextFieldDielectricConstant, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanelPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonOK)
                        .addComponent(jButtonCancel)
                        .addComponent(jLabelBandGap))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldBandGap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelElectronAffinity)
                        .addComponent(jTextFieldElectronAffinity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldName)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelName)
                                .addComponent(jLabelDielectricConstant)
                                .addComponent(jTextFieldDielectricConstant)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelIntrinsicCarrierConcentration)
                            .addComponent(jTextFieldIntrinsicCarrierConcentration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButtonNType)))
                    .addComponent(jPanelPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDopantConcentration)
                    .addComponent(jTextFieldDopantConcentration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButtonPType)
                    .addComponent(jButtonPlotColor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelNotes)
                .addGap(9, 9, 9)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
       // TODO add your handling code here:
    }//GEN-LAST:event_jButtonOKActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BandMaterialSemiconductor dialog = new BandMaterialSemiconductor(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonPlotColor;
    private javax.swing.JLabel jLabelBandGap;
    private javax.swing.JLabel jLabelDielectricConstant;
    private javax.swing.JLabel jLabelDopantConcentration;
    private javax.swing.JLabel jLabelElectronAffinity;
    private javax.swing.JLabel jLabelIntrinsicCarrierConcentration;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelNotes;
    private javax.swing.JPanel jPanelPlotColor;
    private javax.swing.JRadioButton jRadioButtonNType;
    private javax.swing.JRadioButton jRadioButtonPType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaNotes;
    private javax.swing.JTextField jTextFieldBandGap;
    private javax.swing.JTextField jTextFieldDielectricConstant;
    private javax.swing.JTextField jTextFieldDopantConcentration;
    private javax.swing.JTextField jTextFieldElectronAffinity;
    private javax.swing.JTextField jTextFieldIntrinsicCarrierConcentration;
    private javax.swing.JTextField jTextFieldName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
