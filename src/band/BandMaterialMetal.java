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
public class BandMaterialMetal extends MaterialSelect {

   private Metal currentRecord;
   private boolean confirmed;

    /** Creates new form BandDielectric */
    public BandMaterialMetal(java.awt.Frame parent, boolean modal) {
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

        addWindowListener( new WindowAdapter() {
           @Override
           public void windowOpened(WindowEvent e) {
              jTextFieldThickness.requestFocusInWindow();
              jTextFieldThickness.selectAll();
           }
        });
    }
    
    @Override
    public void setCurrentRecord(Material m) {
       Metal oldMetal = currentRecord;
       currentRecord = (Metal) m;
       propertyChangeSupport.firePropertyChange("currentRecord", oldMetal, currentRecord);
    }

    @Override
    public Metal getCurrentRecord() {
       return currentRecord;
    }

    @Action
    public void jButtonOK_Click() {
       if (jTextFieldName.getText().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Name is a required field.");
          jTextFieldName.requestFocus();
          return;
       }
       if (jTextFieldWorkFunction.getText().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Work Function is a required field.");
          jTextFieldWorkFunction.requestFocus();
          return;
       }
       if (jTextFieldCharge.getText().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Charge is a required field.");
          jTextFieldCharge.requestFocus();
          return;
       }
       if (jTextFieldThickness.getText().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Thickness is a required field.");
          jTextFieldThickness.requestFocus();
          return;
       }
       if (currentRecord.getThickness() > 0) {
          setConfirmed(true);
          setVisible(false);
       }
       else {
          JOptionPane.showMessageDialog(rootPane, "The thickness must be bigger than 0!");
       }
    }

    @Action
    public void jButtonCancel_Click() {
       setConfirmed(false);
       setVisible(false);
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
        jLabelWorkFunction = new javax.swing.JLabel();
        jTextFieldWorkFunction = new javax.swing.JTextField();
        jButtonPlotColor = new javax.swing.JButton();
        jLabelCharge = new javax.swing.JLabel();
        jTextFieldCharge = new javax.swing.JTextField();
        jLabelThickness = new javax.swing.JLabel();
        jTextFieldThickness = new javax.swing.JTextField();
        jLabelNotes = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaNotes = new javax.swing.JTextArea();
        jPanelPlotColor = new java.awt.Panel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandMaterialMetal.class);
        setTitle(resourceMap.getString("BandMaterialMetal.title")); // NOI18N
        setName("BandMaterialMetal"); // NOI18N
        setResizable(false);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandMaterialMetal.class, this);
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

        jLabelWorkFunction.setText(resourceMap.getString("jLabelWorkFunction.text")); // NOI18N
        jLabelWorkFunction.setName("jLabelWorkFunction"); // NOI18N

        jTextFieldWorkFunction.setName("jTextFieldWorkFunction"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.workFunction}"), jTextFieldWorkFunction, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jButtonPlotColor.setAction(actionMap.get("showColorChooser")); // NOI18N
        jButtonPlotColor.setText(resourceMap.getString("jButtonPlotColor.text")); // NOI18N
        jButtonPlotColor.setActionCommand(resourceMap.getString("jButtonPlotColor.actionCommand")); // NOI18N
        jButtonPlotColor.setName("jButtonPlotColor"); // NOI18N

        jLabelCharge.setText(resourceMap.getString("jLabelCharge.text")); // NOI18N
        jLabelCharge.setName("jLabelCharge"); // NOI18N

        jTextFieldCharge.setName("jTextFieldCharge"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.extraCharge}"), jTextFieldCharge, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabelThickness.setText(resourceMap.getString("jLabelThickness.text")); // NOI18N
        jLabelThickness.setName("jLabelThickness"); // NOI18N

        jTextFieldThickness.setName("jTextFieldThickness"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.thicknessNm}"), jTextFieldThickness, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

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

        jPanelPlotColor.setName("jPanelPlotColor"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentRecord.fillColor}"), jPanelPlotColor, org.jdesktop.beansbinding.BeanProperty.create("background"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanelPlotColorLayout = new javax.swing.GroupLayout(jPanelPlotColor);
        jPanelPlotColor.setLayout(jPanelPlotColorLayout);
        jPanelPlotColorLayout.setHorizontalGroup(
            jPanelPlotColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 53, Short.MAX_VALUE)
        );
        jPanelPlotColorLayout.setVerticalGroup(
            jPanelPlotColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonPlotColor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanelPlotColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelNotes)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelWorkFunction)
                                .addGap(17, 17, 17)
                                .addComponent(jTextFieldWorkFunction, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelThickness)
                            .addComponent(jLabelCharge))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldCharge)
                            .addComponent(jTextFieldThickness, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanelPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonPlotColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldName)
                            .addComponent(jTextFieldWorkFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelName)
                                .addComponent(jLabelWorkFunction))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelCharge)
                            .addComponent(jTextFieldCharge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldThickness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelThickness)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelNotes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
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
                BandMaterialMetal dialog = new BandMaterialMetal(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabelCharge;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelNotes;
    private javax.swing.JLabel jLabelThickness;
    private javax.swing.JLabel jLabelWorkFunction;
    private java.awt.Panel jPanelPlotColor;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaNotes;
    private javax.swing.JTextField jTextFieldCharge;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldThickness;
    private javax.swing.JTextField jTextFieldWorkFunction;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
