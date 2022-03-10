/**
 *	Matrix 3x3 in decard coordinate system
 */
package dimerconstr.aux.math;

public class Matrix3d {
	/*
	 *	Properties
	 */
	//Matrix
	public Double m00 = 1.0d;
	public Double m01 = 0.0d;
	public Double m02 = 0.0d;
	public Double m10 = 0.0d;
	public Double m11 = 1.0d;
	public Double m12 = 0.0d;
	public Double m20 = 0.0d;
	public Double m21 = 0.0d;
	public Double m22 = 1.0d;
	
	/*
	 *	Interface
	 */
	//Rotation along specific axis. Note that rotation is multiplication of vector U on matrix M. V=U*M
	//Rotate along X by gamma angle (rad)
	public void rotXrad(Double gamma) {
		Double sin_gamma = Math.sin(gamma);
		Double cos_gamma = Math.cos(gamma);
		this.m00 = 1.0d;	this.m01 = 0.0d;		this.m02 = 0.0d;
		this.m10 = 0.0d;	this.m11 = cos_gamma;	this.m12 = -sin_gamma;
		this.m20 = 0.0d;	this.m21 = sin_gamma;	this.m22 = cos_gamma;
	}
	//Rotate along X by gamma angle (deg)
	public void rotXdeg(Double gamma) {
		rotXrad(Math.toRadians(gamma));
	}
	//Rotate along Y by beta angle (rad)
	public void rotYrad(Double beta) {
		Double sin_beta = Math.sin(beta);
		Double cos_beta = Math.cos(beta);
		this.m00 = cos_beta;	this.m01 = 0.0d;	this.m02 = -sin_beta;
		this.m10 = 0.0d;		this.m11 = 1.0d;	this.m12 = 0.0d;
		this.m20 = sin_beta;	this.m21 = 0.0d;	this.m22 = cos_beta;
	}
	//Rotate along Y by beta angle (deg)
	public void rotYdeg(Double beta) {
		rotYrad(Math.toRadians(beta));
	}
	//Rotate along Z by beta angle (rad)
	public void rotZrad(Double alpha) {
		Double sin_alpha = Math.sin(alpha);
		Double cos_alpha = Math.cos(alpha);
		this.m00 = cos_alpha;	this.m01 = -sin_alpha;	this.m02 = 0.0d;
		this.m10 = sin_alpha;	this.m11 = cos_alpha;	this.m12 = 0.0d;
		this.m20 = 0.0d;		this.m21 = 0.0d;		this.m22 = 1.0d;
	}
	//Rotate along Z by beta angle (deg)
	public void rotZdeg(Double alpha) {
		rotZrad(Math.toRadians(alpha));
	}
	//Add. if(null == out) out = this
	public Matrix3d add(Matrix3d right) {
		return new Matrix3d(
							this.m00 + right.m00, this.m01 + right.m01, this.m02 + right.m02,
							this.m10 + right.m10, this.m11 + right.m11, this.m12 + right.m12,
							this.m20 + right.m20, this.m21 + right.m21, this.m22 + right.m22
							);
	}
	//Multiply on matrix
	public Matrix3d mul(Matrix3d right) {
		Matrix3d result = new Matrix3d();
		//Cij = SUM(k=0..2, Aik * Bkj);
		//Row 1
		result.m00 = this.m00 * right.m00 + this.m01 * right.m10 + this.m02 * right.m20;
		result.m01 = this.m00 * right.m01 + this.m01 * right.m11 + this.m02 * right.m21;
		result.m02 = this.m00 * right.m02 + this.m01 * right.m12 + this.m02 * right.m22;
		//Row 2
		result.m10 = this.m10 * right.m00 + this.m11 * right.m10 + this.m12 * right.m20;
		result.m11 = this.m10 * right.m01 + this.m11 * right.m11 + this.m12 * right.m21;
		result.m12 = this.m10 * right.m02 + this.m11 * right.m12 + this.m12 * right.m22;
		//Row 3
		result.m20 = this.m20 * right.m00 + this.m21 * right.m10 + this.m22 * right.m20;
		result.m21 = this.m20 * right.m01 + this.m21 * right.m11 + this.m22 * right.m21;
		result.m22 = this.m20 * right.m02 + this.m21 * right.m12 + this.m22 * right.m22;
		return result;
	}
	//Multiply on vertical vector
	public Vector3d mul(Vector3d right) {
		return new Vector3d(
							this.m00 * right.x + this.m01 * right.y + this.m02 * right.z,
							this.m10 * right.x + this.m11 * right.y + this.m12 * right.z,
							this.m20 * right.x + this.m21 * right.y + this.m22 * right.z
							);
	}
	
	//Scale. if(null == out) out = this
	public Matrix3d scale(Double alpha) {
		return new Matrix3d(
							this.m00 * alpha, this.m01 * alpha, this.m02 * alpha,
							this.m10 * alpha, this.m11 * alpha, this.m12 * alpha,
							this.m20 * alpha, this.m21 * alpha, this.m22 * alpha
							);
	}
	//Determinant
	public Double determinant() {
		return new Double(
							this.m00 * (this.m11 * this.m22 - this.m12 * this.m21)
							- this.m01 * (this.m10 * this.m22 - this.m12 * this.m20)
							+ this.m02 * (this.m10 * this.m21 - this.m11 * this.m20)
							);
	}
	
	//Assign
	public void assign(Matrix3d right) {
		this.m00 = right.m00;
		this.m01 = right.m01;
		this.m02 = right.m02;
		this.m10 = right.m10;
		this.m11 = right.m11;
		this.m12 = right.m12;
		this.m20 = right.m20;
		this.m21 = right.m21;
		this.m22 = right.m22;
	}
	//Equal
	public boolean isEqual(Matrix3d right) {
		if(this.m00 != right.m00)
			return false;
		if(this.m01 != right.m01)
			return false;
		if(this.m02 != right.m02)
			return false;
		if(this.m10 != right.m10)
			return false;
		if(this.m11 != right.m11)
			return false;
		if(this.m12 != right.m12)
			return false;
		if(this.m20 != right.m20)
			return false;
		if(this.m21 != right.m21)
			return false;
		if(this.m22 != right.m22)
			return false;
		return true;
	}
	//Is identity matrix
	public boolean isIdentity() {
		if(1.0d != this.m00)
			return false;
		if(0.0d != this.m01)
			return false;
		if(0.0d != this.m02)
			return false;
		if(0.0d != this.m10)
			return false;
		if(1.0d != this.m11)
			return false;
		if(0.0d != this.m12)
			return false;
		if(0.0d != this.m20)
			return false;
		if(0.0d != this.m21)
			return false;
		if(1.0d != this.m22)
			return false;
		return true;
	}
	//Make identity matrix
	public void identity() {
		this.m00 = 1.0d;
		this.m01 = 0.0d;
		this.m02 = 0.0d;
		this.m10 = 0.0d;
		this.m11 = 1.0d;
		this.m12 = 0.0d;
		this.m20 = 0.0d;
		this.m21 = 0.0d;
		this.m22 = 1.0d;
	}
	
	/*
	 *	Constructors
	 */
	//Default constructor
	public Matrix3d() {
	}
	
	//Initializing constructor
	public Matrix3d(Double _m00, Double _m01, Double _m02, Double _m10, Double _m11, Double _m12, Double _m20, Double _m21, Double _m22) {
		this.m00 = _m00;
		this.m01 = _m01;
		this.m02 = _m02;
		this.m10 = _m10;
		this.m11 = _m11;
		this.m12 = _m12;
		this.m20 = _m20;
		this.m21 = _m21;
		this.m22 = _m22;
	}
	
	//Initialization constructor
	public Matrix3d(Vector3d row1, Vector3d row2, Vector3d row3) {
		this.m00 = row1.x;
		this.m01 = row1.y;
		this.m02 = row1.z;
		this.m10 = row2.x;
		this.m11 = row2.y;
		this.m12 = row2.z;
		this.m20 = row3.x;
		this.m21 = row3.y;
		this.m22 = row3.z;
	}
	
	//Initialization contructor
	public Matrix3d(Matrix3d right) {
		this.m00 = right.m00;
		this.m01 = right.m01;
		this.m02 = right.m02;
		this.m10 = right.m10;
		this.m11 = right.m11;
		this.m12 = right.m12;
		this.m20 = right.m20;
		this.m21 = right.m21;
		this.m22 = right.m22;
	}
}
