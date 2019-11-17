package band;

import org.nfunk.jep.JEP;

public class Functions {
  public static double evaluateExpression(String expression, char variable, double variableValue) {
      // parse through the string
      // right now no parentesis can be used
      
      JEP parser = new JEP();
      parser.addStandardConstants();
      parser.addStandardFunctions();
      parser.setImplicitMul(true);
      
      parser.addVariable(Character.toString(variable), variableValue);
      parser.parseExpression(expression);
      
      if (parser.hasError())
          throw new RuntimeException(parser.getErrorInfo());
      return parser.getValue();
   }

}
