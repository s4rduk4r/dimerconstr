/**
 *	3D-Vector in spheric coordinate system
 */
package dimerconstr.aux.math;
//Import section
import java.lang.Math;

import dimerconstr.aux.math.Vector3d;


//Class implementation
public class Vector3s implements IVector {
	/*
	 *	Properties
	 */
	//Coordinates
	Double rho = 0.0d;
	Double phi = 0.0d;
	Double theta = 0.0d;
	
	/*
	 * Interface
	 */
	//IVector interface
	//Is zero
	public boolean isZero() {
		if(0.0d == this.rho)
			return true;
		return false;
	}
	
	//Is equal
	public boolean isEqual(IVector right){
		Vector3s vect = (Vector3s)right;
		if(!this.rho.equals(vect.rho))
			return false;
		if(!this.phi.equals(vect.phi))
			return false;
		if(!this.theta.equals(vect.theta))
			return false;
		return true;
	}
	
	//Get length
	public Double length() {
		return this.rho;
	}
	
	//Normalize vector
	public IVector normalize() {
		return new Vector3s(1.0d, this.phi, this.theta);
	}
	
	//Convert to decard/polar coordinate system
	public IVector convert() {
		Vector3d vect = new Vector3d(this);
		return vect;
	}
	
	//Vector3p extensions
	//Assign
	public void assign(Double rho, Double phi, Double theta) {
		this.rho = rho;
		this.phi = phi;
		this.theta = theta;
	}
	
	//Assign polar vector
	public void assign(Vector3s right) {
		if(null != right) {
			this.rho = right.rho;
			this.phi = right.phi;
			this.theta = right.theta;
		}
		else {
			this.rho = 0.0d;
			this.phi = 0.0d;
			this.theta = 0.0d;
		}
	}
	
	//Assign decard vector
	public void assign(Vector3d right) {
		//Check for null-vector
		if(null == right)
			return;
		//Check for zero-vector
		if(right.isZero()) 
		{
			this.rho = 0.0d;
			this.phi = 0.0d;
			this.theta = 0.0d;
			return;
		}
		//Proceed with calculations		
		this.rho = Math.sqrt(right.x * right.x + right.y * right.y + right.z * right.z);
		this.theta = Math.acos(right.z / this.rho);
		try
		{
			this.phi = Math.asin(right.y / (Math.pow(right.x, 2.0d) + Math.pow(right.y, 2.0d)));
			//Check for quadrant
			/*							  ^
			 * 					PI-phi    |		phi
			 * 							  |
			 * 				--------------+-------------->
			 * 							  |
			 * 					-PI-phi	  |		-phi
			 */
			if (right.x < 0.0d) {
				if (right.y > 0.0d) {
					this.phi = Math.PI - this.phi;
				}
				else {
					this.phi = -Math.PI - this.phi;
				}
			}
		}
		catch (Exception e) {
			this.phi = 0.0d;
		}
	}
	
	/*
	 * Constructor
	 */
	//Initialization constructors
	//{rho, phi, theta}
	public Vector3s(Double rho, Double phi, Double theta) {
		assign(rho, phi, theta);
	}
	
	//Polar vector
	public Vector3s(Vector3s vect) {
		assign(vect);
	}
	
	//Decard vector
	public Vector3s(Vector3d vect) {
		//Check for null-vector
		if(null == vect)
		{
			this.rho = 0.0d;
			this.phi = 0.0d;
			this.theta = 0.0d;
			return;
		}
		assign(vect);		
	}
	
}
