package band;

public class BandMath {
   private static double TwoOverSqrtPi = 1.12837916709551257389615890312;

   public static double erf(double value) {
      int numOfInterations = 20;
      double returnValue = 0;
      for (int i = 0; i <= numOfInterations; i++) {
         returnValue += value * erfProduct(value, i) / (2 * i + 1);
      }
      returnValue = TwoOverSqrtPi * returnValue;
      if (returnValue < -1) {
         return -1;
      }
      if (returnValue > 1) {
         return 1;
      }
      return returnValue;
   }

   private static double erfProduct(double value, int summationVariable) {
      double returnValue = 1;
      for (int i = 1; i <= summationVariable; i++) {
          returnValue = returnValue * (-value * value) / i;
      }
      return returnValue;
   }
}
