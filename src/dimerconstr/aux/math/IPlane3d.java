package dimerconstr.aux.math;

public interface IPlane3d {
	//Check if two planes are equal
	public boolean isEqual(IPlane3d right);
	//Assign to this plane
	public void assign(IPlane3d right);
	//Check if two planes are intersecting
	public boolean isParallel(IPlane3d right);
	//Get plane normal vector
	public Vector3d getNormal();
	//Get angle between plane and vector (rad)
	public Double getAngle(Vector3d v);
	//Get angle between two planes (rad)
	public Double getAngle(IPlane3d alpha);
	//Check if point belongs to this plane
	public boolean isBelong(Point3d p);
}
