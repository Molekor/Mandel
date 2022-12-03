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
	Vector<PointCalculator> workers = new Vector<>();
	Vector<PointMapping> pointMappings = new Vector<>();
	Iterator<PointMapping> pointMappingsIterator;
	MathContext mc = new MathContext(Mandel.PRECISION, RoundingMode.HALF_UP);
	
	public ThreadCoordinator(PixelCalculationObserver observer) {
		this.observer = observer;
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new SimpleThreadFactory());
		workers = new Vector<>(); 
	}
	
	
	public void startCalculation(int width, int height, BigDecimal xmin, BigDecimal xmax, BigDecimal ymin, BigDecimal ymax, int maxIter) {
		this.maxIter=maxIter;
		synchronized(this) {
		pointMappings.clear();
		for (int px = 1 ; px < width ; px++) {
		    for (int py = 1 ; py < height ; py++) {
		    	BigDecimal xfaktor = xmax.subtract(xmin).divide(new BigDecimal(width), mc).abs();
		    	BigDecimal yfaktor = ymax.subtract(ymin).divide(new BigDecimal(height), mc).abs();
		    	BigDecimal fx = xmin.add(xfaktor.multiply(new BigDecimal(px), mc));
		    	BigDecimal fy = ymin.add(yfaktor.multiply(new BigDecimal(py), mc));
		        pointMappings.add(new PointMapping(new Point(px,py),fx,fy));
		    }
		}
		Collections.shuffle(pointMappings);
		System.out.println("# of Points to compute:" + pointMappings.size());
		pointMappingsIterator = pointMappings.iterator();
		}
	}

	public PointMapping getNextPoinMapping() {
		if(pointMappingsIterator.hasNext()) {
			return pointMappingsIterator.next();
		} else {
			return null;
		}
	}
	
	@Override
	public void run() {
		for(int i=1; i < Runtime.getRuntime().availableProcessors(); i++) {
			executor.execute(new Calculator(observer,maxIter));
		}
	}
	  private class SimpleThreadFactory implements ThreadFactory {
	    public Thread newThread(Runnable r) {
	      Thread thread = new Thread(r);
	      thread.setPriority(MIN_PRIORITY);
	      return thread;
	    }
	  };
	  
	  private class Calculator implements Runnable {
		
		 private PixelCalculationObserver observer;
		 int maxIter;
		public Calculator(PixelCalculationObserver observer, int maxIter) {
			 this.observer = observer;
			 this.maxIter=maxIter;
		 }
		
		@Override
		public void run() {
			PointMapping data = ThreadCoordinator.this.getNextPoinMapping();
			while (data!= null) {
				observer.pixelCalculationComplete(data.point, PointCalculator.getIterationsForPoint(data.fx, data.fy, maxIter));
				synchronized(ThreadCoordinator.this) {
					data = ThreadCoordinator.this.getNextPoinMapping();
				}
			}
		}
		  
	  }
}
