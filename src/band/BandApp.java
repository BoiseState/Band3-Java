/*
 * BandApp.java
 */

package band;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import java.util.prefs.Preferences;

/**
 * The main class of the application.
 */
public class BandApp extends SingleFrameApplication {
   private Preferences prefs;
   private List<Material> listDielectric;
   private List<Material> listMetal;
   private List<Material> listSemiconductor;
   private Structure structure;
   private String userHomeDir = javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory().toString();
   private String writeDir = userHomeDir + "/Band Diagram/";
   private double temperature;
   private double voltage;

   // Movie Parameters
   private double startVoltage;
   private double stopVoltage;
   private int numberOfSteps;

   // Export Tools
   private double exportStartVoltage;
   private double exportStopVoltage;
   private int exportNumberOfSteps;
   public double exportStartTemp;
   public double exportStopTemp;
   public int exportNumberOfTempSteps;
   public int exportSweepChoice;

   private double yMin;
   private double yMax;
   private double xMin;
   private double xMax;
   private boolean xAutoScale;
   private boolean yAutoScale;
   private boolean SmartScale;
   private boolean changed = false;

   public boolean isChanged() {
      return changed;
   }

   public void setChanged(boolean changed) {
      this.changed = changed;
   }

   public int getExportNumberOfSteps() {
      return exportNumberOfSteps;
   }

   public void setExportNumberOfSteps(int exportNumberOfSteps) {
      this.exportNumberOfSteps = exportNumberOfSteps;
   }

   public double getExportStartVoltage() {
      return exportStartVoltage;
   }

   public void setExportStartVoltage(double exportStartVoltage) {
      this.exportStartVoltage = exportStartVoltage;
   }

   public double getExportStopVoltage() {
      return exportStopVoltage;
   }

   public void setExportStopVoltage(double exportStopVoltage) {
      this.exportStopVoltage = exportStopVoltage;
   }

   public int getNumberOfSteps() {
      return numberOfSteps;
   }

   public void setNumberOfSteps(int numberOfSteps) {
      this.numberOfSteps = numberOfSteps;
   }

   public double getStartVoltage() {
      return startVoltage;
   }

   public void setStartVoltage(double startVoltage) {
      this.startVoltage = startVoltage;
   }

   public double getStopVoltage() {
      return stopVoltage;
   }

   public void setStopVoltage(double stopVoltage) {
      this.stopVoltage = stopVoltage;
   }

   public double getTemperature() {
      return temperature;
   }

   public void setTemperature(double temperature) {
      this.temperature = temperature;
   }

   public double getVoltage() {
      return voltage;
   }

   public void setVoltage(double voltage) {
      this.voltage = voltage;
   }

   public double getxMax() {
      return xMax;
   }

   public void setxMax(double xMax) {
      this.xMax = xMax;
   }

   public double getxMin() {
      return xMin;
   }

   public void setxMin(double xMin) {
      this.xMin = xMin;
   }

   public double getyMax() {
      return yMax;
   }

   public void setyMax(double yMax) {
      this.yMax = yMax;
   }

   public double getyMin() {
      return yMin;
   }

   public void setyMin(double yMin) {
      this.yMin = yMin;
   }

   public boolean isxAutoScale() {
      return xAutoScale;
   }

   public void setxAutoScale(boolean xAutoScale) {
      this.xAutoScale = xAutoScale;
   }

   public boolean isSmartScale(){
       return SmartScale;
   }
   
   public void setSmartScale(boolean ySmartScale) {
      this.SmartScale = ySmartScale;
   }
   
   public boolean isyAutoScale() {
      return yAutoScale;
   }

   public void setyAutoScale(boolean yAutoScale) {
      this.yAutoScale = yAutoScale;
   }
   
   private void copyResToFile(InputStream in, File out) throws java.io.IOException {
       try {
        OutputStream o = new FileOutputStream(out);
         byte[] buf = new byte[1024];
         int len;
         while ((len = in.read(buf)) > 0) {
             o.write(buf, 0, len);
         }
         in.close();
         o.close();
       } catch (FileNotFoundException fnf) {
           // This won't happen
       }
   }

   /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
       String os = System.getProperty("os.name").toLowerCase();
       
           BandSplash splash = new BandSplash(null, true);
           final Toolkit toolkit = Toolkit.getDefaultToolkit();
           final Dimension screenSize = toolkit.getScreenSize();
           final int x = (screenSize.width - splash.getWidth()) / 2;
           final int y = (screenSize.height - splash.getHeight()) / 2;
           splash.setLocation(x,y);
           show(splash);
       if(!splash.isConfirmed()) {
           this.exit();
       }
       if(os.indexOf("win") >= 0) {
           writeDir = userHomeDir + "/Band Diagram/";
       }
        
       File userDir = new File(writeDir);
       if (!userDir.exists()) userDir.mkdir();

       File defaultStructure = new File(writeDir + "default.xml");
       if (!defaultStructure.exists()) {
             InputStream in = getClass().getResourceAsStream("default.xml");
             try {
                copyResToFile(in, defaultStructure);
             } catch (java.io.IOException e) {
                 JOptionPane.showMessageDialog(null, "Can't open or write to " 
                             + defaultStructure.getPath() + "!", "File access problem", JOptionPane.ERROR_MESSAGE);
             }
       }
       
       List<Material> listMaterials = null;
       
       File matLibrary = new File(writeDir + "materials.xml");
       
       if (!matLibrary.exists()) {
           InputStream in = getClass().getResourceAsStream("materials.xml");
           try {
            copyResToFile(in, matLibrary);
           } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(null, "Can't open or write to " 
                             + matLibrary.getPath() + "!", "File access problem", JOptionPane.ERROR_MESSAGE);
           }
       }
       
       loadMaterialsLibrary(matLibrary);
       
       loadStructure(defaultStructure);
       
       
       registerPrefs();
       
       
       // Separate out materials into own lists
      
       
       show(new BandView(this, structure));
    }

    private void registerPrefs() {
        prefs = Preferences.userNodeForPackage (getClass ());
        // Temporary defaults for globals
        temperature = prefs.getDouble("temperature", 300);
        voltage = prefs.getDouble("voltage", 1);
        startVoltage = prefs.getDouble("startVoltage",-2);
        stopVoltage = prefs.getDouble("stopVoltage", 2);
        numberOfSteps = prefs.getInt("numberOfSteps", 50);
        exportStartVoltage = prefs.getDouble("exportStartVoltage",-2);
        exportStopVoltage = prefs.getDouble("exportStopVoltage", 2);
        exportNumberOfSteps = prefs.getInt("exportNumberOfSteps", 50);

        yMin = prefs.getDouble("yMin", -15);
        yMax = prefs.getDouble("yMax", 0);
        xMin = prefs.getDouble("xMin", 0);
        xMax = prefs.getDouble("xMax", 100);
        xAutoScale = prefs.getBoolean("xAutoScale", true);
        yAutoScale = prefs.getBoolean("yAutoScale", true);
        SmartScale = prefs.getBoolean("SmartScale", true);
    }

    private List<Material> extractMaterialList(List<Material> listMaterials, Class<?> type) {
        List<Material> retList = new LinkedList<Material>();
        for(Material m : listMaterials) {
           if(m.getClass() == type) {
               retList.add(m);
           }
        }
        
        return retList;
    }
    
    private void loadMaterialsLibrary(File f) throws HeadlessException {
        List<Material> listMaterials;
        XmlIOHandler handler = new XmlIOHandler();
        
        
        try {
            listMaterials = handler.importMaterials(f);
        }
        catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Materials library appears to be corrupt",
                       "File access problem", JOptionPane.ERROR_MESSAGE);
            listMaterials = new LinkedList<Material>();
        }
        catch (FileNotFoundException fe) {
           JOptionPane.showMessageDialog(null, "Materials library seems to be missing" + f.getPath() + System.getProperty("os.name"),
                       "File access problem", JOptionPane.ERROR_MESSAGE);
           listMaterials = new LinkedList<Material>();
        }
        
       listDielectric       = extractMaterialList(listMaterials, Dielectric.class);
       Collections.sort(listDielectric);
       listMetal            = extractMaterialList(listMaterials, Metal.class);
       Collections.sort(listMetal);
       listSemiconductor    = extractMaterialList(listMaterials, Semiconductor.class);
       Collections.sort(listSemiconductor);
    }
    
    public void loadStructure(File f) throws HeadlessException {
        try {
            XmlIOHandler handler = new XmlIOHandler();
            Structure s = handler.readStructure(f);
            this.setStructure(s);
            this.setChanged(false);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "Couldn't find the specified file!", "File Open Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null,
                    "Data file appears to be corrupt!", "File Parse Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

   @Override
   protected void shutdown() {
      // Save preferences
      prefs.putDouble("temperature", temperature);
      prefs.putDouble("voltage", voltage);
      prefs.putDouble("startVoltage",startVoltage);
      prefs.putDouble("stopVoltage", stopVoltage);
      prefs.putInt("numberOfSteps", numberOfSteps);

      prefs.putDouble("yMin", yMin);
      prefs.putDouble("yMax", yMax);
      prefs.putDouble("xMin", xMin);
      prefs.putDouble("xMax", xMax);
      prefs.putBoolean("xAutoScale", xAutoScale);
      prefs.putBoolean("yAutoScale", yAutoScale);
      prefs.putBoolean("SmartScale", SmartScale);
      
      // Make a big list of all materials in library to write to file
      List<Material> listMaterials = new LinkedList<Material>();
      
      listMaterials.addAll(listDielectric);
      listMaterials.addAll(listMetal);
      listMaterials.addAll(listSemiconductor);
      
      XmlIOHandler handler = new XmlIOHandler();
     
      try {
          File f = new File(writeDir + "materials.xml");
          handler.exportMaterials(listMaterials, f); 
      }
      catch (Exception ex) {
         switch(JOptionPane.showConfirmDialog(null, 
                 "Couldn't save materials library! Quit anyway?", 
                 "Library save error",JOptionPane.YES_NO_OPTION)) {
             case JOptionPane.YES_OPTION:
                 break;
             case JOptionPane.NO_OPTION:
             case JOptionPane.CLOSED_OPTION:
             default:
                 return;
         }
      }
      
      super.shutdown();
      System.exit(0);
   }



    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of BandApp
     */
    public static BandApp getApplication() {
        return Application.getInstance(BandApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
       if (System.getProperty("mrj.version") != null) {
        System.setProperty("apple.awt.brushMetalLook", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Band Diagram");
       }
        launch(BandApp.class, args);
    }

     public List<Material> getListDielectric() {
      return listDielectric;
   }

   public List<Material> getListMetal() {
      return listMetal;
   }

   public List<Material> getListSemiconductor() {
      return listSemiconductor;
   }

   public Structure getStructure() {
      return structure;
   }

   public void setStructure(Structure s) {
      structure = s;
   }
   
}
