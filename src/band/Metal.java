package band;

import java.util.Collections;

/**
 * Object to represent a metal. Contains information for editing,
 * viewing and contains calculation results. Is stored as a Material in
 * a Structure.
 *
 * @author Richard Southwick III
 * @author Michael Baker
 * @author Ryan Thompson
 * @version 2.0
 *
 * @see Material
 * @see Structure
 * @see Kernel
 */
public class Metal extends Material {
    private double workFunction;
    private double extraCharge; // coulombs

   /**
    * Empty default constructor.
    */
    public Metal() {
        super();
    }

   /**
    * Constructor used to create a clone of an existing Metal object.
    *
    * @param passMetal - The Metal object to be cloned.
    */
    public Metal(Metal in) {
       
      super(in.name, in.thickness, in.point, in.notes, in.fillColor);

      workFunction = in.workFunction;
      extraCharge = in.extraCharge;
   }

   public double    getExtraCharge(             ) {return extraCharge;}
   public void      setExtraCharge(double value ) {
      double oldExtraCharge = extraCharge;
      extraCharge = value;
      propertyChangeSupport.firePropertyChange("extraCharge", oldExtraCharge, extraCharge);
   }

   public double    getWorkFunction(            ) {return workFunction;}
   public void      setWorkFunction(double value) {
      double oldWorkFunction = workFunction;
      workFunction = value;
      propertyChangeSupport.firePropertyChange("workFunction", oldWorkFunction, workFunction);
   }

   /**
    * Default method for testing the equality of Metals.
    * @param obj - The Object to compare to this one, should be another Metal object.
    * @return <code>true</code> if the objects contain the same data.
    *         <code>false</code> if the objects are not equal.
    */
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Metal other = (Metal) obj;
      if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
         return false;
      }
      if (this.workFunction != other.workFunction) {
         return false;
      }
      if (this.thickness != other.thickness) {
         return false;
      }
      if (this.extraCharge != other.extraCharge) {
         return false;
      }
      if ((this.notes == null) ? (other.notes != null) : !this.notes.equals(other.notes)) {
         return false;
      }
      if (this.point != other.point && (this.point == null || !this.point.equals(other.point))) {
         return false;
      }
      if (this.fillColor != other.fillColor && (this.fillColor == null || !this.fillColor.equals(other.fillColor))) {
         return false;
      }
      return true;
   }

   /**
    * Value used to compare two Metals with a single value comparison. Used with equals.
    *
    * @return hash - A hash value unique to a Metal setup with a specific set of values.
    */
   @Override
   public int hashCode() {
      int hash = 7;
      hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
      hash = 29 * hash + (int) (Double.doubleToLongBits(this.workFunction) ^ (Double.doubleToLongBits(this.workFunction) >>> 32));
      hash = 29 * hash + (int) (Double.doubleToLongBits(this.thickness) ^ (Double.doubleToLongBits(this.thickness) >>> 32));
      hash = 29 * hash + (int) (Double.doubleToLongBits(this.extraCharge) ^ (Double.doubleToLongBits(this.extraCharge) >>> 32));
      hash = 29 * hash + (this.notes != null ? this.notes.hashCode() : 0);
      hash = 29 * hash + (this.point != null ? this.point.hashCode() : 0);
      hash = 29 * hash + (this.fillColor != null ? this.fillColor.hashCode() : 0);
      return hash;
   }
   
   @Override
   public double getEnergyFromVacuumToTopBand() {
       return workFunction;
   }
   
   @Override
   public double getEnergyFromVacuumToBottomBand() {
       return workFunction;
   }
   
   @Override
   public double getEnergyFromVacuumToEfi() {
       return workFunction;
   }
   
   @Override
   public void prepare() {
       this.point.clear();
       this.point.add(new EvalPoint(0,0,0,0));
       this.point.add(new EvalPoint(this.thickness,0,0,0));
       Collections.sort(this.point);
   }
   
   @Override
   public Metal clone() {
       return new Metal(this);
   }
   
   // Since EField is uniform in metal, we can do this
   @Override
   public double getElectricFieldAtLocation(double nm) {
       return point.get(0).getElectricFieldMv();
   }
   
   // Since potential is uniform in metal, we can do this
   @Override
   public double getPotentialAtLocation(double nm) {
       return point.get(0).getPotential();
   }
}
