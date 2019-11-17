package band;

public abstract class MaterialSelect extends javax.swing.JDialog {
    
    public MaterialSelect(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    public abstract void setCurrentRecord(Material m);

    public abstract Material getCurrentRecord();

    public abstract boolean isConfirmed();

    public abstract void setConfirmed(boolean value);
}
