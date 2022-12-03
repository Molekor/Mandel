import java.awt.Point;
import java.math.BigDecimal;

public class PointMapping {
	public Point point;
	public BigDecimal fx,fy;
	
	public PointMapping(Point point, BigDecimal fx2, BigDecimal fy2) {
		this.point = point;
		this.fx=fx2;
		this.fy=fy2;
	}
}
