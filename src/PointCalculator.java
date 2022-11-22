import java.awt.Point;

public class PointCalculator implements Runnable {
	
	private Point point;
	private double fx;
	private double fy;
	private int maxIter;
	private PixelCalculationObserver observer;
	public PointCalculator(double fx, double fy, Point point, int maxIter, PixelCalculationObserver observer) {
        this.point = point;
        this.fx = fx;
        this.fy = fy;
        this.maxIter = maxIter;
        this.observer = observer; 
	}

	@Override
	public void run() {
		//System.out.println("RUNNING:"+Thread.currentThread().getPriority());
		int i = PointCalculator.getIterationsForPoint(fx, fy, maxIter);
		observer.pixelCalculationComplete(point, i);
	}
	
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
