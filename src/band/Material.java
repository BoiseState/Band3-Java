package band;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Color;
import java.util.LinkedList;

public abstract class Material implements Cloneable, Comparable {
   protected String name;
   protected LinkedList<EvalPoint> point;
   protected String notes;
   protected Color fillColor;
   protected double thickness; //cm
   
   protected Material() {
       this.point = new LinkedList<EvalPoint>();
   }
   
   protected Material(String name, double thickness, LinkedList<EvalPoint> point, String notes, Color fillColor) {
       this.name        = name;
       this.notes       = notes;
       this.fillColor   = fillColor;
       this.thickness   = thickness;
       
       this.point       = new LinkedList<EvalPoint>();
       
       // Copy the points - must do it individually
       EvalPoint addPoint;
       for (EvalPoint tempPoint : point) {
           addPoint = new EvalPoint(tempPoint);
           this.point.add(addPoint);
      }
   }

   public String                getName(                            ) { return name;        }
   public void                  setName(String n                    ) { 
       String oldName = name;
       name = n;
       propertyChangeSupport.firePropertyChange("name", oldName, name);
   }

   public Color                 getFillColor(                       ) { return fillColor;   }
   public void                  setFillColor(Color c                ) { 
       Color oldFillColor = fillColor;
       fillColor = c;
       propertyChangeSupport.firePropertyChange("fillColor", oldFillColor, fillColor);
   }
   
   public String                getNotes(                           ) { return notes;       }
   public void                  setNotes(String n                   ) { 
       String oldNotes = notes;
       notes = n;
       propertyChangeSupport.firePropertyChange("notes", oldNotes, notes);
   }
   
   public LinkedList<EvalPoint> getPoint(                           ) { return point;       }
   public void                  setPoint(LinkedList<EvalPoint> in   ) { point = in;         }
   
   public double                getThickness(                       ) { return thickness;   }
   public void                  setThickness(double t               ) {
       double oldThickness = thickness;
       thickness = t;
       propertyChangeSupport.firePropertyChange("thickness", oldThickness, thickness);
   }
   
   public double                getThicknessNm(                     ) { return thickness*1E7;}
   public void                  setThicknessNm(double t             ) {
       double oldThickness = thickness;
       thickness = t * 1E-7;
       propertyChangeSupport.firePropertyChange("thickness", oldThickness, thickness);
   }
   
   public abstract double   getEnergyFromVacuumToTopBand();

   public abstract double   getEnergyFromVacuumToBottomBand();

   public abstract double   getEnergyFromVacuumToEfi();
   
   public abstract void     prepare();
   
   public abstract double   getElectricFieldAtLocation(double nm);
   
   public abstract double   getPotentialAtLocation(double nm);
   
   @Override
   public abstract Material clone();

   protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.removePropertyChangeListener(listener);
   }
   
   @Override
   public int compareTo(Object obj) {
       if (this.equals(obj)) {
           return 0;
       }
       if (obj == null) {
           return -1;
       }
       if(getClass() != obj.getClass()) {
           return -1;
       }
       final Material other = (Material) obj;
       if(this.name == null) {
           if(other.name != null) {
               return 1;
           }
           else {
               return 0;
           }
       }
       
       return(this.name.compareTo(other.name));
   }
}
