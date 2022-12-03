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

	public static int getIterationsForPoint(double x, double y, int maxIter) {
		 int i=0;
         double temp;
         double real=0;
         double imaginary=0;
         for (i = 1 ; i < maxIter ; i++) {
             temp=real;
             real = real * real - imaginary * imaginary + x;
             imaginary = 2 * temp * imaginary + y;
             if ((real * real + imaginary * imaginary)>4)
                 break;
         }
         return i;
	}
	
}
