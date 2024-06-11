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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
//import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 *
 * @author mbaker
 */
public class BandSemiconductor extends javax.swing.JDialog {

   private Semiconductor currentRecord;
   private boolean confirmed;

   /** Creates new form BandSemiconductor */
   public BandSemiconductor(java.awt.Frame parent, boolean modal) {
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

   public void setCurrentRecord(Semiconductor passSemiconductor) {
      Semiconductor oldSemiconductor = currentRecord;
      currentRecord = passSemiconductor;
      propertyChangeSupport.firePropertyChange("currentRecord", oldSemiconductor, currentRecord);
   }

   public Semiconductor getCurrentRecord() {
      return currentRecord;
   }

//   @Action
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

      setConfirmed(true);
      this.setVisible(false);
   }

//   @Action
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

//   @Action
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
    // Generated using JFormDesigner Educational license - Madi Thompson (madithompson)
    private void initComponents() {
	ResourceBundle bundle = ResourceBundle.getBundle("band.resources.BandSemiconductor");
	jButtonOK = new JButton();
	jButtonCancel = new JButton();
	jLabelName = new JLabel();
	jTextFieldName = new JTextField();
	jLabelDielectricConstant = new JLabel();
	jTextFieldDielectricConstant = new JTextField();
	jLabelBandGap = new JLabel();
	jTextFieldBandGap = new JTextField();
	jLabelElectronAffinity = new JLabel();
	jTextFieldElectronAffinity = new JTextField();
	jButtonPlotColor = new JButton();
	jPanelPlotColor = new JPanel();
	jLabelNotes = new JLabel();
	jScrollPane1 = new JScrollPane();
	jTextAreaNotes = new JTextArea();
	jLabelIntrinsicCarrierConcentration = new JLabel();
	jTextFieldIntrinsicCarrierConcentration = new JTextField();

	//======== this ========
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	setTitle(bundle.getString("BandSemiconductor.title"));
	setName("BandSemiconductor");
	var contentPane = getContentPane();

	//---- jButtonOK ----
	jButtonOK.setText(bundle.getString("jButtonOK.text"));
	jButtonOK.setName("jButtonOK");

	//---- jButtonCancel ----
	jButtonCancel.setText(bundle.getString("jButtonCancel.text"));
	jButtonCancel.setName("jButtonCancel");

	//---- jLabelName ----
	jLabelName.setText(bundle.getString("jLabelName.text"));
	jLabelName.setName("jLabelName");

	//---- jTextFieldName ----
	jTextFieldName.setName("jTextFieldName");

	//---- jLabelDielectricConstant ----
	jLabelDielectricConstant.setText(bundle.getString("jLabelDielectricConstant.text"));
	jLabelDielectricConstant.setName("jLabelDielectricConstant");

	//---- jTextFieldDielectricConstant ----
	jTextFieldDielectricConstant.setName("jTextFieldDielectricConstant");

	//---- jLabelBandGap ----
	jLabelBandGap.setText(bundle.getString("jLabelBandGap.text"));
	jLabelBandGap.setName("jLabelBandGap");

	//---- jTextFieldBandGap ----
	jTextFieldBandGap.setName("jTextFieldBandGap");

	//---- jLabelElectronAffinity ----
	jLabelElectronAffinity.setText(bundle.getString("jLabelElectronAffinity.text"));
	jLabelElectronAffinity.setName("jLabelElectronAffinity");

	//---- jTextFieldElectronAffinity ----
	jTextFieldElectronAffinity.setName("jTextFieldElectronAffinity");

	//---- jButtonPlotColor ----
	jButtonPlotColor.setText(bundle.getString("jButtonPlotColor.text"));
	jButtonPlotColor.setActionCommand(bundle.getString("jButtonPlotColor.actionCommand"));
	jButtonPlotColor.setName("jButtonPlotColor");

	//======== jPanelPlotColor ========
	{
	    jPanelPlotColor.setName("jPanelPlotColor");

	    GroupLayout jPanelPlotColorLayout = new GroupLayout(jPanelPlotColor);
	    jPanelPlotColor.setLayout(jPanelPlotColorLayout);
	    jPanelPlotColorLayout.setHorizontalGroup(
		jPanelPlotColorLayout.createParallelGroup()
		    .addGap(0, 112, Short.MAX_VALUE)
	    );
	    jPanelPlotColorLayout.setVerticalGroup(
		jPanelPlotColorLayout.createParallelGroup()
		    .addGap(0, 22, Short.MAX_VALUE)
	    );
	}

	//---- jLabelNotes ----
	jLabelNotes.setText(bundle.getString("jLabelNotes.text"));
	jLabelNotes.setName("jLabelNotes");

	//======== jScrollPane1 ========
	{
	    jScrollPane1.setName("jScrollPane1");

	    //---- jTextAreaNotes ----
	    jTextAreaNotes.setColumns(20);
	    jTextAreaNotes.setLineWrap(true);
	    jTextAreaNotes.setRows(5);
	    jTextAreaNotes.setWrapStyleWord(true);
	    jTextAreaNotes.setName("jTextAreaNotes");
	    jScrollPane1.setViewportView(jTextAreaNotes);
	}

	//---- jLabelIntrinsicCarrierConcentration ----
	jLabelIntrinsicCarrierConcentration.setText(bundle.getString("jLabelIntrinsicCarrierConcentration.text"));
	jLabelIntrinsicCarrierConcentration.setName("jLabelIntrinsicCarrierConcentration");

	//---- jTextFieldIntrinsicCarrierConcentration ----
	jTextFieldIntrinsicCarrierConcentration.setName("jTextFieldIntrinsicCarrierConcentration");

	GroupLayout contentPaneLayout = new GroupLayout(contentPane);
	contentPane.setLayout(contentPaneLayout);
	contentPaneLayout.setHorizontalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addComponent(jScrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addComponent(jButtonOK, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(jButtonCancel)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addComponent(jLabelBandGap)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(jTextFieldBandGap, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addComponent(jLabelElectronAffinity)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				    .addComponent(jTextFieldElectronAffinity, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jLabelNotes)
					.addComponent(jLabelName))
				    .addGroup(contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(jTextFieldName, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
					    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					    .addComponent(jLabelDielectricConstant)
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(jTextFieldDielectricConstant, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE))
					.addGroup(contentPaneLayout.createSequentialGroup()
					    .addGap(58, 58, 58)
					    .addComponent(jLabelIntrinsicCarrierConcentration)
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(jTextFieldIntrinsicCarrierConcentration, GroupLayout.PREFERRED_SIZE, 321, GroupLayout.PREFERRED_SIZE)))))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup()
				.addComponent(jPanelPlotColor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jButtonPlotColor))))
		    .addContainerGap())
	);
	contentPaneLayout.setVerticalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(jButtonOK)
			    .addComponent(jButtonCancel)
			    .addComponent(jLabelBandGap))
			.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(jTextFieldBandGap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(jTextFieldElectronAffinity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(jButtonPlotColor)
			    .addComponent(jLabelElectronAffinity)))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
			.addComponent(jPanelPlotColor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(jLabelName)
			    .addComponent(jLabelDielectricConstant))
			.addComponent(jTextFieldName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(jTextFieldDielectricConstant, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(jLabelNotes)
			.addComponent(jLabelIntrinsicCarrierConcentration)
			.addComponent(jTextFieldIntrinsicCarrierConcentration, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
		    .addContainerGap())
	);
	pack();
	setLocationRelativeTo(getOwner());

	//---- bindings ----
	bindingGroup = new BindingGroup();
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.name}"),
	    jTextFieldName, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.dielectricConstant}"),
	    jTextFieldDielectricConstant, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.bandGapExpression}"),
	    jTextFieldBandGap, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.electronAffinity}"),
	    jTextFieldElectronAffinity, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.fillColor}"),
	    jPanelPlotColor, BeanProperty.create("background")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.notes}"),
	    jTextAreaNotes, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.intrinsicCarrierConcentrationExpression}"),
	    jTextFieldIntrinsicCarrierConcentration, BeanProperty.create("text")));
	bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
      java.awt.EventQueue.invokeLater(new Runnable() {

         public void run() {
            BandSemiconductor dialog = new BandSemiconductor(new javax.swing.JFrame(), true);
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
    private JButton jButtonOK;
    private JButton jButtonCancel;
    private JLabel jLabelName;
    private JTextField jTextFieldName;
    private JLabel jLabelDielectricConstant;
    private JTextField jTextFieldDielectricConstant;
    private JLabel jLabelBandGap;
    private JTextField jTextFieldBandGap;
    private JLabel jLabelElectronAffinity;
    private JTextField jTextFieldElectronAffinity;
    private JButton jButtonPlotColor;
    private JPanel jPanelPlotColor;
    private JLabel jLabelNotes;
    private JScrollPane jScrollPane1;
    private JTextArea jTextAreaNotes;
    private JLabel jLabelIntrinsicCarrierConcentration;
    private JTextField jTextFieldIntrinsicCarrierConcentration;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
