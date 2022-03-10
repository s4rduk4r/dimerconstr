/**
 *	Molecule descriptor class
 */
package dimerconstr.containers.molecule;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import dimerconstr.aux.math.IPlane3d;
import dimerconstr.aux.math.Matrix3d;
import dimerconstr.aux.math.Plane3d;
import dimerconstr.aux.math.Point3d;
import dimerconstr.aux.math.Vector3d;
import dimerconstr.containers.atom.Atom;
import dimerconstr.containers.atom.EDonorAcceptorMarker;
import dimerconstr.containers.atom.IAtom;


//Class implementation
@SuppressWarnings("serial")
public class Molecule extends TreeMap<String, IAtom> implements IMolecule {

	/*
	 *	Properties
	 */
	//World coordinates atoms
	protected TreeMap<String, IAtom> holderWC = null;
	//Last added atom flag
	protected boolean lastAtomAdded = false;
	//Molecule name
	protected String name = null;
	//Molecule plane atoms
	protected Vector<IAtom> planeAtom = new Vector<IAtom>(3);
	//Geometry parameters
	//Mirror flag. TRUE - rotate along Y axis on PI. FALSE - rotate along Y axis on 0
	protected boolean isMirrored = false;
	//Angle between molecular plane and XY plane
	protected Double angKhi = ANG_NULL;
	//Rotation angle in molecular plane
	protected Double angDelta = ANG_NULL;
	//Disk rotation angle
	protected Double angKappa = ANG_NULL;
	//Zenith angle
	protected Double angTheta = ANG_NULL;
	//Radius-vector in World Coordinates
	protected Vector3d r = R0;
	//Rotation matrices
	Matrix3d rotPlane = new Matrix3d();
	Matrix3d rotPlanar = new Matrix3d();
	Matrix3d rotDisk = new Matrix3d();
	Matrix3d rotZenith = new Matrix3d();
	//Dimer2 algorithm transformations
	protected boolean isDimer2Algo = false;
	protected Double angX = ANG_NULL;
	protected Double angY = ANG_NULL;
	protected Double angZ = ANG_NULL;
	
	/*
	 *	In-class constants
	 */
	protected static final double ANG_NULL = 0.0d;
	protected static final Vector3d R0 = new Vector3d(0.0d, 0.0d, 0.0d);
	
		
	/*
	 *	Interface
	 */
	//IMolecule
	//Get molecule name
	@Override
	public String getName() {
		return name;
	}
	//Get atom count
	@Override
	public Integer getAtomCount() {
		return super.size();
	}
	//Get stoichiometry
	@Override
	public String getStoichiometry() {
		//TODO: getStoichiometry()
		return null;
	}
	//Get atom by intramolecular name
	@Override
	public IAtom getAtom(String name) {
		//Calculate World coordinates and return copy of atom with them
		return holderWC.get(name);
		//FIXME return setWorldCoordinates(super.get(name));
	}
	//Get atom names
	@Override
	public Set<String> getAtomNames() {
		return super.keySet();
	}
	//Get iterator to atoms
	@Override
	public Iterator<IAtom> getAtoms() {
		//Calculate World coordinates and return copy of atom with them
		return holderWC.values().iterator();
	}
	//Get acceptors
	@Override
	public List<IAtom> getAcceptors() {
		Iterator<IAtom> iAtom = holderWC.values().iterator();
		List<IAtom> acceptors = new LinkedList<IAtom>();
		while(iAtom.hasNext())
		{
			IAtom atom = iAtom.next();
			if(EDonorAcceptorMarker.acceptor == atom.getDonorAcceptorMarker())
				acceptors.add(atom);
		}
		return acceptors;
	}
	//Get donors
	@Override
	public List<IAtom> getDonors() {
		Iterator<IAtom> iAtom = holderWC.values().iterator();
		List<IAtom> donors = new LinkedList<IAtom>();
		while(iAtom.hasNext())
		{
			IAtom atom = iAtom.next();
			if(EDonorAcceptorMarker.donor == atom.getDonorAcceptorMarker())
				donors.add(atom);
		}
		return donors;
	}
	//Add atom
	@Override
	public void addAtom(IAtom atom) {
		//If last atom added - do nothing
		if(lastAtomAdded)
			return;
		atom.setMolecule(this);
		super.put(atom.getName(), atom);
	}
	//Add atom
	@Override
	public void addAtom(IAtom atom, boolean isPlaneAtom) {
		//If last atom added - do nothing
		if(lastAtomAdded)
			return;
		atom.setMolecule(this);
		super.put(atom.getName(), atom);
		//Fill in vacant place for plane atom
		if(isPlaneAtom && (planeAtom.size() < 3)) {
			planeAtom.add(atom);
		}
	}
	//Last atom added notification
	public void lastAtomAdded() {
		lastAtomAdded = true;
		makeCanonicalView();
		setWorldCoordinates();
	}
	//Get molecule plane
	@Override
	public IPlane3d getPlane() {
		if(planeAtom.isEmpty())
			return null;
		Point3d[] p = new Point3d[3];
		for(int i = 0; i < 3; ++i)
		{
			Vector3d pos = setWorldCoordinates(planeAtom.elementAt(i)).getPosition();
			p[i] = new Point3d(pos.x, pos.y, pos.z);
		}
		return new Plane3d(p[0], p[1], p[2]);
	}
	//Get molecule plane atoms
	@Override
	public Iterator<IAtom> getPlaneAtoms() {
		return planeAtom.iterator();
	}
	//Set angle between molecular plane and XY plane
	@Override
	public void setPlaneAngle(Double angle) {
		if(angKhi != angle) {
			angKhi = angle;
			setWorldCoordinates();
		}
	}
	//Get angle between molecular plane and XY plane
	@Override
	public Double getPlaneAngle() {
		return angKhi;
	}
	//Set rotation angle in molecular plane
	@Override
	public void setRotationAngle(Double angle) {
		if(angDelta != angle) {
			angDelta = angle;
			setWorldCoordinates();
		}
	}
	//Get rotation angle in molecular plane
	@Override
	public Double getRotationAngle() {
		return angDelta;
	}
	//Set disk rotation angle
	@Override
	public void setDiskAngle(Double angle) {
		if(angKappa != angle) {
			angKappa = angle;
			setWorldCoordinates();
		}
	}
	//Get disk rotation angle
	@Override
	public Double getDiskAngle() {
		return angKappa;
	}
	//Set zenith angle
	@Override
	public void setZenithAngle(Double angle) {
		if(angTheta != angle) {
			angTheta = angle;
			setWorldCoordinates();
		}
	}
	//Get zenith angle
	@Override
	public Double getZenithAngle() {
		return angTheta;
	}
	//Set radius-vector
	@Override
	public void setRadiusVector(Double x, Double y, Double z) {
		if((r.x != x) || (r.y != y) || (r.z != z)) {
			r = new Vector3d(x, y, z);
			setWorldCoordinates();
		}
	}
	//Set radius-vector
	@Override
	public void setRadiusVector(Vector3d rv) {
		if((r.x != rv.x) || (r.y != rv.y) || (r.z != rv.z)) {
			r = new Vector3d(rv);
			setWorldCoordinates();
		}
	}
	//Get radius-vector
	@Override
	public Vector3d getRadiusVector() {
		return r;
	}
	//Increment angle between molecular plane and XY plane
	@Override
	public void incPlaneAngle(Double angle) {
		if(0.0d != angle) {
			angKhi = angKhi + angle;
			setWorldCoordinates();
		}
	}
	//Increment rotation angle in molecular plane
	@Override
	public void incRotationAngle(Double angle) {
		if(0.0d != angle) {
			angDelta = angDelta + angle;
			setWorldCoordinates();
		}
	}
	//Increment disk rotation angle
	@Override
	public void incDiskAngle(Double angle) {
		if(0.0d != angle) {
			angKappa = angKappa + angle;
			setWorldCoordinates();
		}
	}
	//Increment zenith angle
	@Override
	public void incZenithAngle(Double angle) {
		if(0.0d != angle) {
			angTheta = angTheta + angle;
			setWorldCoordinates();
		}
	}
	//Increment radius-vector
	@Override
	public void incRadiusVector(Vector3d dr) {
		if(!dr.isZero()) {
			r = r.add(dr);
			setWorldCoordinates();
		}
	}
	//Increment radius-vector x
	public void incRx(Double dx) {
		if(0.0d == dx)
			return;
		if(R0 == r) {
			r = r.add(new Vector3d(dx, 0.0d, 0.0d));
		} else {
			r.x += dx;
		}
		setWorldCoordinates();
	}
	//Increment radius-vector y
	public void incRy(Double dy) {
		if(0.0d == dy)
			return;
		if(R0 == r) {
			r = r.add(new Vector3d(0.0d, dy, 0.0d));
		} else {
			r.y += dy;
		}
		setWorldCoordinates();
	}
	//Incement radius-vector z
	public void incRz(Double dz) {
		if(0.0d == dz)
			return;
		if(R0 == r) {
			r = r.add(new Vector3d(0.0d, 0.0d, dz));
		} else {
			r.z += dz;
		}
		setWorldCoordinates();
	}
	//Reset orientation
	@Override
	public void resetOrientation() {
		angDelta = ANG_NULL;
		angKhi = ANG_NULL;
		angKappa = ANG_NULL;
		r = R0;
		angX = ANG_NULL;
		angY = ANG_NULL;
		angZ = ANG_NULL;
		makeCanonicalView();
		setWorldCoordinates();
	}
	//Mirror complex
	@Override
	public void mirror() {
		isMirrored = !isMirrored;
		setWorldCoordinates();
	}
	//Reorient molecule along atoms
	@Override
	public boolean reorientAlong(IAtom atom1, IAtom atom2, boolean mirror) {
		//If no such atoms - do nothing
		String[] atomNames = new String[2];
		atomNames[0] = atom1.getName();
		atomNames[1] = atom2.getName();
		if(!super.containsKey(atomNames[0]) || !super.containsKey(atomNames[1])) {
			return false;
		}
		atom1 = super.get(atom1.getName());
		atom2 = super.get(atom2.getName());
		//Define 1st and 2nd atoms
		Integer numMax = getIntramolecularIndexFromName(atom1);
		Integer numMin = getIntramolecularIndexFromName(atom2);
		IAtom[] atoms = new IAtom[3];
		atoms[0] = atom1;
		atoms[1] = atom2;
		if(numMax <= numMin) {
			Integer num = numMax;
			numMax = numMin;
			numMin = num;
		}
		//Get 3rd atom TODO!!!
		for(IAtom pAtom : planeAtom)
		{
			//Check if there are size 3
			if(3 == super.values().size()) {
				if(pAtom != atoms[0] && pAtom != atoms[1]) {
					atoms[2] = pAtom;
					break;
				}
			}
			//All other cases
			Integer num = getIntramolecularIndexFromName(pAtom);
			if((num > numMax) || (num < numMin) || ((num > numMin) && (num < numMax)))
			{
				atoms[2] = pAtom;
				break;
			}
		}
		if(null == atoms[2])
			atoms[2] = planeAtom.lastElement();
		//Save old plane atoms before transformations
		Vector<IAtom> oldPlaneAtoms = new Vector<IAtom>(planeAtom);
		planeAtom.set(0, atoms[0]);
		planeAtom.set(1, atoms[1]);
		planeAtom.set(2, atoms[2]);
		//Perform transformations
		makeCanonicalView();
		boolean oldIsDimer2Algo = isDimer2Algo;
		isDimer2Algo = false;
		setWorldCoordinates();
		isDimer2Algo = oldIsDimer2Algo;
		//Mirror along Y atoms if needed
		if(mirror) {
			for(IAtom atom : holderWC.values()) {
				Vector3d pos = atom.getPosition();
				pos.y = -pos.y;
				atom.setPosition(pos);
			}
		}
		//Restore old plane atoms after transformations
		planeAtom.clear();
		planeAtom = oldPlaneAtoms;
		return true;
	}
	@Override
	public boolean reorientAlong(String atom1, String atom2, boolean mirror) {
		String name1 = atom1.toLowerCase();
		String name2 = atom2.toLowerCase();
		//If no such atoms - do nothing
		if(!super.containsKey(name1) || !super.containsKey(name2))
			return false;
		return reorientAlong(super.get(name1), super.get(name2), mirror);
	}
	//Transformations
	@Override
	public void rotX(double angle) {
		if(0.0d != angle) {
			angX = angle;
			isDimer2Algo = true;
			setWorldCoordinates();
		}
	}
	@Override
	public void rotY(double angle) {
		if(0.0d != angle) {
			angY = angle;
			isDimer2Algo = true;
			setWorldCoordinates();
		}
	}
	@Override
	public void rotZ(double angle) {
		if(0.0d != angle) {
			angZ = angle;
			isDimer2Algo = true;
			setWorldCoordinates();
		}
	}
	@Override
	public void setPosition(Vector3d pos) {
		if(null == pos)
			return;
		r = pos;
		isDimer2Algo = true;
		setWorldCoordinates();
	}
	@Override
	public void setPosition(Double x, Double y, Double z) {
		setPosition(new Vector3d(x, y, z));
	}
	@Override
	public Vector3d getPosition() {
		return r;
	}
	
	/*
	 *	Auxiliary methods
	 */
	//Get atoms in canonical view
	protected Iterator<IAtom> getAtomsCanon() {
		return super.values().iterator();
	}
	
	//Set molecule on XY plane
	protected void makeCanonicalView() {
		//If not all 3 plane atoms are in place - do nothing
		if(planeAtom.size() < 3)
			return;
		//Take plane atoms
		Iterator<IAtom> atoms = this.getPlaneAtoms();
		//Set 1st plane atom into (0, 0, 0)
		Vector3d r = new Vector3d(atoms.next().getPosition());
		atoms = this.getAtomsCanon();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			atom.setPosition(atom.getPosition().sub(r));
		}
		//Take vectors U, V
		atoms = this.getPlaneAtoms();
		atoms.next();
		Vector3d u = atoms.next().getPosition();
		IAtom p2 = atoms.next();
		//Calculate first 2 rotation angles
		Double angZ = Math.acos(u.x / Math.sqrt(u.x * u.x + u.y * u.y));
		if(u.y >= 0.0d) 
			angZ = -angZ;
		Double angY = Math.acos(u.z / u.length()) - Math.PI/2.0d;
		//Rotate along Z axis
		Matrix3d rot = new Matrix3d();
		rot.rotZrad(angZ);
		//Rotate along Y axis
		Matrix3d rotY = new Matrix3d();
		rotY.rotYrad(angY);
		rot = rotY.mul(rot);
		//Apply rotation
		atoms = this.getAtomsCanon();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			r = atom.getPosition();
			atom.setPosition(rot.mul(r));
		}
		//Calculate last rotation angle
		Vector3d v = p2.getPosition();
		Double angX = Math.acos(v.y / Math.sqrt(v.y * v.y + v.z * v.z));
		if(v.z > 0.0d) 
			angX = -angX;
		//Rotate along X axis
		rot.rotXrad(angX);
		//Mirror flag
		boolean mirrorY = false;
		if(rot.mul(v).y < 0.0d)
			mirrorY = true;
		atoms = this.getAtomsCanon();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			r = atom.getPosition();
			r = rot.mul(r);
			if(mirrorY)
				r.y = -r.y;
			atom.setPosition(r);
		}
	}
	
	//Calculate World Coordinates
	protected IAtom setWorldCoordinates(IAtom atom) {
		//Calculate rotation
		rotPlane.rotXrad(angKhi);
		rotPlanar.rotZrad(angDelta);
		rotDisk.rotZrad(angKappa);
		rotZenith.rotZrad(angTheta);
		//Mirror
		Vector3d pos = new Vector3d(atom.getPosition());
		if(isMirrored) {
			pos.x = -pos.x;
		}
		//Rotate
		if(ANG_NULL != angDelta) {
			pos = pos.mul(rotPlanar);
		}
		//Translate
		pos = pos.add(r);
		//Disk rotate
		if(ANG_NULL != angKappa) {
			pos = pos.mul(rotDisk);
		}
		//Plane rotate
		if(ANG_NULL != angKhi) {
			pos = pos.mul(rotPlane);
		}
		//Zenith rotate
		if(ANG_NULL != angTheta) {
			pos = pos.mul(rotZenith);
		}
		//Apply changes
		IAtom copy = new Atom((Atom) atom);
		copy.setPosition(pos);
		return copy;
	}
	//Calculate World Coordinates for Dimer2 Algo
	protected IAtom setWorldCoordinates2(IAtom atom) {
		//Calculate rotation
		Matrix3d rot = new Matrix3d();
		Vector3d pos = new Vector3d(atom.getPosition());
		//Rotations
		if(ANG_NULL != angX) {
			rot.rotXrad(angX);
			pos = pos.mul(rot);
		}
		if(ANG_NULL != angY) {
			rot.rotYrad(angY);
			pos = pos.mul(rot);
		}
		if(ANG_NULL != angZ) {
			rot.rotZrad(angZ);
			pos = pos.mul(rot);
		}
		//Translations
		pos = pos.add(r);
		IAtom copy = new Atom((Atom) atom);
		copy.setPosition(pos);
		return copy;
	}
	
	//Calculate World Coordinates
	protected void setWorldCoordinates() {
		if(null == holderWC)
			holderWC = new TreeMap<String, IAtom>();
		//Create copy of atoms
		for(IAtom atom : super.values())
		{
			holderWC.put(atom.getName(), isDimer2Algo ? setWorldCoordinates2(atom) : setWorldCoordinates(atom));
		}
	}
	
	//Get atomic index from intramolecular name
	protected Integer getIntramolecularIndexFromName(IAtom atom) {
		String RE_ATOM_AXX = "[a-zA-Z]\\d+(-\\d+)?";
		String RE_ATOM_AAXX = "[a-zA-Z]{2,2}\\d+(-\\d+)?";
		String atomName = atom.getName();
		String s = null;
		for(int i = 0; i < 2; ++i)
		{
			s = atomName.split("-")[0];
			if(atomName.matches(RE_ATOM_AXX)) {
				s = s.substring(1);
			} else {
				if(atomName.matches(RE_ATOM_AAXX)) {
					s = s.substring(2);
				}
			}
		}
		return new Integer(s);
	}
	
	/*
	 *	Constructor
	 */
	//Initialization constructor
	public Molecule(String molName) {
		super();
		this.name = molName;
	}
	
	//Copy constructor
	public Molecule(Molecule right) {
		super();		
		this.name = right.name;
		this.angDelta = right.angDelta;
		this.angKappa = right.angKappa;
		this.angKhi = right.angKhi;
		this.r = right.r;
		//Copy atoms
		Iterator<IAtom> atoms = right.values().iterator();
		while(atoms.hasNext())
		{
			IAtom atom = new Atom((Atom) atoms.next());
			atom.setMolecule(this);
			super.put(atom.getName(), atom);
		}
		//Copy plane atoms
		for(IAtom atom : right.planeAtom)
		{
			planeAtom.add(super.get(atom.getName()));
		}
		this.lastAtomAdded = right.lastAtomAdded;
		this.isDimer2Algo = right.isDimer2Algo;
		angX = right.angX;
		angY = right.angY;
		angZ = right.angZ;
		makeCanonicalView();
		setWorldCoordinates();
	}
}
