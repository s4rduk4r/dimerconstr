package dimerconstr.aux.math;

public interface IVector {
	//Is zero
	public boolean isZero();
	//Is equal
	public boolean isEqual(IVector right);
	//Get length
	public Double length();
	//Normalize vector
	public IVector normalize();
	//Convert to decard/polar coordinate system
	public IVector convert();
}
