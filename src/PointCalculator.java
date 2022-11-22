public class PointCalculator {
	
	public static int getIterationsForPoint(double x, double y, int maxIterations) {
		 int i=0;
         double temp;
         double real=0;
         double imaginary=0;
         for (i = 1 ; i < maxIterations ; i++) {
             temp=real;
             real = real * real - imaginary * imaginary + x;
             imaginary = 2 * temp * imaginary + y;
             if ((real * real + imaginary * imaginary)>4)
                 break;
         }
         return i;
	}
	
}
