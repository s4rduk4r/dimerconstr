/**
 *	Plane is a 3D plane in decard coordinate system
 */
package dimerconstr.aux.math;

public class Plane3d implements IPlane3d {
	/*
	 *	Properties
	 */
	//Normal vector
	protected Vector3d n = null;
	//Plane term D
	protected Double d = null;
	
	/*
	 *	Interface
	 */
	//IPlane3d
	//Check if two planes are equal
	public boolean isEqual(IPlane3d right) {
		//If no valid plane provided - do nothing
		if(null == right)
			return false;
		//A1 = l * A2; B1 = l * B2; C1 = l * C2; D1 = l * D2
		Plane3d rPlane = (Plane3d) right;
		Double lambda = this.n.x / rPlane.n.x;
		if(this.n.y / rPlane.n.y != lambda)
			return false;
		if(this.n.z / rPlane.n.z != lambda)
			return false;
		if(this.d / rPlane.d != lambda)
			return false;
		return true;
	}
	
	//Assign to this plane
	public void assign(IPlane3d right) {
		//If no valid plane provided - do nothing
		if(null == right)
			return;
		Plane3d rPlane = (Plane3d) right;
		this.n = rPlane.n;
		this.d = rPlane.d;
	}
	
	//Check if two planes are intersecting
	public boolean isParallel(IPlane3d right) {
		//If no valid plane provided - do nothing
		if(null == right)
			return false;
		//A1 = l * A2; B1 = l * B2; C1 = l * C2
		Plane3d rPlane = (Plane3d) right;
		Double lambda = this.n.x / rPlane.n.x;
		if(this.n.y / rPlane.n.y != lambda)
			return false;
		if(this.n.z/ rPlane.n.z != lambda)
			return false;
		return true;
	}
	
	//Get plane normal vector
	public Vector3d getNormal() {
		return this.n;
	}
	
	//Get angle between plane and vector (rad)
	public Double getAngle(Vector3d v) {
		return this.n.angle(v);
	}
	
	//Get angle between two planes (rad)
	public Double getAngle(IPlane3d alpha) {
		Plane3d rPlane = (Plane3d) alpha;
		return this.n.angle(rPlane.n);
	}
	
	//Check if point belongs to this plane
	public boolean isBelong(Point3d p) {
		Double value = new Double(p.x * this.n.x + p.y * this.n.y + p.z * this.n.z + this.d);
		if(0.0d == value)
			return true;
		return false;
	}
	
	/*
	 *	Auxiliary methods
	 */
	//Calculate plane normal and terms
	protected void calculateParameters(Point3d p1, Point3d p2, Point3d p3) {
		Matrix3d m = new Matrix3d(	0.0d,			0.0d,			0.0d, 
									p2.x - p1.x,	p2.y - p1.y,	p2.z - p1.z,
									p3.x - p1.x,	p3.y - p1.y,	p3.z - p1.z);
		this.n = new Vector3d(	new Double(m.m11 * m.m22 - m.m12 * m.m21),
								new Double(m.m12 * m.m20 - m.m10 * m.m22),
								new Double(m.m10 * m.m21 - m.m11 * m.m20)
							 );
		this.n.normalize();
		this.d = new Double( (-1) * (p1.x * this.n.x + p1.y * this.n.y + p1.z * this.n.z) );
	}

	protected void calculateParameters(Vector3d v1, Vector3d v2, Point3d p) {
		this.n = new Vector3d(	new Double(v1.y * v2.z - v1.z * v2.y),
								new Double(v1.z * v2.x - v1.x * v2.z),
								new Double(v1.x * v2.y - v1.y * v2.x)
							 );
		this.n.normalize();
		this.d = new Double( (-1) * (p.x * this.n.x + p.y * this.n.y + p.z * this.n.z) );
	}
	
	/*
	 *	Constructors
	 */
	//Initialization constructor
	//Direct coefficients providing
	public Plane3d(Double a, Double b, Double c, Double d) {
		if(null == a)
			a = 0.0d;
		if(null == b)
			b = 0.0d;
		if(null == c)
			c = 0.0d;
		if(null == d)
			d = 0.0d;
		this.n = new Vector3d(a, b, c);
		this.n.normalize();
		this.d = d;
	}
	
	//From other plane
	public Plane3d(Plane3d plane) {
		if(null != plane)
			assign(plane);
		else {
			this.n = new Vector3d(0.0d, 0.0d, 0.0d);
			this.d = 0.0d;
		}
	}
	
	//3 points
	public Plane3d(Point3d p1, Point3d p2, Point3d p3) {
		calculateParameters(p1, p2, p3);
	}
	
	//2 narrowing non-collinear vectors and point belonging to this plane
	public Plane3d(Vector3d v1, Vector3d v2, Point3d p) {
		calculateParameters(v1, v2, p);
	}
	
	//Normal vector to plane and point belonging to this plane
	public Plane3d(Vector3d n, Point3d p) {
		this.n = (Vector3d) n.normalize();
		this.d = new Double( (-1) * (p.x * this.n.x + p.y * this.n.y + p.z * this.n.z) );
	}
	
}
