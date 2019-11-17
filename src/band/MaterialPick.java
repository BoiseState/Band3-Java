
package band;

public abstract class MaterialPick extends javax.swing.JDialog{
    
    public MaterialPick(java.awt.Frame parent, boolean modal) {
        super(parent, modal);     
    }
    
    public abstract Material getNewMaterial();
    
    public abstract boolean isConfirmed();
    
    public abstract void setReplace(double thickness);
    
}
