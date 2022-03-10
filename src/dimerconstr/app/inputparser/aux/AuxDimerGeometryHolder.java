package dimerconstr.app.inputparser.aux;

import dimerconstr.containers.molecule.Molecule;

public class AuxDimerGeometryHolder {
	/*
	 *	Properties
	 */
	//1st monomer
	public Molecule mol1 = null;
	//2nd monomer
	public Molecule mol2 = null;
	
	/*
	 *	Constructor
	 */
	//Default constructor
	public AuxDimerGeometryHolder() {
	}
	
	//Initialization constructor
	public AuxDimerGeometryHolder(Molecule m1, Molecule m2) {
		mol1 = m1;
		mol2 = m2;
	}
}
