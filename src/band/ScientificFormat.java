package band;

import java.text.DecimalFormat;
import java.text.FieldPosition;

public class ScientificFormat extends DecimalFormat {

   public ScientificFormat() {}

   @Override
   public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
       if (number == 0 || (number >= 0.01 && number <= 100) || (number <= -0.01 && number >= -100)) {
            DecimalFormat df = new DecimalFormat("#.####");
            return df.format(number, result, fieldPosition);
      }
      else {
         DecimalFormat df = new DecimalFormat("0.####E0");
         return df.format(number, result, fieldPosition);
      }
   }

   @Override
   public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
      if (number == 0 || (number >= 0.01 && number <= 100) || (number >= 0.01 && number <= 100)) {
            DecimalFormat df = new DecimalFormat("#.####");
            return df.format(number, result, fieldPosition);
      }
      else {
         DecimalFormat df = new DecimalFormat("0.####E0");
         return df.format(number, result, fieldPosition);
      }
   }
}
