package band;

public class Constant {
   public static int EDIT = 1;
   public static int METAL = 10;
   public static int DIELECTRIC = 11;
   public static int SEMICONDUCTOR = 12;

   public static int CHARGE = 20;
   public static int ELECTRICFIELD = 21;
   public static int POTENTIAL = 22;
   public static int ENERGY = 23;

   public static int NONE = 30;
   public static int CONSTANT = 31;
   public static int PARABOLIC = 32;
   public static int GAUSSIAN = 33;

   public static double PermitivityOfFreeSpace_m = 8.8541878176E-12;
   public static double PermitivityOfFreeSpace_cm = 8.8541878176E-14;
   public static double ElectronCharge = 1.602176487E-19;
   public static double BoltzmannsConstant = 1.3806504E-23; // (J/K)
   public static double Temperature = 300; //(K)
   public static double ThermalVoltage = BoltzmannsConstant * Temperature / ElectronCharge;
   public static double Sqrt2Pi = 2.50662827463100050241576528481;
   public static double Sqrt2 = 1.41421356237309504880168872421;
}
