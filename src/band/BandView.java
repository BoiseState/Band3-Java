/*
 * BandView.java
 */
package band;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.LayoutStyle;
import band.BandViewImportedMaterials;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import java.awt.KeyEventDispatcher;
import org.jdesktop.application.ResourceMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.FastXYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import java.awt.KeyboardFocusManager;
import java.util.concurrent.TimeUnit;
import java.util.Stack;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import org.jdesktop.application.Application;

/**
 * The application's main frame.
 */
public class BandView extends FrameView {

    public static final String EDIT_LAYER_COMMAND = "EditLayer";
    public static final String COPY_LOCATION_DATA = "CopyData";
    public static final String REMOVE_LAYER_COMMAND = "RemoveLayer";
    public static final String ADD_DIELECTRIC_BEFORE_COMMAND = "AddDielectricBefore";
    public static final String ADD_DIELECTRIC_AFTER_COMMAND = "AddDielectricAfter";
    public static final String ADD_METAL_BEFORE_COMMAND = "AddMetalBefore";
    public static final String ADD_METAL_AFTER_COMMAND = "AddMetalAfter";
    public static final String REPLACE_LAYER_COMMAND = "ReplaceLayer";
    public static final String COMPOSE_LAYERS_COMMAND = "ComposeLayers";
    public static final String UNFREEZE_ROLLOVER_DATA = "UnfreezeRollover";
    ChartPanel cp;
    XYSeriesCollection dataset;
    double xClickValue;
    double yClickValue;
    int lastClickedIndex;
    boolean continueAnimation;
    double lastPointX;
    // use this for our undos
    public Stack<Structure> structureHistory = new Stack<Structure>();
    ArrayList<JTextField> sidebarinfo;
    ArrayList<JTextField> topbarinfo;
    DecimalFormat fourDForm;
    DecimalFormat EForm;
    DecimalFormat twofourDForm;
    boolean barIsFrozen;
    long lastMouseMove;
    String rolloverText;
    boolean chartAnimating;
    
    
    public BandView(SingleFrameApplication app, Structure s) {
        super(app);
        
        // Don't do anything on close - we want to control that in our shutdown() method
        this.getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.getFrame().addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                BandApp.getApplication().shutdown();
            }
        });
        
        fourDForm = new DecimalFormat("0.000");
        EForm = new DecimalFormat("0.000E0");
        twofourDForm = new DecimalFormat("00.000");
        twofourDForm.setMaximumFractionDigits(3);   
        barIsFrozen = false;
        
        initComponents();

        dataset = new XYSeriesCollection();

        this.jTextFieldTemp.setText(String.valueOf(BandApp.getApplication().getTemperature()));
        this.jTextFieldVoltage.setText(String.valueOf(BandApp.getApplication().getVoltage()));
        
        topbarinfo = new ArrayList<JTextField>();
        topbarinfo.add(jTextFieldFlatbandVoltage);
        topbarinfo.add(jTextFieldStackCap);
        topbarinfo.add(jTextFieldEOT);
        topbarinfo.add(jTextFieldThresholdVoltage);
        
        displayStructure(s);
        
        // Adding our own KeyEventDispatcher allows us to handle ESC key presses
        // regardless of which component has focus
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                   barIsFrozen = false;
                    jTextFieldRolloverData.setForeground(Color.black);
                    jTextFieldRolloverData.setText(rolloverText); 
                }
                return false;
            }
        });

        
    }
    
    @Action
    public void editLayer() {
        Structure s = BandApp.getApplication().getStructure();
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        
        Material clicked = s.getLayer(lastClickedIndex);
        MaterialSelect materialBox;

        if (clicked instanceof Metal) {
            materialBox = new BandMaterialMetal(mainFrame, true);
        }
        else if (clicked instanceof Dielectric) {
            materialBox = new BandMaterialDielectric(mainFrame, true, clicked);
        }
        else {// Material is Semiconductor
            materialBox = new BandMaterialSemiconductor(mainFrame, true);
        }

        materialBox.setLocationRelativeTo(mainFrame);
        materialBox.setCurrentRecord(clicked);

        BandApp.getApplication().show(materialBox);

        if (materialBox.isConfirmed()) {
            structureHistory.push(s.clone());

            s.removeLayer(lastClickedIndex);
            s.addLayer(lastClickedIndex, clicked);
            BandApp.getApplication().setChanged(true);
        }

        displayStructure(s);
    }



    @Action
    public void removeLayer() {
        Structure s = BandApp.getApplication().getStructure();
        if (lastClickedIndex > 0 && lastClickedIndex < s.numLayers - 1) {
            // Push a copy onto the stack
            structureHistory.push(s.clone());
            s.removeLayer(lastClickedIndex);

            // If structure still good after removing
            if(s.isValid()) {   
                BandApp.getApplication().setChanged(true);
                displayStructure(s);
            }
            // Put it back the way it was
            else
                s = structureHistory.pop();
        }
    }

    @Action
    public void addDielectricBefore() {
        Structure s = BandApp.getApplication().getStructure();
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        BandPickDielectric pickDielectricsBox = new BandPickDielectric(mainFrame, true);
        pickDielectricsBox.setVisible(true);
        if (pickDielectricsBox.isConfirmed()) {
            structureHistory.push(s.clone());

            s.addLayer(lastClickedIndex, pickDielectricsBox.getNewMaterial());
            BandApp.getApplication().setChanged(true);
            displayStructure(s);
        }
    }

    @Action
    public void addDielectricAfter() {
        Structure s = BandApp.getApplication().getStructure();
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        BandPickDielectric pickDielectricsBox = new BandPickDielectric(mainFrame, true);
        pickDielectricsBox.setVisible(true);
        if (pickDielectricsBox.isConfirmed()) {
                structureHistory.push(s.clone());

                s.addLayer(lastClickedIndex + 1, pickDielectricsBox.getNewMaterial());
                BandApp.getApplication().setChanged(true);
                displayStructure(s);
        }
    }

    @Action
    public void addMetalBefore() {
        Structure s = BandApp.getApplication().getStructure();
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        BandPickMetal pickMetalBox = new BandPickMetal(mainFrame, true);
        pickMetalBox.setVisible(true);
        if (pickMetalBox.isConfirmed()) {
            structureHistory.push(s.clone());

            s.addLayer(lastClickedIndex, pickMetalBox.getNewMaterial());
            BandApp.getApplication().setChanged(true);
            displayStructure(s);
        }
    }

    @Action
    public void addMetalAfter() {
        Structure s = BandApp.getApplication().getStructure();
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        BandPickMetal pickMetalBox = new BandPickMetal(mainFrame, true);
        pickMetalBox.setVisible(true);
        if (pickMetalBox.isConfirmed()) {
            structureHistory.push(s.clone());

            s.addLayer(lastClickedIndex + 1, pickMetalBox.getNewMaterial());
            BandApp.getApplication().setChanged(true);
            displayStructure(s);
        }
    }

    @Action
    public void replaceLayer() {
        Structure s = BandApp.getApplication().getStructure();
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        MaterialPick pickMaterialBox;
        Material clicked = s.getLayer(lastClickedIndex);

        if(clicked instanceof Metal) {
            pickMaterialBox = new BandPickMetal(mainFrame, true);
            pickMaterialBox.setReplace(clicked.getThicknessNm());
        }
        else if(clicked instanceof Dielectric) {
            pickMaterialBox = new BandPickDielectric(mainFrame, true);
            pickMaterialBox.setReplace(clicked.getThicknessNm()); 
        }
        else { // Material is Semiconducotr
            pickMaterialBox = new BandPickSemiconductor(mainFrame, true);
            pickMaterialBox.setReplace(((Semiconductor)clicked).getDopantConcentration());
        }

        pickMaterialBox.setVisible(true);
            if (pickMaterialBox.isConfirmed()) {
                structureHistory.push(s.clone());

                s.removeLayer(lastClickedIndex);
                s.addLayer(lastClickedIndex, pickMaterialBox.getNewMaterial());
                BandApp.getApplication().setChanged(true);
                displayStructure(s);
            }
    }

    @Action
    public void composeLayers() {
    }
    
    @Action
    public void copyLocationData() {
        StringSelection data = new StringSelection(jTextFieldRolloverData.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(data,data);
    }

    public void setNewDielectric(Dielectric dielectric) {
    }

    @Action
    public void undoClick() {
        if (!structureHistory.isEmpty()) {
            Structure s = structureHistory.pop();
            BandApp.getApplication().setStructure(s);
            displayStructure(s);
        }
    }

    @Action
    public void animateChart() {
        cp.setVerticalAxisTrace(true); // fixes choppy redraw when viewing program fullscreen on slower computers
        ResourceMap resourceMap = this.getResourceMap();
        if (continueAnimation == true) {
            continueAnimation = false;
            jButtonPlay.setIcon(resourceMap.getIcon("animateChart.Action.largeIcon"));
        } else {
            SwingWorker worker = new SwingWorker<Boolean, Void>() {

                @Override
                public Boolean doInBackground() {
                    animateChartDoWork();
                    return true;
                }
            };
            continueAnimation = true;
            jButtonPlay.setIcon(resourceMap.getIcon("animateChart.Action.smallIcon"));
            worker.execute(); 
        }
    }

    boolean chartUpdating =false;
    @SuppressWarnings("static-access")
    public void animateChartDoWork() {
        chartAnimating = true;
        BandApp app = BandApp.getApplication();

        double startVoltage = app.getStartVoltage();
        double stopVoltage = app.getStopVoltage();
        double numberOfSteps = app.getNumberOfSteps();
        double inc = (stopVoltage - startVoltage) / numberOfSteps;
        boolean prevAutox = app.isxAutoScale();
        boolean prevAutoy = app.isyAutoScale();
        
        jTextFieldVoltage.setText(String.valueOf(startVoltage));
        
        double smallest_y=Double.MAX_VALUE,smallest_x=Double.MAX_VALUE,
                largest_x=Double.MIN_VALUE,largest_y=Double.MIN_VALUE;
        cp.getChart().setNotify(false);
        if (app.isSmartScale()){
            double[] voltages={startVoltage,stopVoltage};
            for (double volt: voltages){
                app.setVoltage(volt);
                if (!updateDataset(app.getStructure()))
                    return;
                for (int i=0;i<dataset.getSeriesCount();i++){
                    XYSeries curr=dataset.getSeries(i);
                    smallest_x=Math.min(curr.getMinX(), smallest_x);
                    
                    smallest_y=Math.min(curr.getMinY(),smallest_y);
                    largest_y=Math.max(curr.getMaxY(),largest_y);
                }
            }

            app.setyMin(smallest_y);
            app.setyMax(largest_y);
            app.setxAutoScale(true);
            app.setyAutoScale(false);
        }
        
        app.setVoltage(startVoltage);
        updateDataset(app.getStructure());
        cp.getChart().setNotify(true);
        updateChart();
        cp.setDoubleBuffered(true);
        cp.getChart().addProgressListener(new ChartProgressListener() {

            @Override
            public void chartProgress(ChartProgressEvent cpe) {
                switch(cpe.getType()){
                    case(ChartProgressEvent.DRAWING_STARTED):
                        chartUpdating=true;
                        break;
                    case(ChartProgressEvent.DRAWING_FINISHED):
                        chartUpdating=false;
                        break;
                    default:
                        chartUpdating=true;
                        break;
                }
            }
        });
                    
        
        for (int i = 0; i < numberOfSteps; i++) {
            if (!continueAnimation) {
                break;
            }
            long loop_time = System.nanoTime();
            cp.getChart().setNotify(false);
            updateDataset(BandApp.getApplication().getStructure());
            
            app.setVoltage(app.getVoltage() + inc);
            jTextFieldVoltage.setText(String.valueOf(Math.floor((app.getVoltage()*1000)+.5)/1000));
            try{
            modifySideToolBar();
            } catch (Exception e) {
                
            }
            cp.getChart().setNotify(true);
            refreshChart();
            
            try {
            while (chartUpdating == true || System.nanoTime()-loop_time < 50000000){ //25 fps
                TimeUnit.MILLISECONDS.sleep(1);
            }   
            } catch (Exception e) {
            } // Give everything a bit to refresh
            
        }
        
        cp.getChart().setNotify(false);
        app.setxAutoScale(prevAutox);
 
        updateDataset(BandApp.getApplication().getStructure());
        updateChart();
        cp.getChart().setNotify(true);
        app.setyAutoScale(prevAutoy);
        ResourceMap resourceMap = this.getResourceMap();
        jButtonPlay.setIcon(resourceMap.getIcon("animateChart.Action.largeIcon"));
        app.setVoltage(Math.floor((app.getVoltage()*1000)+.5)/1000);
        
        continueAnimation=false;
        chartAnimating = false;
        cp.setVerticalAxisTrace(false); // fixes choppy redraw when viewing program fullscreen on slower computers
    }

    @Action
    public void closeStructure() {
        if (!saveCheck()) {
            return;
        }

        clearStructure();
    }

    public void clearStructure() {
        Structure s = BandApp.getApplication().getStructure();
        s.clear();
        lastPointX = 0; //??
        displayStructure(s);
    }

    @Action
    public void updateTVChart() {
        BandApp app = BandApp.getApplication();
        app.setTemperature(Double.valueOf(jTextFieldTemp.getText()));
        app.setVoltage(Double.valueOf(jTextFieldVoltage.getText()));

        updateChart();
        
        // Figure out which field caused this and highlight its text
        if(jTextFieldVoltage.isFocusOwner()) {
            jTextFieldVoltage.selectAll();
        }
        if(jTextFieldTemp.isFocusOwner()) {
            jTextFieldTemp.selectAll();
        }
    }

    @Action
    public void updateChart() {
        displayStructure(BandApp.getApplication().getStructure());
    }
    
    public void refreshChart() {
        refreshStructure(BandApp.getApplication().getStructure());
    }
    
    private void refreshStructure(Structure s) {
        evaluateStructure(s);
        
        JFreeChart chart;
        
        if (jRadioButtonMenuItemPotential.isSelected()) {
            chart = createEnergyChart(s.getPotentialDataset(), "Potential", "Distance (nm)", "Potential (V)");
        }
        else if (jRadioButtonMenuItemElectricField.isSelected()) {
            chart = createEnergyChart(s.getElectricFieldDataset(), "Electric Field", "Distance (nm)", "Electric Field (MV/cm)");
        }
        else if (jRadioButtonMenuItemChargeDensity.isSelected()) {
            chart = createEnergyChart(s.getChargeDensityDataset(), "Charge Density", "Distance (nm)", "Charge Density (C/cm2)");
        }
        else {
            chart = createEnergyChart(s.getEnergyDataset(), "Energy", "Distance (nm)", "Energy (eV)");
        }
        
        cp.setChart(chart);
    }

    private void displayStructure(Structure s) {

        evaluateStructure(s);

        JFreeChart chart;
        
        if (jRadioButtonMenuItemPotential.isSelected()) {
            chart = createEnergyChart(s.getPotentialDataset(), "Potential", "Distance (nm)", "Potential (V)");
        }
        else if (jRadioButtonMenuItemElectricField.isSelected()) {
            chart = createEnergyChart(s.getElectricFieldDataset(), "Electric Field", "Distance (nm)", "Electric Field (MV/cm)");
        }
        else if (jRadioButtonMenuItemChargeDensity.isSelected()) {
            chart = createEnergyChart(s.getChargeDensityDataset(), "Charge Density", "Distance (nm)", "Charge Density (C/cm2)");
        }
        else {
            chart = createEnergyChart(s.getEnergyDataset(), "Energy", "Distance (nm)", "Energy (eV)");
        }     

        if (cp == null) {
            cp = new ChartPanel(chart);
            cp.setMouseWheelEnabled(true);
            cp.setRefreshBuffer(true);
            GroupLayout layout = (GroupLayout) mainPanel.getLayout();
            layout.replace(jPanelChart, cp);
            
        } else {
            cp.setChart(chart);
        }
        
        updateSideToolbar();
        jToolBarSide.revalidate();
        cp.setDoubleBuffered(true);
        try {
            topbarinfo.get(0).setText(fourDForm.format(s.calculateVFB()));
            topbarinfo.get(2).setText(fourDForm.format(s.calculateEotNm()));
            topbarinfo.get(1).setText(EForm.format(s.stackCap()));
            topbarinfo.get(3).setText(fourDForm.format(s.calculateVTH()));
        } catch(Exception e) {
            
        }
    }


    private void modifySideToolBar() throws Exception {
        Structure s = BandApp.getApplication().getStructure();
        
        int j=0;
        for (int i = 0; i < s.numLayers; i++) {
            if (s.getLayer(i) instanceof Metal) {
                continue;
            }
            if (s.getLayer(i) instanceof Dielectric) {
                Dielectric tempDielectric = (Dielectric)s.getLayer(i);

                sidebarinfo.get(j).setText(EForm.format(tempDielectric.getCoxFPerCm2()));
                j++;
                
                sidebarinfo.get(j).setText(fourDForm.format(tempDielectric.getVoltageDrop()));
                j++;
                
            }
            else {
                Semiconductor tempSemiconductor = (Semiconductor)s.getLayer(i);
                
                sidebarinfo.get(j).setText(EForm.format(tempSemiconductor.getCapacitanceFPerCm()));
                j++;

                sidebarinfo.get(j).setText(fourDForm.format(tempSemiconductor.getSurfacePot()));
                j++;
            }
        }      
    }
    
    private void updateSideToolbar() {
        Structure s = BandApp.getApplication().getStructure();
        
        sidebarinfo = new ArrayList<JTextField>();
        jToolBarSide.removeAll();
        for (int i = 0; i < s.numLayers; i++) {
            if (s.getLayer(i) instanceof Metal) {
                JLabel tempLabel = new JLabel(s.getLayer(i).getName());
                Font f = tempLabel.getFont();
                tempLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
                tempLabel.setMinimumSize(new Dimension(125, 20));
                tempLabel.setPreferredSize(new Dimension(125, 20));
                tempLabel.setMaximumSize(new Dimension(125, 20));
                jToolBarSide.add(tempLabel);
            }
            if (s.getLayer(i) instanceof Dielectric) {
                Dielectric tempDielectric = (Dielectric)s.getLayer(i);

                JLabel tempLabel = new JLabel(s.getLayer(i).getName());
                Font f = tempLabel.getFont();
                tempLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
                tempLabel.setMinimumSize(new Dimension(125, 20));
                tempLabel.setPreferredSize(new Dimension(125, 20));
                tempLabel.setMaximumSize(new Dimension(125, 20));
                jToolBarSide.add(tempLabel);

                tempLabel = new JLabel("<html>Cap. (F/cm<sup>2</sup>)</html>");
                tempLabel.setToolTipText("<html>Layer Capacitance (F/cm<sup>2</sup>)</html>");
                tempLabel.setMinimumSize(new Dimension(125, 25));
                tempLabel.setPreferredSize(new Dimension(125, 25));
                tempLabel.setMaximumSize(new Dimension(125, 25));
                jToolBarSide.add(tempLabel);

                JTextField tempTextField = new JTextField(EForm.format(tempDielectric.getCoxFPerCm2()));
                tempTextField.setMinimumSize(new Dimension(125, 20));
                tempTextField.setPreferredSize(new Dimension(125, 20));
                tempTextField.setMaximumSize(new Dimension(125, 20));
                tempTextField.setAlignmentX(0.0f);
                tempTextField.setEditable(false);
                sidebarinfo.add(tempTextField);
                jToolBarSide.add(tempTextField);

                tempLabel = new JLabel("Voltage Drop (V)");
                tempLabel.setMinimumSize(new Dimension(125, 20));
                tempLabel.setPreferredSize(new Dimension(125, 20));
                tempLabel.setMaximumSize(new Dimension(125, 20));
                jToolBarSide.add(tempLabel);

                tempTextField = new JTextField(fourDForm.format(tempDielectric.getVoltageDrop()));
                tempTextField.setMinimumSize(new Dimension(125, 20));
                tempTextField.setPreferredSize(new Dimension(125, 20));
                tempTextField.setMaximumSize(new Dimension(125, 20));
                tempTextField.setAlignmentX(0.0f);
                tempTextField.setEditable(false);
                sidebarinfo.add(tempTextField);
                jToolBarSide.add(tempTextField);

            } else if (s.getLayer(i) instanceof Semiconductor) {

                Semiconductor tempSemiconductor = (Semiconductor)s.getLayer(i);


                JLabel tempLabel = new JLabel(s.getLayer(i).getName());
                Font f = tempLabel.getFont();
                tempLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
                tempLabel.setMinimumSize(new Dimension(125, 20));
                tempLabel.setPreferredSize(new Dimension(125, 20));
                tempLabel.setMaximumSize(new Dimension(125, 20));
                jToolBarSide.add(tempLabel);

                tempLabel = new JLabel("<html>Cap.(F/cm<sup>2</sup>)</html>");
                tempLabel.setToolTipText("<html>Layer Capacitance (F/cm<sup>2</sup>)</html>");
                tempLabel.setMinimumSize(new Dimension(125, 25));
                tempLabel.setPreferredSize(new Dimension(125, 25));
                tempLabel.setMaximumSize(new Dimension(125, 25));
                jToolBarSide.add(tempLabel);

                JTextField tempTextField = new JTextField(EForm.format(tempSemiconductor.getCapacitanceFPerCm()));
                tempTextField.setMinimumSize(new Dimension(125, 20));
                tempTextField.setPreferredSize(new Dimension(125, 20));
                tempTextField.setMaximumSize(new Dimension(125, 20));
                tempTextField.setAlignmentX(0.0f);
                tempTextField.setEditable(false);
                sidebarinfo.add(tempTextField);
                jToolBarSide.add(tempTextField);

                tempLabel = new JLabel("Voltage Drop (V)");
                tempLabel.setMinimumSize(new Dimension(125, 20));
                tempLabel.setPreferredSize(new Dimension(125, 20));
                tempLabel.setMaximumSize(new Dimension(125, 20));
                jToolBarSide.add(tempLabel);

                tempTextField = new JTextField(fourDForm.format(tempSemiconductor.getSurfacePot()));
                tempTextField.setMinimumSize(new Dimension(125, 20));
                tempTextField.setPreferredSize(new Dimension(125, 20));
                tempTextField.setMaximumSize(new Dimension(125, 20));
                tempTextField.setAlignmentX(0.0f);
                tempTextField.setEditable(false);
                sidebarinfo.add(tempTextField);
                jToolBarSide.add(tempTextField);
            }
            jToolBarSide.addSeparator();
        }
    }

    private boolean updateDataset(Structure s) {

        if (!evaluateStructure(s))
            return false;

        if (jRadioButtonMenuItemPotential.isSelected()) {
            dataset = s.getPotentialDataset();
        }
        else if (jRadioButtonMenuItemElectricField.isSelected()) {
            dataset = s.getElectricFieldDataset();
        }
        else if (jRadioButtonMenuItemChargeDensity.isSelected()) {
            dataset = s.getChargeDensityDataset();
        } 
        else { //jRadioButtonMenuItemEnergy.isSelected()
            dataset = s.getEnergyDataset();
        }
        try {
            jTextFieldFlatbandVoltage.setText(fourDForm.format(s.calculateVFB()));
            this.jTextFieldStackCap.setText(EForm.format(s.stackCap()));
            this.jTextFieldEOT.setText(fourDForm.format(s.calculateEotNm()));
            if (s.isSemiconductorBottomLayer()) { 
                this.jTextFieldThresholdVoltage.setText(fourDForm.format(s.calculateVTH()));
            }
        } catch (Exception ex) {
        }

        //updateSideToolbar();
        //modifySideToolBar();
        //jToolBarSide.revalidate();
        return true;
    }

    // Maybe this should be rolled into something elsewhere.
    private boolean evaluateStructure(Structure s) {
        if (s.isValid()) {
            try {
                double voltage = BandApp.getApplication().getVoltage();
                double temperature = BandApp.getApplication().getTemperature();
                try{
                s.Evaluate(voltage, temperature);
                }catch (RuntimeException e){
                    if (cp == null){
                        s.Evaluate(voltage, 300);
                        BandApp.getApplication().setTemperature(300);
                    }
                    else throw e;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                return false;
            }
            return true;
        }
        else {
            return false;
        }
    }

    private JFreeChart createEnergyChart(XYDataset dataset, String title, String xAxis, String yAxis) {
        // create the chart...
        JFreeChart chart = createFastXYLineChart(
                title, // chart title
                xAxis, // x axis label
                yAxis, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
                );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setInsets(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        if (title.equals("Charge Density")) {
            NumberAxis axis2 = new NumberAxis("Semiconductor Charge Density (0.1 GigaC/cm3)");
            axis2.setTickLabelsVisible(false);
            axis2.setLabelPaint(Color.RED);
            plot.setRangeAxis(1, axis2);
            plot.setRangeAxisLocation(1, AxisLocation.TOP_OR_RIGHT);
        }

        BandApp app = BandApp.getApplication();
        Structure structure = app.getStructure();
        
        if (!app.isxAutoScale()) {
            plot.getDomainAxis().setRange(app.getxMin(), app.getxMax());
        } else {
            plot.getDomainAxis().setRange(0, structure.getThicknessNm());
        }

        if (!app.isyAutoScale()) {
            plot.getRangeAxis().setRange(app.getyMin(), app.getyMax());
        }

        // If we don't have a structure we can't render it.
        if (structure.numLayers < 1) {
            return chart;
        }

        FastXYLineAndShapeRenderer renderer = (FastXYLineAndShapeRenderer) plot.getRenderer();
        for (int i = 0; i < structure.numLayers + 2; i++) {
            if (i != structure.numLayers + 1) {
                renderer.setSeriesStroke(i, new BasicStroke(2.0f));
            }
            if (i < structure.numLayers - 1) {
                renderer.setSeriesPaint(i, structure.getLayer(i).getFillColor());
            } else {
                renderer.setSeriesPaint(i, structure.getLayer(structure.numLayers - 1).getFillColor());
            }
        }
        renderer.setSeriesPaint(structure.numLayers + 2, Color.black);

        return chart;
    }

//    private JFreeChart createOtherChart(XYDataset dataset, String title, String xAxis, String yAxis) {
//        // create the chart...
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                title, // chart title
//                xAxis, // x axis label
//                yAxis, // y axis label
//                dataset, // data
//                PlotOrientation.VERTICAL,
//                false, // include legend
//                true, // tooltips
//                false // urls
//                );
//
//        chart.setBackgroundPaint(Color.white);
//
//        XYPlot plot = (XYPlot) chart.getPlot();
//        plot.setBackgroundPaint(Color.white);
//        plot.setInsets(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
//        plot.setDomainGridlinePaint(Color.lightGray);
//        plot.setRangeGridlinePaint(Color.lightGray);
//        plot.setDomainCrosshairLockedOnData(false);
//        plot.setRangeCrosshairLockedOnData(false);
//
//        BandApp app = BandApp.getApplication();
//        if (!app.isxAutoScale()) {
//            plot.getDomainAxis().setRange(app.getxMin(), app.getxMax());
//        }
//
//        if (!app.isyAutoScale()) {
//            plot.getRangeAxis().setRange(app.getyMin(), app.getyMax());
//        }
//
//        FastXYLineAndShapeRenderer renderer = (FastXYLineAndShapeRenderer) plot.getRenderer();
//        List<Material> structure = BandApp.getApplication().getStructure();
//        for (int i = 0; i < structure.size(); i++) {
//            renderer.setSeriesStroke(i, new BasicStroke(3.0f));
//            renderer.setSeriesPaint(i, structure.get(i).getFillColor());
//        }
//
//        return chart;
//    }

    public static JFreeChart createFastXYLineChart(String title,
            String xAxisLabel,
            String yAxisLabel,
            XYDataset dataset,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls) {

        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new FastXYLineAndShapeRenderer(true, false);
        ((FastXYLineAndShapeRenderer) renderer).setDrawSeriesLineAsPath(true);
        
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        if (tooltips) {
            renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        ChartFactory.getChartTheme().apply(chart);
        return chart;
    }

    @Action
    public void copyPlot() {
        cp.doCopy();
    }

    @Action
    public void toggleShowStackParameters() {
        if (jMenuItemShowStackParameters.getText().equals("Hide Stack Parameters")) {
            jToolBarSide.setVisible(false);
            jMenuItemShowStackParameters.setText("Show Stack Parameters");
        } else {
            jToolBarSide.setVisible(true);
            jMenuItemShowStackParameters.setText("Hide Stack Parameters");
        }
    }

    @Action
    public void exportData() {
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
            try {
                String filePath = chooser.getSelectedFile().getPath();
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                }
                if (jRadioButtonMenuItemEnergy.isSelected()) {
                    exportEnergyBand(filePath);
                } else if (jRadioButtonMenuItemPotential.isSelected()) {
                    exportPotential(filePath);
                } else if (jRadioButtonMenuItemElectricField.isSelected()) {
                    exportElectricField(filePath);
                } else if (jRadioButtonMenuItemChargeDensity.isSelected()) {
                    exportChargeDensity(filePath);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

    private void exportEnergyBand(String filePath) throws Exception {
        Structure s = BandApp.getApplication().getStructure();
        XYSeriesCollection energy = s.getEnergyDataset();
        
        BufferedWriter br = null;
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

        String header = "Distance (nm)";
        for (int i=0; i<s.numLayers; i++) {
            if (s.getLayer(i) instanceof Semiconductor) {
                header += "," + s.getLayer(i).getName() + " CB (eV)," + s.getLayer(i).getName() + " VB (eV),"
                        + s.getLayer(i).getName() + " IFL (eV)," + s.getLayer(i).getName() + " FL (eV)";
            }
            else {
                header += "," + s.getLayer(i).getName() + " (eV)";
            }
        }
            
        br.write(header);
        br.newLine();
        br.flush();
        
        XYSeries series;
        String line;
        // Write the non semiconductor layers
        for(int i=0; i<s.numLayers;i++) {
            if(!(s.getLayer(i) instanceof Semiconductor)) {
                series = energy.getSeries(i);  
                for(int j=0; j<series.getItemCount();j++) {
                    line = "";
                    line += String.format("%f,",series.getX(j));
                    for(int k=0; k<i; k++) {
                        line += ",";
                    }
                    line += String.format("%f",series.getY(j));
                    for(int k = i; k<energy.getSeriesCount();k++) {
                        line += ",";
                    }
                    br.write(line);
                    br.newLine();
                }
            }
        }
        
        br.flush();
        if(s.isSemiconductorBottomLayer()) {
            XYSeries CB  = energy.getSeries(s.numLayers - 1);
            XYSeries VB  = energy.getSeries(s.numLayers);
            XYSeries IFL = energy.getSeries(s.numLayers + 1);
            XYSeries FL  = energy.getSeries(s.numLayers + 2);
            
            for(int i=0; i<CB.getItemCount(); i++) {
                line = String.format("%f",CB.getX(i));
                for(int j=0; j<s.numLayers;j++) {
                    line+=",";
                }
                line+= String.format("%f,%f,%f,%f",CB.getY(i),VB.getY(i),IFL.getY(i),FL.getY(i));
                br.write(line);
                br.newLine();
                br.flush();
            }
        }
        
        br.close();
    }

    private void exportChargeDensity(String filePath) throws Exception {
        Structure s = BandApp.getApplication().getStructure();
        XYSeriesCollection cDensity = s.getChargeDensityDataset();
        
        BufferedWriter br = null;
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

        String header = "Distance (nm),ChargeDensity (C/cm2)";
        if (s.isSemiconductorBottomLayer()) {
            header += ",Semiconductor Charge Density (0.1 GigaC/cm3)";
        }
        br.write(header);
        br.newLine();

        XYSeries series;
        for(int i=0; i<cDensity.getSeriesCount(); i++) {
            series = cDensity.getSeries(i);
            for(int j=0; j<series.getItemCount();j++) {
                if(s.isSemiconductorBottomLayer() && i == cDensity.getSeriesCount() -1) {
                    br.write(String.format("%f,,%s\n",series.getX(j),
                            EForm.format(series.getY(j))));
                }
                else {
                    br.write(String.format("%f,%s,\n",series.getX(j),
                            EForm.format(series.getY(j))));
                }
            }
        }
        
        br.close();
    }

    private void exportElectricField(String filePath) throws Exception {
        Structure s = BandApp.getApplication().getStructure();
        XYSeriesCollection eField = s.getElectricFieldDataset();
        

        BufferedWriter br = null;
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

            String header = "Distance (nm),EField (MV/cm)";
            br.write(header);
            br.newLine();
            XYSeries series;
            
            for(int i=0; i < eField.getSeriesCount(); i++) {
                series = eField.getSeries(i);
                for(int j=0; j < series.getItemCount(); j++) {
                    br.write(String.format("%f,%f\n", series.getX(j),series.getY(j)));
                }
            }
            
            br.close();
    }

    private void exportPotential(String filePath) throws Exception {
        Structure s = BandApp.getApplication().getStructure();
        XYSeriesCollection potential = s.getPotentialDataset();
        
        BufferedWriter br = null;
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

            String header = "Distance (nm),Potential (V)";
            br.write(header);
            br.newLine();
            XYSeries series;
            
            for(int i=0; i < potential.getSeriesCount(); i++) {
                series = potential.getSeries(i);
                for(int j=0; j < series.getItemCount(); j++) {
                    br.write(String.format("%f,%f\n", series.getX(j),series.getY(j)));
                }
            }
            
            br.close();  
    }

    @Action
    public void saveStructure() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".bds") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Band Diagram Structure (*.bds)";
            }
        });
        int retVal = chooser.showSaveDialog(null);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().getPath();
            if (!filename.endsWith(".bds")) {
                filename += ".bds";
            }

            File f = new File(filename);
            if (f.exists()) {
                int result = JOptionPane.showConfirmDialog(this.getFrame(), "The file exists, would you like to overwrite it?", "Overwrite file...", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            try {
                XmlIOHandler handler = new XmlIOHandler();
                handler.saveStructure(BandApp.getApplication().getStructure(), f);
                BandApp.getApplication().setChanged(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, 
                        "Could not save structure to file!", "File Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Action
    public void newStructure() {
        if (!saveCheck()) {
            return;
        }

        clearStructure();
        BandApp.getApplication().setChanged(false);
    }

    public boolean saveCheck() {
        if (BandApp.getApplication().isChanged()) {
            int result = JOptionPane.showConfirmDialog(null, "Your current structure has changed, would you like to save your changes?", null, JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveStructure();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    @Action
    public void openStructure() {
        if (!saveCheck()) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".bds") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Band Diagram Structure (*.bds)";
            }
        });
        int retVal = chooser.showOpenDialog(null);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            BandApp.getApplication().loadStructure(f);

            displayStructure(BandApp.getApplication().getStructure());
        }
    }
        

    @Action
    public void showAboutBox() {
        JDialog aboutBox = null;
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        aboutBox = new BandAboutBox(mainFrame);
        aboutBox.setLocationRelativeTo(mainFrame);
        BandApp.getApplication().show(aboutBox);
    }

    @Action
    public void showMetalsBox() {
        JDialog metalsBox = null;
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        metalsBox = new BandViewMetals(mainFrame, true);
        metalsBox.setLocationRelativeTo(mainFrame);
        BandApp.getApplication().show(metalsBox);
    }

    @Action
    public void showDielectricsBox() {
        JDialog dielectricsBox = null;
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        dielectricsBox = new BandViewDielectrics(mainFrame, true);
        dielectricsBox.setLocationRelativeTo(mainFrame);
        BandApp.getApplication().show(dielectricsBox);
    }

    @Action
    public void showSemiconductorsBox() {
        JDialog semiconductorsBox = null;
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        semiconductorsBox = new BandViewSemiconductors(mainFrame, true);
        semiconductorsBox.setLocationRelativeTo(mainFrame);
        BandApp.getApplication().show(semiconductorsBox);
    }

    @Action
    public void showWindowBox() {
        JDialog windowBox = null;
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        windowBox = new BandViewWindow(mainFrame, true);
        windowBox.setLocationRelativeTo(mainFrame);
        BandApp.getApplication().show(windowBox);

        displayStructure(BandApp.getApplication().getStructure());
    }

    @Action
    public void showMovieParametersBox() {
        JDialog movieParametersBox = null;
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        movieParametersBox = new BandMovieParameters(mainFrame, true);
        movieParametersBox.setLocationRelativeTo(mainFrame);
        BandApp.getApplication().show(movieParametersBox);
    }

    @Action
    public void showExportToolBox() {
        JDialog exportToolBox = null;
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        exportToolBox = new BandExportTool(mainFrame, true);
        exportToolBox.setLocationRelativeTo(mainFrame);
        BandApp.getApplication().show(exportToolBox);
    }

    @Action
    public void showComposeBox() {
        JDialog composeBox = null;
        JFrame mainFrame = BandApp.getApplication().getMainFrame();
        composeBox = new BandCompose(mainFrame, true);
        composeBox.setLocationRelativeTo(mainFrame);
        BandApp.getApplication().show(composeBox);

        /*
        List<Material> structure = BandApp.getApplication().getStructure();
        for (int i = 0; i < structure.size(); i++) {
        structure.get(i).setPoint(new LinkedList<EvalPoint>());
        }
         */
        displayStructure(BandApp.getApplication().getStructure());
    }

    private void jTextFieldVoltageFocusGained(FocusEvent e) {
	jTextFieldVoltage.selectAll();
    }

    private void jTextFieldTempFocusGained(FocusEvent e) {
	 jTextFieldTemp.selectAll();
    }
    
    @Action
    public void importMaterials() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".xml") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Materials Database (*.xml)";
            }
        });
        int retVal = chooser.showOpenDialog(null);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            
            List<Material> importedList;
            XmlIOHandler handler = new XmlIOHandler();
            
            try {
                // Bring in the new materials
                importedList = handler.importMaterials(f);
                
                // Check to see if there are duplicates, and remove them
                List<Material> listDielectric = BandApp.getApplication()
                        .getListDielectric();
                List<Material> listMetal = BandApp.getApplication()
                        .getListMetal();
                List<Material> listSemiconductor = BandApp.getApplication()
                        .getListSemiconductor();
                
                // We have to add materials we don't have to a new list rather
                // than remove the materials we do have from the imported list
                // because the latter will cause a ConcurrentModificationException
                List<Material> newMaterials = new LinkedList<Material>();
                
                for(Material m : importedList) {
                    if(!listDielectric.contains(m) && !listMetal.contains(m) &&
                            !listSemiconductor.contains(m)) {
                        newMaterials.add(m);
                    }
                }
                
                // Check if the list is now empty
                if(newMaterials.isEmpty()) {
                    JOptionPane.showMessageDialog(null, 
                            "Didn't find any new materials!",
                       "Material Import", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JFrame mainFrame = BandApp.getApplication().getMainFrame();
                    BandViewImportedMaterials view = new BandViewImportedMaterials(
                            mainFrame,
                            true, newMaterials);
                    view.setLocationRelativeTo(mainFrame);
                    BandApp.getApplication().show(view);
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, 
                        "Materials library appears to be corrupt",
                       "File access problem", JOptionPane.ERROR_MESSAGE);
            } catch (FileNotFoundException fe) {
                JOptionPane.showMessageDialog(null, "Couldn't find the file" 
                        + f.getPath() + System.getProperty("os.name"),
                       "File access problem", JOptionPane.ERROR_MESSAGE);
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
	ResourceBundle bundle = ResourceBundle.getBundle("band.resources.BandView");
	mainPanel = new JPanel();
	jToolBarTop = new JToolBar();
	jLabelVoltage = new JLabel();
	jTextFieldVoltage = new JTextField();
	jLabelTemp = new JLabel();
	jTextFieldTemp = new JTextField();
	jButtonPlay = new JButton();
	jLabelFlatbandVoltage = new JLabel();
	jTextFieldFlatbandVoltage = new JTextField();
	jLabelEOT = new JLabel();
	jTextFieldEOT = new JTextField();
	jLabelStackCap = new JLabel();
	jTextFieldStackCap = new JTextField();
	jLabelThresholdVoltage = new JLabel();
	jTextFieldThresholdVoltage = new JTextField();
	jToolBarSide = new JToolBar();
	/// My great code goes here
	jPanelChart = new JPanel();
	jTextFieldRolloverData = new JTextField();
	menuBar = new JMenuBar();
	var jMenuFile = new JMenu();
	jMenuItemNew = new JMenuItem();
	jMenuItemOpen = new JMenuItem();
	jMenuItemSave = new JMenuItem();
	jMenuItemClose = new JMenuItem();
	jSeparator1 = new JSeparator();
	jMenuItemImport = new JMenuItem();
	jMenuItemSaveAsDefault = new JMenuItem();
	jMenuItemExport = new JMenuItem();
	jSeparator2 = new JSeparator();
	var jMenuItemExit = new JMenuItem();
	jMenuEdit = new JMenu();
	jMenuItemUndo = new JMenuItem();
	jMenuItemCopyPlot = new JMenuItem();
	jMenuView = new JMenu();
	jRadioButtonMenuItemChargeDensity = new JRadioButtonMenuItem();
	jRadioButtonMenuItemElectricField = new JRadioButtonMenuItem();
	jRadioButtonMenuItemPotential = new JRadioButtonMenuItem();
	jRadioButtonMenuItemEnergy = new JRadioButtonMenuItem();
	jSeparator3 = new JSeparator();
	jMenuMaterials = new JMenu();
	jMenuItemMetals = new JMenuItem();
	jMenuItemDielectrics = new JMenuItem();
	jMenuItemSemiconductors = new JMenuItem();
	jMenuItemShowStackParameters = new JMenuItem();
	jSeparator4 = new JSeparator();
	jMenuItemViewWindow = new JMenuItem();
	jMenuItemMovieParameters = new JMenuItem();
	jMenuStructure = new JMenu();
	jMenuItemCompose = new JMenuItem();
	jMenuItemExportTool = new JMenuItem();
	var jMenuHelp = new JMenu();
	var jMenuItemAbout = new JMenuItem();
	buttonGroupView = new ButtonGroup();

	//======== mainPanel ========
	{
	    mainPanel.setMaximumSize(new Dimension(5000, 5000));
	    mainPanel.setMinimumSize(new Dimension(400, 200));
	    mainPanel.setName("mainPanel");
	    mainPanel.setPreferredSize(new Dimension(898, 875));
	    mainPanel.setRequestFocusEnabled(false);

	    //======== jToolBarTop ========
	    {
		jToolBarTop.setFloatable(false);
		jToolBarTop.setRollover(true);
		jToolBarTop.setMinimumSize(new Dimension(551, 100));
		jToolBarTop.setName("jToolBarTop");
		jToolBarTop.setPreferredSize(new Dimension(717, 100));

		//---- jLabelVoltage ----
		jLabelVoltage.setText(bundle.getString("jLabelVoltage.text"));
		jLabelVoltage.setName("jLabelVoltage");
		jToolBarTop.add(jLabelVoltage);

		//---- jTextFieldVoltage ----
		jTextFieldVoltage.setText(bundle.getString("jTextFieldVoltage.text"));
		jTextFieldVoltage.setMaximumSize(new Dimension(60, 25));
		jTextFieldVoltage.setMinimumSize(new Dimension(40, 25));
		jTextFieldVoltage.setName("jTextFieldVoltage");
		jTextFieldVoltage.setPreferredSize(new Dimension(60, 25));
		jTextFieldVoltage.addFocusListener(new FocusAdapter() {
		    @Override
		    public void focusGained(FocusEvent e) {
			jTextFieldVoltageFocusGained(e);
		    }
		});
		jToolBarTop.add(jTextFieldVoltage);

		//---- jSeparator5 ----
		jSeparator5.setName("jSeparator5");
		jToolBarTop.addSeparator();

		//---- jLabelTemp ----
		jLabelTemp.setText(bundle.getString("jLabelTemp.text"));
		jLabelTemp.setName("jLabelTemp");
		jToolBarTop.add(jLabelTemp);

		//---- jTextFieldTemp ----
		jTextFieldTemp.setText(bundle.getString("jTextFieldTemp.text"));
		jTextFieldTemp.setMaximumSize(new Dimension(60, 25));
		jTextFieldTemp.setMinimumSize(new Dimension(40, 25));
		jTextFieldTemp.setName("jTextFieldTemp");
		jTextFieldTemp.setPreferredSize(new Dimension(60, 25));
		jTextFieldTemp.addFocusListener(new FocusAdapter() {
		    @Override
		    public void focusGained(FocusEvent e) {
			jTextFieldTempFocusGained(e);
		    }
		});
		jToolBarTop.add(jTextFieldTemp);

		//---- jSeparator6 ----
		jSeparator6.setName("jSeparator6");
		jToolBarTop.addSeparator();

		//---- jButtonPlay ----
		jButtonPlay.setText(bundle.getString("jButtonPlay.text"));
		jButtonPlay.setToolTipText(bundle.getString("jButtonPlay.toolTipText"));
		jButtonPlay.setFocusable(false);
		jButtonPlay.setHorizontalTextPosition(SwingConstants.CENTER);
		jButtonPlay.setMaximumSize(new Dimension(31, 40));
		jButtonPlay.setMinimumSize(new Dimension(31, 40));
		jButtonPlay.setName("jButtonPlay");
		jButtonPlay.setPreferredSize(new Dimension(31, 40));
		jButtonPlay.setVerticalTextPosition(SwingConstants.BOTTOM);
		jToolBarTop.add(jButtonPlay);

		//---- jSeparator8 ----
		jSeparator8.setName("jSeparator8");
		jToolBarTop.addSeparator();

		//---- jLabelFlatbandVoltage ----
		jLabelFlatbandVoltage.setText(bundle.getString("jLabelFlatbandVoltage.text"));
		jLabelFlatbandVoltage.setMaximumSize(new Dimension(40, 25));
		jLabelFlatbandVoltage.setMinimumSize(new Dimension(40, 25));
		jLabelFlatbandVoltage.setName("jLabelFlatbandVoltage");
		jLabelFlatbandVoltage.setPreferredSize(new Dimension(40, 25));
		if (System.getProperty("mrj.version") != null) {
		    jLabelFlatbandVoltage.setMaximumSize(new java.awt.Dimension(60,25));
		    jLabelFlatbandVoltage.setPreferredSize(new java.awt.Dimension(50, 25));
		}
		jToolBarTop.add(jLabelFlatbandVoltage);

		//---- jTextFieldFlatbandVoltage ----
		jTextFieldFlatbandVoltage.setBackground(new Color(0xf0f0f0));
		jTextFieldFlatbandVoltage.setEditable(false);
		jTextFieldFlatbandVoltage.setText(bundle.getString("jTextFieldFlatbandVoltage.text"));
		jTextFieldFlatbandVoltage.setAutoscrolls(false);
		jTextFieldFlatbandVoltage.setMaximumSize(new Dimension(60, 25));
		jTextFieldFlatbandVoltage.setMinimumSize(new Dimension(40, 25));
		jTextFieldFlatbandVoltage.setName("jTextFieldFlatbandVoltage");
		jTextFieldFlatbandVoltage.setPreferredSize(new Dimension(60, 25));
		jToolBarTop.add(jTextFieldFlatbandVoltage);

		//---- jSeparator9 ----
		jSeparator9.setName("jSeparator9");
		jToolBarTop.addSeparator();

		//---- jLabelEOT ----
		jLabelEOT.setText(bundle.getString("jLabelEOT.text"));
		jLabelEOT.setMaximumSize(new Dimension(50, 25));
		jLabelEOT.setMinimumSize(new Dimension(40, 25));
		jLabelEOT.setName("jLabelEOT");
		jLabelEOT.setPreferredSize(new Dimension(40, 25));
		if (System.getProperty("mrj.version") != null) {
		    jLabelEOT.setMaximumSize(new java.awt.Dimension(60,25));
		    jLabelEOT.setPreferredSize(new java.awt.Dimension(50, 25));
		}
		jToolBarTop.add(jLabelEOT);

		//---- jTextFieldEOT ----
		jTextFieldEOT.setEditable(false);
		jTextFieldEOT.setText(bundle.getString("jTextFieldEOT.text"));
		jTextFieldEOT.setAutoscrolls(false);
		jTextFieldEOT.setMaximumSize(new Dimension(60, 25));
		jTextFieldEOT.setMinimumSize(new Dimension(40, 25));
		jTextFieldEOT.setName("jTextFieldEOT");
		jTextFieldEOT.setPreferredSize(new Dimension(60, 25));
		jToolBarTop.add(jTextFieldEOT);

		//---- jSeparator10 ----
		jSeparator10.setName("jSeparator10");
		jToolBarTop.addSeparator();

		//---- jLabelStackCap ----
		jLabelStackCap.setText(bundle.getString("jLabelStackCap.text"));
		jLabelStackCap.setMaximumSize(new Dimension(70, 25));
		jLabelStackCap.setMinimumSize(new Dimension(40, 25));
		jLabelStackCap.setName("jLabelStackCap");
		jLabelStackCap.setPreferredSize(new Dimension(40, 25));
		if (System.getProperty("mrj.version") != null) {
		    jLabelStackCap.setMaximumSize(new java.awt.Dimension(100,25));
		    jLabelStackCap.setPreferredSize(new java.awt.Dimension(90, 25));
		}
		jToolBarTop.add(jLabelStackCap);

		//---- jTextFieldStackCap ----
		jTextFieldStackCap.setEditable(false);
		jTextFieldStackCap.setText(bundle.getString("jTextFieldStackCap.text"));
		jTextFieldStackCap.setAutoscrolls(false);
		jTextFieldStackCap.setMaximumSize(new Dimension(80, 25));
		jTextFieldStackCap.setMinimumSize(new Dimension(50, 25));
		jTextFieldStackCap.setName("jTextFieldStackCap");
		jTextFieldStackCap.setPreferredSize(new Dimension(70, 25));
		jToolBarTop.add(jTextFieldStackCap);

		//---- jSeparator11 ----
		jSeparator11.setName("jSeparator11");
		jToolBarTop.addSeparator();

		//---- jLabelThresholdVoltage ----
		jLabelThresholdVoltage.setText(bundle.getString("jLabelThresholdVoltage.text"));
		jLabelThresholdVoltage.setMaximumSize(new Dimension(40, 25));
		jLabelThresholdVoltage.setMinimumSize(new Dimension(40, 25));
		jLabelThresholdVoltage.setName("jLabelThresholdVoltage");
		jLabelThresholdVoltage.setPreferredSize(new Dimension(40, 25));
		jToolBarTop.add(jLabelThresholdVoltage);

		//---- jTextFieldThresholdVoltage ----
		jTextFieldThresholdVoltage.setEditable(false);
		jTextFieldThresholdVoltage.setText(bundle.getString("jTextFieldThresholdVoltage.text"));
		jTextFieldThresholdVoltage.setAutoscrolls(false);
		jTextFieldThresholdVoltage.setMaximumSize(new Dimension(60, 25));
		jTextFieldThresholdVoltage.setMinimumSize(new Dimension(40, 25));
		jTextFieldThresholdVoltage.setName("jTextFieldThresholdVoltage");
		jTextFieldThresholdVoltage.setPreferredSize(new Dimension(60, 25));
		jToolBarTop.add(jTextFieldThresholdVoltage);
	    }

	    //======== jToolBarSide ========
	    {
		jToolBarSide.setFloatable(false);
		jToolBarSide.setOrientation(SwingConstants.VERTICAL);
		jToolBarSide.setRollover(true);
		jToolBarSide.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		jToolBarSide.setMaximumSize(new Dimension(125, 125));
		jToolBarSide.setMinimumSize(new Dimension(125, 125));
		jToolBarSide.setName("jToolBarSide");
		jToolBarSide.setPreferredSize(new Dimension(2, 175));
	    }

	    //======== jPanelChart ========
	    {
		jPanelChart.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		jPanelChart.setName("jPanelChart");
		jPanelChart.setPreferredSize(new Dimension(640, 480));

		GroupLayout jPanelChartLayout = new GroupLayout(jPanelChart);
		jPanelChart.setLayout(jPanelChartLayout);
		jPanelChartLayout.setHorizontalGroup(
		    jPanelChartLayout.createParallelGroup()
			.addGap(0, 680, Short.MAX_VALUE)
		);
		jPanelChartLayout.setVerticalGroup(
		    jPanelChartLayout.createParallelGroup()
			.addGap(0, 550, Short.MAX_VALUE)
		);
	    }

	    //---- jTextFieldRolloverData ----
	    jTextFieldRolloverData.setEditable(false);
	    jTextFieldRolloverData.setBorder(null);
	    jTextFieldRolloverData.setName("jTextFieldRolloverData");

	    GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
	    mainPanel.setLayout(mainPanelLayout);
	    mainPanelLayout.setHorizontalGroup(
		mainPanelLayout.createParallelGroup()
		    .addComponent(jToolBarTop, GroupLayout.DEFAULT_SIZE, 821, Short.MAX_VALUE)
		    .addGroup(mainPanelLayout.createSequentialGroup()
			.addComponent(jToolBarSide, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(mainPanelLayout.createParallelGroup()
			    .addComponent(jTextFieldRolloverData, GroupLayout.PREFERRED_SIZE, 680, GroupLayout.PREFERRED_SIZE)
			    .addComponent(jPanelChart, GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))
			.addContainerGap())
	    );
	    mainPanelLayout.setVerticalGroup(
		mainPanelLayout.createParallelGroup()
		    .addGroup(mainPanelLayout.createSequentialGroup()
			.addComponent(jToolBarTop, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(mainPanelLayout.createParallelGroup()
			    .addGroup(mainPanelLayout.createSequentialGroup()
				.addComponent(jToolBarSide, GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
				.addGap(35, 35, 35))
			    .addGroup(mainPanelLayout.createSequentialGroup()
				.addComponent(jPanelChart, GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
			.addComponent(jTextFieldRolloverData, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
			.addContainerGap())
	    );
	}

	//======== menuBar ========
	{
	    menuBar.setName("menuBar");

	    //======== jMenuFile ========
	    {
		jMenuFile.setText(bundle.getString("jMenuFile.text"));
		jMenuFile.setName("jMenuFile");

		//---- jMenuItemNew ----
		jMenuItemNew.setName("jMenuItemNew");
		jMenuFile.add(jMenuItemNew);

		//---- jMenuItemOpen ----
		jMenuItemOpen.setName("jMenuItemOpen");
		jMenuFile.add(jMenuItemOpen);

		//---- jMenuItemSave ----
		jMenuItemSave.setName("jMenuItemSave");
		jMenuFile.add(jMenuItemSave);

		//---- jMenuItemClose ----
		jMenuItemClose.setText(bundle.getString("jMenuItemClose.text"));
		jMenuItemClose.setName("jMenuItemClose");
		jMenuFile.add(jMenuItemClose);

		//---- jSeparator1 ----
		jSeparator1.setName("jSeparator1");
		jMenuFile.add(jSeparator1);

		//---- jMenuItemImport ----
		jMenuItemImport.setText(bundle.getString("jMenuItemImport.text"));
		jMenuItemImport.setName("jMenuItemImport");
		jMenuFile.add(jMenuItemImport);

		//---- jSeparator7 ----
		jSeparator7.setName("jSeparator7");
		jMenuFile.addSeparator();

		//---- jMenuItemSaveAsDefault ----
		jMenuItemSaveAsDefault.setText(bundle.getString("jMenuItemSaveAsDefault.text"));
		jMenuItemSaveAsDefault.setName("jMenuItemSaveAsDefault");
		jMenuFile.add(jMenuItemSaveAsDefault);

		//---- jMenuItemExport ----
		jMenuItemExport.setName("jMenuItemExport");
		jMenuFile.add(jMenuItemExport);

		//---- jSeparator2 ----
		jSeparator2.setName("jSeparator2");
		if (System.getProperty("mrj.version") == null)
		jMenuFile.add(jSeparator2);

		//---- jMenuItemExit ----
		jMenuItemExit.setName("jMenuItemExit");
		if (System.getProperty("mrj.version") == null)
		jMenuFile.add(jMenuItemExit);
	    }
	    menuBar.add(jMenuFile);

	    //======== jMenuEdit ========
	    {
		jMenuEdit.setText(bundle.getString("jMenuEdit.text"));
		jMenuEdit.setName("jMenuEdit");

		//---- jMenuItemUndo ----
		jMenuItemUndo.setText(bundle.getString("jMenuItemUndo.text"));
		jMenuItemUndo.setName("jMenuItemUndo");
		jMenuEdit.add(jMenuItemUndo);

		//---- jMenuItemCopyPlot ----
		jMenuItemCopyPlot.setText(bundle.getString("jMenuItemCopyPlot.text"));
		jMenuItemCopyPlot.setName("jMenuItemCopyPlot");
		jMenuEdit.add(jMenuItemCopyPlot);
	    }
	    menuBar.add(jMenuEdit);

	    //======== jMenuView ========
	    {
		jMenuView.setText(bundle.getString("jMenuView.text"));
		jMenuView.setName("jMenuView");

		//---- jRadioButtonMenuItemChargeDensity ----
		jRadioButtonMenuItemChargeDensity.setText(bundle.getString("jRadioButtonMenuItemChargeDensity.text"));
		jRadioButtonMenuItemChargeDensity.setName("jRadioButtonMenuItemChargeDensity");
		jMenuView.add(jRadioButtonMenuItemChargeDensity);

		//---- jRadioButtonMenuItemElectricField ----
		jRadioButtonMenuItemElectricField.setText(bundle.getString("jRadioButtonMenuItemElectricField.text"));
		jRadioButtonMenuItemElectricField.setName("jRadioButtonMenuItemElectricField");
		jMenuView.add(jRadioButtonMenuItemElectricField);

		//---- jRadioButtonMenuItemPotential ----
		jRadioButtonMenuItemPotential.setText(bundle.getString("jRadioButtonMenuItemPotential.text"));
		jRadioButtonMenuItemPotential.setName("jRadioButtonMenuItemPotential");
		jMenuView.add(jRadioButtonMenuItemPotential);

		//---- jRadioButtonMenuItemEnergy ----
		jRadioButtonMenuItemEnergy.setSelected(true);
		jRadioButtonMenuItemEnergy.setText(bundle.getString("jRadioButtonMenuItemEnergy.text"));
		jRadioButtonMenuItemEnergy.setName("jRadioButtonMenuItemEnergy");
		jMenuView.add(jRadioButtonMenuItemEnergy);

		//---- jSeparator3 ----
		jSeparator3.setName("jSeparator3");
		jMenuView.add(jSeparator3);

		//======== jMenuMaterials ========
		{
		    jMenuMaterials.setText(bundle.getString("jMenuMaterials.text"));
		    jMenuMaterials.setName("jMenuMaterials");

		    //---- jMenuItemMetals ----
		    jMenuItemMetals.setName("jMenuItemMetals");
		    jMenuMaterials.add(jMenuItemMetals);

		    //---- jMenuItemDielectrics ----
		    jMenuItemDielectrics.setName("jMenuItemDielectrics");
		    jMenuMaterials.add(jMenuItemDielectrics);

		    //---- jMenuItemSemiconductors ----
		    jMenuItemSemiconductors.setName("jMenuItemSemiconductors");
		    jMenuMaterials.add(jMenuItemSemiconductors);
		}
		jMenuView.add(jMenuMaterials);

		//---- jMenuItemShowStackParameters ----
		jMenuItemShowStackParameters.setText(bundle.getString("jMenuItemShowStackParameters.text"));
		jMenuItemShowStackParameters.setName("jMenuItemShowStackParameters");
		jMenuView.add(jMenuItemShowStackParameters);

		//---- jSeparator4 ----
		jSeparator4.setName("jSeparator4");
		jMenuView.add(jSeparator4);

		//---- jMenuItemViewWindow ----
		jMenuItemViewWindow.setName("jMenuItemViewWindow");
		jMenuView.add(jMenuItemViewWindow);

		//---- jMenuItemMovieParameters ----
		jMenuItemMovieParameters.setName("jMenuItemMovieParameters");
		jMenuView.add(jMenuItemMovieParameters);
	    }
	    menuBar.add(jMenuView);

	    //======== jMenuStructure ========
	    {
		jMenuStructure.setText(bundle.getString("jMenuStructure.text"));
		jMenuStructure.setName("jMenuStructure");

		//---- jMenuItemCompose ----
		jMenuItemCompose.setName("jMenuItemCompose");
		jMenuStructure.add(jMenuItemCompose);

		//---- jMenuItemExportTool ----
		jMenuItemExportTool.setName("jMenuItemExportTool");
		jMenuStructure.add(jMenuItemExportTool);
	    }
	    menuBar.add(jMenuStructure);

	    //======== jMenuHelp ========
	    {
		jMenuHelp.setText(bundle.getString("jMenuHelp.text"));
		jMenuHelp.setName("jMenuHelp");

		//---- jMenuItemAbout ----
		jMenuItemAbout.setName("jMenuItemAbout");
		if (System.getProperty("mrj.version") == null) {
		jMenuHelp.add(jMenuItemAbout);
	    }
	    menuBar.add(jMenuHelp);
	}

	//---- buttonGroupView ----
	buttonGroupView.add(jMenuView);
	buttonGroupView.add(jRadioButtonMenuItemChargeDensity);
	buttonGroupView.add(jRadioButtonMenuItemElectricField);
	buttonGroupView.add(jRadioButtonMenuItemPotential);
	buttonGroupView.add(jRadioButtonMenuItemEnergy);
    }// </editor-fold>//GEN-END:initComponents
    }
   private void mouseClicked(ChartMouseEvent evt) {
        // Material double-click
        if (evt.getTrigger().getClickCount() > 1 && evt.getTrigger().getButton() == MouseEvent.BUTTON1) {
            editLayer();
        }
        else if (evt.getTrigger().getButton() == MouseEvent.BUTTON1) {
        // Material single click, freeze the parameter bar
            barIsFrozen = true;
            jTextFieldRolloverData.setForeground(Color.BLUE);
            jTextFieldRolloverData.setText(rolloverText + "   press ESC to unlock");
        }
   }
   private void mouseMoved(ChartMouseEvent evt) {
       if(!chartAnimating) {
        Structure s = BandApp.getApplication().getStructure();
        // Get new coordinates
        xClickValue = cp.getChart().getXYPlot().getDomainAxis().java2DToValue(evt.getTrigger().getX(), cp.getScreenDataArea(), RectangleEdge.BOTTOM);
        yClickValue = cp.getChart().getXYPlot().getRangeAxis().java2DToValue(evt.getTrigger().getY(), cp.getScreenDataArea(), RectangleEdge.LEFT);               
         
        try {
            
            
            Material clickedLayer = s.getLayerAtThickness(xClickValue);
            if(!barIsFrozen) {
                rolloverText = "Location: " + twofourDForm.format(xClickValue) 
                            + " (nm)   Material: " + clickedLayer.getName()
                            + "   E-Field: " + fourDForm.format(s.getElectricFieldAtLocation(xClickValue))
                            + " (MV/cm)   Potential: " + fourDForm.format(s.getPotentialAtLocation(xClickValue)) + " (V)";

                jTextFieldRolloverData.setForeground(Color.black);
                jTextFieldRolloverData.setText(rolloverText);
            
            }
           // if(!cp.getPopupMenu().isVisible()) {
                lastClickedIndex = s.indexOf(s.getLayerAtThickness(xClickValue));
                // Reset all menu items to enabled
                cp.getPopupMenu().getComponent(2).setEnabled(true);
                cp.getPopupMenu().getComponent(4).setEnabled(true);
                cp.getPopupMenu().getComponent(5).setEnabled(true);
                cp.getPopupMenu().getComponent(6).setEnabled(true);
                cp.getPopupMenu().getComponent(7).setEnabled(true);

                if(clickedLayer instanceof Metal) {
                    // Can't add a metal before or after a metal
                    cp.getPopupMenu().getComponent(6).setEnabled(false);
                    cp.getPopupMenu().getComponent(7).setEnabled(false);
                    if(s.getLayerAbove(clickedLayer) == null) {
                        // Can't add a dielectric before the top metal layer
                        cp.getPopupMenu().getComponent(4).setEnabled(false);
                        // Can't remove the top metal either
                        cp.getPopupMenu().getComponent(2).setEnabled(false);
                    }
                    if(s.getLayerBelow(clickedLayer) == null) {
                        // Can't add a dielectric after the bottom metal layer
                        cp.getPopupMenu().getComponent(5).setEnabled(false);
                        // Can't remove the bottom layer either
                        cp.getPopupMenu().getComponent(2).setEnabled(false);
                    }
                }

                if(clickedLayer instanceof Semiconductor) {
                    // Semiconductor always at the end so you can't add after it
                    cp.getPopupMenu().getComponent(5).setEnabled(false);
                    cp.getPopupMenu().getComponent(7).setEnabled(false);
                    cp.getPopupMenu().getComponent(2).setEnabled(false);
                    if(s.getLayerAbove(clickedLayer) instanceof Metal) {
                        // Can't add a metal before Semiconductor if there's already metal there
                        cp.getPopupMenu().getComponent(6).setEnabled(false);
                    } 
                }

                if(clickedLayer instanceof Dielectric) {
                    if(s.getLayerAbove(clickedLayer) instanceof Metal) {
                        // Can't add a metal before if there's already metal there
                        cp.getPopupMenu().getComponent(6).setEnabled(false);
                    }
                    if(s.getLayerBelow(clickedLayer) instanceof Metal) {
                        // Can't add a metal after if there's already metal there
                        cp.getPopupMenu().getComponent(7).setEnabled(false);
                    }
                    if(s.getLayerAbove(clickedLayer) instanceof Metal && s.getLayerBelow(clickedLayer) instanceof Metal) {
                        cp.getPopupMenu().getComponent(2).setEnabled(false);
                    }
                    if(s.getLayerAbove(clickedLayer) instanceof Metal && s.getLayerBelow(clickedLayer) instanceof Semiconductor) {
                        cp.getPopupMenu().getComponent(2).setEnabled(false);
                    }
                }
            //}
        } catch (BadLocationException e) {
            // Just don't do anything because you're off the chart
        }
       }
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Generated using JFormDesigner Educational license - Madi Thompson (madithompson)
    private JPanel mainPanel;
    private JToolBar jToolBarTop;
    private JLabel jLabelVoltage;
    private JTextField jTextFieldVoltage;
    private JLabel jLabelTemp;
    private JTextField jTextFieldTemp;
    private JButton jButtonPlay;
    private JLabel jLabelFlatbandVoltage;
    private JTextField jTextFieldFlatbandVoltage;
    private JLabel jLabelEOT;
    private JTextField jTextFieldEOT;
    private JLabel jLabelStackCap;
    private JTextField jTextFieldStackCap;
    private JLabel jLabelThresholdVoltage;
    private JTextField jTextFieldThresholdVoltage;
    private JToolBar jToolBarSide;
    private JPanel jPanelChart;
    private JTextField jTextFieldRolloverData;
    private JMenuBar menuBar;
    private JMenuItem jMenuItemNew;
    private JMenuItem jMenuItemOpen;
    private JMenuItem jMenuItemSave;
    private JMenuItem jMenuItemClose;
    private JSeparator jSeparator1;
    private JMenuItem jMenuItemImport;
    private JMenuItem jMenuItemSaveAsDefault;
    private JMenuItem jMenuItemExport;
    private JSeparator jSeparator2;
    private JMenu jMenuEdit;
    private JMenuItem jMenuItemUndo;
    private JMenuItem jMenuItemCopyPlot;
    private JMenu jMenuView;
    private JRadioButtonMenuItem jRadioButtonMenuItemChargeDensity;
    private JRadioButtonMenuItem jRadioButtonMenuItemElectricField;
    private JRadioButtonMenuItem jRadioButtonMenuItemPotential;
    private JRadioButtonMenuItem jRadioButtonMenuItemEnergy;
    private JSeparator jSeparator3;
    private JMenu jMenuMaterials;
    private JMenuItem jMenuItemMetals;
    private JMenuItem jMenuItemDielectrics;
    private JMenuItem jMenuItemSemiconductors;
    private JMenuItem jMenuItemShowStackParameters;
    private JSeparator jSeparator4;
    private JMenuItem jMenuItemViewWindow;
    private JMenuItem jMenuItemMovieParameters;
    private JMenu jMenuStructure;
    private JMenuItem jMenuItemCompose;
    private JMenuItem jMenuItemExportTool;
    private ButtonGroup buttonGroupView;
    // End of variables declaration//GEN-END:variables
    private JSeparator jSeparator5;
    private JSeparator jSeparator6;
    private JSeparator jSeparator7;
    private JSeparator jSeparator8;
    private JSeparator jSeparator9;
    private JSeparator jSeparator10;
    private JSeparator jSeparator11;
    
    private class MacOSAboutHandler extends Application {

        public MacOSAboutHandler() {
//            setAboutHandler(new AboutBoxHandler());
        }

//        class AboutBoxHandler implements AboutHandler {
//            @Override
//            public void handleAbout(AppEvent.AboutEvent event) {
//                showAboutBox();
//            }
//        }

        @Override
        protected void startup() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
    
    @Action
    public void help() {
    }
}