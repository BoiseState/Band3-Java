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
public class Dielectric extends Material {
   private double dielectricConstant;
   private String dielectricConstantExpression;
   private double bandGap; //eV
   private double electronAffinity; //eV
   private double electronEffMass;
   private double holeEffMass;
   
   /**
    * Empty default constructor.
    */
   public Dielectric() {
       super();
   }

   /**
    * Constructor used to create a clone of an existing Metal object.
    *
    * @param passMetal - The Metal object to be cloned.
    */
   public Dielectric(Dielectric in) {
      super(in.name, in.thickness, in.point, in.notes, in.fillColor);
      this.dielectricConstant           = in.dielectricConstant;
      this.dielectricConstantExpression = in.dielectricConstantExpression;
      bandGap                           = in.bandGap;
      electronAffinity                  = in.electronAffinity;
   }
   
   // *** GETTERS AND SETTERS ***
   public double    getElectronAffinity(                ) { return electronAffinity;}
   public void      setElectronAffinity(double value    ) {
      double oldElectronAffinity = electronAffinity;
      electronAffinity = value;
      propertyChangeSupport.firePropertyChange("electronAffinity", oldElectronAffinity, electronAffinity);
   }

   public double    getBandGap(                             ) { return bandGap;         }
   public void      setBandGap(double value                 ) {
      double oldBandGap = bandGap;
      bandGap = value;
      propertyChangeSupport.firePropertyChange("bandGap", oldBandGap, bandGap);
   }
   
   public double    getElectronEffMass  (                   ) { return electronEffMass;}
   public void      setElectronEffMass  (double value       ) { 
       double oldElectronEffMass = electronEffMass;
       electronEffMass = value;
       propertyChangeSupport.firePropertyChange("electronEffMass", oldElectronEffMass, electronEffMass);
   }

   public double    getHoleEffMass      (                   ) { return holeEffMass;    }
   public void      setHoleEffMass      (double value       ) {
       double oldHoleEffMass = holeEffMass;
       holeEffMass = value;
       propertyChangeSupport.firePropertyChange("holeEffMass", oldHoleEffMass, holeEffMass);
   }
   
   public double    getDielectricConstant(                  ) { return dielectricConstant;}
   public void      setDielectricConstant(double value      ) {
      double oldDielectricConstant = dielectricConstant;
      dielectricConstant = value;
      propertyChangeSupport.firePropertyChange("dielectricConstant", oldDielectricConstant, value);

      String oldDielectricConstantExpression = dielectricConstantExpression;
      dielectricConstantExpression = String.valueOf(value);
      propertyChangeSupport.firePropertyChange("dielectricConstantExpression", oldDielectricConstantExpression, dielectricConstantExpression);
   }

   public String    getDielectricConstantExpression(        ) { return dielectricConstantExpression;}
   public void      setDielectricConstantExpression(String v) {
      String oldDielectricConstantExpression = dielectricConstantExpression;
      dielectricConstantExpression = v;
      propertyChangeSupport.firePropertyChange("dielectricConstantExpression", oldDielectricConstantExpression, v);

      double oldDielectricConstant = dielectricConstant;
      dielectricConstant = Functions.evaluateExpression(dielectricConstantExpression, 'F', 0);
      propertyChangeSupport.firePropertyChange("dielectricConstant", oldDielectricConstant, dielectricConstant);
   }

   /**
    * Default method for testing the equality of Dielectrics.
    * @param obj - The Object to compare to this one, should be another Dielectric object.
    * @return <code>true</code> if the objects contain the same data.
    *         <code>false</code> if the objects are not equal.
    */
//   @Override
//   public boolean equals(Object obj) {
//      if (obj == null) {
//         return false;
//      }
//      if (getClass() != obj.getClass()) {
//         return false;
//      }
//      final Dielectric other = (Dielectric) obj;
//      if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
//         return false;
//      }
//      if (this.dielectricConstant != other.dielectricConstant) {
//         return false;
//      }
//      if ((this.dielectricConstantExpression == null) ? (other.dielectricConstantExpression != null) : !this.dielectricConstantExpression.equals(other.dielectricConstantExpression)) {
//         return false;
//      }
//      if (this.bandGap != other.bandGap) {
//         return false;
//      }
//      if (this.electronAffinity != other.electronAffinity) {
//         return false;
//      }
//      return true;
//   }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Dielectric other = (Dielectric) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (Double.doubleToLongBits(this.dielectricConstant) != Double.doubleToLongBits(other.dielectricConstant)) {
            return false;
        }
        if ((this.dielectricConstantExpression == null) ? (other.dielectricConstantExpression != null) : !this.dielectricConstantExpression.equals(other.dielectricConstantExpression)) {
            return false;
        }
        if (Double.doubleToLongBits(this.bandGap) != Double.doubleToLongBits(other.bandGap)) {
            return false;
        }
        if (Double.doubleToLongBits(this.electronAffinity) != Double.doubleToLongBits(other.electronAffinity)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.dielectricConstant) ^ (Double.doubleToLongBits(this.dielectricConstant) >>> 32));
        hash = 53 * hash + (this.dielectricConstantExpression != null ? this.dielectricConstantExpression.hashCode() : 0);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.bandGap) ^ (Double.doubleToLongBits(this.bandGap) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.electronAffinity) ^ (Double.doubleToLongBits(this.electronAffinity) >>> 32));
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
   
   
   
   // *** SUPERCLASS METHODS ***
   
   @Override
   public double getEnergyFromVacuumToTopBand() {
       return electronAffinity;
   }
   
   @Override
   public double getEnergyFromVacuumToBottomBand() {
       return electronAffinity + bandGap;
   }
   
   @Override
   public double getEnergyFromVacuumToEfi() {
       return electronAffinity + bandGap;
   }
   
   @Override
   public void prepare() {
      EvalPoint p;

      // Sort the points so they are all in order
      Collections.sort(this.getPoint());

      // Check to see if there is a point at the beginning if not add one
      if (this.getPoint().size() > 0) { // is if there are any points
         if (this.getPoint().get(0).getLocation() > 0) { // check to see if there is a point at the beginning
            p = new EvalPoint(0,0,0,0);
            this.getPoint().add(0,p);
         }
      }
      else {
         p = new EvalPoint(0,0,0,0);
         this.getPoint().add(p);
      }

      // Check to see if there is a point at the end (at the thickness) if not add one
      if (this.getPoint().get(this.getPoint().size() - 1).getLocation() < this.getThickness()) {
         p = new EvalPoint(this.getThickness(),0,0,0);
         this.getPoint().add(p);
      }

      for (int l= 1; l < this.getPoint().size() - 1; l++) {
         if (this.getPoint().get(l).getCharge() == 0) {
            this.getPoint().remove(l);
            l--;
         }
      }

      // Sort the points
      Collections.sort(this.getPoint());
   }
   
   @Override
   public Material clone() {
       return new Dielectric(this);
   }
   
   /**
    * Get the dielectric constant. If the dielectric constant is an expression
    * it will use the eField value to calculate the value, otherwise the value is returned.
    *
    * @param eField - Value to use when calculating the dielectric constant expression.
    *                 This value is referred to as F in the equation.
    *
    * @return dielectricConstant - The calculated or retrieved value depending on the
    *                              existence of the dielectric constant expression.
    */
   public double eFieldDielectricConstant(double eField) {
      if (this.dielectricConstantExpression == null || this.dielectricConstantExpression.equals("")) {
         return dielectricConstant;
      }
      else {
         return Functions.evaluateExpression(dielectricConstantExpression, 'F', eField);
      }
   }

   /**
    * Find the distance to the valance band in Cm when the energy is set to the
    * provided level.
    *
    * @param energy - energy level used to evaluate the structure.
    *
    * @return thickness - distance to valance band in Cm.
    */
   public double distanceToVBCm(double energy) {
      // find between which points the energy of interest lies
      // if energy is above all the points return 0 distance
      boolean energyBelowVB = true;
      for (int i = 0; i < point.size(); i++) {
         if (energyVBPoint(i) < energy) {
            energyBelowVB = false;
         }
      }
      if (energyBelowVB == true) {
         return 0;
      }

      int abovePoint = -1;
      double aboveEnergy = 1e10; // a rediculus start energy if we
      // find a point above the energy it will definately be lower than this value.
      for (int i = 0; i < point.size(); i++) {
         if (energyVBPoint(i) > energy && energyVBPoint(i) < aboveEnergy) {
            aboveEnergy = energyVBPoint(i);
            abovePoint = i;
         }
      }

      int belowPoint = -1;
      double belowEnergy = -1e10; // a rediculus start energy if we
      // find a point below the energy it will definately be lower than this value.
      for (int i = 0; i < point.size(); i++) {
         if (energyVBPoint(i) < energy && energyVBPoint(i) > belowEnergy) {
            belowEnergy = energyVBPoint(i);
            belowPoint = i;
         }
      }

      // if we didn't find a point in energy below the input energy then tunnel through whole dielectric
      if (abovePoint < 0) {
         return thickness;
      }

      // make sure that we have above and below points
      if (belowPoint < 0 || abovePoint < 0) {
         return -1; // negative thickness so we know something is wrong.
      }

      // interpolate cross points
      double slope = (aboveEnergy - belowEnergy) / (point.get(abovePoint).getLocation() - point.get(belowPoint).getLocation());
      double intercept = aboveEnergy - slope * point.get(abovePoint).getLocation();
      double distance = (energy - intercept) / slope;

      return distance;
   }

   /**
    * Find the distance to the conduction band in Cm when the energy is set to the
    * provided level.
    *
    * @param energy - energy level used to evaluate the structure.
    *
    * @return thickness - distance to conduction band in Cm.
    */
   public double distanceToCBCm(double energy) {
      // find between which points the energy of interest lies
      // if energy is above all the points return 0 distance
      boolean energyAboveCB = true;
      for (int i = 0; i < point.size(); i++) {
         if (energyCBPoint(i) > energy) {
            energyAboveCB = false;
         }
      }
      if (energyAboveCB == true) {
         return 0;
      }

      int abovePoint = -1;
      double aboveEnergy = 1e10; // a rediculus start energy if we
      // find a point above the energy it will definately be lower than this value.

      for (int i = 0; i < point.size(); i++) {
         if (energyCBPoint(i) > energy && energyCBPoint(i) < aboveEnergy) {
            aboveEnergy = energyCBPoint(i);
            abovePoint = i;
         }
      }

      int belowPoint = -1;
      double belowEnergy = -1e10; // a rediculus start energy if we
      // find a point below the energy it will definately be lower than this value.
      for (int i = 0; i < point.size(); i++) {
         if (energyCBPoint(i) < energy && energyCBPoint(i) > belowEnergy) {
            belowEnergy = energyCBPoint(i);
            belowPoint = i ;
         }
      }

      // if we didn't find a point in energy below the input energy then tunnel through whole dielectric
      if (belowPoint < 0) {
         return thickness;
      }

      // make sure that we have above and below points
      if (belowPoint < 0 || abovePoint < 0) {
         // we did something wrong
         return -1; // negative thickness so we know something is wrong
      }

      // interpolate cross points
      double slope = (aboveEnergy - belowEnergy) / (point.get(abovePoint).getLocation() - point.get(belowPoint).getLocation());
      double intercept = aboveEnergy - slope * point.get(abovePoint).getLocation();
      double distance = (energy - intercept) / slope;

      return distance;
   }

   /**
    * The energy value for the conduction band at the given point's location.
    *
    * @param index - index value for the point to evaluate.
    *
    * @return energy - energy value at the specified point.
    */
   public double energyCBPoint(int index) {
      return -point.get(index).getPotential() - electronAffinity;
   }

   /**
    * The energy value for the valance band at the given point's location.
    *
    * @param index - index value for the point to evaluate.
    *
    * @return energy - energy value at the specified point.
    */
   public double energyVBPoint(int index) {
      return -point.get(index).getPotential() - electronAffinity - bandGap;
   }

   public double getPermittivityFPerCm() {
      return dielectricConstant * Constant.PermitivityOfFreeSpace_cm;
   }

   public double eFieldPermittivityFPerCm(double eField) {
      if (this.dielectricConstantExpression == null || this.dielectricConstantExpression.equals("")) {
         return dielectricConstant * Constant.PermitivityOfFreeSpace_cm;
      }
      else {
          double evald = Functions.evaluateExpression(dielectricConstantExpression, 'F', eField);
//          System.out.printf("Expression was %s\n",dielectricConstantExpression);
//          System.out.printf("eField guess was" + eField + "\n");
//          System.out.printf("We got " + evald + "\n");
         return evald * Constant.PermitivityOfFreeSpace_cm;
      }
   }

   public double getPermittivityFPerM() {
      return this.dielectricConstant * Constant.PermitivityOfFreeSpace_m;
   }

   public double getCoxFPerCm2() {
      if (this.dielectricConstantExpression != null) {
         if (this.dielectricConstantExpression.contains("F")) {
            // If there are not at least two points then return dielectric Constant evaluated at F=0
            if (point.size() < 2) {
               return Functions.evaluateExpression(this.dielectricConstantExpression, 'F', 0) *
                  Constant.PermitivityOfFreeSpace_cm / thickness;
            }
            else {
               // Ok evaluate dependenting of what the current e-field is
               double oneOverCap = 0;
               double dielectricPointCap;
               for (int j = 1; j < point.size(); j++) {
                  dielectricPointCap = eFieldPermittivityFPerCm(point.get(j-1).getElectricField()) /
                     point.get(j).getLocation();
                  oneOverCap += 1 / dielectricPointCap;
               }
               return 1 / oneOverCap;
            }
         }
      }

      return this.dielectricConstant * Constant.PermitivityOfFreeSpace_cm / thickness;
   }
   
   public double getVoltageDrop() {
       return point.get(0).getPotential() - point.get(point.size() -1).getPotential();
   }
   
   @Override
   public double getPotentialAtLocation(double nm) {
       double startPot  = point.get(0).getPotential();
       double endPot    = point.get(point.size() - 1).getPotential();
       double pSlope    = (endPot - startPot) / this.getThicknessNm();
       return pSlope * nm + startPot;
   }
   
   // Since eField uniform in dielectric can do this
   @Override
   public double getElectricFieldAtLocation(double nm) {
       return point.get(0).getElectricFieldMv();
   }
}
