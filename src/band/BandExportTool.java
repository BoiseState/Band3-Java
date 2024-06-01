/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BandExportTool.java
 *
 * Created on Jan 21, 2010, 9:22:26 PM
 */
package band;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.FastXYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author mbaker
 */
public class BandExportTool extends javax.swing.JDialog {

    protected ObservableList<String> listOutputParameters = ObservableCollections.observableList(new ArrayList());
    private List<String> headerValues;
    private List<List<Double>> dataValues = new ArrayList<List<Double>>();
    ChartPanel cp;
    boolean windowLoaded = false;

    /** Creates new form BandExportTool */
    public BandExportTool(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        InputMap iMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

        getRootPane().getActionMap().put("enter", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        initComponents();
        jProgressBar1.setVisible(false);
        getRootPane().setDefaultButton(jButtonOK);
        

        loadValues();
        windowLoaded = true;
    }

    public List<String> getListOutputParameters() {
        return listOutputParameters;
    }

    public void updateStepSize() {
        BandApp app = BandApp.getApplication();

        updateValues();

        jTextFieldStepSize.setText(String.valueOf((app.getExportStopVoltage() - app.getExportStartVoltage()) / app.getExportNumberOfSteps()));
        try {
            double tempStepSize = (app.exportStopTemp - app.exportStartTemp) / app.exportNumberOfTempSteps;
            jTextFieldStepSizeForTemp.setText(Double.toString(tempStepSize));
        } catch (Exception e) {
        };
    }

    @Action
    public void updateValues() {
        BandApp app = BandApp.getApplication();

        app.setExportStartVoltage(Double.valueOf(jTextFieldStartVoltage.getText()));
        app.setExportStopVoltage(Double.valueOf(jTextFieldStopVoltage.getText()));
        app.setExportNumberOfSteps(Integer.valueOf(jTextFieldNumberOfSteps.getText()));
        app.exportStartTemp = Double.valueOf(jTextFieldStartTemp.getText());
        app.exportStopTemp = Double.valueOf(jTextFieldStopTemp.getText());
        app.exportNumberOfTempSteps = Integer.valueOf(jTextFieldNumberOfStepsForTemp.getText());
        app.exportSweepChoice = jComboBoxSweepChoice.getSelectedIndex();


    }

    private void loadValues() {
        BandApp app = BandApp.getApplication();
        if (app.exportStartTemp == 0 && app.exportStopTemp == 0 && app.exportNumberOfTempSteps == 0) {
            app.exportStartTemp = 150;
            app.exportStopTemp = 300;
            app.exportNumberOfTempSteps = 25;
        }
        jTextFieldStartVoltage.setText(String.valueOf(app.getExportStartVoltage()));
        jTextFieldStopVoltage.setText(String.valueOf(app.getExportStopVoltage()));
        jTextFieldNumberOfSteps.setText(String.valueOf(app.getExportNumberOfSteps()));
        jTextFieldStepSize.setText(String.valueOf((app.getExportStopVoltage() - app.getExportStartVoltage()) / app.getExportNumberOfSteps()));
        jTextFieldStartTemp.setText(String.valueOf(app.exportStartTemp));
        jTextFieldStopTemp.setText(String.valueOf(app.exportStopTemp));
        jTextFieldNumberOfStepsForTemp.setText(String.valueOf(app.exportNumberOfTempSteps));
        jComboBoxSweepChoice.setSelectedIndex(app.exportSweepChoice);
        

        try {
            headerValues = BandApp.getApplication().getStructure().outputParametersTitles();
            listOutputParameters.clear();
            for (int i = 1; i < headerValues.size(); i++) {
                listOutputParameters.add(headerValues.get(i));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e.getMessage());
        }
        updateStepSize();
    }

    @Action
    public void previewValues() {
            storeData();
            jListOutputParameters.setEnabled(true);
            jButtonSave.setEnabled(true);
            if (lastSelectedIndex != -1)
                drawChartOfOutputParameterAt(lastSelectedIndex, lastSelectedValue);   
    }

    @Action
    public void saveValues() {

        int sweepType = jComboBoxSweepChoice.getSelectedIndex();
        if (Double.valueOf(jTextFieldStartTemp.getText()) < 100 || Double.valueOf(jTextFieldStopTemp.getText()) < 100) {
            JOptionPane.showMessageDialog(rootPane, "Extreme low temperatures where dopant freeze-out occur are not modeled in the temperature sweep. For results raise the temperatures to or above 100K");
            return;
        }
        if (sweepType == 2 && (jListOutputParameters.getSelectedIndices().length > 1 || jListOutputParameters.getSelectedIndex() == -1)) {
            JOptionPane.showMessageDialog(rootPane, "One and only one parameter must be selected");
            jListOutputParameters.clearSelection();
            return;
        }


        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".csv") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Comma Seperated Values (*.csv)";
            }
        });
        int retVal = chooser.showSaveDialog(null);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            BufferedWriter br = null;
            try {
                String filePath = chooser.getSelectedFile().getPath();
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                }

                File file = new File(filePath);
                if (file.exists()) {
                    if (JOptionPane.showConfirmDialog(null, "The file " + file.getName() + " already exists, would you like to overwrite it?", "Overwrite file", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        file.delete();
                        file.createNewFile();
                    } else {
                        return; // They want to keep their file, exit.
                    }
                }

                br = new BufferedWriter(new FileWriter(file));

                Structure structure = BandApp.getApplication().getStructure();

                // make a copy of the structure so we don't ruin anything
                Structure structure2 = structure.clone();

                BandApp app = BandApp.getApplication();
                double startVoltage = app.getExportStartVoltage();
                double stopVoltage = app.getExportStopVoltage();
                double numberOfSteps = app.getExportNumberOfSteps();
                double inc = (stopVoltage - startVoltage) / (numberOfSteps - 2); // adjusted by 2 to match Band2 output.
                double currentVoltage = startVoltage;

                double tempStepSize = 1;
                double startTemp = BandApp.getApplication().getTemperature();
                double stopTemp = startTemp;

                List<String> headers = structure2.outputParametersTitles();
                String header = "";
                if (sweepType != 2) {
                    for (int i = 0; i < headers.size(); i++) {
                        if (header.equals("")) {
                            header = "Temperature(k)," + headers.get(i);
                        } else {
                            header = header + "," + headers.get(i);
                        }
                    }
                } else {

                    header = headers.get(jListOutputParameters.getSelectedIndex());
                    for (double i = startVoltage; i <= stopVoltage; i += inc) {
                        header += "," + i;
                    }
                }

                br.write(header);
                br.newLine();

                if (sweepType > 0) {
                    startTemp = Double.valueOf(jTextFieldStartTemp.getText());
                    stopTemp = Double.valueOf(jTextFieldStopTemp.getText());
                    if (sweepType == 1) {
                        startVoltage = app.getVoltage();
                        stopVoltage = app.getVoltage();
                        inc = 1;
                        numberOfSteps = 2;
                    }

                    tempStepSize = (stopTemp - startTemp) / Double.valueOf(jTextFieldNumberOfStepsForTemp.getText());
                    if (tempStepSize <= 0 || startTemp == stopTemp) {
                        tempStepSize = 1;
                    }
                }
                double initVoltage = currentVoltage;
                for (; startTemp <= stopTemp; startTemp += tempStepSize) {
                    currentVoltage = initVoltage;
                    for (int i = 0; i < numberOfSteps - 1; i++) {
                        structure2.Evaluate(currentVoltage, startTemp);
                        List<Double> values = structure2.outputParameters(currentVoltage);
                        String line = "";
                        if (sweepType != 2) {
                            for (int j = 0; j < values.size(); j++) {
                                if (line.equals("")) {
                                    line = startTemp + "," + String.valueOf(values.get(j));
                                } else {
                                    line = line + "," + String.valueOf(values.get(j));
                                }
                            }
                        } else {
                            if (i == 0) {
                                line = startTemp + ",";
                            }
                            line += String.valueOf(values.get(jListOutputParameters.getSelectedIndex())) + ",";
                        }

                        br.write(line);
                        if (sweepType != 2) {
                            br.newLine();
                        }

                        currentVoltage += inc;
                    }
                    if (sweepType == 2) {
                        br.newLine();
                    }
                }

                br.close();
            } catch (IOException ex) {
                try {
                    br.close();
                } catch (Exception e) {
                }
                JOptionPane.showMessageDialog(null, ex);
            } catch (Exception e) {
                try {
                    br.close();
                } catch (Exception e2) {
                }
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private void storeData() {
        //jProgressBar1.
        BandApp app = BandApp.getApplication();
        dataValues.clear();
        Structure structure = BandApp.getApplication().getStructure();
        Structure structure2 = structure.clone();
        
        double startVoltage = app.getExportStartVoltage();
        double stopVoltage = app.getExportStopVoltage();
        double numberOfSteps = app.getExportNumberOfSteps();
        double inc = (stopVoltage - startVoltage) / (numberOfSteps - 2); // adjusted by 2 to match Band2 output.
        double currentVoltage = startVoltage;

        double tempStepSize = 1;
        double startTemp = BandApp.getApplication().getTemperature();
        double stopTemp = startTemp;
        

        if (jComboBoxSweepChoice.getSelectedIndex() > 0) {
            startTemp = Double.valueOf(jTextFieldStartTemp.getText());
            stopTemp = Double.valueOf(jTextFieldStopTemp.getText());
            if (jComboBoxSweepChoice.getSelectedIndex() == 1) {
                startVoltage = app.getVoltage();
                stopVoltage = app.getVoltage();
                inc = 1;
                numberOfSteps = 2;
            }
            tempStepSize = (stopTemp - startTemp) / Double.valueOf(jTextFieldNumberOfStepsForTemp.getText());
            if (tempStepSize <= 0 || startTemp == stopTemp) {
                tempStepSize = 1;
            }
        }
        
        
        double voltProgressStep = 100 / numberOfSteps;
        double tempProgressStep = voltProgressStep / Double.valueOf(jTextFieldNumberOfStepsForTemp.getText());
        
        int i=0, j=0;

        for (double voltage = startVoltage; voltage <= stopVoltage; voltage += inc) {
            for (double iTemp = startTemp; iTemp <= stopTemp; iTemp += tempStepSize) {
                try {
                    structure2.Evaluate(voltage, iTemp);
                } catch (Exception e) {
                }

                List<Double> lineData = null;
                try {
                    if (jComboBoxSweepChoice.getSelectedIndex() == 1) {
                        lineData = structure2.outputParameters(iTemp);
                    } else {
                        lineData = structure2.outputParameters(voltage);
                    }

                } catch (Exception e) {
                }
                dataValues.add(lineData);
                
                jProgressBar1.setValue((int) (i*voltProgressStep+j*tempProgressStep));
                j++;
            }
            i++;
        }
        
        //jProgressBar1.setVisible(false);
    }

    @Action
    public void closeExportToolBox() {
        updateValues();

        dispose();
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
	ResourceBundle bundle = ResourceBundle.getBundle("band.resources.BandExportTool");
	jLabelStartVoltage = new JLabel();
	jTextFieldStartVoltage = new JTextField();
	jLabelStopVoltage = new JLabel();
	jTextFieldStopVoltage = new JTextField();
	jLabelNumberOfSteps = new JLabel();
	jTextFieldNumberOfSteps = new JTextField();
	jLabelStepSize = new JLabel();
	jTextFieldStepSize = new JTextField();
	jButtonOK = new JButton();
	jButtonSave = new JButton();
	jButtonPreview = new JButton();
	jPanelOutputPreview = new JPanel();
	jPanelReplace = new JPanel();
	jLabelOutputParameters = new JLabel();
	jScrollPaneOutputParameters = new JScrollPane();
	jListOutputParameters = new JList<>();
	jTextFieldStepSizeForTemp = new JTextField();
	jLabelStepSize1 = new JLabel();
	jLabelNumberOfSteps1 = new JLabel();
	jTextFieldNumberOfStepsForTemp = new JTextField();
	jTextFieldStopTemp = new JTextField();
	jLabelStopVoltage1 = new JLabel();
	jLabelStartVoltage1 = new JLabel();
	jTextFieldStartTemp = new JTextField();
	jComboBoxSweepChoice = new JComboBox<>();
	jProgressBar1 = new JProgressBar();

	//======== this ========
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	setName("BandExportTool");
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowOpened(WindowEvent e) {
		formWindowOpened(e);
	    }
	});
	var contentPane = getContentPane();

	//---- jLabelStartVoltage ----
	jLabelStartVoltage.setText(bundle.getString("jLabelStartVoltage.text"));
	jLabelStartVoltage.setName("jLabelStartVoltage");

	//---- jTextFieldStartVoltage ----
	jTextFieldStartVoltage.setName("jTextFieldStartVoltage");
	jTextFieldStartVoltage.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		jTextFieldStartVoltageKeyPressed(e);
	    }
	});

	//---- jLabelStopVoltage ----
	jLabelStopVoltage.setText(bundle.getString("jLabelStopVoltage.text"));
	jLabelStopVoltage.setName("jLabelStopVoltage");

	//---- jTextFieldStopVoltage ----
	jTextFieldStopVoltage.setName("jTextFieldStopVoltage");
	jTextFieldStopVoltage.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		jTextFieldStopVoltageKeyPressed(e);
	    }
	});

	//---- jLabelNumberOfSteps ----
	jLabelNumberOfSteps.setText(bundle.getString("jLabelNumberOfSteps.text"));
	jLabelNumberOfSteps.setName("jLabelNumberOfSteps");

	//---- jTextFieldNumberOfSteps ----
	jTextFieldNumberOfSteps.setName("jTextFieldNumberOfSteps");
	jTextFieldNumberOfSteps.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		jTextFieldNumberOfStepsKeyPressed(e);
	    }
	    @Override
	    public void keyReleased(KeyEvent e) {
		jTextFieldNumberOfStepsKeyReleased(e);
	    }
	    @Override
	    public void keyTyped(KeyEvent e) {
		jTextFieldNumberOfStepsKeyTyped(e);
	    }
	});

	//---- jLabelStepSize ----
	jLabelStepSize.setText(bundle.getString("jLabelStepSize.text"));
	jLabelStepSize.setName("jLabelStepSize");

	//---- jTextFieldStepSize ----
	jTextFieldStepSize.setEditable(false);
	jTextFieldStepSize.setName("jTextFieldStepSize");

	//---- jButtonOK ----
	jButtonOK.setText(bundle.getString("jButtonOK.text"));
	jButtonOK.setActionCommand(bundle.getString("jButtonOK.actionCommand"));
	jButtonOK.setName("jButtonOK");

	//---- jButtonSave ----
	jButtonSave.setText(bundle.getString("jButtonSave.text"));
	jButtonSave.setName("jButtonSave");

	//---- jButtonPreview ----
	jButtonPreview.setText(bundle.getString("jButtonPreview.text"));
	jButtonPreview.setName("jButtonPreview");

	//======== jPanelOutputPreview ========
	{
	    jPanelOutputPreview.setBorder(new TitledBorder("Output Preview"));
	    jPanelOutputPreview.setName("jPanelOutputPreview");

	    //======== jPanelReplace ========
	    {
		jPanelReplace.setBackground(Color.white);
		jPanelReplace.setName("jPanelReplace");

		GroupLayout jPanelReplaceLayout = new GroupLayout(jPanelReplace);
		jPanelReplace.setLayout(jPanelReplaceLayout);
		jPanelReplaceLayout.setHorizontalGroup(
		    jPanelReplaceLayout.createParallelGroup()
			.addGap(0, 636, Short.MAX_VALUE)
		);
		jPanelReplaceLayout.setVerticalGroup(
		    jPanelReplaceLayout.createParallelGroup()
			.addGap(0, 272, Short.MAX_VALUE)
		);
	    }

	    GroupLayout jPanelOutputPreviewLayout = new GroupLayout(jPanelOutputPreview);
	    jPanelOutputPreview.setLayout(jPanelOutputPreviewLayout);
	    jPanelOutputPreviewLayout.setHorizontalGroup(
		jPanelOutputPreviewLayout.createParallelGroup()
		    .addGroup(jPanelOutputPreviewLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(jPanelReplace, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addContainerGap())
	    );
	    jPanelOutputPreviewLayout.setVerticalGroup(
		jPanelOutputPreviewLayout.createParallelGroup()
		    .addGroup(jPanelOutputPreviewLayout.createSequentialGroup()
			.addComponent(jPanelReplace, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addContainerGap())
	    );
	}

	//---- jLabelOutputParameters ----
	jLabelOutputParameters.setName("jLabelOutputParameters");

	//======== jScrollPaneOutputParameters ========
	{
	    jScrollPaneOutputParameters.setName("jScrollPaneOutputParameters");

	    //---- jListOutputParameters ----
	    jListOutputParameters.setModel(new AbstractListModel<String>() {
		String[] values = {
		    "Item 1",
		    "Item 2",
		    "Item 3",
		    "Item 4",
		    "Item 5"
		};
		@Override
		public int getSize() { return values.length; }
		@Override
		public String getElementAt(int i) { return values[i]; }
	    });
	    jListOutputParameters.setEnabled(false);
	    jListOutputParameters.setName("jListOutputParameters");
	    jListOutputParameters.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
		    jListOutputParametersMouseReleased(e);
		}
	    });
	    jListOutputParameters.addListSelectionListener(e -> jListOutputParametersValueChanged(e));
	    jScrollPaneOutputParameters.setViewportView(jListOutputParameters);
	}

	//---- jTextFieldStepSizeForTemp ----
	jTextFieldStepSizeForTemp.setEditable(false);
	jTextFieldStepSizeForTemp.setName("jTextFieldStepSizeForTemp");

	//---- jLabelStepSize1 ----
	jLabelStepSize1.setText(bundle.getString("jLabelStepSize1.text"));
	jLabelStepSize1.setName("jLabelStepSize1");

	//---- jLabelNumberOfSteps1 ----
	jLabelNumberOfSteps1.setText(bundle.getString("jLabelNumberOfSteps1.text"));
	jLabelNumberOfSteps1.setName("jLabelNumberOfSteps1");

	//---- jTextFieldNumberOfStepsForTemp ----
	jTextFieldNumberOfStepsForTemp.setEnabled(false);
	jTextFieldNumberOfStepsForTemp.setName("jTextFieldNumberOfStepsForTemp");
	jTextFieldNumberOfStepsForTemp.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		jTextFieldNumberOfStepsForTempKeyReleased(e);
	    }
	});

	//---- jTextFieldStopTemp ----
	jTextFieldStopTemp.setEnabled(false);
	jTextFieldStopTemp.setName("jTextFieldStopTemp");
	jTextFieldStopTemp.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		jTextFieldStopTempKeyReleased(e);
	    }
	});

	//---- jLabelStopVoltage1 ----
	jLabelStopVoltage1.setText(bundle.getString("jLabelStopVoltage1.text"));
	jLabelStopVoltage1.setName("jLabelStopVoltage1");

	//---- jLabelStartVoltage1 ----
	jLabelStartVoltage1.setText(bundle.getString("jLabelStartVoltage1.text"));
	jLabelStartVoltage1.setName("jLabelStartVoltage1");

	//---- jTextFieldStartTemp ----
	jTextFieldStartTemp.setEnabled(false);
	jTextFieldStartTemp.setName("jTextFieldStartTemp");
	jTextFieldStartTemp.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		jTextFieldStartTempKeyReleased(e);
	    }
	});

	//---- jComboBoxSweepChoice ----
	jComboBoxSweepChoice.setModel(new DefaultComboBoxModel<>(new String[] {
	    "Only Sweep Voltage",
	    "Only Sweep Temperature",
	    "Sweep Both - One Parameter"
	}));
	jComboBoxSweepChoice.setActionCommand(bundle.getString("jComboBoxSweepChoice.actionCommand"));
	jComboBoxSweepChoice.setName("jComboBoxSweepChoice");
	jComboBoxSweepChoice.addItemListener(e -> jComboBoxSweepChoiceItemStateChanged(e));

	//---- jProgressBar1 ----
	jProgressBar1.setFocusable(false);
	jProgressBar1.setName("jProgressBar1");

	GroupLayout contentPaneLayout = new GroupLayout(contentPane);
	contentPane.setLayout(contentPaneLayout);
	contentPaneLayout.setHorizontalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup()
			.addComponent(jPanelOutputPreview, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addGroup(contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jLabelStopVoltage)
					.addComponent(jLabelStartVoltage))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jTextFieldStopVoltage, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
					.addComponent(jTextFieldStartVoltage, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addComponent(jLabelNumberOfSteps)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(jTextFieldNumberOfSteps, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addComponent(jLabelStepSize)
				    .addGap(18, 18, 18)
				    .addComponent(jTextFieldStepSize, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addComponent(jLabelStepSize1)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addComponent(jTextFieldStepSizeForTemp, GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addGroup(contentPaneLayout.createParallelGroup()
					.addComponent(jLabelNumberOfSteps1)
					.addComponent(jLabelStartVoltage1)
					.addComponent(jLabelStopVoltage1))
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				    .addGroup(contentPaneLayout.createParallelGroup()
					.addComponent(jTextFieldStartTemp, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addComponent(jTextFieldNumberOfStepsForTemp, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addComponent(jTextFieldStopTemp, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)))
				.addComponent(jComboBoxSweepChoice, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
				    .addComponent(jLabelOutputParameters)
				    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 479, Short.MAX_VALUE))
				.addComponent(jScrollPaneOutputParameters, GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)))
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addComponent(jButtonOK)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jButtonSave)
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addComponent(jButtonPreview))
			.addComponent(jProgressBar1, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE))
		    .addContainerGap())
	);
	contentPaneLayout.setVerticalGroup(
	    contentPaneLayout.createParallelGroup()
		.addGroup(contentPaneLayout.createSequentialGroup()
		    .addContainerGap()
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
			.addGroup(contentPaneLayout.createSequentialGroup()
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelStartVoltage)
				.addComponent(jLabelOutputParameters)
				.addComponent(jTextFieldStartVoltage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelStopVoltage)
				.addComponent(jTextFieldStopVoltage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelNumberOfSteps)
				.addComponent(jTextFieldNumberOfSteps, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelStepSize)
				.addComponent(jTextFieldStepSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			    .addComponent(jComboBoxSweepChoice, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addGap(18, 18, 18)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelStartVoltage1)
				.addComponent(jTextFieldStartTemp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelStopVoltage1)
				.addComponent(jTextFieldStopTemp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelNumberOfSteps1)
				.addComponent(jTextFieldNumberOfStepsForTemp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(jLabelStepSize1)
				.addComponent(jTextFieldStepSizeForTemp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
			.addComponent(jScrollPaneOutputParameters, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(jButtonOK)
			.addComponent(jButtonSave)
			.addComponent(jButtonPreview))
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(jPanelOutputPreview, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		    .addComponent(jProgressBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	);
	pack();
	setLocationRelativeTo(getOwner());

	//---- bindings ----
	bindingGroup = new BindingGroup();
	bindingGroup.addBinding(SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE,
	    this, (ELProperty) ELProperty.create("${listOutputParameters}"), jListOutputParameters));
	bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldStartVoltageKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldStartVoltageKeyPressed
        updateStepSize();
    }//GEN-LAST:event_jTextFieldStartVoltageKeyPressed

    private void jTextFieldStopVoltageKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldStopVoltageKeyPressed
        updateStepSize();
    }//GEN-LAST:event_jTextFieldStopVoltageKeyPressed

    private void jTextFieldNumberOfStepsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNumberOfStepsKeyPressed
        try {
            updateStepSize();

        } catch (NumberFormatException e) {
            jTextFieldNumberOfSteps.setText("0");
            jTextFieldNumberOfSteps.revalidate();
        }
    }//GEN-LAST:event_jTextFieldNumberOfStepsKeyPressed

    private void jListOutputParametersMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListOutputParametersMouseReleased
    }//GEN-LAST:event_jListOutputParametersMouseReleased
    int lastSelectedIndex =-1;
    String lastSelectedValue;
    private void drawChartOfOutputParameterAt(int selectedIndex, String selectedValue){
        XYSeriesCollection dataset = new XYSeriesCollection();

        double pointX = 0;
        double pointY = 0;
        if (jComboBoxSweepChoice.getSelectedIndex() != 2) {
            XYSeries series = new XYSeries(selectedValue);
            for (int i = 0; i < dataValues.size(); i++) {
                pointX = dataValues.get(i).get(0).doubleValue();
                pointY = dataValues.get(i).get(selectedIndex).doubleValue();
                series.add(pointX, pointY);
            }
            dataset.addSeries(series);
        } else {
            ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>();
            int tempSteps = BandApp.getApplication().exportNumberOfTempSteps + 1;
            for (int i = 0; i < tempSteps; i++) {
                seriesList.add(new XYSeries(selectedIndex + i));
            }

            for (int i = 0; i < dataValues.size(); i++) {
                pointX = dataValues.get(i).get(0).doubleValue();
                pointY = dataValues.get(i).get(selectedIndex).doubleValue();
                seriesList.get(i % tempSteps).add(pointX, pointY);
            }
            for (XYSeries series : seriesList) {
                dataset.addSeries(series);
            }
        }


        String xAxis;
        if (jComboBoxSweepChoice.getSelectedIndex() == 1) {
            xAxis = "Temperature (K)";
        } else {
            xAxis = "Voltage (V)";
        }
        JFreeChart chart = BandView.createFastXYLineChart(
                null, // chart title
                xAxis, // x axis label
                selectedValue, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                false, // tooltips
                false // urls
                );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setInsets(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        FastXYLineAndShapeRenderer renderer = (FastXYLineAndShapeRenderer) plot.getRenderer();

        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(3.0f));
            renderer.setSeriesPaint(i, Color.getHSBColor(1, 1F / (1 + (float) Math.log(i + 2) / 2.5F), 1F));
        }

        ValueAxis rAxis = chart.getXYPlot().getRangeAxis();
        if (rAxis instanceof NumberAxis) {
            NumberAxis rAxisN = (NumberAxis) rAxis;
            rAxisN.setNumberFormatOverride(new ScientificFormat());
        }

        if (cp == null) {
            cp = new ChartPanel(chart);
            GroupLayout layout = (GroupLayout) jPanelOutputPreview.getLayout();
            layout.replace(jPanelReplace, cp);
        } else {
            cp.setChart(chart);
        }
    }
    private void jListOutputParametersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListOutputParametersValueChanged
        int selectedIndex = jListOutputParameters.getSelectedIndex() + 1; // Remeber we left out Voltage
        String selectedValue = (String) jListOutputParameters.getSelectedValue();
        lastSelectedIndex=selectedIndex;
        lastSelectedValue=selectedValue;
        
        drawChartOfOutputParameterAt(selectedIndex, selectedValue);

        
    }//GEN-LAST:event_jListOutputParametersValueChanged

    private void jTextFieldNumberOfStepsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNumberOfStepsKeyTyped
        updateStepSize();
    }//GEN-LAST:event_jTextFieldNumberOfStepsKeyTyped

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    private void jTextFieldStartTempKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldStartTempKeyReleased
        updateStepSize();
    }//GEN-LAST:event_jTextFieldStartTempKeyReleased

    private void jTextFieldStopTempKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldStopTempKeyReleased
        updateStepSize();
    }//GEN-LAST:event_jTextFieldStopTempKeyReleased

    private void jTextFieldNumberOfStepsForTempKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNumberOfStepsForTempKeyReleased
        updateStepSize();
    }//GEN-LAST:event_jTextFieldNumberOfStepsForTempKeyReleased

private void jTextFieldNumberOfStepsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNumberOfStepsKeyReleased
    updateStepSize();
}//GEN-LAST:event_jTextFieldNumberOfStepsKeyReleased

private void jComboBoxSweepChoiceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxSweepChoiceItemStateChanged
    boolean useVoltage = jComboBoxSweepChoice.getSelectedIndex() != 1;
    boolean useTemp = jComboBoxSweepChoice.getSelectedIndex() != 0;
    jTextFieldStartVoltage.setEnabled(useVoltage);
    jTextFieldStopVoltage.setEnabled(useVoltage);
    jTextFieldNumberOfSteps.setEnabled(useVoltage);
    jTextFieldStartTemp.setEnabled(useTemp);
    jTextFieldStopTemp.setEnabled(useTemp);
    jTextFieldNumberOfStepsForTemp.setEnabled(useTemp);
    if(windowLoaded) {
        previewValues();
    }
}//GEN-LAST:event_jComboBoxSweepChoiceItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                BandExportTool dialog = new BandExportTool(new javax.swing.JFrame(), true);
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
    private JLabel jLabelStartVoltage;
    private JTextField jTextFieldStartVoltage;
    private JLabel jLabelStopVoltage;
    private JTextField jTextFieldStopVoltage;
    private JLabel jLabelNumberOfSteps;
    private JTextField jTextFieldNumberOfSteps;
    private JLabel jLabelStepSize;
    private JTextField jTextFieldStepSize;
    private JButton jButtonOK;
    private JButton jButtonSave;
    private JButton jButtonPreview;
    private JPanel jPanelOutputPreview;
    private JPanel jPanelReplace;
    private JLabel jLabelOutputParameters;
    private JScrollPane jScrollPaneOutputParameters;
    private JList<String> jListOutputParameters;
    private JTextField jTextFieldStepSizeForTemp;
    private JLabel jLabelStepSize1;
    private JLabel jLabelNumberOfSteps1;
    private JTextField jTextFieldNumberOfStepsForTemp;
    private JTextField jTextFieldStopTemp;
    private JLabel jLabelStopVoltage1;
    private JLabel jLabelStartVoltage1;
    private JTextField jTextFieldStartTemp;
    private JComboBox<String> jComboBoxSweepChoice;
    private JProgressBar jProgressBar1;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
