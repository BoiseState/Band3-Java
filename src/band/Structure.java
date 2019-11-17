package band;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import org.jfree.data.xy.*;
import java.util.Iterator;
import javax.swing.text.BadLocationException;

public class Structure implements Serializable, Cloneable, Iterable {
   private List<Material> structure;
   public int numLayers;

   public Structure() {
      structure = new LinkedList<Material>();
      numLayers = 0;
   }

   public Structure(List<Material> passStructure) {
      structure = passStructure;
      numLayers = passStructure.size();
   }
   
   public void addLayer(Material m) {
       structure.add(m);
       numLayers++;
   }
   
   public void addLayer(int i, Material m) {
       structure.add(i, m);
       numLayers++;
   }
   
   public Material removeLayer(int index) {
       numLayers--;
       return structure.remove(index);
   }
   
   public void clear() {
       structure.clear();
       numLayers = 0;
   }
   
   public void moveLayerUp(int index) {
       if(index < 1)
           return;
       Material m = structure.remove(index);
       structure.add(index-1, m);
   }
   
   public void moveLayerDown(int index) {
       if(index >= structure.size())
           return;
       Material m = structure.remove(index);
       structure.add(index+1, m);
   }
   
   public Material getLayer(int index) {
       return structure.get(index);
   }
   
   public Material getTopLayer() {
       return structure.get(0);
   }
   
   public Material getBottomLayer() {
       return structure.get(structure.size() - 1);
   }
   
   public Material getLayerAtThickness(double nm) throws BadLocationException {
        double thickness = 0;
        if(nm < 0) {
            throw new BadLocationException("Can't have a negative location!",(int)nm);
        }
        for (int i=0; i < structure.size(); i++) {
            thickness += structure.get(i).getThicknessNm();
            if( nm < thickness)
                return structure.get(i);
        }
        
        throw new BadLocationException("Structure isn't that thick!",(int)nm);
   }
   
   public Material getLayerAbove(Material m) {
       int i = structure.indexOf(m);
       if(i > 0) {
        return structure.get(i-1);
       }
       else {
           return null;
       }
   }
   
   public Material getLayerBelow(Material m) {
       int i = structure.indexOf(m);
       if(i < structure.size() -1) {
           return structure.get(i+1);
       }
       else {
           return null;
       }
   }
   
   public int indexOf(Material m) {
       return structure.indexOf(m);
   }
   
   public boolean isMetalTopLayer() {
       return this.getTopLayer() instanceof Metal;
   }
   
   public boolean isSemiconductorBottomLayer() {
       return this.getBottomLayer() instanceof Semiconductor;
   }
   
   public boolean isMetalBottomLayer() {
       return this.getBottomLayer() instanceof Metal;
   }
   
   @Override
   public Iterator<Material> iterator() {
       return structure.iterator();
   }
   
   /**
    * 
    * @return 
    */
   public List<Material> getMaterialList() {
       return structure;
   }
   
   @Override
   public Structure clone() {
       Structure temp = new Structure();
       for(Material m : structure)
           temp.addLayer(m.clone());
       return temp;
   }

   public double getThicknessNm() {
       double total_thickness = 0;
       for(Material m : structure) {
           total_thickness += m.getThicknessNm();
       }
       return total_thickness;
   }

   public double workFunctionDifference() {
        // Get top and bottom layers of structure
        Material topLayer       = structure.get(0);
        Material bottomLayer    = structure.get(structure.size() - 1); 
        

        double value = 0;
        if(topLayer instanceof Metal) {
            value = ((Metal)topLayer).getWorkFunction();
        } else if(topLayer instanceof Semiconductor) {
            value = ((Semiconductor)topLayer).getWorkFunction();
        }


        // subtract the bottom workfunction
        if (bottomLayer instanceof Metal) {
            value -= ((Metal)bottomLayer).getWorkFunction();
        }
        else {
            value -= ((Semiconductor)bottomLayer).getWorkFunction();
        }

        return value;
   }
   
   public boolean isValid() {
      // Make sure there is at least 1 material
      if (structure.isEmpty()) {
         return false;
      }

      // Make sure the top and bottom of the structure don't have a dielectric
      if (this.getTopLayer() instanceof Dielectric) {
         // Display error
         // MessageBox: An oxide cannot be a starting material!

         // Invalidate structure
         return false;
      }

      if (this.getBottomLayer() instanceof Dielectric) {
         // Display error
         // MessageBox: An oxide cannot be an end material!

         // Invalidate structure
         return false;
      }

      // Make sure the top structure is not a semiconductor
      if (this.getTopLayer() instanceof Semiconductor) {
         // Display error
         // MessageBox: A semiconductor cannot be a starting material!

         // Invalidate structure
         return false;
      }
      
      // Make sure there are no semiconductors except at the very end
      for (int i = 0; i < structure.size() - 1; i++) {
         if (structure.get(i) instanceof Semiconductor ) {
             // display error
             // MessageBox: A semiconductor can only be an end material!

            // Invalidate Structure
            return false;
         }
      }

      // Make sure two metals are not next to each other
      boolean metalExist = false;
      for (Material m : structure) {
         if (m instanceof Metal) {
            if (metalExist == true)
               return false;
            else
               metalExist = true;
         }
         else
            metalExist = false;
      }
      
      return true;
   }
   
   public void Evaluate(double voltageBias, double temperature) throws Exception {
      int iterationNumber = 0;
      int maxIterations = 1000;

      // Since we integrate left to right and we want to specify the voltage on the left
      voltageBias = -voltageBias;

      Semiconductor evalSemiconductor;
      
      // check to see if we have a semiconductor at the end if we do set the temp.
      if (this.getBottomLayer() instanceof Semiconductor) {
         ((Semiconductor)this.getBottomLayer()).setTemperature(temperature);
      }

      voltageBias += this.workFunctionDifference(); // Add the work function difference to the calculated potential

      // Iterate until we find the desired voltage
      double chargeHigh = 1;
      double chargeLow = -1;
      double chargeGuess = (chargeHigh + chargeLow) / 2;

      // !!!!!!!!!!!!!!!!!!!!
      this.evaluateGivenCharge(chargeGuess);
      // !!!!!!!!!!!!!!!!!!!!
      
      double potentialCalc = this.getBottomLayer().getPoint().get(1).getPotential();

      // Iterate
      for (iterationNumber = 0; (potentialCalc > voltageBias + Math.abs(voltageBias*1e-6) + 1e-6
            || potentialCalc < voltageBias - Math.abs(voltageBias*1e-6) - 1e-6)
            && (iterationNumber < maxIterations); iterationNumber++) {
         if (potentialCalc > voltageBias) {
            chargeLow = chargeGuess;
         }
         else {
            chargeHigh = chargeGuess;
         }

         // update the guessCharge
         chargeGuess = (chargeHigh + chargeLow) / 2;

         // !!!!!!!!!!!!!!!!!!!!!!
         this.evaluateGivenCharge(chargeGuess);
         // !!!!!!!!!!!!!!!!!!!!!!
         
         potentialCalc = this.getBottomLayer().getPoint().get(1).getPotential();

         if (iterationNumber == maxIterations - 1) {
            if (!(potentialCalc > voltageBias + 1e-3 || potentialCalc < voltageBias - 1e-3)) {
               // Message: Solution found only to accuracy to 1e-3 of desired voltage!
            }
            else {
               throw new Exception("Could not find a solution! Max number of iterations reached.");
            }
         }
      }

      // If the last material is a semiconductor fill in the mission points
      if (this.getBottomLayer() instanceof Semiconductor) {
         // get the semiconductor
         evalSemiconductor = (Semiconductor)this.getBottomLayer();

         EvalPoint point;

         double storePotential = evalSemiconductor.getPoint().get(0).getPotential();

         // First remove all the points
             evalSemiconductor.getPoint().clear();

         // Calculate how long the semiconductor should be for the band bending
         // Set the max width to 1.25 times the max dep width
         double depCharge = evalSemiconductor.depletionCharge(evalSemiconductor.getSurfacePot());
         double thickness = Math.abs(depCharge / (Constant.ElectronCharge * evalSemiconductor.getDopantConcentration()));

         // Find the thickness through integration
         // integrate from 0 to the surface potential
         // integrate 2000 times so change stepSize depending on the surface potential
         // double stepSize = 0.001; // potential step
         double stepSize = evalSemiconductor.getSurfacePot() / 2000;

         if (evalSemiconductor.getSurfacePot() > 0) {
            stepSize = Math.abs(stepSize);
         }
         else {
            stepSize = -Math.abs(stepSize);
         }

         double previousValue = 1 /evalSemiconductor.electricField(evalSemiconductor.getSurfacePot());
         double value = 1 / evalSemiconductor.electricField(evalSemiconductor.getSurfacePot() - stepSize);
         double runningThickness = 0;
         double previousThickness = 0;
         double potentialValue;
         point = new EvalPoint(runningThickness, evalSemiconductor.chargeY(evalSemiconductor.getSurfacePot()) *
            1e-8, 1 / previousValue, evalSemiconductor.getSurfacePot());

         evalSemiconductor.getPoint().add(point);

         for (int i = 1; Math.abs(evalSemiconductor.getSurfacePot() - stepSize * i) > 0.001 && i < 100000; i++) {
            potentialValue = evalSemiconductor.getSurfacePot() - stepSize * i;
            value = 1 / evalSemiconductor.electricField(potentialValue);
            previousThickness = runningThickness;
            runningThickness += ((previousValue + value) / 2) * stepSize;
            point = new EvalPoint(runningThickness, evalSemiconductor.chargeY(potentialValue) *
               1e-8, 1 / value, potentialValue);
            evalSemiconductor.getPoint().add(point);
            previousValue = value;
         }

         // Now add the offset in potential
         double checkCharge = 0; // check and see if the charges add up
           for(EvalPoint itr: evalSemiconductor.getPoint()){
            itr.setPotential(itr.getPotential() + storePotential);
            itr.setPotential(itr.getPotential() - evalSemiconductor.getSurfacePot());
            checkCharge += itr.getCharge();
           }
      }

      // now subtract the potential from all the points so the right is ref to 0
      int lastPoint = this.getBottomLayer().getPoint().size() - 1;
      double potential = this.getBottomLayer().getPoint().get(lastPoint).getPotential();
      for (Material m: structure)
          for (EvalPoint e: m.getPoint()){
              e.setPotential(e.getPotential() - potential);
          }

      EvalPoint last = this.getBottomLayer().getPoint().get(lastPoint);
      EvalPoint trueLast = new EvalPoint(last);
      if (trueLast.getLocationNm() < 50) {
         trueLast.setLocationNm(50);
      }
      this.getBottomLayer().getPoint().add(trueLast);
   }
   
   // Calculate based on top charge return running charge
   public double evaluateGivenCharge(double topCharge) throws Exception {
      // Evaluate the structure given a top charge

      EvalPoint point;

      // Set the top metal to have a charge at the bottom (location = thickness)
      Metal topMetal = (Metal)this.getTopLayer();
      topMetal.prepare();

      // Set the first point to all zeros
      point = new EvalPoint(0,0,0,0);
      topMetal.getPoint().set(0, point);

      // Add charge to the last point
      point = new EvalPoint(topMetal.getThickness(), topCharge, 0, 0);
      topMetal.getPoint().set(1, point);

      // Now integrate the charges to get the electric field in all the dielectrics
      Dielectric evalDielectric;
      Metal evalMetal;
      double runningCharge = topCharge;
      double runningPotential = 0;
      for (int i = 1; i < structure.size() - 1; i++) { // assume the last is a metal as well
         // Check to see what kind of a material we are dealing with
         if (structure.get(i) instanceof Dielectric) {
            // Get the oxide
            evalDielectric = (Dielectric)structure.get(i);
            // Prep the Dielectric (oxide)
            evalDielectric.prepare();

            for (int j = 0; j < evalDielectric.getPoint().size(); j++) {

               // Integrate the charge (sum really)
               runningCharge += evalDielectric.getPoint().get(j).getCharge();

               // Calculate the Electric Field
               if (evalDielectric.getDielectricConstantExpression() != null) {
                  if (evalDielectric.getDielectricConstantExpression().contains("F")) {
                     // here we need to do some fancy footwork for the dielectric dependent calculation
                     // let's use slope of the function to find the dielectric constant
                     double highEField = 1e3;
                     double lowEField = -1e3;
                     double eFieldGuess = (highEField + lowEField) / 2;
                     double calcEField = runningCharge / evalDielectric.eFieldPermittivityFPerCm(eFieldGuess);
                     boolean bHighInRange = false;
                     boolean bLowInRange = false;
                     double highrange = 0;
                     double lowrange = 0;
                     for (int k = 0; k < 1000; k++) {
//                         highrange = eFieldGuess + Math.abs(eFieldGuess*1e-6) + 1e-6;
//                         lowrange = eFieldGuess + Math.abs(eFieldGuess*1e-6) - 1e-6;
//                         System.out.printf("High range = " + highrange + "\n");
//                         System.out.printf("calcEField = " + calcEField + "\n");
//                         System.out.printf("Low range = " + lowrange + "\n");
                        if (eFieldGuess + Math.abs(eFieldGuess * 1e-6) + 1e-6 > calcEField &&
                              eFieldGuess - Math.abs(eFieldGuess * 1e-6) - 1e-6 < calcEField) {
                           break;
                        }

                        // Check range of numbers to search within
                        if (bHighInRange == false) {
                           if (eFieldGuess < calcEField) {
                              // Could not find a solution in the range of interest
                              highEField = highEField * 1000; // increase the range
                           }
                           else {
                              // solution is in range
                              bHighInRange = true;
                           }
                        }

                        if (bLowInRange == false) {
                           if (eFieldGuess > calcEField) {
                              // could not find a solution in the range of interest
                              lowEField = lowEField * 1000; // increase the range
                           }
                           else {
                              // solution is in range
                              bLowInRange = true;
                           }
                        }

                        // Check to see if we could not converge within 1000 iterations
                        if (k == 1000 - 1) {
                           throw new Exception("Could not converge electric field dependent dielectric constant!");
                        }

                        if (calcEField < eFieldGuess) {
                           highEField = eFieldGuess;
                        }
                        else {
                           lowEField = eFieldGuess;
                        }

                        eFieldGuess = (highEField + lowEField) / 2;
                        calcEField = runningCharge / evalDielectric.eFieldPermittivityFPerCm(eFieldGuess);
                     }
                     evalDielectric.getPoint().get(j).setElectricField(eFieldGuess);
                  }
                  else {
                     evalDielectric.getPoint().get(j).setElectricField(runningCharge / evalDielectric.getPermittivityFPerCm());
                  }
               }
               else {
                  evalDielectric.getPoint().get(j).setElectricField(runningCharge / evalDielectric.getPermittivityFPerCm());
               }

               // Calculate the potential
               if (j == 0) { // only for the first point
                  evalDielectric.getPoint().get(j).setPotential(runningPotential);
               }
               else {
                  runningPotential += -evalDielectric.getPoint().get(j-1).getElectricFieldM() *
                     (evalDielectric.getPoint().get(j).getLocationM() - evalDielectric.getPoint().get(j - 1).getLocationM());
                  evalDielectric.getPoint().get(j).setPotential(runningPotential);
               }
            }
         }
         else {
            if (structure.get(i) instanceof Metal) {
               // Get the metal
               evalMetal = (Metal)structure.get(i);
               // Prep the metal
               evalMetal.prepare();

               // For the first point put the neg of the charge we have accumulated so far
               evalMetal.getPoint().get(0).setCharge(-runningCharge);
               evalMetal.getPoint().get(0).setElectricField(0);
               evalMetal.getPoint().get(0).setPotential(runningPotential);

               // For the last point put the accumulated charge pulse the free charge
               runningCharge += evalMetal.getExtraCharge(); // Integrate the charge
               evalMetal.getPoint().get(1).setCharge(runningCharge);
               evalMetal.getPoint().get(1).setElectricField(0);
               evalMetal.getPoint().get(1).setPotential(runningPotential);
            }
            else { // we have a material that is not a metal or oxide
               // do nothing
            }
         }
      }

      // Now add the stuff for the last point - here we assume that it is a metal
      if (this.getBottomLayer() instanceof Metal) {
         // Get the metal
         evalMetal = (Metal)this.getBottomLayer();

         // Prep the metal
         evalMetal.prepare();

         // For the first point put the neg of the charge we have accumulated so far
         evalMetal.getPoint().get(0).setCharge(-runningCharge);
         evalMetal.getPoint().get(0).setElectricField(0);
         evalMetal.getPoint().get(0).setPotential(runningPotential);

         // For the last point put no charge
         evalMetal.getPoint().get(1).setCharge(0);
         evalMetal.getPoint().get(1).setElectricField(0);
         evalMetal.getPoint().get(1).setPotential(runningPotential);
      }
      else {
         if (this.getBottomLayer() instanceof Semiconductor) {
            // Get the semiconductor
            Semiconductor evalSemiconductor = (Semiconductor)this.getBottomLayer();

            // Calculate the surface potential given the charge
            evalSemiconductor.setSurfacePot(evalSemiconductor.surfacePotential(-runningCharge));

            // Prep the semiconductor
            evalSemiconductor.prepare();

            // Evaluate the potential drop given the remaining charge
            evalSemiconductor.getPoint().get(0).setCharge(-runningCharge);
            evalSemiconductor.getPoint().get(0).setPotential(runningPotential);

            // Last Point
            evalSemiconductor.getPoint().get(1).setPotential(runningPotential - evalSemiconductor.getSurfacePot());
         }
      }
      return runningCharge;
   }
   
   public double calculateVTH() throws Exception {
       
       // Check to see if we have a semiconductor
       if (this.getBottomLayer() instanceof Semiconductor) {
           // we have a semiconductor that we can calculate the threshold voltage from

           // make a deep copy of the structure so we don't ruin anything        
           Structure VBStructure = this.clone();

           Semiconductor tempSemiconductor = (Semiconductor)VBStructure.getBottomLayer();
           double surfacePotential = 2 * tempSemiconductor.phiF();
           double chargeAtVth = tempSemiconductor.charge(surfacePotential);
           double trappedCharge = 0;

           // get all the charge in the metals and dielectrics
           for(int i = 1; i < VBStructure.structure.size() - 1;i++) {
               if(VBStructure.structure.get(i) instanceof Metal)
                   trappedCharge += ((Metal)VBStructure.structure.get(i)).getExtraCharge();
               if(VBStructure.structure.get(i) instanceof Dielectric)
                   for(EvalPoint p : VBStructure.structure.get(i).point)
                       trappedCharge += p.getCharge();
           }

           VBStructure.evaluateGivenCharge(-chargeAtVth - trappedCharge);

           double thresholdVoltage = VBStructure.workFunctionDifference() -
              VBStructure.getBottomLayer().getPoint().get(1).getPotential();
           return thresholdVoltage;
       }
       else {
           // Messagebox: There is no semiconductor in the stack. A threshold voltage could not be calculated.
           return 0;
       }
   }
   
   public double calculateVFB() throws Exception {
      // make a deep copy of the structure so we don't ruin anything
      Structure VBStructure = this.clone();
      double semiconductorCharge = 0;

      // If the end material is a semiconductor
      if (VBStructure.getBottomLayer() instanceof Semiconductor) {
         // get the semiconductor charge for a surface potential of zero
         Semiconductor tempSemiconductor = (Semiconductor)VBStructure.getBottomLayer();
         semiconductorCharge = tempSemiconductor.charge(0);
      }

      double runningTrapCharge = 0;
      
      // Now add up all the trap charge in the structure
      for (int i = 1; i < VBStructure.structure.size() -1; i++) {
         if (VBStructure.structure.get(i) instanceof Metal) {
             runningTrapCharge += ((Metal)VBStructure.structure.get(i)).getExtraCharge();
         }
         if (VBStructure.structure.get(i) instanceof Dielectric) {
             for (EvalPoint p : VBStructure.structure.get(i).point)
                 runningTrapCharge += p.getCharge();
         }
         else {
             throw new Exception("Material exception unhandled in Flatband voltage calculation");
         }
      }

      VBStructure.evaluateGivenCharge(-runningTrapCharge - semiconductorCharge);

      double flatBandVoltage = VBStructure.workFunctionDifference() - VBStructure.getBottomLayer().getPoint().get(0).getPotential();
      return flatBandVoltage;
   }
   
    // Calculate the oxide capacitance
    public double cox() throws Exception {
        double oneOverCap = 0;

        for (int i = 1; i < structure.size() - 1; i++) {
            if (structure.get(i) instanceof Dielectric) {
                oneOverCap += 1 / ((Dielectric)structure.get(i)).getCoxFPerCm2();
            }
        }
        return 1 / oneOverCap;
    }
    
    // Calculate the stack capacitance // assumes the structure has already been evaluated
    public double stackCap() throws Exception {
        // stack capacitance
        double oneOverCap = 0;
        for (Material m : structure) {
            if (m instanceof Dielectric)
                oneOverCap += 1 / ((Dielectric)m).getCoxFPerCm2();
            
            if (m instanceof Semiconductor)
                oneOverCap += 1 / ((Semiconductor)m).getCapacitanceFPerCm();
        }

        return 1/oneOverCap;
    }

    public double calculateEotNm() throws Exception {
        return (3.9 * Constant.PermitivityOfFreeSpace_cm * 1e7) / this.cox();
    }
    
    public List<String> outputParametersTitles() throws Exception {
        ArrayList<String> outputParameters = new ArrayList<String>();

        outputParameters.add("Voltage (V)");
        outputParameters.add("Stack Capacitance (F/cm2)");
        outputParameters.add("Cox (F/cm2)");
        outputParameters.add("GateCharge (C/cm2)");

        // iterate through the structure
        for (Material m : structure) {
            if (m instanceof Metal) {
                outputParameters.add(m.getName() + " Potential (V)");
            }
            if (m instanceof Dielectric) {
               // iterate through each point
               for (EvalPoint p : m.point) {
                  outputParameters.add(m.getName() + " Potential (V) @ " +
                     p.getLocationNm() + "nm");
                  outputParameters.add(m.getName() + " EField (MV/cm) @ " +
                     p.getLocationNm() + "nm");
               }

               outputParameters.add(m.getName() + " Cap (F/cm2)");
               outputParameters.add(m.getName() + " Gate Tunnel Distance to CB (nm)");
               outputParameters.add(m.getName() + " Gate Tunnel Distance to VB (nm)");

               if (this.getBottomLayer() instanceof Semiconductor) {
                  outputParameters.add(m.getName() + " S/C CB TunnelDistance to CB (nm)");
                  outputParameters.add(m.getName() + " S/C VB TunnelDistance to VB (nm)");
                  outputParameters.add(m.getName() + " S/C Ef TunnelDistance to CB (nm)");
                  outputParameters.add(m.getName() + " S/C Ef TunnelDistance to VB (nm)");
               }
               else {
                  outputParameters.add(m.getName() + " Bottom Gate Tunnel Distance CB (nm)");
                  outputParameters.add(m.getName() + " Bottom Gate Tunnel Distance VB (nm)");
               }
            }
            if (m instanceof Semiconductor) {
              // record just the potential at 0nm an the surface potential
              outputParameters.add(m.getName() + " Potential (V) @ 0nm");
              outputParameters.add(m.getName() + " SurfacePotential (ev)");
              outputParameters.add(m.getName() + " Capacitance (F/cm2)");

              Semiconductor tempSemiconductor = (Semiconductor)m;
              if (tempSemiconductor.getDitType() == Constant.CONSTANT ||
                     tempSemiconductor.getDitType() == Constant.PARABOLIC || 
                     tempSemiconductor.getDitType() == Constant.GAUSSIAN) {
                 outputParameters.add("Qit (C/cm2)");
                 outputParameters.add("Cit (F/cm2)");
              }
            }
        }
        return outputParameters;
    }
    
    public List<Double> outputParameters(double voltage) throws Exception {
        List<Double> columnList = new ArrayList<Double>();

        Dielectric tempDielectric;
        Semiconductor refSemiconductor;

        double gateEnergy;
        double bottomGateEnergy = 0;
        double bottomCBEnergy = 0;
        double bottomVBEnergy = 0;
        double SCEfermi = 0;

        // add the parameters
        columnList.add(voltage);
        columnList.add(this.stackCap());
        columnList.add(this.cox());
        columnList.add(this.getTopLayer().getPoint().get(1).getCharge());

        gateEnergy = -this.getTopLayer().getPoint().get(0).getPotential() - this.getTopLayer().getEnergyFromVacuumToTopBand();

        if (this.getBottomLayer() instanceof Semiconductor) {
            bottomCBEnergy = -this.getBottomLayer().getPoint().get(0).getPotential() -
                this.getBottomLayer().getEnergyFromVacuumToTopBand();
            bottomVBEnergy = -this.getBottomLayer().getPoint().get(0).getPotential() -
                this.getBottomLayer().getEnergyFromVacuumToBottomBand();

            SCEfermi = -((Semiconductor)this.getBottomLayer()).getWorkFunction();
        }
        else { // bottom material is a metal
            bottomGateEnergy = -this.getBottomLayer().getPoint().get(0).getPotential() -
            this.getBottomLayer().getEnergyFromVacuumToTopBand();
        }

        // iterate through the structure2
        for (Material m : structure) {
            if (m instanceof Metal) {
                columnList.add(m.getPoint().get(0).getPotential());
            }
            if (m instanceof Dielectric) {
                // iterate through each point
                for (EvalPoint p : m.point) {
                    columnList.add(p.getPotential());
                    columnList.add(p.getElectricFieldMv());
                }

                tempDielectric = (Dielectric)m;
                columnList.add(tempDielectric.getCoxFPerCm2());
                columnList.add(tempDielectric.distanceToCBCm(gateEnergy) * 1e7);
                columnList.add(tempDielectric.distanceToVBCm(gateEnergy) * 1e7);

                // bottom material is a semiconductor
                if (this.getBottomLayer() instanceof Semiconductor) {
                    double tunnelDistance = tempDielectric.distanceToCBCm(bottomCBEnergy) * 1e7;
                    if (tunnelDistance == 0 || tunnelDistance == tempDielectric.getThicknessNm()) {
                    columnList.add(tunnelDistance);
                    }
                    else {
                    columnList.add(tempDielectric.getThicknessNm() - tunnelDistance);
                    }

                    tunnelDistance = tempDielectric.distanceToVBCm(bottomVBEnergy) * 1e7;
                    if (tunnelDistance == 0 || tunnelDistance == tempDielectric.getThicknessNm()) {
                    columnList.add(tunnelDistance);
                    }
                    else {
                    columnList.add(tempDielectric.getThicknessNm() - tunnelDistance);
                    }

                    tunnelDistance = tempDielectric.distanceToCBCm(SCEfermi) * 1e7;
                    if (tunnelDistance == 0 || tunnelDistance == tempDielectric.getThicknessNm()) {
                    columnList.add(tunnelDistance);
                    }
                    else {
                    columnList.add(tempDielectric.getThicknessNm() - tunnelDistance);
                    }

                    tunnelDistance = tempDielectric.distanceToVBCm(SCEfermi) * 1e7;
                    if (tunnelDistance == 0 || tunnelDistance == tempDielectric.getThicknessNm()) {
                    columnList.add(tunnelDistance);
                    }
                    else {
                    columnList.add(tempDielectric.getThicknessNm() - tunnelDistance);
                    }
                }
                else { // bottom material is a metal
                    double tunnelDistance = tempDielectric.distanceToCBCm(bottomGateEnergy) * 1e7;
                    if (tunnelDistance == 0 || tunnelDistance == tempDielectric.getThicknessNm()) {
                    columnList.add(tunnelDistance);
                    }
                    else {
                    columnList.add(tempDielectric.getThicknessNm() - tunnelDistance);
                    }
                    tunnelDistance = tempDielectric.distanceToVBCm(bottomGateEnergy) * 1e7;
                    if (tunnelDistance == 0 || tunnelDistance == tempDielectric.getThicknessNm()) {
                    columnList.add(tunnelDistance);
                    }
                    else {
                    columnList.add(tempDielectric.getThicknessNm() - tunnelDistance);
                    }
                }
            }
            if (m instanceof Semiconductor) {
                refSemiconductor = (Semiconductor)m;
                columnList.add(m.getPoint().get(0).getPotential());
                columnList.add(refSemiconductor.getSurfacePot());
                columnList.add(refSemiconductor.getCapacitanceFPerCm());

                if (refSemiconductor.getDitType() == Constant.CONSTANT ||
                    refSemiconductor.getDitType() == Constant.PARABOLIC ||
                    refSemiconductor.getDitType() == Constant.GAUSSIAN) {
                        columnList.add(refSemiconductor.qit(refSemiconductor.getSurfacePot()));
                        columnList.add(refSemiconductor.citFPerCm(refSemiconductor.getSurfacePot()));
                }
            }
        }

        return columnList;
    }
    
    public XYSeriesCollection getPotentialDataset() {
        XYSeriesCollection potential = new XYSeriesCollection();

        double pointX = 0;
        double pointY = 0;
        double thickness = 0;
        XYSeries series;
        for (int i = 0; i < structure.size(); i++) {
            series = new XYSeries(structure.get(i).getName());
            
            for (EvalPoint j : structure.get(i).getPoint()){
                pointX = j.getLocationNm() + thickness;
                pointY = j.getPotential();
                series.add(pointX, pointY);
            }
            if (i < structure.size() - 1) {
                pointY = structure.get(i + 1).getPoint().get(0).getPotential();
                series.add(pointX, pointY);
            }

            thickness += structure.get(i).getThicknessNm();
            potential.addSeries(series);
        }
        
        return potential;
    }
    
    public XYSeriesCollection getChargeDensityDataset() {
        XYSeriesCollection charge = new XYSeriesCollection();

        double pointX = 0;
        double pointY = 0;
        double thickness = 0;
        XYSeries series;
        for (int i = 0; i < structure.size(); i++) {
            series = new XYSeries(structure.get(i).getName());
            for (int j = 0; j < structure.get(i).getPoint().size(); j++) {
                if (structure.get(i) instanceof Semiconductor) {
                    if (j == 0) {
                        pointX = structure.get(i).getPoint().get(j).getLocationNm() + thickness;
                        series.add(pointX, 0);

                        pointY = structure.get(i).getPoint().get(j).getCharge();
                        series.add(pointX, pointY);
                    } else {
                        pointX = structure.get(i).getPoint().get(j).getLocationNm() + thickness;
                        pointY = structure.get(i).getPoint().get(j).getCharge();
                        series.add(pointX, pointY);
                    }
                } else {
                    pointX = structure.get(i).getPoint().get(j).getLocationNm() + thickness;
                    series.add(pointX, 0);

                    pointY = structure.get(i).getPoint().get(j).getCharge();
                    series.add(pointX, pointY);

                    pointX = structure.get(i).getPoint().get(j).getLocationNm() + thickness;
                    series.add(pointX, 0);
                }
            }

            thickness += structure.get(i).getThicknessNm();
            charge.addSeries(series);
        }
        
        return charge;
    }
    
    public XYSeriesCollection getElectricFieldDataset() {
        XYSeriesCollection eField = new XYSeriesCollection();
        double pointX = 0;
        double pointY = 0;
        double thickness = 0;
        XYSeries series;
        for (int i = 0; i < structure.size(); i++) {
            series = new XYSeries(structure.get(i).getName());
            if (i > 0) {
                pointY = structure.get(i - 1).getPoint().getLast().getElectricFieldMv();
                series.add(pointX, pointY);
            }

            for (EvalPoint j: structure.get(i).getPoint()){
                if (structure.get(i) instanceof Dielectric
                        && j != structure.get(i).getPoint().getLast()) {
                    pointX = j.getLocationNm() + thickness;
                    series.add(pointX, pointY);
                }
                pointX = j.getLocationNm() + thickness;
                pointY = j.getElectricFieldMv();
                series.add(pointX, pointY);
            }

            thickness += structure.get(i).getThicknessNm();
            eField.addSeries(series);
        }
        
        return eField;
    }
    
    public XYSeriesCollection getEnergyDataset() {
        XYSeriesCollection energy = new XYSeriesCollection();
        
        double pointX = 0;
        double pointY = 0;
        double thickness = 0;
        XYSeries series;
        for (int i = 0; i < structure.size(); i++) {
            if (structure.get(i) instanceof Metal) {
                series = new XYSeries(structure.get(i).getName());
                pointX = structure.get(i).getPoint().getFirst().getLocationNm() + thickness;
                pointY = -structure.get(i).getPoint().getFirst().getPotential() - structure.get(i).getEnergyFromVacuumToTopBand();
                series.add(pointX, pointY);
                pointX = structure.get(i).getPoint().getLast().getLocationNm() + thickness;
                pointY = -structure.get(i).getPoint().get(1).getPotential() - structure.get(i).getEnergyFromVacuumToTopBand();
                series.add(pointX, pointY);
                energy.addSeries(series);
            }
            if (structure.get(i) instanceof Dielectric) {
                series = new XYSeries(structure.get(i).getName(), false);
                // For first point
                pointX = structure.get(i).getPoint().getFirst().getLocationNm() + thickness;
                pointY = -structure.get(i).getPoint().getFirst().getPotential() - structure.get(i).getEnergyFromVacuumToTopBand();
                series.add(pointX, pointY);
                pointX = structure.get(i).getPoint().getFirst().getLocationNm() + thickness;
                pointY = -structure.get(i).getPoint().getFirst().getPotential() - structure.get(i).getEnergyFromVacuumToBottomBand();
                series.add(pointX, pointY);

                // for in between points
                for (EvalPoint j: structure.get(i).getPoint()){
                    if (j == structure.get(i).getPoint().getFirst())
                        continue;
                    
                    pointX = j.getLocationNm() + thickness;
                    pointY = -j.getPotential() - structure.get(i).getEnergyFromVacuumToBottomBand();
                    series.add(pointX, pointY);
                }

                // for the last point
                pointX = structure.get(i).getPoint().getLast().getLocationNm() + thickness;
                pointY = -structure.get(i).getPoint().getLast().getPotential() - structure.get(i).getEnergyFromVacuumToTopBand();
                series.add(pointX, pointY);

                // for in between points
               for (Iterator<EvalPoint> itr= structure.get(i).getPoint().descendingIterator();
                       itr.hasNext();){
                   EvalPoint j = itr.next();
                    pointX = j.getLocationNm() + thickness;
                    pointY = -j.getPotential() - structure.get(i).getEnergyFromVacuumToTopBand();
                    series.add(pointX, pointY);
                }

                pointX = structure.get(i).getPoint().getFirst().getLocationNm() + thickness;
                pointY = -structure.get(i).getPoint().getFirst().getPotential() - structure.get(i).getEnergyFromVacuumToTopBand();
                series.add(pointX, pointY);


                energy.addSeries(series);
            }
            if (structure.get(i) instanceof Semiconductor) {
                series = new XYSeries(structure.get(i).getName() + " - Conduction Band");
                for (EvalPoint j :structure.get(i).getPoint()){
                    pointX = j.getLocationNm() + thickness;
                    pointY = -j.getPotential() - structure.get(i).getEnergyFromVacuumToTopBand();
                    series.add(pointX, pointY);
                }
                energy.addSeries(series);

                series = new XYSeries(structure.get(i).getName() + " - Valance Band");
                for(EvalPoint j:structure.get(i).getPoint()){
                    pointX = j.getLocationNm() + thickness;
                    pointY = -j.getPotential() - structure.get(i).getEnergyFromVacuumToBottomBand();
                    series.add(pointX, pointY);
                }
                energy.addSeries(series);

                series = new XYSeries(structure.get(i).getName() + " - Fermi Level");
                //for (int j = 0; j < passStructure.get(i).getPoint().size(); j++) {
                for (EvalPoint j: structure.get(i).getPoint()){
                    pointX = j.getLocationNm() + thickness;
                    pointY = -j.getPotential() - structure.get(i).getEnergyFromVacuumToEfi();
                    series.add(pointX, pointY);
                }
                energy.addSeries(series);

                series = new XYSeries(structure.get(i).getName() + " - Work Function");
                for(EvalPoint j: structure.get(i).getPoint()) {
                    pointX = j.getLocationNm() + thickness;
                    pointY = -structure.get(i).getPoint().getLast().getPotential() - ((Semiconductor)structure.get(i)).getWorkFunction();
                    series.add(pointX, pointY);
                }

                energy.addSeries(series);
            }

            thickness += structure.get(i).getThicknessNm();
        }
        
        return energy;
    }
    
    public double getElectricFieldAtLocation(double nm) {
        double thickness = 0;
        double lastThickness = 0;
        for (int i=0; i < structure.size(); i++) {
            lastThickness = thickness;
            thickness += structure.get(i).getThicknessNm();
            if(nm < thickness)
                return structure.get(i).getElectricFieldAtLocation(nm - lastThickness);
        }
        
        return 0;
    }
    
    public double getPotentialAtLocation(double nm) {
        double thickness = 0;
        double lastThickness = 0;
        for (int i=0; i < structure.size(); i++) {
            lastThickness = thickness;
            thickness += structure.get(i).getThicknessNm();
            if(nm < thickness)
                return structure.get(i).getPotentialAtLocation(nm - lastThickness);
        }
        
        return 0;
    }
}
