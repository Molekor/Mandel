import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class PointCalculator {
	
	public static int getIterationsForPoint(BigDecimal fx, BigDecimal fy, int maxIterations) {
		 int i=0;
		 BigDecimal temp;
		 BigDecimal real=BigDecimal.valueOf(0);
		 BigDecimal imaginary=BigDecimal.valueOf(0);
		 MathContext mc = new MathContext(Mandel.PRECISION, RoundingMode.HALF_UP);
         for (i = 1 ; i < maxIterations ; i++) {      	        	 
             temp=real;
             real = real.multiply(real, mc).subtract(imaginary.multiply(imaginary, mc)).add(fx);
             imaginary = imaginary.multiply(temp, mc).multiply(BigDecimal.valueOf(2), mc).add(fy);
             if (real.multiply(real, mc).add(imaginary.multiply(imaginary, mc)).compareTo(BigDecimal.valueOf(4)) > 0 )
                 break;
         }
         return i;
	}
	
}
