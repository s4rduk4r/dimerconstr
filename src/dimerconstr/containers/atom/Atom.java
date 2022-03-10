/**
 *	Atom descriptor
 */
package dimerconstr.containers.atom;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import dimerconstr.aux.math.Vector3d;
import dimerconstr.aux.periodicTableOfElements.IUPACElement;
import dimerconstr.aux.periodicTableOfElements.IUPACPeriodicTableOfElements;
import dimerconstr.containers.molecule.IMolecule;




public class Atom implements IAtom {
	/*
	 *	Properties
	 */
	//Owning molecule
	protected IMolecule molecule = null;
	//Intramolecular name in format `XxNn`, where Xx - atomic symbol; Nn - intramolecular atomic number. E.g. N3, C5, H4-1
	protected String name = null;
	//Atomic index in Universal Periodic Table
	protected Integer index = null;
	//van der Waals radius, Angstrom
	protected Double radius = null;
	//Atomic coordinates in decard coordinate system, Angstroms
	protected Vector3d pos = null;
	//Donor-acceptor marker
	protected EDonorAcceptorMarker dam = null;
	
	/*
	 *	Interface
	 */
	//IAtom
	//Get owner molecule
	@Override
	public IMolecule getMolecule() {
		return molecule;
	}
	//Set owner molecule
	@Override
	public void setMolecule(IMolecule molecule) {
		this.molecule = molecule;
	}
	//Get intramolecular name
	@Override
	public String getName() {
		return name;
	}
	//Get atomic index
	@Override
	public Integer getIndex() {
		return index;
	}
	//Get van der Waals radius
	@Override
	public Double getVanDerWaalsRadius() {
		return radius;
	}
	//Get atomic coordinates
	@Override
	public Vector3d getPosition() {
		return pos;
	}
	//Set atomic coordinates
	@Override
	public void setPosition(Vector3d position) {
		this.pos = position;
	}
	//Get donor-acceptor marker (DAM)
	@Override
	public EDonorAcceptorMarker getDonorAcceptorMarker() {
		return this.dam;
	}
	
	/*
	 *	Auxiliary methods
	 */
	//Recognize atomic index and van der Waals radius by it's intramolecular name
	protected void recognizeAtomicProperties(String intraName) throws InvalidAtomParametersException {
		//Convert intamolecular name to lower case
		String intraNameLowerCase = intraName.toLowerCase();
		//Check if provided name is XxNn format
		boolean isSingularSymbol = false;
		Pattern symbolSingular1 = Pattern.compile("[a-z]\\d+");
		Pattern symbolSingular2 = Pattern.compile("[a-z]\\d+-\\d+");
		Pattern symbolDouble1 = Pattern.compile("[a-z][a-z]\\d+");
		Pattern symbolDouble2 = Pattern.compile("[a-z][a-z]\\d+-\\d+");
		if(	symbolSingular1.matcher(intraNameLowerCase).matches()
			|| symbolSingular2.matcher(intraNameLowerCase).matches() )
			isSingularSymbol = true;
		else {
			if( !symbolDouble1.matcher(intraNameLowerCase).matches()
				|| !symbolDouble2.matcher(intraNameLowerCase).matches() )
					isSingularSymbol = false;
			else
				throw new InvalidAtomParametersException();
		}		
		
		//Iterate through all known atoms to recognize this particular atom
		Set<String> periodicTable = IUPACPeriodicTableOfElements.getInstance().getKnownAtoms();
		Iterator<String> name = periodicTable.iterator();
		while(name.hasNext())
		{
			String atomic_symbol = name.next();
			//If provided atom is in our base, then consider intramolecular name as valid
			if(intraNameLowerCase.contains(atomic_symbol.toLowerCase())) {
				if((isSingularSymbol && atomic_symbol.length() == 1)
					|| (!isSingularSymbol && atomic_symbol.length() == 2))
				{
					IUPACElement element = IUPACPeriodicTableOfElements.getInstance().getAtom(atomic_symbol);
					this.name = intraName;
					this.index = element.number;
					this.radius = element.radius;
					return;	
				}
			}
		}	
		
		throw new InvalidAtomParametersException();
	}
	
	/*
	 *	Constructors
	 */	
	//Initialization constructor
	public Atom(String intraMolName, Double x, Double y, Double z) throws InvalidAtomParametersException {
		recognizeAtomicProperties(intraMolName);
		this.pos = new Vector3d(x, y, z);
		this.dam = EDonorAcceptorMarker.none;
	}
	
	//Initialization constructor
	public Atom(String intraMolName, Double x, Double y, Double z, EDonorAcceptorMarker dam) throws InvalidAtomParametersException {
		recognizeAtomicProperties(intraMolName);
		this.pos = new Vector3d(x, y, z);
		this.dam = dam;
	}
	
	//Initialization constructor
	public Atom(IMolecule molecule, String intraMolName, Double x, Double y, Double z) throws InvalidAtomParametersException {
		recognizeAtomicProperties(intraMolName);
		this.pos = new Vector3d(x, y, z);
		this.dam = EDonorAcceptorMarker.none;
		this.molecule = molecule;
	}
	
	//Initialization constructor
	public Atom(IMolecule molecule, String intraMolName, Double x, Double y, Double z, EDonorAcceptorMarker dam) throws InvalidAtomParametersException {
		recognizeAtomicProperties(intraMolName);
		this.pos = new Vector3d(x, y, z);
		this.dam = dam;
		this.molecule = molecule;
	}
	
	//Initialization constructor
	public Atom(String intraMolName, Vector3d r) throws InvalidAtomParametersException {
		recognizeAtomicProperties(intraMolName);
		this.pos = new Vector3d(r);
		this.dam = EDonorAcceptorMarker.none;
	}
	
	//Initialization constructor
	public Atom(IMolecule molecule, String intraMolName, Vector3d r) throws InvalidAtomParametersException {
		recognizeAtomicProperties(intraMolName);
		this.pos = new Vector3d(r);
		this.dam = EDonorAcceptorMarker.none;
		this.molecule = molecule;
	}
	
	//Initialization constructor
	public Atom(String intraMolName, Vector3d r, EDonorAcceptorMarker dam) throws InvalidAtomParametersException {
		recognizeAtomicProperties(intraMolName);
		this.pos = new Vector3d(r);
		this.dam = dam;
	}
	
	//Initialization constructor
	public Atom(IMolecule molecule, String intraMolName, Vector3d r, EDonorAcceptorMarker dam) throws InvalidAtomParametersException {
		recognizeAtomicProperties(intraMolName);
		this.pos = new Vector3d(r);
		this.dam = dam;
		this.molecule = molecule;
	}
	
	//Copy constructor
	public Atom(Atom right) {
		this.name = right.name;
		this.index = right.index;
		this.radius = right.radius;
		this.pos = new Vector3d(right.pos);
		this.dam = right.dam;
		this.molecule = right.molecule;
	}
}
