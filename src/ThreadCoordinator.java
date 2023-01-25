import java.awt.Point;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue ;

public class ThreadCoordinator {

	private PixelCalculationObserver observer;
	private int maxIter;
	Vector<Calculator> workers = new Vector<>();
	Vector<PointMapping> pointMappings = new Vector<>();
	Iterator<PointMapping> pointMappingsIterator;
	MathContext mc = new MathContext(Mandel.PRECISION, RoundingMode.HALF_UP);
	BigDecimal xfaktor;
	LinkedBlockingQueue <PointMapping> queue = new LinkedBlockingQueue <PointMapping>(100);
	public ThreadCoordinator(PixelCalculationObserver observer, int maxIter) {
		this.observer = observer;
		this.maxIter = maxIter;
	}

	public void startCalculation(int width, int height, BigDecimal xmin, BigDecimal xmax, BigDecimal ymin, BigDecimal ymax) {
		for(Calculator worker:workers) {
			worker.interrupt();
		}
		workers.clear();
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
		queue.clear();
		queue = new LinkedBlockingQueue <PointMapping>(width*height);
		while(pointMappingsIterator.hasNext()) {
			queue.add(pointMappingsIterator.next());
		}
		for (int i = 1; i < Runtime.getRuntime().availableProcessors(); i++) {
			Calculator worker = new Calculator(maxIter,queue);
			worker.start();
			this.workers.add(worker);
		}
	}

	public synchronized PointMapping getNextPoinMapping() {
		if (pointMappingsIterator!= null && pointMappingsIterator.hasNext()) {
			return pointMappingsIterator.next();
		} else {
			return null;
		}
	}

	private class Calculator extends Thread {
		int maxIter;
		private LinkedBlockingQueue <PointMapping> queue;

		public Calculator(int maxIter, LinkedBlockingQueue <PointMapping> queue) {
			System.out.println("New Calculator for #" + queue.size());
			this.maxIter = maxIter;
			this.queue = queue;
		}

		@Override
		public void run() {
			PointMapping data = null;
			synchronized (ThreadCoordinator.this) {
				data = queue.poll(); // ThreadCoordinator.this.getNextPoinMapping();
				System.out.println("Polled: " + data);
			}
			int iterations = 0;
			while (data != null) {
				try {
					if (ThreadCoordinator.this.xfaktor.compareTo(new BigDecimal("1.0E-15")) > 0) {
						iterations = this.getIterationsForPoint(data.fx.doubleValue(), data.fy.doubleValue(), maxIter);
					} else {
						iterations = this.getIterationsForPoint(data.fx, data.fy, maxIter);
					}
					observer.pixelCalculationComplete(data.point, iterations);
					synchronized (ThreadCoordinator.this) {
						data = queue.poll(); // ThreadCoordinator.this.getNextPoinMapping();
					}
				} catch(InterruptedException ex) {
					System.out.println("Calculation aborted: " + this);
					return;
				}
			}
			System.out.println("Out of Data: " + this);
		}

		public void interrupt() {
			System.out.println("Called interrupt() on " + this);
			super.interrupt();
		}

		public int getIterationsForPoint(BigDecimal fx, BigDecimal fy, int maxIterations) throws InterruptedException {
			int i = 0;
			BigDecimal temp;
			BigDecimal real = BigDecimal.valueOf(0);
			BigDecimal imaginary = BigDecimal.valueOf(0);
			MathContext mc = new MathContext(Mandel.PRECISION, RoundingMode.HALF_UP);
			for (i = 1; i < maxIterations; i++) {
				if (Thread.interrupted()) {
					System.out.println("INTERRUPTED at " + i);
					throw new InterruptedException();
				}
				temp = real;
				real = real.multiply(real, mc).subtract(imaginary.multiply(imaginary, mc)).add(fx);
				imaginary = imaginary.multiply(temp, mc).multiply(BigDecimal.valueOf(2), mc).add(fy);
				if (real.multiply(real, mc).add(imaginary.multiply(imaginary, mc)).compareTo(BigDecimal.valueOf(4)) > 0)
					break;
			}
			return i;
		}

		public int getIterationsForPoint(double x, double y, int maxIter) throws InterruptedException {
			int i = 0;
			double temp;
			double real = 0;
			double imaginary = 0;
			for (i = 1; i < maxIter; i++) {
				if (this.isInterrupted()) {
					System.out.println("INTERRUPTED at " + x + "," + y);
					System.out.println("INTERRUPTED at " + i);
					throw new InterruptedException();
				}
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
