package graphical.model;

public class XYZ extends XY {
	private double height;
	private String name;
	public XYZ(double x,double y, double z){
		this(x,y,z,"");
	}
	public XYZ(double x, double y, double z, String name) {
		super(x,y);
		this.height = z;
		this.name = name;
	}
	public XYZ(double x, double y, String name) {
		this(x,y,0.0,name);
	}
	public XYZ(double x, double y) {
		this(x,y,0.0,"");
	}
	public XYZ() {
		this(0,0,0.0,"");
	}
	public XYZ(XY xy){
		this(xy.getX(),xy.getY());
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double z) {
		this.height = z;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int hashCode() {
		if(name.equals("")){
			final int prime = 31;
			int result = super.hashCode();
			long temp;
			temp = Double.doubleToLongBits(height);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
		else{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
	}
	@Override
	public boolean equals(Object obj) {
		if(name.equals("")){
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			XYZ other = (XYZ) obj;
			if (Double.doubleToLongBits(height) != Double
					.doubleToLongBits(other.height))
				return false;
			return true;
		}
		else{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			XYZ other = (XYZ) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
}