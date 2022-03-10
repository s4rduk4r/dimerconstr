/**
 *	Interface for Molecule class
 */
package dimerconstr.containers.molecule;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dimerconstr.aux.math.IPlane3d;
import dimerconstr.aux.math.Vector3d;
import dimerconstr.containers.atom.IAtom;


//Interface declaration
public interface IMolecule {
	//Get molecule name
	public String getName();
	//Get atom count
	public Integer getAtomCount();
	//Get stoichiometry
	public String getStoichiometry();
	//Get atom by intramolecular name
	public IAtom getAtom(String name);
	//Get atom names
	public Set<String> getAtomNames();
	//Get iterator to atoms
	public Iterator<IAtom> getAtoms();
	//Get acceptors
	public List<IAtom> getAcceptors();
	//Get donors
	public List<IAtom> getDonors();
	//Add atom
	public void addAtom(IAtom atom);
	//Add atom
	public void addAtom(IAtom atom, boolean isPlaneAtom);
	//Last atom added notification
	public void lastAtomAdded();
	//Get molecule plane
	public IPlane3d getPlane();
	//Get molecule plane atoms
	public Iterator<IAtom> getPlaneAtoms();
	//Set angle between molecular plane and XY plane
	public void setPlaneAngle(Double angle);
	//Get angle between molecular plane and XY plane
	public Double getPlaneAngle();
	//Set rotation angle in molecular plane
	public void setRotationAngle(Double angle);
	//Get rotation angle in molecular plane
	public Double getRotationAngle(); 
	//Set disk rotation angle
	public void setDiskAngle(Double angle);
	//Get disk rotation angle
	public Double getDiskAngle();
	//Set zenith angle
	public void setZenithAngle(Double angle);
	//Get zenith angle
	public Double getZenithAngle();
	//Set radius-vector
	public void setRadiusVector(Double x, Double y, Double z);
	//Set radius-vector
	public void setRadiusVector(Vector3d rv);
	//Get radius-vector
	public Vector3d getRadiusVector();
	//Increment angle between molecular plane and XY plane
	public void incPlaneAngle(Double angle);
	//Increment rotation angle in molecular plane
	public void incRotationAngle(Double angle);
	//Increment disk rotation angle
	public void incDiskAngle(Double angle);
	//Increment zenith angle
	public void incZenithAngle(Double angle);
	//Increment radius-vector
	public void incRadiusVector(Vector3d dr);
	//Increment radius-vector x
	public void incRx(Double dx);
	//Increment radius-vector y
	public void incRy(Double dy);
	//Incement radius-vector z
	public void incRz(Double dz);
	//Reset orientation
	public void resetOrientation();
	//Mirror complex
	public void mirror();
	//Reorient molecule along atoms
	public boolean reorientAlong(IAtom atom1, IAtom atom2, boolean mirror);
	public boolean reorientAlong(String atom1, String atom2, boolean mirror);
	//Transformations
	public void rotX(double angle);
	public void rotY(double angle);
	public void rotZ(double angle);
	public void setPosition(Vector3d pos);
	public void setPosition(Double x, Double y, Double z);
	public Vector3d getPosition();
}
