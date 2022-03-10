package dimerconstr.containers.molecule;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import dimerconstr.aux.math.IPlane3d;
import dimerconstr.aux.math.Vector3d;
import dimerconstr.containers.atom.EDonorAcceptorMarker;
import dimerconstr.containers.atom.IAtom;

@SuppressWarnings("serial")
public class DimerComplex	extends		TreeMap<String, IAtom> 
							implements	IMolecule,
										IDimerComplex
{
	/*
	 *	Properties
	 */
	//Name
	protected String name = null;
	//Angle between molecular plane and XY plane
	protected Double anglePlanar = null;
	//Rotation angle in molecular plane
	protected Double angleRotational = null;
	//Disk rotation angle
	protected Double angleDiskRot = null;
	//Zenith rotation angle
	protected Double angleZenithRot = null;
	//Radius-vector module in World Coordinates
	protected Vector3d r = null;
	//Maximal bond length
	protected Double bondLengthMax = null;
	
	/*
	 *	In-class constants
	 */
	//Numeric index of atomic intramolecular name
	protected String REGEXP_ATOMICNUM = "\\d+(-\\d+)?";

	/*
	 *	Interface
	 */
	//Get molecule name
	@Override
	public String getName() {
		return this.name;
	}

	//Get atom count
	@Override
	public Integer getAtomCount() {
		return super.size();
	}

	//Get stoichiometry
	@Override
	public String getStoichiometry() {
		//FIXME: getStoichiometry()
		return null;
	}

	//Get atom by intramolecular name
	@Override
	public IAtom getAtom(String name) {
		return super.get(name);
	}

	//Get atom names
	@Override
	public Set<String> getAtomNames() {
		return super.keySet();
	}

	//Get iterator to atoms
	@Override
	public Iterator<IAtom> getAtoms() {
		return super.values().iterator();
	}

	//Get acceptors
	@Override
	public List<IAtom> getAcceptors() {
		Iterator<IAtom> atoms = super.values().iterator();
		List<IAtom> acceptors = new LinkedList<IAtom>();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			if(EDonorAcceptorMarker.acceptor == atom.getDonorAcceptorMarker())
				acceptors.add(atom);
		}
		return acceptors;
	}

	//Get donors
	@Override
	public List<IAtom> getDonors() {
		Iterator<IAtom> atoms = super.values().iterator();
		List<IAtom> donors = new LinkedList<IAtom>();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			if(EDonorAcceptorMarker.donor == atom.getDonorAcceptorMarker())
				donors.add(atom);
		}
		return donors;
	}

	//Add atom
	@Override
	public void addAtom(IAtom atom) {
		String name = atom.getName();
		name = name.split(REGEXP_ATOMICNUM)[0];
		name = name + new Integer(super.size() + 1).toString();
		super.put(name, atom);
	}

	//Add atom
	@Override
	public void addAtom(IAtom atom, boolean isPlaneAtom) {
		String name = atom.getName();
		name = name.split(REGEXP_ATOMICNUM)[0];
		name = name + new Integer(super.size() + 1).toString();
		super.put(name, atom);
	}

	//Last atom added notification
	@Override
	public void lastAtomAdded() { return; }

	//Get molecule plane
	@Override
	public IPlane3d getPlane() { return null; }

	//Get molecule plane atoms
	@Override
	public Iterator<IAtom> getPlaneAtoms() { return null; }

	//Set angle between molecular plane and XY plane
	@Override
	public void setPlaneAngle(Double angle) {
		this.anglePlanar = angle;
	}

	//Get angle between molecular plane and XY plane
	@Override
	public Double getPlaneAngle() {
		return anglePlanar;
	}

	//Set rotation angle in molecular plane
	@Override
	public void setRotationAngle(Double angle) {
		this.angleRotational = angle;
	}

	//Get rotation angle in molecular plane
	@Override
	public Double getRotationAngle() {
		return angleRotational;
	}

	//Set disk rotation angle
	@Override
	public void setDiskAngle(Double angle) {
		this.angleDiskRot = angle;
	}
	
	//Get disk rotation angle
	@Override
	public Double getDiskAngle() {
		return angleDiskRot;
	}

	//Set zenith angle
	@Override
	public void setZenithAngle(Double angle) {
		angleZenithRot = angle;
	}
	
	//Get zenith angle
	@Override
	public Double getZenithAngle() {
		return angleZenithRot;
	}
	
	//Set radius-vector
	@Override
	public void setRadiusVector(Double x, Double y, Double z) {
		if(null != x)
			r.x = new Double(x);
		if(null != y)
			r.y = new Double(y);
		if(null != z)
			r.z = new Double(z);
	}
	
	//Set radius-vector
	@Override
	public void setRadiusVector(Vector3d rv) {
		r = new Vector3d(rv);
	}

	//Get radius-vector
	@Override
	public Vector3d getRadiusVector() {
		return r;
	}

	//Increment angle between molecular plane and XY plane
	@Override
	public void incPlaneAngle(Double angle) { }

	//Increment rotation angle in molecular plane
	@Override
	public void incRotationAngle(Double angle) { }

	//Increment disk rotation angle
	@Override
	public void incDiskAngle(Double angle) { }

	//Increment zenith angle
	public void incZenithAngle(Double angle) { }
	
	//Increment radius-vector module
	@Override
	public void incRadiusVector(Vector3d dr) { }
	
	//Increment radius-vector x
	@Override
	public void incRx(Double dx) { }
	
	//Increment radius-vector y
	@Override
	public void incRy(Double dy) { }
	
	//Incement radius-vector z
	@Override
	public void incRz(Double dz) { }

	//Reset orientation
	@Override
	public void resetOrientation() { }

	//Mirror complex
	@Override
	public void mirror() { }
	
	//Reorient molecule along atoms
	@Override
	public boolean reorientAlong(IAtom atom1, IAtom atom2, boolean mirror) { return false; }
	
	@Override
	public boolean reorientAlong(String atom1, String atom2, boolean mirror) { return false; }
	
	//Transformations
	@Override
	public void rotX(double angle) { }
	@Override
	public void rotY(double angle) { }
	@Override
	public void rotZ(double angle) { }
	@Override
	public void setPosition(Vector3d pos) { }
	@Override
	public void setPosition(Double x, Double y, Double z) { }
	@Override
	public Vector3d getPosition() { return r; }
	
	//IDimerComplex
	//Get maximum bond length
	@Override
	public Double getMaxBondLength() {
		return bondLengthMax;
	}
	
	/*
	 *	Constructors
	 */
	//Initialization constructor
	public DimerComplex(String name, Double maxBondLength) {
		this.name = name;
		this.bondLengthMax = maxBondLength;
	}
	//Initialization constructor
	public DimerComplex(String name, List<IAtom> atoms, Double maxBondLength) {
		this.name = name;
		this.bondLengthMax = maxBondLength;
		Iterator<IAtom> iAtoms = atoms.iterator();
		while(iAtoms.hasNext())
		{
			IAtom atom = iAtoms.next();
			super.put(atom.getName(), atom);
		}
	}
	//Copy constructor
	public DimerComplex(DimerComplex right) {
		this.name = right.name;
		this.bondLengthMax = right.bondLengthMax;
		this.anglePlanar = right.anglePlanar;
		this.angleRotational = right.angleRotational;
		this.angleDiskRot = right.angleDiskRot;
		super.putAll(right);
	}
	//Copy constructor 2
	public DimerComplex(String name, DimerComplex right) {
		this.name = name;
		this.bondLengthMax = right.bondLengthMax;
		this.anglePlanar = right.anglePlanar;
		this.angleRotational = right.angleRotational;
		this.angleDiskRot = right.angleDiskRot;
		super.putAll(right);
	}
}
