import java.awt.Point;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
public class ThreadCoordinator extends Thread {

	private PixelCalculationObserver observer;
	private int width;
	private int height;
	private int maxIter;
	private double xmin; 
	private double xmax;
	private double ymin;
	private double ymax;
	
	public ThreadCoordinator(PixelCalculationObserver observer) {
		this.observer = observer;
	}
	
	
	public void startCalculation(int width, int height, double xmin, double xmax, double ymin, double ymax, int maxIter) {
		this.width=width;
		this.height=height;
		this.xmin = xmin;
		this.xmax=xmax;
		this.ymax=ymax;
		this.ymin=ymin;
		this.maxIter=maxIter;
	}


	@Override
	public void run() {
		System.out.println("ThreadCoordinator RUN:"+this.getPriority());

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new SimpleThreadFactory());
		Vector<Runnable> workers = new Vector<>(); 
		double xfaktor = Math.abs(xmax-xmin) / width;
		double yfaktor = Math.abs(ymax-ymin) / height;
		for (int px = 1 ; px < width ; px++) {
		    for (int py = 1 ; py < height ; py++) {
				double fx= ((double)px*xfaktor)+(xmin);
		        double fy = ((double)py*yfaktor)+(ymin);
		    	Runnable worker = new PointCalculator(fx,fy, new Point(px,py), maxIter, observer);
		    	workers.add(worker);
		    }
		}
		Collections.shuffle(workers);
		for(Runnable worker:workers){
			executor.execute(worker);
		}
//		System.out.println("EXECUTOR:" + executor.toString());
//		executor.shutdown();
//		try {
//			executor.awaitTermination(20,TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("EXECUTOR TERMINATED");
	}
	  private class SimpleThreadFactory implements ThreadFactory {
		    public Thread newThread(Runnable r) {
		      Thread thread = new Thread(r);
		      thread.setPriority(MIN_PRIORITY);
		      return thread;
		    }
		  };
}
