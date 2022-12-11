import java.awt.Point;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadCoordinator extends Thread {

	private PixelCalculationObserver observer;
	private int maxIter;
	ExecutorService executor;
	//Vector<PointCalculator> workers = new Vector<>();
	Vector<PointMapping> pointMappings = new Vector<>();
	Iterator<PointMapping> pointMappingsIterator;
	MathContext mc = new MathContext(Mandel.PRECISION, RoundingMode.HALF_UP);
	private int width;
	private int height;
	BigDecimal xfaktor;
	
	public ThreadCoordinator(PixelCalculationObserver observer) {
		this.observer = observer;
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new SimpleThreadFactory());
	}
	
	public void startCalculation(int width, int height, BigDecimal xmin, BigDecimal xmax, BigDecimal ymin, BigDecimal ymax, int maxIter) {
		this.maxIter = maxIter;
		this.width = width;
		this.height = height;
		synchronized (this) {
			pointMappings.clear();
			xfaktor = xmax.subtract(xmin).divide(new BigDecimal(width), mc).abs();
			if (this.xfaktor.compareTo(new BigDecimal("1.0E-15")) < 0) {
				System.out.println("HIGH PRECISION!" + xfaktor.toString());
			}
			BigDecimal yfaktor = ymax.subtract(ymin).divide(new BigDecimal(height), mc).abs();
			for (int px = 1; px < width; px++) {
				for (int py = 1; py < height; py++) {
					BigDecimal fx = xmin.add(xfaktor.multiply(new BigDecimal(px), mc));
					BigDecimal fy = ymin.add(yfaktor.multiply(new BigDecimal(py), mc));
					pointMappings.add(new PointMapping(new Point(px, py), fx, fy));
				}
			}
			Collections.shuffle(pointMappings);
			System.out.println("# of Points to compute:" + pointMappings.size());
			pointMappingsIterator = pointMappings.iterator();
		}
	}

	public PointMapping getNextPoinMapping() {
		if (pointMappingsIterator.hasNext()) {
			return pointMappingsIterator.next();
		} else {
			return null;
		}
	}

	@Override
	public void run() {
		for (int i = 1; i < Runtime.getRuntime().availableProcessors(); i++) {
			executor.execute(new Calculator(observer, maxIter));
		}
	}

	private class SimpleThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setPriority(MIN_PRIORITY);
			return thread;
		}
	};

	private class Calculator extends Thread {

		private PixelCalculationObserver observer;
		int maxIter;

		public Calculator(PixelCalculationObserver observer, int maxIter) {
			this.observer = observer;
			this.maxIter = maxIter;
		}

		@Override
		public void run() {
			PointMapping data = ThreadCoordinator.this.getNextPoinMapping();
			int iterations;
			while (data != null) {
				if (ThreadCoordinator.this.xfaktor.compareTo(new BigDecimal("1.0E-15")) > 0) {
					iterations = this.getIterationsForPoint(data.fx.doubleValue(), data.fy.doubleValue(), maxIter);
				} else {
					iterations = this.getIterationsForPoint(data.fx, data.fy, maxIter);
				}
				if (!this.isInterrupted()) {
					observer.pixelCalculationComplete(data.point, iterations);
				}
				synchronized (ThreadCoordinator.this) {
					data = ThreadCoordinator.this.getNextPoinMapping();
				}
				
			}
		}
		
		public int getIterationsForPoint(BigDecimal fx, BigDecimal fy, int maxIterations) {
			int i = 0;
			BigDecimal temp;
			BigDecimal real = BigDecimal.valueOf(0);
			BigDecimal imaginary = BigDecimal.valueOf(0);
			MathContext mc = new MathContext(Mandel.PRECISION, RoundingMode.HALF_UP);
			for (i = 1; i < maxIterations; i++) {
				temp = real;
				real = real.multiply(real, mc).subtract(imaginary.multiply(imaginary, mc)).add(fx);
				imaginary = imaginary.multiply(temp, mc).multiply(BigDecimal.valueOf(2), mc).add(fy);
				if (real.multiply(real, mc).add(imaginary.multiply(imaginary, mc)).compareTo(BigDecimal.valueOf(4)) > 0)
					break;
			}
			return i;
		}

		public int getIterationsForPoint(double x, double y, int maxIter) {
			int i = 0;
			double temp;
			double real = 0;
			double imaginary = 0;
			for (i = 1; i < maxIter; i++) {
				temp = real;
				real = real * real - imaginary * imaginary + x;
				imaginary = 2 * temp * imaginary + y;
				if ((real * real + imaginary * imaginary) > 4)
					break;
			}
			return i;
		}
	}
}
