package band;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.table.DefaultTableCellRenderer;

public class ScientificRenderer extends DefaultTableCellRenderer {
      NumberFormat formatter;

      public ScientificRenderer() {
         super();
      }

      @Override
      public void setValue(Object value) {
         if (formatter == null) {
            formatter = new DecimalFormat("0.#####E0");
         }
         setText((value == null) ? "" : formatter.format(value));
      }
   }