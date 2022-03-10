/**
 *	Point in 3D decard coordinate system
 */
package dimerconstr.aux.math;

public class Point3d {
	/*
	 *	Properties
	 */
	//Coordinates
	public Double x = null;
	public Double y = null;
	public Double z = null;
	
	/*
	 *	Interface
	 */
	//Equal
	public boolean isEqual(Point3d right) {
		if(this.x != right.x)
			return false;
		if(this.y != right.y)
			return false;
		if(this.z != right.z)
			return false;
		return true;
	}
	
	//Assign
	//{x,y,z}
	public void assign(Double x, Double y, Double z) {
		if( (null == x) || (null == y) || (null == z) )
			return;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	//Point3d
	public void assign(Point3d right) {
		this.x = right.x;
		this.y = right.y;
		this.z = right.z;
	}
	
	/*
	 *	Constructor
	 */
	//Initialization constructor
	public Point3d(Double x, Double y, Double z) {
		if(null == x)
			x = 0.0d;
		if(null == y)
			y = 0.0d;
		if(null == z)
			z = 0.0d;
		assign(x, y, z);
	}
	
	//Initialization constructor
	public Point3d(Point3d right) {
		assign(right);
	}
}
