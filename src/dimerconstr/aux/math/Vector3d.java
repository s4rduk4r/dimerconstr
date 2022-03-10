/**
 *	3D-vector in decard coordinate system
 */
package dimerconstr.aux.math;
import java.lang.Math;

//Class implementation
public class Vector3d implements IVector {
	/*
	 *	Properties
	 */
	//Coordinates
	public Double x = 0.0d;
	public Double y = 0.0d;
	public Double z = 0.0d;
	
	/*
	 *	In-class constants
	 */
	//Orthos
	public static final Vector3d I = new Vector3d(1.0d, 0.0d, 0.0d);
	public static final Vector3d J = new Vector3d(0.0d, 1.0d, 0.0d);
	public static final Vector3d K = new Vector3d(0.0d, 0.0d, 1.0d);
	
	/*
	 * Interface
	 */
	//IVector
	//Is zero
	public boolean isZero() {
		if((0.0d == this.x)
			&& (0.0d == this.y)
			&& (0.0d == this.z)
		  )
			return true;
		return false;
	}
	//Is equal
	public boolean isEqual(IVector right) {
		Vector3d vect = (Vector3d)right;
		if(this.x != vect.x)
			return false;
		if(this.y != vect.y)
			return false;
		if(this.z != vect.z)
			return false;
		return true;
	}
	//Get length
	public Double length() {
		return new Double(Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z));
	}
	//Normalize vector
	public IVector normalize() {
		Double length = new Double(this.length());
		return new Vector3d(this.x / length, this.y / length, this.z / length);
	}
	//Convert to decard/polar coordinate system
	public IVector convert() {
		Vector3s vect = new Vector3s(this);
		return vect;
	}
	
	//Vector3d extensions
	//Convert to right coordinate system (RCS) or left coordinate system (LCS)
	public Vector3d toRightOrLeftCoordinateSystem() {
		return new Vector3d(this.x, -this.y, this.z);
	}
	//Add vector
	public Vector3d add(Vector3d right) {
		return new Vector3d(this.x + right.x, this.y + right.y, this.z + right.z);
	}
	
	//Subtract
	public Vector3d sub(Vector3d right) {
		return new Vector3d(this.x - right.x, this.y - right.y, this.z - right.z);
	}
	
	//Angle between this and other vectors (rad)
	public Double angle(Vector3d right) {
		return new Double(Math.acos(this.dot(right) / (this.length() * right.length())));
	}
	
	//Dot product
	public Double dot(Vector3d right) {
		return new Double(this.x * right.x + this.y * right.y + this.z * right.z);
	}
	
	//Vector product
	public Vector3d vector(Vector3d right) {
		return new Vector3d(
							this.y * right.z - this.z * right.y,
							this.z * right.x - this.x * right.z,
							this.x * right.y - this.y * right.x 
							);
	}
	
	//Mixed product
	public Double mixed(Vector3d b, Vector3d c) {
		return dot(new Vector3d(b.vector(c)));
	}
	
	//Multiply on matrix
	public Vector3d mul(Matrix3d right) {
		return new Vector3d(this.x * right.m00 + this.y * right.m10 + this.z * right.m20,
							this.x * right.m01 + this.y * right.m11 + this.z * right.m21,
							this.x * right.m02 + this.y * right.m12 + this.z * right.m22
							);
	}
	
	//Assign
	public void assign(Double x, Double y, Double z) {
		if( (null == x) || (null == y) || (null == z) )
			return;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//Assign decard vector
	public void assign(Vector3d right) {
		//Check for null
		if(null == right)
			return;
		this.x = right.x;
		this.y = right.y;
		this.z = right.z;
	}
	
	//Assign polar vector
	public void assign(Vector3s right) {
		//Check for null
		if(null == right)
			return;
		//Check if zero-vector
		if(right.isZero()) {
			this.x = 0.0d;
			this.y = 0.0d;
			this.z = 0.0d;
		}
		//Calculate coordinates otherwise
		this.x = right.rho * Math.sin(right.theta) * Math.cos(right.phi);
		this.y = right.rho * Math.sin(right.theta) * Math.sin(right.phi);
		this.z = right.rho * Math.cos(right.theta);
	}
	
	
	/*
	 * Constructor
	 */
	//Initialization constructors
	//{x, y, z}
	public Vector3d(Double x, Double y, Double z) {
		if(null == x)
			x = 0.0d;
		if(null == y)
			y = 0.0d;
		if(null == z)
			z = 0.0d;
		assign(x, y, z);
	}
	
	public Vector3d(Double x0, Double y0, Double z0, Double x1, Double y1, Double z1) {
		if(null == x0)
			x0 = 0.0d;
		if(null == y0)
			y0 = 0.0d;
		if(null == z0)
			z0 = 0.0d;
		if(null == x1)
			x1 = 0.0d;
		if(null == y1)
			y1 = 0.0d;
		if(null == z1)
			z1 = 0.0d;
		assign(x1 - x0, y1 - y0, z1 - z0);
	}
	
	//Point3d
	public Vector3d(Point3d v) {
		assign(v.x, v.y, v.z);
	}
	
	public Vector3d(Point3d p0, Point3d p1) {
		assign(p1.x - p0.x, p1.y - p0.y, p1.z - p0.z);
	}
	
	//Decard vector
	public Vector3d(Vector3d vect) {
		//Check if null
		if(null == vect) {
			this.x = 0.0d;
			this.y = 0.0d;
			this.z = 0.0d;
			return;
		}
		assign(vect);
	}
	
	//Polar vector
	public Vector3d(Vector3s vect) {
		//Check if null
		if(null == vect) {
			this.x = 0.0d;
			this.y = 0.0d;
			this.z = 0.0d;
			return;
		}
		assign(vect);
	}

}
