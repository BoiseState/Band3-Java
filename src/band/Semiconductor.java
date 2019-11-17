package band;

public class Semiconductor extends Material{
   private double surfacePot;
   private double dielectricConstant;
   private double bandGap; // eV
   private double electronAffinity; // eV
   private double dopantConcentration; // cm-3
   private double intrinsicCarrierConcentration; // cm-3
   private boolean dopingType; // if true then ptype doping
   private double nit;
   private int ditType;
   private double co;
   private double u1;
   private double u2;
   private double o1;
   private double o2;
   private double per1;
   private String bandGapExpression;
   private String intrinsicCarrierConcentrationExpression;
   private double temperature;
   private double thermalVoltage;
   private double transEffMass;
   private double longEffMass;

   public Semiconductor() {  
      super();
      temperature = 300;
      thermalVoltage = Constant.BoltzmannsConstant * temperature / Constant.ElectronCharge;
      bandGapExpression = "";
      intrinsicCarrierConcentrationExpression = "";
   }

    public Semiconductor(Semiconductor in) {
        super(in.name, in.thickness, in.point, in.notes, in.fillColor);

        dielectricConstant = in.dielectricConstant;
        bandGap = in.bandGap;
        electronAffinity = in.electronAffinity;
        intrinsicCarrierConcentration = in.intrinsicCarrierConcentration;
        dopantConcentration = in.dopantConcentration;
        dopingType = in.dopingType;
        surfacePot = in.surfacePot;
        nit = in.nit;
        ditType = in.ditType;
        co = in.co;
        u1 = in.u1;
        u2 = in.u2;
        o1 = in.o1;
        o2 = in.o2;
        per1 = in.per1;
        bandGapExpression = in.bandGapExpression;
        intrinsicCarrierConcentrationExpression = in.intrinsicCarrierConcentrationExpression;

        // Can't have absolute temperature, free out occurs
        if (in.temperature == 0)
            temperature = 300;
        else
            temperature = in.temperature;
        
        thermalVoltage = Constant.BoltzmannsConstant * temperature / Constant.ElectronCharge;
        if (!bandGapExpression.equals("") && bandGapExpression != null) {
            bandGap = tempBandGap(bandGapExpression, temperature);
        }
        if (!intrinsicCarrierConcentrationExpression.equals("") && intrinsicCarrierConcentrationExpression != null) {
            intrinsicCarrierConcentration = tempNi(intrinsicCarrierConcentrationExpression, temperature);
        }
    }
    
    // *** GETTERS AND SETTERS ***
    public double    getDielectricConstant(                                  ) {return dielectricConstant;}
    public void      setDielectricConstant(double value                      ) {
        double oldDielectricConstant = dielectricConstant;
        this.dielectricConstant = value;
        propertyChangeSupport.firePropertyChange("dielectricConstant", oldDielectricConstant, dielectricConstant);
    }

    public double    getTemperature(                                         ) {return temperature;}
    public void      setTemperature(double value                             ) {
        if (value < 100) {         
         throw new RuntimeException("Extreme low temperatures where dopant freeze-out can occur are not modeled in this program. For better results raise the temperature above 100K.");
        }
        double oldTemperature = temperature;
        temperature = value;
        propertyChangeSupport.firePropertyChange("temperature", oldTemperature, temperature);

        thermalVoltage = Constant.BoltzmannsConstant * temperature / Constant.ElectronCharge;
        if (!bandGapExpression.equals("") && bandGapExpression != null) {
         bandGap = tempBandGap(bandGapExpression, temperature);
        }
        if (!this.intrinsicCarrierConcentrationExpression.equals("") && this.intrinsicCarrierConcentrationExpression != null) {
         this.intrinsicCarrierConcentration = tempNi(intrinsicCarrierConcentrationExpression, temperature);
        }

        if (this.dopantConcentration < this.intrinsicCarrierConcentration * 100) {         
         throw new RuntimeException("Temperatures where the dopant concentration is not significantly bigger than the intrinsic carrier concentration are not modeled in this program. For better results lower the temperature.");
        }
    }

    public double    getIntrinsicCarrierConcentration(                       ) {return intrinsicCarrierConcentration;}
    public void      setIntrinsicCarrierConcentration(double value           ) {
        double oldIntrinsicCarrierConcentration = intrinsicCarrierConcentration;
        intrinsicCarrierConcentration = value;
        propertyChangeSupport.firePropertyChange("intrinsicCarrierConcentration", oldIntrinsicCarrierConcentration, intrinsicCarrierConcentration);

        String oldIntrinsicCarrierConcentrationExpression = intrinsicCarrierConcentrationExpression;
        intrinsicCarrierConcentrationExpression = String.valueOf(value);
        propertyChangeSupport.firePropertyChange("intrinsicCarrierConcentrationExpression", oldIntrinsicCarrierConcentrationExpression, intrinsicCarrierConcentrationExpression);
    }

    public String    getIntrinsicCarrierConcentrationExpression(             ) {return intrinsicCarrierConcentrationExpression;}
    public void      setIntrinsicCarrierConcentrationExpression(String expr  ) {
        String oldIntrinsicCarrierConcentrationExpression = intrinsicCarrierConcentrationExpression;
        intrinsicCarrierConcentrationExpression = expr;
        propertyChangeSupport.firePropertyChange("intrinsicCarrierConcentrationExpression", oldIntrinsicCarrierConcentrationExpression, intrinsicCarrierConcentrationExpression);

        double oldIntrinsicCarrierConcentration = intrinsicCarrierConcentration;
        intrinsicCarrierConcentration = tempNi(expr, temperature);
        propertyChangeSupport.firePropertyChange("intrinsicCarrierConcentration", oldIntrinsicCarrierConcentration, intrinsicCarrierConcentration);
    }

    public double    getDitType(                                             ) {return ditType;}
    public void      setDitType(int value                                    ) {
        int oldDitType = ditType;
        ditType = value;
        propertyChangeSupport.firePropertyChange("ditType", oldDitType, ditType);
    }

    public boolean   getNType(                                               ) {return !this.dopingType;}
    public void      setNType(boolean value                                  ) {
        boolean oldDopingType = dopingType;
        dopingType = !value;
        propertyChangeSupport.firePropertyChange("dopingType", oldDopingType, dopingType);
    }

    public boolean   getPType(                                               ) {return dopingType;}
    public void      setPType(boolean value                                  ) {
        boolean oldDopingType = dopingType;
        dopingType = value;
        propertyChangeSupport.firePropertyChange("dopingType", oldDopingType, dopingType);
    }

    public double    getElectronAffinity(                                    ) {return this.electronAffinity;}
    public void      setElectronAffinity(double value                        ) {
        double oldElectronAffinity = electronAffinity;
        electronAffinity = value;
        propertyChangeSupport.firePropertyChange("electronAffinity", oldElectronAffinity, electronAffinity);
    }

    public double    getBandGap(                                             ) {return bandGap;}
    public void      setBandGap(double value                                 ) {
        double oldBandGap = bandGap;
        bandGap = value;
        propertyChangeSupport.firePropertyChange("bandGap", oldBandGap, value);

        String oldBandGapExpression = bandGapExpression;
        bandGapExpression = String.valueOf(value);
        propertyChangeSupport.firePropertyChange("bandGapExpression", oldBandGapExpression, bandGapExpression);
    }

    public String    getBandGapExpression(                                   ) {return bandGapExpression;}
    public void      setBandGapExpression(String expr                        ) {
        String oldBandGapExpression = bandGapExpression;
        bandGapExpression = expr;
        propertyChangeSupport.firePropertyChange("bandGapExpression", oldBandGapExpression, expr);

        double oldBandGap = bandGap;
        bandGap = tempBandGap(expr, temperature);
        propertyChangeSupport.firePropertyChange("bandGap", oldBandGap, bandGap);      
    }

    public double    getSurfacePot(                                          ) {return surfacePot;}
    public void      setSurfacePot(double value                              ) {
        double oldSurfacePot = surfacePot;
        surfacePot = value;
        propertyChangeSupport.firePropertyChange("surfacePot", oldSurfacePot, surfacePot);
    }

    public double    getDopantConcentration(                                 ) {return this.dopantConcentration;}
    public void      setDopantConcentration(double value                     ) {
        double oldDopantConcentration = dopantConcentration;
        dopantConcentration = value;
        propertyChangeSupport.firePropertyChange("dopantConcentration", oldDopantConcentration, dopantConcentration);
    }

    public double    getO1(                                                  ) {return o1;}
    public void      setO1(double value                                      ) {
        double oldO1 = o1;
        o1 = Math.abs(value);
        propertyChangeSupport.firePropertyChange("o1", oldO1, o1);
    }

    public double    getO2(                                                  ) {return o2;}
    public void      setO2(double value                                      ) {
        double oldO2 = o2;
        o2 = Math.abs(value);
        propertyChangeSupport.firePropertyChange("o2", oldO2, o1);
    }

    public double    getPer1(                                                ) {return per1;}
    public void      setPer1(double value                                    ) {
        double oldPer1 = per1;
        if (value > 1)
         per1 = 1;
        else {
         if (value < 0)
            per1 = 0;
         else
            per1 = value;
        }
        propertyChangeSupport.firePropertyChange("per1", oldPer1, per1);
    }

    public double    getTransEffMass(                                        ) { return transEffMass;   }
    public void      setTransEffMass(double value                            ) { 
        double oldTransEffMass = transEffMass;
        transEffMass = value;
        propertyChangeSupport.firePropertyChange("transEffMass", oldTransEffMass, transEffMass);
    }

    public double    getLongEffMass(                                         ) { return longEffMass;    }
    public void      setLongEffMass(double value                             ) {
        double oldLongEffMass = longEffMass;
        longEffMass = value;
        propertyChangeSupport.firePropertyChange("longEffMass", oldLongEffMass, longEffMass);
    }
    
    // *** SUPERCLASS OVERRIDES ***
    @Override
    public double   getThickness(                                           ) {
        return this.getThicknessNm() * 1E-7;
    }
    
    @Override
    public void     setThickness(double value                               ) {
        //RT: We can't do this for semiconductors I guess.
    }
    
    @Override
    public double   getThicknessNm(                                         ) {
        return 50;
    }
    
    @Override
    public void     setThicknessNm(double value                             ) {
        //RT: We can't do this for semicondcutors I guess.
    }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Semiconductor other = (Semiconductor) obj;
      if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
         return false;
      }
      if (this.surfacePot != other.surfacePot) {
         return false;
      }
      if (this.dielectricConstant != other.dielectricConstant) {
         return false;
      }
      if (this.bandGap != other.bandGap) {
         return false;
      }
      if (this.electronAffinity != other.electronAffinity) {
         return false;
      }
      if (this.dopantConcentration != other.dopantConcentration) {
         return false;
      }
      if (this.intrinsicCarrierConcentration != other.intrinsicCarrierConcentration) {
         return false;
      }
      if (this.dopingType != other.dopingType) {
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
      if (this.nit != other.nit) {
         return false;
      }
      if (this.ditType != other.ditType) {
         return false;
      }
      if (this.co != other.co) {
         return false;
      }
      if (this.u1 != other.u1) {
         return false;
      }
      if (this.u2 != other.u2) {
         return false;
      }
      if (this.o1 != other.o1) {
         return false;
      }
      if (this.o2 != other.o2) {
         return false;
      }
      if (this.per1 != other.per1) {
         return false;
      }
      if ((this.bandGapExpression == null) ? (other.bandGapExpression != null) : !this.bandGapExpression.equals(other.bandGapExpression)) {
         return false;
      }
      if ((this.intrinsicCarrierConcentrationExpression == null) ? (other.intrinsicCarrierConcentrationExpression != null) : !this.intrinsicCarrierConcentrationExpression.equals(other.intrinsicCarrierConcentrationExpression)) {
         return false;
      }
      if (this.temperature != other.temperature) {
         return false;
      }
      if (this.thermalVoltage != other.thermalVoltage) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.surfacePot) ^ (Double.doubleToLongBits(this.surfacePot) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.dielectricConstant) ^ (Double.doubleToLongBits(this.dielectricConstant) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.bandGap) ^ (Double.doubleToLongBits(this.bandGap) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.electronAffinity) ^ (Double.doubleToLongBits(this.electronAffinity) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.dopantConcentration) ^ (Double.doubleToLongBits(this.dopantConcentration) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.intrinsicCarrierConcentration) ^ (Double.doubleToLongBits(this.intrinsicCarrierConcentration) >>> 32));
      hash = 79 * hash + (this.dopingType ? 1 : 0);
      hash = 79 * hash + (this.notes != null ? this.notes.hashCode() : 0);
      hash = 79 * hash + (this.point != null ? this.point.hashCode() : 0);
      hash = 79 * hash + (this.fillColor != null ? this.fillColor.hashCode() : 0);
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.nit) ^ (Double.doubleToLongBits(this.nit) >>> 32));
      hash = 79 * hash + this.ditType;
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.co) ^ (Double.doubleToLongBits(this.co) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.u1) ^ (Double.doubleToLongBits(this.u1) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.u2) ^ (Double.doubleToLongBits(this.u2) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.o1) ^ (Double.doubleToLongBits(this.o1) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.o2) ^ (Double.doubleToLongBits(this.o2) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.per1) ^ (Double.doubleToLongBits(this.per1) >>> 32));
      hash = 79 * hash + (this.bandGapExpression != null ? this.bandGapExpression.hashCode() : 0);
      hash = 79 * hash + (this.intrinsicCarrierConcentrationExpression != null ? this.intrinsicCarrierConcentrationExpression.hashCode() : 0);
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.temperature) ^ (Double.doubleToLongBits(this.temperature) >>> 32));
      hash = 79 * hash + (int) (Double.doubleToLongBits(this.thermalVoltage) ^ (Double.doubleToLongBits(this.thermalVoltage) >>> 32));
      return hash;
   }
   
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
       return electronAffinity + bandGap/2;
   }
   
   @Override
   public void prepare() {
       this.point.clear();
       this.point.add(new EvalPoint(0,0,0,0));
       this.point.add(new EvalPoint(1e-5,0,0,0));
   }
   
   @Override
   public Material clone() {
       return new Semiconductor(this);
   }
   
   // *** NEW FUNCTIONS ***
   public double getCapacitanceFPerCm() {
       return capacitance();
   }
   
   public double getPermitivityFPerCm() {
      return dielectricConstant * Constant.PermitivityOfFreeSpace_cm;
   }

   public double getPermitivityFPerM() {
      return dielectricConstant * Constant.PermitivityOfFreeSpace_m;
   }

   public double getWorkFunction() {
      return electronAffinity + bandGap / 2 + phiF();
   }
   
   public double phiF() {
      if (dopingType == true) { // p-type
         return thermalVoltage * Math.log(dopantConcentration / this.intrinsicCarrierConcentration);
      }
      else { // n-type
         return -thermalVoltage * Math.log(dopantConcentration / this.intrinsicCarrierConcentration);
      }
   }

   public static double tempBandGap(String expr, double value) { // returns band gap at temp = value    
      return Functions.evaluateExpression(expr, 'T', value);
   }

   //returns intrinsic carrier concentration at temp = value
   public static double tempNi(String expr, double value) {
      return Functions.evaluateExpression(expr, 'T', value);
   }

   // return charge given the potential Y inside the S/C (cm-3)
   public double chargeY(double potential) {
      if (dopingType == true) { // p-type
         return Constant.ElectronCharge * (freeHoles()
            * Math.exp(-potential / thermalVoltage) - freeElectrons()
            * Math.exp(potential / thermalVoltage) - dopantConcentration);
      }
      else { // n-type
         return -Constant.ElectronCharge * (freeElectrons()
            * Math.exp(potential / thermalVoltage) - freeHoles()
            * Math.exp(-potential / thermalVoltage) - dopantConcentration);
      }
   }

   public double freeElectrons() { // (cm-3)
      if (dopingType == true) { // p-type
         return this.intrinsicCarrierConcentration * this.intrinsicCarrierConcentration / this.dopantConcentration;
      }
      else { // n-type
         return this.dopantConcentration;
      }
   }

   public double freeHoles() { // (cm-3)
      if (dopingType == false) { // n-type
         return this.intrinsicCarrierConcentration * this.intrinsicCarrierConcentration / this.dopantConcentration;
      }
      else { // p-type
         return this.dopantConcentration;
      }
   }

   public double charge(double surfacePotential) {
      double value;

      // acceptor doping
      if (dopingType == true) { // p-type
         value = Math.sqrt(2 * Constant.ElectronCharge * Constant.PermitivityOfFreeSpace_cm * this.dielectricConstant *
            this.dopantConcentration) * Math.sqrt(thermalVoltage * Math.exp(-surfacePotential /
            thermalVoltage) + surfacePotential - thermalVoltage + Math.exp(-2 * phiF() /
            thermalVoltage) * (thermalVoltage * Math.exp(surfacePotential /
            thermalVoltage) - surfacePotential - thermalVoltage));

         if (surfacePotential >= 0) {
            return -value + qit(surfacePotential);
         }

         return value + qit(surfacePotential);
      }
      // donar doping
      else { // n-type
         value = Math.sqrt(2 * Constant.ElectronCharge * Constant.PermitivityOfFreeSpace_cm * this.dielectricConstant *
            this.dopantConcentration) * Math.sqrt(thermalVoltage * Math.exp(surfacePotential /
            thermalVoltage) - surfacePotential - thermalVoltage + Math.exp(2 * phiF() /
            thermalVoltage) * (thermalVoltage * Math.exp(-surfacePotential /
            thermalVoltage) - surfacePotential - thermalVoltage));

         if (surfacePotential >= 0) {
            return -value + qit(surfacePotential);
         }

         return value + qit(surfacePotential);         
      }
   }

   public double surfacePotential(double charge) {
      double highPotential = bandGap * 3;
      double lowPotential = 0 - 2 * bandGap;
      double guessPotential = (highPotential + lowPotential) / 2;
      double guessCharge = charge(guessPotential);

      for (int iterationNumber = 0; ((charge - Math.abs(charge * 1e-6) > guessCharge) || (guessCharge > charge + Math.abs(charge * 1e-6))) && (iterationNumber < 1000); iterationNumber++) {
         if (guessCharge > charge) {
            lowPotential = guessPotential;
         }
         else {
            highPotential = guessPotential;
         }
         guessPotential = (highPotential + lowPotential) / 2;
         guessCharge = charge(guessPotential);
      }
      return guessPotential;
   }

   private double capacitance() {
      // capacitance value taken from Tsividis
      double numerator;
      double denominator;
      double value;

      // get the surface potential
      double phiS = surfacePot;

      if (dopingType == true) { // p-type
         numerator = 1 - Math.exp(-phiS / thermalVoltage) + Math.exp(-2 * phiF() /
            thermalVoltage) * (Math.exp(phiS / thermalVoltage) - 1);

         denominator = 2 * Math.sqrt(thermalVoltage * Math.exp(-phiS /
            thermalVoltage) + phiS - thermalVoltage + Math.exp(-2 * phiF() /
            thermalVoltage) * (thermalVoltage * Math.exp(phiS /
            thermalVoltage) - phiS - thermalVoltage));

         value = Math.sqrt(2 * Constant.ElectronCharge * Constant.PermitivityOfFreeSpace_cm * dielectricConstant *
            dopantConcentration) * numerator/denominator;

         if (phiS > 0) {
            return value + citFPerCm(phiS);
         }
         else {
            return -value + citFPerCm(phiS); // add capacitance due to interface traps
         }
      }
      else { // n-type
         numerator = 1 - Math.exp(phiS / thermalVoltage) + Math.exp(2 * phiF() /
            thermalVoltage) * (Math.exp(-phiS / thermalVoltage) - 1);

         denominator = 2 * Math.sqrt(thermalVoltage * Math.exp(phiS /
            thermalVoltage) - phiS - thermalVoltage + Math.exp(2 * phiF() /
            thermalVoltage) * (thermalVoltage * Math.exp(-phiS /
            thermalVoltage) + phiS - thermalVoltage));

         value = Math.sqrt(2 * Constant.ElectronCharge * Constant.PermitivityOfFreeSpace_cm * dielectricConstant *
            dopantConcentration) * numerator / denominator;

         if (phiS > 0) {
            return -value + citFPerCm(phiS);
         }
         else {
            return value + citFPerCm(phiS); // add capacitance due to interface traps
         }
      }
   }

   public double electricField(double potential) {
      double value;

      if (dopingType == true) { // p-type
         value = Math.sqrt(2 * Constant.ElectronCharge * dielectricConstant * Constant.PermitivityOfFreeSpace_cm *
         dopantConcentration) / (Constant.PermitivityOfFreeSpace_cm * dielectricConstant) *
         Math.sqrt(thermalVoltage * Math.exp(-potential / thermalVoltage) + potential -
         thermalVoltage + Math.exp(-2 * phiF() / thermalVoltage) * (thermalVoltage *
         Math.exp(potential / thermalVoltage) - potential - thermalVoltage));
         if (potential >= 0) {
            return value;
         }
         return -value;
      }
      else { // n-type
         value = Math.sqrt(2 * Constant.ElectronCharge * dielectricConstant * Constant.PermitivityOfFreeSpace_cm *
         dopantConcentration) / (Constant.PermitivityOfFreeSpace_cm * dielectricConstant) *
         Math.sqrt(thermalVoltage * Math.exp(potential / thermalVoltage) - potential -
         thermalVoltage + Math.exp(2 * phiF() / thermalVoltage) * (thermalVoltage *
         Math.exp(-potential / thermalVoltage) + potential - thermalVoltage));
         if (potential >= 0) {
            return value;
         }
         return -value;
      }
   }
   
   public Double depletionCharge(double SurfacePotential) {
      if (dopingType == true) { // p-type material
         if (SurfacePotential > 0) {
            return -Math.sqrt(2 * Constant.ElectronCharge * dielectricConstant *
               Constant.PermitivityOfFreeSpace_cm * dopantConcentration) *
               Math.sqrt(SurfacePotential - thermalVoltage);
         }
         else { // no depletion is occuring
            return 0.0;
         }
      }
      else { // n-type material
      // need to finish *****************************************************************************

      return 0.0;
      }
   }

   public double dit(double energy) {
      if (ditType == Constant.NONE) {
         return 0.0;
      }
      else {
         if (ditType == Constant.CONSTANT) {
            if (energy < -electronAffinity - bandGap) {
               return 0.0;
            }
            if (energy > -electronAffinity) {
               return 0.0;
            }
            return nit / bandGap;
         }
         else {
            if (ditType == Constant.PARABOLIC) {
               if (energy < -electronAffinity - bandGap) {
                  return 0.0;
               }
               if (energy > -electronAffinity) {
                  return 0.0;
               }
               double offset = energy + electronAffinity + bandGap;
               return 12 * (-co * bandGap + nit) / Math.pow(bandGap,3) *
               Math.pow(offset - bandGap / 2,2) + co;
            }
            else {
               if (ditType == Constant.GAUSSIAN) {
                  if (o1 == 0 || o2 == 0) {
                     return 0.0;
                  }

                  return nit * per1 / (o1 * Constant.Sqrt2Pi) * Math.exp(-Math.pow(energy - u1, 2) /
                     (2 * o1 * o1)) + nit * (1 - per1) / (o2 * Constant.Sqrt2Pi) *
                     Math.exp(-Math.pow(energy - u2, 2) / (2 * o2 * o2));
               }
            }
         }
      }
      return 0.0;
   }

   public double citFPerCm(double phiS) {
      if (ditType == Constant.NONE) {
         return 0.0;
      }
      else {
         if (ditType == Constant.CONSTANT) {
            double offset;
            if (getPType() == true) {
               offset = bandGap / 2 - phiF() + phiS;
            }
            else {
               offset = bandGap / 2 - phiF() + phiS;
            }
            if (offset < 0) {
               return 0.0;
            }
            if (offset > bandGap) {
               return 0.0;
            }
            return Constant.ElectronCharge * nit / bandGap;
         }
         else {
            if (ditType == Constant.PARABOLIC) {
               double offset;
               if (getPType() == true) {
                  offset = bandGap / 2 - phiF() + phiS;
               }
               else {
                  offset = bandGap / 2 - phiF() + phiS;
               }
               if (offset < 0) {
                  return 0.0;
               }
               if (offset > bandGap) {
                  return 0.0;
               }
               return Math.abs(Constant.ElectronCharge * (12 * (-co * bandGap + nit) /
                  Math.pow(bandGap, 3) *
                  Math.pow(offset - bandGap / 2, 2) + co));
            }
            else {
               if (ditType == Constant.GAUSSIAN) {
                  double offset;
                  if (getPType() == true) {
                     offset = -electronAffinity - bandGap / 2 - phiF() + phiS;
                  }
                  else {
                     offset = -electronAffinity - bandGap / 2 - phiF() + phiS;
                  }

                  if (o1 == 0.0 || o2 == 0.0) {
                     return 0.0;
                  }

                  return Constant.ElectronCharge*Math.abs(nit * per1 / (o1 * Constant.Sqrt2Pi) * Math.exp(-Math.pow(offset - u1, 2) /
                     (2 * o1 * o1)) + nit * (1 - per1) / (o2 * Constant.Sqrt2Pi) *
                     Math.exp(-Math.pow(offset - u2, 2) / (2 * o2 * o2)));
               }
            }
         }
      }
      return 0.0;
   }

   public double qit(double phiS) {
      if (ditType == Constant.NONE) {
         return 0.0;
      }
      else {
         if (ditType == Constant.CONSTANT) {
            double offset;
            if (getPType() == true) {
               offset = bandGap / 2 - phiF() + phiS;
            }
            else {
               offset = bandGap / 2 - phiF() + phiS;
            }
            if (offset < 0) {
               return 0.0;
            }
            if (offset > bandGap) {
               return -Constant.ElectronCharge * nit;
            }
            return -Constant.ElectronCharge* (offset) * nit / bandGap;
         }
         else {
            if (ditType == Constant.PARABOLIC) {
               double offset;
               if (getPType() == true) {
                  offset = bandGap / 2 - phiF() + phiS;
               }
               else {
                  offset = bandGap / 2 - phiF() + phiS;
               }
               if (offset < 0) {
                  return 0.0;
               }
               if (offset > bandGap) {
                  return -Constant.ElectronCharge * nit;
               }

               return -Constant.ElectronCharge * (offset / Math.pow(bandGap, 3) *
                  (-2 * co * bandGap * (2 * Math.pow(offset, 2) - 3 * offset * bandGap +
                  bandGap * bandGap) + (4 * Math.pow(offset, 2) - 6 * offset * bandGap +
                  3 * bandGap * bandGap) * nit));
            }
            else {
               if (ditType == Constant.GAUSSIAN) {
                  double offset = -electronAffinity - bandGap / 2 - phiF() + phiS;
                  return -Constant.ElectronCharge * (nit * getPer1() / 2 * (1 + BandMath.erf((offset - u1) / (o1 *
                     Constant.Sqrt2))) + nit * (1 - getPer1())) / 2 * (1 + BandMath.erf((offset - u2) / (o2 *
                     Constant.Sqrt2)));
               }
            }
         }
      }
      return 0.0;
   }

   public double ditMaxX() {
      double buffer = 0.4;
      if (ditType == Constant.NONE) {
         return -electronAffinity + buffer;
      }
      else {
         if (ditType == Constant.CONSTANT) {
            return -electronAffinity + buffer;
         }
         else {
            if (ditType == Constant.PARABOLIC) {
               return -electronAffinity + buffer;
            }
            else {
               if (ditType == Constant.GAUSSIAN) {
                  double max = u1 + 4 * o1;
                  if (max < u2 + 4 * o2) {
                     max = u2 + 4 * o2;
                  }
                  if (max < -electronAffinity + buffer) {
                     max = -electronAffinity + buffer;
                  }
                  return max;
               }
               else {
                  return -electronAffinity + buffer;
               }
            }
         }
      }
   }

   public double ditMinX() {
      double buffer = 0.4;
      if (ditType == Constant.NONE) {
         return -electronAffinity - bandGap - buffer;
      }
      else {
         if (ditType == Constant.CONSTANT) {
            return -electronAffinity - bandGap - buffer;
         }
         else {
            if (ditType == Constant.PARABOLIC) {
               return -electronAffinity - bandGap - buffer;
            }
            else {
               if (ditType == Constant.GAUSSIAN) {
                  double min = u1 - 4 * o1;
                  if (min > u2 - 4 * o2) {
                     min = u2 - 4 * o2;
                  }
                  if (min > -electronAffinity - bandGap - buffer) {
                     min = -electronAffinity - bandGap - buffer;
                  }
                  return min;
               }
               else {
                  return -electronAffinity - bandGap - buffer;
               }
            }
         }
      }
   }

   public double maxDit() {
      if (ditType == Constant.NONE) {
         return 0;
      }
      else {
         if (ditType == Constant.CONSTANT) {
            return nit / bandGap;
         }
         else {
            if (ditType == Constant.PARABOLIC) {
               return 12 * (-co * bandGap + nit) / Math.pow(bandGap, 3) *
                  Math.pow(bandGap / 2, 2) + co;
            }
            else {
               if (ditType == Constant.GAUSSIAN) {
                  return nit * per1 / (o1 * Constant.Sqrt2Pi) + nit * (1 - per1) / (o2 * Constant.Sqrt2Pi);
               }
               else {
                  return 0;
               }
            }
         }
      }
   }
   
   @Override
   public double getElectricFieldAtLocation(double nm) {
       for(EvalPoint p : point) {
           if(nm < p.getLocationNm()) {
               return p.getElectricFieldMv();
           }
       }
       return 0;
   }
   
   @Override
   public double getPotentialAtLocation(double nm) {
       for(EvalPoint p : point) {
           if(nm < p.getLocationNm()) {
               return p.getPotential();
           }
       }
       return 0;
   }
}