package graphical.model;

public  class XY {
	private double x,y;
	
	public XY(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public double distanceTo(XY other) {
		return distance(this, other);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XY other = (XY) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "XY [x=" + x + ", y=" + y + "]";
	}
	
	public static double distance(XY xy1, XY xy2){
		return Math.sqrt((xy1.x-xy2.x)*(xy1.x-xy2.x)+(xy1.y-xy2.y)*(xy1.y-xy2.y));
	}
	
	public static double distance(double x1,double y1,double x2,double y2){
		double d=Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
		return d;
	}
	
	
}