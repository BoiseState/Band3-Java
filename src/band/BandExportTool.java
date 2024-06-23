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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.application.Action;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
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
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabelStartVoltage = new javax.swing.JLabel();
        jTextFieldStartVoltage = new javax.swing.JTextField();
        jLabelStopVoltage = new javax.swing.JLabel();
        jTextFieldStopVoltage = new javax.swing.JTextField();
        jLabelNumberOfSteps = new javax.swing.JLabel();
        jTextFieldNumberOfSteps = new javax.swing.JTextField();
        jLabelStepSize = new javax.swing.JLabel();
        jTextFieldStepSize = new javax.swing.JTextField();
        jButtonOK = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonPreview = new javax.swing.JButton();
        jPanelOutputPreview = new javax.swing.JPanel();
        jPanelReplace = new javax.swing.JPanel();
        jLabelOutputParameters = new javax.swing.JLabel();
        jScrollPaneOutputParameters = new javax.swing.JScrollPane();
        jListOutputParameters = new javax.swing.JList();
        jTextFieldStepSizeForTemp = new javax.swing.JTextField();
        jLabelStepSize1 = new javax.swing.JLabel();
        jLabelNumberOfSteps1 = new javax.swing.JLabel();
        jTextFieldNumberOfStepsForTemp = new javax.swing.JTextField();
        jTextFieldStopTemp = new javax.swing.JTextField();
        jLabelStopVoltage1 = new javax.swing.JLabel();
        jLabelStartVoltage1 = new javax.swing.JLabel();
        jTextFieldStartTemp = new javax.swing.JTextField();
        jComboBoxSweepChoice = new javax.swing.JComboBox();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("BandExportTool"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabelStartVoltage.setFont(new Font("Tahoma", Font.PLAIN, 12));
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandExportTool.class);
        jLabelStartVoltage.setText(resourceMap.getString("jLabelStartVoltage.text")); // NOI18N
        jLabelStartVoltage.setName("jLabelStartVoltage"); // NOI18N

        jTextFieldStartVoltage.setEditable(false);
        jTextFieldStartVoltage.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldStartVoltage.setName("jTextFieldStartVoltage"); // NOI18N
        jTextFieldStartVoltage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldStartVoltageKeyPressed(evt);
            }
        });

        jLabelStopVoltage.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelStopVoltage.setText(resourceMap.getString("jLabelStopVoltage.text")); // NOI18N
        jLabelStopVoltage.setName("jLabelStopVoltage"); // NOI18N

        jTextFieldStopVoltage.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldStopVoltage.setName("jTextFieldStopVoltage"); // NOI18N
        jTextFieldStopVoltage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldStopVoltageKeyPressed(evt);
            }
        });

        jLabelNumberOfSteps.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelNumberOfSteps.setText(resourceMap.getString("jLabelNumberOfSteps.text")); // NOI18N
        jLabelNumberOfSteps.setName("jLabelNumberOfSteps"); // NOI18N

        jTextFieldNumberOfSteps.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldNumberOfSteps.setName("jTextFieldNumberOfSteps"); // NOI18N
        jTextFieldNumberOfSteps.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldNumberOfStepsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNumberOfStepsKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldNumberOfStepsKeyTyped(evt);
            }
        });

        jLabelStepSize.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelStepSize.setText(resourceMap.getString("jLabelStepSize.text")); // NOI18N
        jLabelStepSize.setName("jLabelStepSize"); // NOI18N

        jTextFieldStepSize.setEditable(false);
        jTextFieldStepSize.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldStepSize.setName("jTextFieldStepSize"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandExportTool.class, this);
        jButtonOK.setAction(actionMap.get("closeExportToolBox")); // NOI18N
        jButtonOK.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonOK.setText(resourceMap.getString("jButtonOK.text")); // NOI18N
        jButtonOK.setActionCommand(resourceMap.getString("jButtonOK.actionCommand")); // NOI18N
        jButtonOK.setName("jButtonOK"); // NOI18N

        jButtonSave.setAction(actionMap.get("saveValues")); // NOI18N
        jButtonSave.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonSave.setText(resourceMap.getString("jButtonSave.text")); // NOI18N
        jButtonSave.setName("jButtonSave"); // NOI18N

        jButtonPreview.setAction(actionMap.get("previewValues")); // NOI18N
        jButtonPreview.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jButtonPreview.setText(resourceMap.getString("jButtonPreview.text")); // NOI18N
        jButtonPreview.setName("jButtonPreview"); // NOI18N

        jPanelOutputPreview.setBorder(javax.swing.BorderFactory.createTitledBorder(null, resourceMap.getString("jPanelOutputPreview.border.title"), 0, 0, new Font("Tahoma", Font.PLAIN, 12))); // NOI18N
        jPanelOutputPreview.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jPanelOutputPreview.setName("jPanelOutputPreview"); // NOI18N

        jPanelReplace.setBackground(resourceMap.getColor("jPanelReplace.background")); // NOI18N
        jPanelReplace.setName("jPanelReplace"); // NOI18N

        javax.swing.GroupLayout jPanelReplaceLayout = new javax.swing.GroupLayout(jPanelReplace);
        jPanelReplace.setLayout(jPanelReplaceLayout);
        jPanelReplaceLayout.setHorizontalGroup(
            jPanelReplaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 650, Short.MAX_VALUE)
        );
        jPanelReplaceLayout.setVerticalGroup(
            jPanelReplaceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelOutputPreviewLayout = new javax.swing.GroupLayout(jPanelOutputPreview);
        jPanelOutputPreview.setLayout(jPanelOutputPreviewLayout);
        jPanelOutputPreviewLayout.setHorizontalGroup(
            jPanelOutputPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOutputPreviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelReplace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelOutputPreviewLayout.setVerticalGroup(
            jPanelOutputPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOutputPreviewLayout.createSequentialGroup()
                .addComponent(jPanelReplace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabelOutputParameters.setName("jLabelOutputParameters"); // NOI18N

        jScrollPaneOutputParameters.setName("jScrollPaneOutputParameters"); // NOI18N

        jListOutputParameters.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jListOutputParameters.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListOutputParameters.setEnabled(false);
        jListOutputParameters.setName("jListOutputParameters"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${listOutputParameters}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jListOutputParameters);
        bindingGroup.addBinding(jListBinding);

        jListOutputParameters.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jListOutputParametersMouseReleased(evt);
            }
        });
        jListOutputParameters.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListOutputParametersValueChanged(evt);
            }
        });
        jScrollPaneOutputParameters.setViewportView(jListOutputParameters);

        jTextFieldStepSizeForTemp.setEditable(false);
        jTextFieldStepSizeForTemp.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldStepSizeForTemp.setName("jTextFieldStepSizeForTemp"); // NOI18N

        jLabelStepSize1.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelStepSize1.setText(resourceMap.getString("jLabelStepSize1.text")); // NOI18N
        jLabelStepSize1.setName("jLabelStepSize1"); // NOI18N

        jLabelNumberOfSteps1.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelNumberOfSteps1.setText(resourceMap.getString("jLabelNumberOfSteps1.text")); // NOI18N
        jLabelNumberOfSteps1.setName("jLabelNumberOfSteps1"); // NOI18N

        jTextFieldNumberOfStepsForTemp.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldNumberOfStepsForTemp.setEnabled(false);
        jTextFieldNumberOfStepsForTemp.setName("jTextFieldNumberOfStepsForTemp"); // NOI18N
        jTextFieldNumberOfStepsForTemp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNumberOfStepsForTempKeyReleased(evt);
            }
        });

        jTextFieldStopTemp.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldStopTemp.setEnabled(false);
        jTextFieldStopTemp.setName("jTextFieldStopTemp"); // NOI18N
        jTextFieldStopTemp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldStopTempKeyReleased(evt);
            }
        });

        jLabelStopVoltage1.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelStopVoltage1.setText(resourceMap.getString("jLabelStopVoltage1.text")); // NOI18N
        jLabelStopVoltage1.setName("jLabelStopVoltage1"); // NOI18N

        jLabelStartVoltage1.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jLabelStartVoltage1.setText(resourceMap.getString("jLabelStartVoltage1.text")); // NOI18N
        jLabelStartVoltage1.setName("jLabelStartVoltage1"); // NOI18N

        jTextFieldStartTemp.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextFieldStartTemp.setEnabled(false);
        jTextFieldStartTemp.setName("jTextFieldStartTemp"); // NOI18N
        jTextFieldStartTemp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldStartTempKeyReleased(evt);
            }
        });

        jComboBoxSweepChoice.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jComboBoxSweepChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Only Sweep Voltage", "Only Sweep Temperature", "Sweep Both - One Parameter" }));
        jComboBoxSweepChoice.setActionCommand(resourceMap.getString("jComboBoxSweepChoice.actionCommand")); // NOI18N
        jComboBoxSweepChoice.setName("jComboBoxSweepChoice"); // NOI18N
        jComboBoxSweepChoice.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxSweepChoiceItemStateChanged(evt);
            }
        });

        jProgressBar1.setFocusable(false);
        jProgressBar1.setName("jProgressBar1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelOutputPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabelStopVoltage)
                                    .addComponent(jLabelStartVoltage))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldStopVoltage, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                                    .addComponent(jTextFieldStartVoltage, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelNumberOfSteps)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldNumberOfSteps, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelStepSize)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldStepSize, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelStepSize1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldStepSizeForTemp, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelNumberOfSteps1)
                                    .addComponent(jLabelStartVoltage1)
                                    .addComponent(jLabelStopVoltage1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldStartTemp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                    .addComponent(jTextFieldNumberOfStepsForTemp, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                    .addComponent(jTextFieldStopTemp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)))
                            .addComponent(jComboBoxSweepChoice, 0, 200, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelOutputParameters)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 479, Short.MAX_VALUE))
                            .addComponent(jScrollPaneOutputParameters, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPreview))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelStartVoltage)
                            .addComponent(jLabelOutputParameters)
                            .addComponent(jTextFieldStartVoltage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelStopVoltage)
                            .addComponent(jTextFieldStopVoltage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelNumberOfSteps)
                            .addComponent(jTextFieldNumberOfSteps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelStepSize)
                            .addComponent(jTextFieldStepSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxSweepChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelStartVoltage1)
                            .addComponent(jTextFieldStartTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelStopVoltage1)
                            .addComponent(jTextFieldStopTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelNumberOfSteps1)
                            .addComponent(jTextFieldNumberOfStepsForTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelStepSize1)
                            .addComponent(jTextFieldStepSizeForTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPaneOutputParameters, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonSave)
                    .addComponent(jButtonPreview))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelOutputPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();

        pack();
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
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonPreview;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JComboBox jComboBoxSweepChoice;
    private javax.swing.JLabel jLabelNumberOfSteps;
    private javax.swing.JLabel jLabelNumberOfSteps1;
    private javax.swing.JLabel jLabelOutputParameters;
    private javax.swing.JLabel jLabelStartVoltage;
    private javax.swing.JLabel jLabelStartVoltage1;
    private javax.swing.JLabel jLabelStepSize;
    private javax.swing.JLabel jLabelStepSize1;
    private javax.swing.JLabel jLabelStopVoltage;
    private javax.swing.JLabel jLabelStopVoltage1;
    private javax.swing.JList jListOutputParameters;
    private javax.swing.JPanel jPanelOutputPreview;
    private javax.swing.JPanel jPanelReplace;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPaneOutputParameters;
    private javax.swing.JTextField jTextFieldNumberOfSteps;
    private javax.swing.JTextField jTextFieldNumberOfStepsForTemp;
    private javax.swing.JTextField jTextFieldStartTemp;
    private javax.swing.JTextField jTextFieldStartVoltage;
    private javax.swing.JTextField jTextFieldStepSize;
    private javax.swing.JTextField jTextFieldStepSizeForTemp;
    private javax.swing.JTextField jTextFieldStopTemp;
    private javax.swing.JTextField jTextFieldStopVoltage;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
