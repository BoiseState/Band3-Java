package band;

import java.io.Serializable;

/**
 * Contains all data for one measurement on the charts. Used in point List objects
 * Within each material type.
 *
 * @author Richard Southwick III
 * @author Michael Baker
 *
 * @version 1.0
 */
public class EvalPoint implements Comparable<EvalPoint> {
   private double location; // cm
   private double charge; // coulombs
   private double electricField; // V/cm
   private double potential; // V
   private double electronCharge;

   /**
    * Create an EvalPoint initialized to 0.
    */
   public EvalPoint() {
      location = 0;
      charge = 0;
      electronCharge = 0;
      electricField = 0;
      potential = 0;
   }

   /**
    * Create a new EvalPoint from existing point values.
    *
    * @param passLocation - Location within the object containing this point.
    * @param passCharge - charge value for this point in coulombs.
    * @param passElectricField - electric field value for this point in V/cm
    * @param passPotential - potential value for this point in V
    */
   public EvalPoint(double passLocation, double passCharge, double passElectricField, double passPotential) {
      location = passLocation;
      charge = passCharge;
      electronCharge = charge / Constant.ElectronCharge;
      electricField = passElectricField;
      potential = passPotential;
   }

   /**
    * Create a new EvalPoint from an existing point.
    * The new point will be a clone.
    *
    * @param passPoint - Point to be cloned.
    */
   public EvalPoint(EvalPoint passPoint) {
      location = passPoint.location;
      charge = passPoint.charge;
      electronCharge = passPoint.electronCharge;
      electricField = passPoint.electricField;
      potential = passPoint.potential;
   }

   /**
    * Get the location value for this point.
    *
    * @return location value in cm.
    */
   public double getLocation() {
      return location;
   }   

   /**
    * Get the electric field value for this point.
    *
    * @return electric field value in V/cm.
    */
   public double getElectricField() {
      return electricField;
   }

   /**
    * Set the electric field value for this point.
    *
    * @param value - electric field value in V/cm.
    */
   public void setElectricField(double value) {
      electricField = value;
   }


   public double getPotential() {
      return potential;
   }

   public void setPotential(double value) {
      potential = value;
   }

   /**
    * Default method for testing the equality of EvalPoints.
    * @param obj - The object to compare to this one, should be another EvalPoint object.
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
      final EvalPoint other = (EvalPoint) obj;
      if (this.location != other.location) {
         return false;
      }
      if (this.charge != other.charge) {
         return false;
      }
      if (this.electricField != other.electricField) {
         return false;
      }
      if (this.potential != other.potential) {
         return false;
      }
      return true;
   }

   /**
    * Value used to compare two EvalPoints with a single value comparison. Used with equals.
    *
    * @return hash - A hash value unique to an Eval Point setup with a specific set of values.
    */
   @Override
   public int hashCode() {
      int hash = 3;
      hash = 89 * hash + (int) (Double.doubleToLongBits(this.location) ^ (Double.doubleToLongBits(this.location) >>> 32));
      hash = 89 * hash + (int) (Double.doubleToLongBits(this.charge) ^ (Double.doubleToLongBits(this.charge) >>> 32));
      hash = 89 * hash + (int) (Double.doubleToLongBits(this.electricField) ^ (Double.doubleToLongBits(this.electricField) >>> 32));
      hash = 89 * hash + (int) (Double.doubleToLongBits(this.potential) ^ (Double.doubleToLongBits(this.potential) >>> 32));
      return hash;
   }

   /**
    * Method used to compare one EvalPoint object to another, works off of location within a Structure.
    * Used for sorting EvalPoint objects within a list.
    *
    * @param passPoint - The EvalPoint object to compare to this one.
    *
    * @return -1 if the current object has a smaller location value than the passed in object.
    *          0 if the current object has the same location value as the passed in object.
    * @return  1 if the current object has a greater location value than the passed in object.
    * 
    * @see Comparable
    */
   @Override
   public int compareTo(EvalPoint passPoint) {
      if (passPoint == null) {
         return 1;
      }
      else {
         if (this.location < passPoint.location) {
            return -1;
         }
         else {
            if (this.location > passPoint.location) {
               return 1;
            }
            else {
               return 0;
            }
         }
      }
   }

   public double getElectronCharge() {
      return electronCharge;
   }

   public void setElectronCharge(double value) {
      this.electronCharge = value;
      this.charge = value * Constant.ElectronCharge;
   }

   public double getCharge() {
      return charge;
   }

   public void setCharge(double value) {
      charge = value;
      electronCharge = value / Constant.ElectronCharge;
   }

   public double getLocationNm() {
      return this.location * 1E7;
   }

   public void setLocationNm(double value) {
      this.location = value * 1E-7;
   }

   public double getLocationM() {
      return this.getLocation() * 1E-2;
   }

   public void setLocationM(double value) {
      this.location = value * 1E2;
   }
   
   public void setLocation(double value) {
      location = value;
   }

   public double getElectricFieldM() {
      return this.getElectricField() * 1E2;
   }

   public void setElectricFieldM(double value) {
      this.electricField = value * 1E-2;
   }

   public double getElectricFieldMv() {
      return this.electricField / 1E6;
   }

}
