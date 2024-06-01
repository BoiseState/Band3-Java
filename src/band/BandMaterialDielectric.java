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
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
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
    // Generated using JFormDesigner Educational license - Madi Thompson (madithompson)
    private void initComponents() {
	ResourceBundle bundle = ResourceBundle.getBundle("band.resources.BandMaterialDielectric");
	jButtonOK = new JButton();
	jButtonCancel = new JButton();
	jLabelName = new JLabel();
	jTextFieldName = new JTextField();
	jLabelBandGap = new JLabel();
	jTextFieldBandGap = new JTextField();
	jLabelElectronAffinity = new JLabel();
	jTextFieldElectronAffinity = new JTextField();
	jLabelDielectricConstant = new JLabel();
	jTextFieldDielectricConstant = new JTextField();
	jButtonPlotColor = new JButton();
	jPanelPlotColor = new JPanel();
	jLabelThickness = new JLabel();
	jTextFieldThickness = new JTextField();
	jPanelFixedCharge = new JPanel();
	jLabelLocation = new JLabel();
	jTextFieldLocation = new JTextField();
	jLabelChargeC = new JLabel();
	jTextFieldChargeC = new JTextField();
	jLabelChargeE = new JLabel();
	jTextFieldChargeE = new JTextField();
	jButtonFixedChargeAdd = new JButton();
	jButtonFixedChargeDelete = new JButton();
	jScrollPaneFixedCharge = new JScrollPane();
	jTableFixedCharge = new JTable();
	jLabelNotes = new JLabel();
	jScrollPaneNotes = new JScrollPane();
	jTextAreaNotes = new JTextArea();

	//======== this ========
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	setTitle(bundle.getString("BandMaterialDielectric.title"));
	setName("BandMaterialDielectric");
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

	//---- jLabelDielectricConstant ----
	jLabelDielectricConstant.setText(bundle.getString("jLabelDielectricConstant.text"));
	jLabelDielectricConstant.setName("jLabelDielectricConstant");

	//---- jTextFieldDielectricConstant ----
	jTextFieldDielectricConstant.setName("jTextFieldDielectricConstant");

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
		    .addGap(0, 91, Short.MAX_VALUE)
	    );
	    jPanelPlotColorLayout.setVerticalGroup(
		jPanelPlotColorLayout.createParallelGroup()
		    .addGap(0, 20, Short.MAX_VALUE)
	    );
	}

	//---- jLabelThickness ----
	jLabelThickness.setText(bundle.getString("jLabelThickness.text"));
	jLabelThickness.setName("jLabelThickness");

	//---- jTextFieldThickness ----
	jTextFieldThickness.setName("jTextFieldThickness");

	//======== jPanelFixedCharge ========
	{
	    jPanelFixedCharge.setBorder(new TitledBorder("Fixed Charge"));
	    jPanelFixedCharge.setName("jPanelFixedCharge");

	    //---- jLabelLocation ----
	    jLabelLocation.setText(bundle.getString("jLabelLocation.text"));
	    jLabelLocation.setName("jLabelLocation");

	    //---- jTextFieldLocation ----
	    jTextFieldLocation.setName("jTextFieldLocation");

	    //---- jLabelChargeC ----
	    jLabelChargeC.setText(bundle.getString("jLabelChargeC.text"));
	    jLabelChargeC.setName("jLabelChargeC");

	    //---- jTextFieldChargeC ----
	    jTextFieldChargeC.setName("jTextFieldChargeC");
	    jTextFieldChargeC.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    jTextFieldChargeCFocusGained(e);
		}
	    });

	    //---- jLabelChargeE ----
	    jLabelChargeE.setText(bundle.getString("jLabelChargeE.text"));
	    jLabelChargeE.setName("jLabelChargeE");

	    //---- jTextFieldChargeE ----
	    jTextFieldChargeE.setName("jTextFieldChargeE");
	    jTextFieldChargeE.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    jTextFieldChargeEFocusGained(e);
		}
	    });

	    //---- jButtonFixedChargeAdd ----
	    jButtonFixedChargeAdd.setText(bundle.getString("jButtonFixedChargeAdd.text"));
	    jButtonFixedChargeAdd.setName("jButtonFixedChargeAdd");

	    //---- jButtonFixedChargeDelete ----
	    jButtonFixedChargeDelete.setText(bundle.getString("jButtonFixedChargeDelete.text"));
	    jButtonFixedChargeDelete.setName("jButtonFixedChargeDelete");

	    //======== jScrollPaneFixedCharge ========
	    {
		jScrollPaneFixedCharge.setName("jScrollPaneFixedCharge");

		//---- jTableFixedCharge ----
		jTableFixedCharge.setAutoCreateRowSorter(true);
		jTableFixedCharge.setName("jTableFixedCharge");
		jScrollPaneFixedCharge.setViewportView(jTableFixedCharge);
	    }

	    GroupLayout jPanelFixedChargeLayout = new GroupLayout(jPanelFixedCharge);
	    jPanelFixedCharge.setLayout(jPanelFixedChargeLayout);
	    jPanelFixedChargeLayout.setHorizontalGroup(
		jPanelFixedChargeLayout.createParallelGroup()
		    .addGroup(jPanelFixedChargeLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(jPanelFixedChargeLayout.createParallelGroup()
			    .addGroup(jPanelFixedChargeLayout.createSequentialGroup()
				.addGroup(jPanelFixedChargeLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				    .addComponent(jLabelChargeC)
				    .addComponent(jLabelLocation)
				    .addComponent(jLabelChargeE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(jPanelFixedChargeLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
				    .addComponent(jTextFieldChargeC)
				    .addComponent(jTextFieldLocation)
				    .addComponent(jTextFieldChargeE, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)))
			    .addGroup(jPanelFixedChargeLayout.createSequentialGroup()
				.addComponent(jButtonFixedChargeAdd)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jButtonFixedChargeDelete)))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(jScrollPaneFixedCharge, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
			.addContainerGap())
	    );
	    jPanelFixedChargeLayout.setVerticalGroup(
		jPanelFixedChargeLayout.createParallelGroup()
		    .addGroup(jPanelFixedChargeLayout.createSequentialGroup()
			.addGroup(jPanelFixedChargeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(jLabelLocation)
			    .addComponent(jTextFieldLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(jPanelFixedChargeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(jLabelChargeC)
			    .addComponent(jTextFieldChargeC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(jPanelFixedChargeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(jLabelChargeE)
			    .addComponent(jTextFieldChargeE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addGroup(jPanelFixedChargeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(jButtonFixedChargeAdd)
			    .addComponent(jButtonFixedChargeDelete)))
		    .addComponent(jScrollPaneFixedCharge, 0, 0, Short.MAX_VALUE)
	    );
	}

	//---- jLabelNotes ----
	jLabelNotes.setText(bundle.getString("jLabelNotes.text"));
	jLabelNotes.setName("jLabelNotes");

	//======== jScrollPaneNotes ========
	{
	    jScrollPaneNotes.setName("jScrollPaneNotes");

	    //---- jTextAreaNotes ----
	    jTextAreaNotes.setColumns(20);
	    jTextAreaNotes.setLineWrap(true);
	    jTextAreaNotes.setRows(5);
	    jTextAreaNotes.setName("jTextAreaNotes");
	    jScrollPaneNotes.setViewportView(jTextAreaNotes);
	}

	GroupLayout contentPaneLayout = new GroupLayout(contentPane);
	contentPane.setLayout(contentPaneLayout);
	contentPaneLayout.setHorizontalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addComponent(jPanelFixedCharge, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addGroup(contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addComponent(jButtonOK, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(jButtonCancel)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
				    .addComponent(jLabelBandGap))
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addComponent(jLabelName)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(jTextFieldName, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addComponent(jLabelDielectricConstant))
				.addComponent(jLabelThickness, GroupLayout.Alignment.TRAILING))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jTextFieldDielectricConstant, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
					.addGroup(contentPaneLayout.createSequentialGroup()
					    .addComponent(jTextFieldBandGap, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
					    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					    .addComponent(jLabelElectronAffinity)
					    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(jTextFieldElectronAffinity, 0, 1, Short.MAX_VALUE)))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
					.addComponent(jPanelPlotColor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(jButtonPlotColor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
				.addComponent(jTextFieldThickness, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)))
			.addComponent(jScrollPaneNotes, GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
			.addComponent(jLabelNotes))
		    .addContainerGap())
	);
	contentPaneLayout.setVerticalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(jButtonOK)
			.addComponent(jLabelBandGap)
			.addComponent(jTextFieldBandGap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(jLabelElectronAffinity)
			.addComponent(jTextFieldElectronAffinity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(jButtonPlotColor)
			.addComponent(jButtonCancel))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelName)
				.addComponent(jTextFieldName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jLabelDielectricConstant)
				.addComponent(jTextFieldDielectricConstant, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jTextFieldThickness, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jLabelThickness)))
			.addComponent(jPanelPlotColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(jPanelFixedCharge, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(jLabelNotes)
		    .addGap(6, 6, 6)
		    .addComponent(jScrollPaneNotes, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
		    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	);
	pack();
	setLocationRelativeTo(getOwner());

	//---- bindings ----
	bindingGroup = new BindingGroup();
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.name}"),
	    jTextFieldName, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.bandGap}"),
	    jTextFieldBandGap, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.electronAffinity}"),
	    jTextFieldElectronAffinity, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.dielectricConstantExpression}"),
	    jTextFieldDielectricConstant, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.fillColor}"),
	    jPanelPlotColor, BeanProperty.create("background")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.thicknessNm}"),
	    jTextFieldThickness, BeanProperty.create("text")));
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentPoint.locationNm}"),
	    jTextFieldLocation, BeanProperty.create("text")));
	{
	    var binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
		this, (ELProperty) ELProperty.create("${listPoints}"), jTableFixedCharge);
	    binding.addColumnBinding(ELProperty.create("${locationNm}"))
		.setColumnName("Location Nm")
		.setColumnClass(Double.class);
	    binding.addColumnBinding(ELProperty.create("${electronCharge}"))
		.setColumnName("Electron Charge")
		.setColumnClass(Double.class);
	    binding.addColumnBinding(ELProperty.create("${charge}"))
		.setColumnName("Charge")
		.setColumnClass(Double.class);
	    bindingGroup.addBinding(binding);
	    binding.bind();
	}
	bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
	    this, ELProperty.create("${currentRecord.notes}"),
	    jTextAreaNotes, BeanProperty.create("text")));
	bindingGroup.bind();
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
    // Generated using JFormDesigner Educational license - Madi Thompson (madithompson)
    private JButton jButtonOK;
    private JButton jButtonCancel;
    private JLabel jLabelName;
    private JTextField jTextFieldName;
    private JLabel jLabelBandGap;
    private JTextField jTextFieldBandGap;
    private JLabel jLabelElectronAffinity;
    private JTextField jTextFieldElectronAffinity;
    private JLabel jLabelDielectricConstant;
    private JTextField jTextFieldDielectricConstant;
    private JButton jButtonPlotColor;
    private JPanel jPanelPlotColor;
    private JLabel jLabelThickness;
    private JTextField jTextFieldThickness;
    private JPanel jPanelFixedCharge;
    private JLabel jLabelLocation;
    private JTextField jTextFieldLocation;
    private JLabel jLabelChargeC;
    private JTextField jTextFieldChargeC;
    private JLabel jLabelChargeE;
    private JTextField jTextFieldChargeE;
    private JButton jButtonFixedChargeAdd;
    private JButton jButtonFixedChargeDelete;
    private JScrollPane jScrollPaneFixedCharge;
    private JTable jTableFixedCharge;
    private JLabel jLabelNotes;
    private JScrollPane jScrollPaneNotes;
    private JTextArea jTextAreaNotes;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
