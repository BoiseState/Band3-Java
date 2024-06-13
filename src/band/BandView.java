/*
 * BandView.java
 */
package band;

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
import com.apple.eawt.*;
import java.awt.KeyboardFocusManager;
import java.util.concurrent.TimeUnit;
import java.util.Stack;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;


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
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jToolBarTop = new javax.swing.JToolBar();
        jLabelVoltage = new javax.swing.JLabel();
        jTextFieldVoltage = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jLabelTemp = new javax.swing.JLabel();
        jTextFieldTemp = new javax.swing.JTextField();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButtonPlay = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jLabelFlatbandVoltage = new javax.swing.JLabel();
        jTextFieldFlatbandVoltage = new javax.swing.JTextField();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jLabelEOT = new javax.swing.JLabel();
        jTextFieldEOT = new javax.swing.JTextField();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jLabelStackCap = new javax.swing.JLabel();
        jTextFieldStackCap = new javax.swing.JTextField();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        jLabelThresholdVoltage = new javax.swing.JLabel();
        jTextFieldThresholdVoltage = new javax.swing.JTextField();
        jToolBarSide = new javax.swing.JToolBar();
        /// My great code goes here
        jPanelChart = new javax.swing.JPanel();
        jTextFieldRolloverData = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemClose = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemImport = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSaveAsDefault = new javax.swing.JMenuItem();
        jMenuItemExport = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemUndo = new javax.swing.JMenuItem();
        jMenuItemCopyPlot = new javax.swing.JMenuItem();
        jMenuView = new javax.swing.JMenu();
        jRadioButtonMenuItemChargeDensity = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemElectricField = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemPotential = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemEnergy = new javax.swing.JRadioButtonMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuMaterials = new javax.swing.JMenu();
        jMenuItemMetals = new javax.swing.JMenuItem();
        jMenuItemDielectrics = new javax.swing.JMenuItem();
        jMenuItemSemiconductors = new javax.swing.JMenuItem();
        jMenuItemShowStackParameters = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItemViewWindow = new javax.swing.JMenuItem();
        jMenuItemMovieParameters = new javax.swing.JMenuItem();
        jMenuStructure = new javax.swing.JMenu();
        jMenuItemCompose = new javax.swing.JMenuItem();
        jMenuItemExportTool = new javax.swing.JMenuItem();
        javax.swing.JMenu jMenuHelp = new javax.swing.JMenu();
        javax.swing.JMenuItem jMenuItemAbout = new javax.swing.JMenuItem();
        buttonGroupView = new javax.swing.ButtonGroup();

        mainPanel.setMaximumSize(new java.awt.Dimension(5000, 5000));
        mainPanel.setMinimumSize(new java.awt.Dimension(400, 200));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(898, 875));
        mainPanel.setRequestFocusEnabled(false);

        jToolBarTop.setFloatable(false);
        jToolBarTop.setRollover(true);
        jToolBarTop.setMinimumSize(new java.awt.Dimension(551, 100));
        jToolBarTop.setName("jToolBarTop"); // NOI18N
        jToolBarTop.setPreferredSize(new java.awt.Dimension(717, 100));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getResourceMap(BandView.class);
        jLabelVoltage.setText(resourceMap.getString("jLabelVoltage.text")); // NOI18N
        jLabelVoltage.setAlignmentX(0.5F);
        jLabelVoltage.setName("jLabelVoltage"); // NOI18N
        jToolBarTop.add(jLabelVoltage);

        jTextFieldVoltage.setText(resourceMap.getString("jTextFieldVoltage.text")); // NOI18N
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(band.BandApp.class).getContext().getActionMap(BandView.class, this);
        jTextFieldVoltage.setAction(actionMap.get("updateTVChart")); // NOI18N
        jTextFieldVoltage.setMaximumSize(new java.awt.Dimension(60, 25));
        jTextFieldVoltage.setMinimumSize(new java.awt.Dimension(40, 25));
        jTextFieldVoltage.setName("jTextFieldVoltage"); // NOI18N
        jTextFieldVoltage.setPreferredSize(new java.awt.Dimension(60, 25));
        jTextFieldVoltage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldVoltageFocusGained(evt);
            }
        });
        jToolBarTop.add(jTextFieldVoltage);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jToolBarTop.add(jSeparator5);

        jLabelTemp.setFont(jLabelTemp.getFont());
        jLabelTemp.setText(resourceMap.getString("jLabelTemp.text")); // NOI18N
        jLabelTemp.setAlignmentX(0.5F);
        jLabelTemp.setName("jLabelTemp"); // NOI18N
        jToolBarTop.add(jLabelTemp);

        jTextFieldTemp.setText(resourceMap.getString("jTextFieldTemp.text")); // NOI18N
        jTextFieldTemp.setAction(actionMap.get("updateTVChart")); // NOI18N
        jTextFieldTemp.setMaximumSize(new java.awt.Dimension(60, 25));
        jTextFieldTemp.setMinimumSize(new java.awt.Dimension(40, 25));
        jTextFieldTemp.setName("jTextFieldTemp"); // NOI18N
        jTextFieldTemp.setPreferredSize(new java.awt.Dimension(60, 25));
        jTextFieldTemp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldTempFocusGained(evt);
            }
        });
        jToolBarTop.add(jTextFieldTemp);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBarTop.add(jSeparator6);

        jButtonPlay.setAction(actionMap.get("animateChart")); // NOI18N
        jButtonPlay.setText(resourceMap.getString("jButtonPlay.text")); // NOI18N
        jButtonPlay.setToolTipText(resourceMap.getString("jButtonPlay.toolTipText")); // NOI18N
        jButtonPlay.setFocusable(false);
        jButtonPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPlay.setMaximumSize(new java.awt.Dimension(31, 40));
        jButtonPlay.setMinimumSize(new java.awt.Dimension(31, 40));
        jButtonPlay.setName("jButtonPlay"); // NOI18N
        jButtonPlay.setPreferredSize(new java.awt.Dimension(31, 40));
        jButtonPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarTop.add(jButtonPlay);

        jSeparator8.setName("jSeparator8"); // NOI18N
        jToolBarTop.add(jSeparator8);

        jLabelFlatbandVoltage.setText(resourceMap.getString("jLabelFlatbandVoltage.text")); // NOI18N
        jLabelFlatbandVoltage.setAlignmentX(0.5F);
        jLabelFlatbandVoltage.setMaximumSize(new Dimension(47,30));
        jLabelFlatbandVoltage.setMinimumSize(new Dimension(47,30));
        jLabelFlatbandVoltage.setName("jLabelFlatbandVoltage"); // NOI18N
        jLabelFlatbandVoltage.setPreferredSize(new Dimension(47,30));
        if (System.getProperty("mrj.version") != null) {
            jLabelFlatbandVoltage.setMaximumSize(new java.awt.Dimension(60,25));
            jLabelFlatbandVoltage.setPreferredSize(new java.awt.Dimension(50, 25));
        }
        jToolBarTop.add(jLabelFlatbandVoltage);

        jTextFieldFlatbandVoltage.setBackground(resourceMap.getColor("jTextFieldFlatbandVoltage.background")); // NOI18N
        jTextFieldFlatbandVoltage.setEditable(false);
        jTextFieldFlatbandVoltage.setText(resourceMap.getString("jTextFieldFlatbandVoltage.text")); // NOI18N
        jTextFieldFlatbandVoltage.setAutoscrolls(false);
        jTextFieldFlatbandVoltage.setMaximumSize(new java.awt.Dimension(60, 25));
        jTextFieldFlatbandVoltage.setMinimumSize(new java.awt.Dimension(40, 25));
        jTextFieldFlatbandVoltage.setName("jTextFieldFlatbandVoltage"); // NOI18N
        jTextFieldFlatbandVoltage.setPreferredSize(new java.awt.Dimension(60, 25));
        jToolBarTop.add(jTextFieldFlatbandVoltage);

        jSeparator9.setName("jSeparator9"); // NOI18N
        jToolBarTop.add(jSeparator9);

        jLabelEOT.setText(resourceMap.getString("jLabelEOT.text")); // NOI18N
        jLabelEOT.setAlignmentX(0.5F);
        jLabelEOT.setMaximumSize(new java.awt.Dimension(60, 30));
        jLabelEOT.setMinimumSize(new java.awt.Dimension(60, 30));
        jLabelEOT.setName("jLabelEOT"); // NOI18N
        jLabelEOT.setPreferredSize(new java.awt.Dimension(60, 30));
        jLabelEOT.setRequestFocusEnabled(false);
        if (System.getProperty("mrj.version") != null) {
            jLabelEOT.setMaximumSize(new java.awt.Dimension(60,25));
            jLabelEOT.setPreferredSize(new java.awt.Dimension(50, 25));
        }
        jToolBarTop.add(jLabelEOT);

        jTextFieldEOT.setEditable(false);
        jTextFieldEOT.setText(resourceMap.getString("jTextFieldEOT.text")); // NOI18N
        jTextFieldEOT.setAutoscrolls(false);
        jTextFieldEOT.setMaximumSize(new java.awt.Dimension(60, 25));
        jTextFieldEOT.setMinimumSize(new java.awt.Dimension(40, 25));
        jTextFieldEOT.setName("jTextFieldEOT"); // NOI18N
        jTextFieldEOT.setPreferredSize(new java.awt.Dimension(60, 25));
        jToolBarTop.add(jTextFieldEOT);

        jSeparator10.setName("jSeparator10"); // NOI18N
        jToolBarTop.add(jSeparator10);

        jLabelStackCap.setText(resourceMap.getString("jLabelStackCap.text")); // NOI18N
        jLabelStackCap.setAlignmentX(0.5F);
        jLabelStackCap.setMaximumSize(new java.awt.Dimension(88, 30));
        jLabelStackCap.setMinimumSize(new java.awt.Dimension(88, 30));
        jLabelStackCap.setName("jLabelStackCap"); // NOI18N
        jLabelStackCap.setPreferredSize(new java.awt.Dimension(88, 30));
        if (System.getProperty("mrj.version") != null) {
            jLabelStackCap.setMaximumSize(new java.awt.Dimension(100,25));
            jLabelStackCap.setPreferredSize(new java.awt.Dimension(90, 25));
        }
        jToolBarTop.add(jLabelStackCap);

        jTextFieldStackCap.setEditable(false);
        jTextFieldStackCap.setText(resourceMap.getString("jTextFieldStackCap.text")); // NOI18N
        jTextFieldStackCap.setAutoscrolls(false);
        jTextFieldStackCap.setMaximumSize(new java.awt.Dimension(80, 25));
        jTextFieldStackCap.setMinimumSize(new java.awt.Dimension(50, 25));
        jTextFieldStackCap.setName("jTextFieldStackCap"); // NOI18N
        jTextFieldStackCap.setPreferredSize(new java.awt.Dimension(70, 25));
        jToolBarTop.add(jTextFieldStackCap);

        jSeparator11.setName("jSeparator11"); // NOI18N
        jToolBarTop.add(jSeparator11);

        jLabelThresholdVoltage.setFont(jLabelThresholdVoltage.getFont());
        jLabelThresholdVoltage.setText(resourceMap.getString("jLabelThresholdVoltage.text")); // NOI18N
        jLabelThresholdVoltage.setAlignmentX(0.5F);
        jLabelThresholdVoltage.setMaximumSize(new java.awt.Dimension(46, 30));
        jLabelThresholdVoltage.setMinimumSize(new java.awt.Dimension(46, 30));
        jLabelThresholdVoltage.setName("jLabelThresholdVoltage"); // NOI18N
        jLabelThresholdVoltage.setPreferredSize(new java.awt.Dimension(46, 30));
        jToolBarTop.add(jLabelThresholdVoltage);

        jTextFieldThresholdVoltage.setEditable(false);
        jTextFieldThresholdVoltage.setText(resourceMap.getString("jTextFieldThresholdVoltage.text")); // NOI18N
        jTextFieldThresholdVoltage.setAutoscrolls(false);
        jTextFieldThresholdVoltage.setMaximumSize(new java.awt.Dimension(60, 25));
        jTextFieldThresholdVoltage.setMinimumSize(new java.awt.Dimension(40, 25));
        jTextFieldThresholdVoltage.setName("jTextFieldThresholdVoltage"); // NOI18N
        jTextFieldThresholdVoltage.setPreferredSize(new java.awt.Dimension(60, 25));
        jToolBarTop.add(jTextFieldThresholdVoltage);

        jToolBarSide.setFloatable(false);
        jToolBarSide.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBarSide.setRollover(true);
        jToolBarSide.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jToolBarSide.setMaximumSize(new java.awt.Dimension(125, 125));
        jToolBarSide.setMinimumSize(new java.awt.Dimension(125, 125));
        jToolBarSide.setName("jToolBarSide"); // NOI18N
        jToolBarSide.setPreferredSize(new java.awt.Dimension(2, 175));

        jPanelChart.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        jPanelChart.setName("jPanelChart"); // NOI18N
        jPanelChart.setPreferredSize(new java.awt.Dimension(640, 480));

        javax.swing.GroupLayout jPanelChartLayout = new javax.swing.GroupLayout(jPanelChart);
        jPanelChart.setLayout(jPanelChartLayout);
        jPanelChartLayout.setHorizontalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 709, Short.MAX_VALUE)
        );
        jPanelChartLayout.setVerticalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 529, Short.MAX_VALUE)
        );

        jTextFieldRolloverData.setEditable(false);
        jTextFieldRolloverData.setBorder(null);
        jTextFieldRolloverData.setName("jTextFieldRolloverData"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBarTop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 841, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jToolBarSide, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jTextFieldRolloverData, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE))
                            .addComponent(jPanelChart, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE))))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jToolBarTop, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBarSide, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                    .addComponent(jPanelChart, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldRolloverData, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        /// Hacky code to set up a blank charPanel
        XYSeriesCollection dummy = new XYSeriesCollection();
        dummy.addSeries(new XYSeries("dummy"));
        JFreeChart chart = createFastXYLineChart(
            "", // chart title
            "", // x axis label
            "", // y axis label
            dummy, // data
            PlotOrientation.VERTICAL,
            false, // include legend
            true, // tooltips
            false // urls
        );
        cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);
        cp.setRefreshBuffer(true);
        GroupLayout layout = (GroupLayout) mainPanel.getLayout();
        layout.replace(jPanelChart, cp);

        // Add listeners for chartPanel
        cp.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent e) {
                mouseClicked(e);
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent e) {
                mouseMoved(e);
            }
        });

        // Setup Popup Menu
        JPopupMenu existingPopup = cp.getPopupMenu();

        JPopupMenu popup = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem(actionMap.get("copyLocationData"));
        popup.add(menuItem);

        menuItem = new JMenuItem(actionMap.get("editLayer"));
        popup.add(menuItem);

        menuItem = new JMenuItem(actionMap.get("removeLayer"));
        popup.add(menuItem);

        popup.addSeparator();

        menuItem = new JMenuItem(actionMap.get("addDielectricBefore"));
        popup.add(menuItem);

        menuItem = new JMenuItem(actionMap.get("addDielectricAfter"));
        popup.add(menuItem);

        menuItem = new JMenuItem(actionMap.get("addMetalBefore"));
        popup.add(menuItem);

        menuItem = new JMenuItem(actionMap.get("addMetalAfter"));
        popup.add(menuItem);

        popup.addSeparator();

        menuItem = new JMenuItem(actionMap.get("replaceLayer"));
        popup.add(menuItem);

        menuItem = new JMenuItem(actionMap.get("showComposeBox"));
        popup.add(menuItem);

        popup.addSeparator();

        JMenu chartOptions = new JMenu("Chart Options...");
        popup.add(chartOptions);
        while (existingPopup.getComponentCount() > 0) {
            chartOptions.add(existingPopup.getComponent(0));
        }

        cp.setPopupMenu(popup);

        menuBar.setName("menuBar"); // NOI18N

        jMenuFile.setText(resourceMap.getString("jMenuFile.text")); // NOI18N
        jMenuFile.setName("jMenuFile"); // NOI18N

        jMenuItemNew.setAction(actionMap.get("newStructure")); // NOI18N
        jMenuItemNew.setName("jMenuItemNew"); // NOI18N
        jMenuFile.add(jMenuItemNew);

        jMenuItemOpen.setAction(actionMap.get("openStructure")); // NOI18N
        jMenuItemOpen.setName("jMenuItemOpen"); // NOI18N
        jMenuFile.add(jMenuItemOpen);

        jMenuItemSave.setAction(actionMap.get("saveStructure")); // NOI18N
        jMenuItemSave.setName("jMenuItemSave"); // NOI18N
        jMenuFile.add(jMenuItemSave);

        jMenuItemClose.setAction(actionMap.get("closeStructure")); // NOI18N
        jMenuItemClose.setText(resourceMap.getString("jMenuItemClose.text")); // NOI18N
        jMenuItemClose.setName("jMenuItemClose"); // NOI18N
        jMenuFile.add(jMenuItemClose);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jMenuFile.add(jSeparator1);

        jMenuItemImport.setAction(actionMap.get("importMaterials")); // NOI18N
        jMenuItemImport.setText(resourceMap.getString("jMenuItemImport.text")); // NOI18N
        jMenuItemImport.setName("jMenuItemImport"); // NOI18N
        jMenuFile.add(jMenuItemImport);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jMenuFile.add(jSeparator7);

        jMenuItemSaveAsDefault.setText(resourceMap.getString("jMenuItemSaveAsDefault.text")); // NOI18N
        jMenuItemSaveAsDefault.setName("jMenuItemSaveAsDefault"); // NOI18N
        jMenuFile.add(jMenuItemSaveAsDefault);

        jMenuItemExport.setAction(actionMap.get("exportData")); // NOI18N
        jMenuItemExport.setName("jMenuItemExport"); // NOI18N
        jMenuFile.add(jMenuItemExport);

        jSeparator2.setName("jSeparator2"); // NOI18N
        if (System.getProperty("mrj.version") == null)
        jMenuFile.add(jSeparator2);

        jMenuItemExit.setAction(actionMap.get("quit")); // NOI18N
        jMenuItemExit.setName("jMenuItemExit"); // NOI18N
        if (System.getProperty("mrj.version") == null)
        jMenuFile.add(jMenuItemExit);

        menuBar.add(jMenuFile);

        jMenuEdit.setText(resourceMap.getString("jMenuEdit.text")); // NOI18N
        jMenuEdit.setName("jMenuEdit"); // NOI18N

        jMenuItemUndo.setAction(actionMap.get("undoClick")); // NOI18N
        jMenuItemUndo.setText(resourceMap.getString("jMenuItemUndo.text")); // NOI18N
        jMenuItemUndo.setName("jMenuItemUndo"); // NOI18N
        jMenuEdit.add(jMenuItemUndo);

        jMenuItemCopyPlot.setAction(actionMap.get("copyPlot")); // NOI18N
        jMenuItemCopyPlot.setText(resourceMap.getString("jMenuItemCopyPlot.text")); // NOI18N
        jMenuItemCopyPlot.setName("jMenuItemCopyPlot"); // NOI18N
        jMenuEdit.add(jMenuItemCopyPlot);

        menuBar.add(jMenuEdit);

        jMenuView.setText(resourceMap.getString("jMenuView.text")); // NOI18N
        buttonGroupView.add(jMenuView);
        jMenuView.setName("jMenuView"); // NOI18N

        jRadioButtonMenuItemChargeDensity.setAction(actionMap.get("updateChart")); // NOI18N
        buttonGroupView.add(jRadioButtonMenuItemChargeDensity);
        jRadioButtonMenuItemChargeDensity.setText(resourceMap.getString("jRadioButtonMenuItemChargeDensity.text")); // NOI18N
        jRadioButtonMenuItemChargeDensity.setName("jRadioButtonMenuItemChargeDensity"); // NOI18N
        jMenuView.add(jRadioButtonMenuItemChargeDensity);

        jRadioButtonMenuItemElectricField.setAction(actionMap.get("updateChart")); // NOI18N
        buttonGroupView.add(jRadioButtonMenuItemElectricField);
        jRadioButtonMenuItemElectricField.setText(resourceMap.getString("jRadioButtonMenuItemElectricField.text")); // NOI18N
        jRadioButtonMenuItemElectricField.setName("jRadioButtonMenuItemElectricField"); // NOI18N
        jMenuView.add(jRadioButtonMenuItemElectricField);

        jRadioButtonMenuItemPotential.setAction(actionMap.get("updateChart")); // NOI18N
        buttonGroupView.add(jRadioButtonMenuItemPotential);
        jRadioButtonMenuItemPotential.setText(resourceMap.getString("jRadioButtonMenuItemPotential.text")); // NOI18N
        jRadioButtonMenuItemPotential.setName("jRadioButtonMenuItemPotential"); // NOI18N
        jMenuView.add(jRadioButtonMenuItemPotential);

        jRadioButtonMenuItemEnergy.setAction(actionMap.get("updateChart")); // NOI18N
        buttonGroupView.add(jRadioButtonMenuItemEnergy);
        jRadioButtonMenuItemEnergy.setSelected(true);
        jRadioButtonMenuItemEnergy.setText(resourceMap.getString("jRadioButtonMenuItemEnergy.text")); // NOI18N
        jRadioButtonMenuItemEnergy.setName("jRadioButtonMenuItemEnergy"); // NOI18N
        jMenuView.add(jRadioButtonMenuItemEnergy);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jMenuView.add(jSeparator3);

        jMenuMaterials.setText(resourceMap.getString("jMenuMaterials.text")); // NOI18N
        jMenuMaterials.setName("jMenuMaterials"); // NOI18N

        jMenuItemMetals.setAction(actionMap.get("showMetalsBox")); // NOI18N
        jMenuItemMetals.setName("jMenuItemMetals"); // NOI18N
        jMenuMaterials.add(jMenuItemMetals);

        jMenuItemDielectrics.setAction(actionMap.get("showDielectricsBox")); // NOI18N
        jMenuItemDielectrics.setName("jMenuItemDielectrics"); // NOI18N
        jMenuMaterials.add(jMenuItemDielectrics);

        jMenuItemSemiconductors.setAction(actionMap.get("showSemiconductorsBox")); // NOI18N
        jMenuItemSemiconductors.setName("jMenuItemSemiconductors"); // NOI18N
        jMenuMaterials.add(jMenuItemSemiconductors);

        jMenuView.add(jMenuMaterials);

        jMenuItemShowStackParameters.setAction(actionMap.get("toggleShowStackParameters")); // NOI18N
        jMenuItemShowStackParameters.setText(resourceMap.getString("jMenuItemShowStackParameters.text")); // NOI18N
        jMenuItemShowStackParameters.setName("jMenuItemShowStackParameters"); // NOI18N
        jMenuView.add(jMenuItemShowStackParameters);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jMenuView.add(jSeparator4);

        jMenuItemViewWindow.setAction(actionMap.get("showWindowBox")); // NOI18N
        jMenuItemViewWindow.setName("jMenuItemViewWindow"); // NOI18N
        jMenuView.add(jMenuItemViewWindow);

        jMenuItemMovieParameters.setAction(actionMap.get("showMovieParametersBox")); // NOI18N
        jMenuItemMovieParameters.setName("jMenuItemMovieParameters"); // NOI18N
        jMenuView.add(jMenuItemMovieParameters);

        menuBar.add(jMenuView);

        jMenuStructure.setText(resourceMap.getString("jMenuStructure.text")); // NOI18N
        jMenuStructure.setName("jMenuStructure"); // NOI18N

        jMenuItemCompose.setAction(actionMap.get("showComposeBox")); // NOI18N
        jMenuItemCompose.setName("jMenuItemCompose"); // NOI18N
        jMenuStructure.add(jMenuItemCompose);

        jMenuItemExportTool.setAction(actionMap.get("showExportToolBox")); // NOI18N
        jMenuItemExportTool.setName("jMenuItemExportTool"); // NOI18N
        jMenuStructure.add(jMenuItemExportTool);

        menuBar.add(jMenuStructure);

        jMenuHelp.setText(resourceMap.getString("jMenuHelp.text")); // NOI18N
        jMenuHelp.setName("jMenuHelp"); // NOI18N

        jMenuItemAbout.setAction(actionMap.get("showAboutBox")); // NOI18N
        jMenuItemAbout.setName("jMenuItemAbout"); // NOI18N
        if (System.getProperty("mrj.version") == null) {
            jMenuHelp.add(jMenuItemAbout);
        }
        else {
            new MacOSAboutHandler();
        }

        menuBar.add(jMenuHelp);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

private void jTextFieldVoltageFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldVoltageFocusGained
    jTextFieldVoltage.selectAll();
}//GEN-LAST:event_jTextFieldVoltageFocusGained

private void jTextFieldTempFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldTempFocusGained
    jTextFieldTemp.selectAll();
}//GEN-LAST:event_jTextFieldTempFocusGained

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
    private javax.swing.ButtonGroup buttonGroupView;
    private javax.swing.JButton jButtonPlay;
    private javax.swing.JLabel jLabelEOT;
    private javax.swing.JLabel jLabelFlatbandVoltage;
    private javax.swing.JLabel jLabelStackCap;
    private javax.swing.JLabel jLabelTemp;
    private javax.swing.JLabel jLabelThresholdVoltage;
    private javax.swing.JLabel jLabelVoltage;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemCompose;
    private javax.swing.JMenuItem jMenuItemCopyPlot;
    private javax.swing.JMenuItem jMenuItemDielectrics;
    private javax.swing.JMenuItem jMenuItemExport;
    private javax.swing.JMenuItem jMenuItemExportTool;
    private javax.swing.JMenuItem jMenuItemImport;
    private javax.swing.JMenuItem jMenuItemMetals;
    private javax.swing.JMenuItem jMenuItemMovieParameters;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAsDefault;
    private javax.swing.JMenuItem jMenuItemSemiconductors;
    private javax.swing.JMenuItem jMenuItemShowStackParameters;
    private javax.swing.JMenuItem jMenuItemUndo;
    private javax.swing.JMenuItem jMenuItemViewWindow;
    private javax.swing.JMenu jMenuMaterials;
    private javax.swing.JMenu jMenuStructure;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPanelChart;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemChargeDensity;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemElectricField;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemEnergy;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemPotential;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTextField jTextFieldEOT;
    private javax.swing.JTextField jTextFieldFlatbandVoltage;
    private javax.swing.JTextField jTextFieldRolloverData;
    private javax.swing.JTextField jTextFieldStackCap;
    private javax.swing.JTextField jTextFieldTemp;
    private javax.swing.JTextField jTextFieldThresholdVoltage;
    private javax.swing.JTextField jTextFieldVoltage;
    private javax.swing.JToolBar jToolBarSide;
    private javax.swing.JToolBar jToolBarTop;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    // End of variables declaration//GEN-END:variables

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
    }

    @Action
    public void help() {
    }
}