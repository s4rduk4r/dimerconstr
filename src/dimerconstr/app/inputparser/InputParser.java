/**
 *	Input files parser
 */
package dimerconstr.app.inputparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import dimerconstr.app.inputparser.aux.AuxDimerGeometryHolder;
import dimerconstr.app.jobs.IJobDescriptor;
import dimerconstr.app.jobs.InvalidJobParametersException;
import dimerconstr.app.jobs.JobDescriptor;
import dimerconstr.containers.atom.Atom;
import dimerconstr.containers.atom.EDonorAcceptorMarker;
import dimerconstr.containers.atom.IAtom;
import dimerconstr.containers.atom.InvalidAtomParametersException;
import dimerconstr.containers.bond.HBond;
import dimerconstr.containers.bond.IHBond;
import dimerconstr.containers.molecule.IMolecule;
import dimerconstr.containers.molecule.Molecule;


public class InputParser implements IInputParser {
	/*
	 *	Properties
	 */
	//Singleton instance
	protected final static InputParser instance = new InputParser();
	//

	/*
	 *	In-class constants
	 */
	//Special characters
	protected final static String DAM_ACCEPTOR = "a";
	protected final static String DAM_DONOR = "d";
	protected final static String P = "p";
	protected final static String BOND_ATOM_SEPARATOR = ":";
	//Regular expressions
	protected final static String REGEXP_COMMENT = "#+.*";
	protected final static String REGEXP_ATOM = "[a-z]{1,2}\\d+(-\\d+)?";
	protected final static String REGEXP_FLOAT = "[+-]?\\d+[.]\\d+";
	protected final static String REGEXP_DAM = "[" + DAM_ACCEPTOR + DAM_DONOR +"]";
	protected final static String REGEXP_P = "[" + P + "]";
	protected final static String REGEXP_ATOMICLINE = REGEXP_ATOM + "\\s+"
													+ REGEXP_FLOAT + "\\s+"
													+ REGEXP_FLOAT + "\\s+"
													+ REGEXP_FLOAT +
													"(\\s+"	+ REGEXP_DAM + ")?"
													+ "(\\s+" + REGEXP_P + ")?"
													+ "(\\s+)?";
	protected final static String REGEXP_HBONDLINE = REGEXP_ATOM + "[" + BOND_ATOM_SEPARATOR + "]" + REGEXP_ATOM 
													 + "\\s+"
													 + REGEXP_ATOM + "[" + BOND_ATOM_SEPARATOR + "]" + REGEXP_ATOM
													 + "(\\s+)?";
	
	/*
	 *	Interface
	 */
	//Get instance
	public static InputParser getInstance() {
		return instance;
	}
	//IInputParser
	//Parse file
	@Override
	public IJobDescriptor parse(String filename) throws IOException,
														InvalidInputFileStructureException,
														InvalidAtomParametersException
	{
		IJobDescriptor result = null;
		//Found data variables
		//Job name
		String jobName = null;
		//Monomers
		AuxDimerGeometryHolder monomers = null;
		//H-bonds
		List<IHBond> bonds = new LinkedList<IHBond>();
		//Parse input file
		BufferedReader ofile = new BufferedReader(new FileReader(filename));
		//Parse 1st section. Job title
		jobName = parseSection1(ofile);
		//Parse 2nd section. Monomers descriptions
		monomers = parseSection2(ofile);
		//Parse 3rd section. Dimer H-bonds descriptions
		bonds = parseSection3(ofile, monomers);
		try {
			if(null == monomers.mol1.getPlane() || null == monomers.mol2.getPlane())
				throw new InvalidInputFileStructureException();
			result = new JobDescriptor(jobName, monomers.mol1, monomers.mol2, bonds);
		} catch(InvalidJobParametersException e) {
			System.out.println(e.getMessage());
			throw new InvalidInputFileStructureException();
		}
		return result;
	}
	
	
	/*
	 *	Auxiliary methods 
	 */
	//First section parser. Return jobName
	protected String parseSection1(BufferedReader ofile) throws IOException,
																InvalidInputFileStructureException
	{
		String buffer = null;
		String jobName = new String();
		while(null != (buffer = ofile.readLine()))
		{
			//Check if section ended
			if(buffer.isEmpty())
				break;
			//Skip commentary lines
			if(buffer.matches(REGEXP_COMMENT))
				continue;
			//Take job name
			jobName += buffer;
		}
		//Check if section structure is faulty
		if(jobName.isEmpty())
			throw new InvalidInputFileStructureException();
		return jobName;
	}
	//Second section parser. Return description of 1st and 2nd monomers
	protected AuxDimerGeometryHolder parseSection2(BufferedReader ofile) throws InvalidAtomParametersException, 
																		IOException, 
																		InvalidInputFileStructureException
	{
		String buffer = null;
		//1st monomer filler flag. If true, then collected data goes to mol1, otherwise to mol2
		boolean isFirstMolecule = true;
		//Prepare molecule descriptors for filling
		AuxDimerGeometryHolder result = new AuxDimerGeometryHolder();
		//Fill in molecule descriptors
		while(null != (buffer = ofile.readLine()))
		{
			//Check if section ended
			if(buffer.isEmpty())
				break;
			//Skip commentary lines
			if(buffer.matches(REGEXP_COMMENT))
				continue;
			//Check for atom descriptions
			buffer = buffer.toLowerCase();
			if(buffer.matches(REGEXP_ATOMICLINE)) {
				String s[] = buffer.split("\\s+");
				EDonorAcceptorMarker dam = EDonorAcceptorMarker.none;
				//Check if atom has DAM
				if(s.length > 4) {
					if(s[4].equals(DAM_ACCEPTOR)) {
						dam = EDonorAcceptorMarker.acceptor;
					}
					if(s[4].equals(DAM_DONOR)) {
						dam = EDonorAcceptorMarker.donor;
					}
				}
				//Check if atom has P marker
				boolean isPlaneAtom = false;
				if(s.length > 4 || s.length > 5) {
					if(s[s.length -1].equals(P))
						isPlaneAtom = true;
				}
				Atom atom = new Atom(s[0], new Double(s[1]), new Double(s[2]), new Double(s[3]), dam);
				IMolecule mol = isFirstMolecule ? result.mol1 : result.mol2;
				mol.addAtom(atom, isPlaneAtom);
			} else {
				//Check for monomer name
				if(null == result.mol1) {
					result.mol1 = new Molecule(buffer);
				}
				else {
					if(null == result.mol2) {
						result.mol2 = new Molecule(buffer);
						isFirstMolecule = false;
					}
				}
			}
		}
		//Check if section structure is faulty
		boolean isFaultySection = (null == result.mol1) || (null == result.mol2) 
								|| (result.mol1.getName().isEmpty()) || (result.mol2.getName().isEmpty())
								|| (0 == result.mol1.getAtomCount()) || (0 == result.mol2.getAtomCount());
		if(isFaultySection) {
			result.mol1 = null;
			result.mol2 = null;
			throw new InvalidInputFileStructureException();
		}
		//Lock molecules for changes
		result.mol1.lastAtomAdded();
		result.mol2.lastAtomAdded();
		return result;
	}
	//Third section parser. Return potential H-bonds
	protected List<IHBond> parseSection3(BufferedReader ofile, AuxDimerGeometryHolder dimer) throws	IOException,
																							InvalidInputFileStructureException
	{
		List<IHBond> bonds = new LinkedList<IHBond>();
		String buffer = null;
		while(null != (buffer = ofile.readLine()))
		{
			//Check if section ended
			if(buffer.isEmpty())
				break;
			//Skip commentary lines
			if(buffer.matches(REGEXP_COMMENT))
				continue;
			//Check for H-bonds descriptions
			buffer = buffer.toLowerCase();
			if(buffer.matches(REGEXP_HBONDLINE)) {
				String sb[] = buffer.split("\\s+");
				for(int i = 0; i <= 1 ; ++i)
				{
					String sba[] = sb[i].split(BOND_ATOM_SEPARATOR);
					//Check if atomic names expressed correctly
					for(String s : sba)
					{
						if(!s.matches(REGEXP_ATOM)) {
							throw new InvalidInputFileStructureException();
						}
					}
					//Check DAM
					IAtom atom1 = dimer.mol1.getAtom(sba[0]);
					IAtom atom2 = dimer.mol2.getAtom(sba[1]);
					HBond bond = null;
					if(EDonorAcceptorMarker.acceptor == atom1.getDonorAcceptorMarker()) {
						bond = new HBond(atom1, atom2);
					} else {
						bond = new HBond(atom2, atom1);
					}
					//Add bond to the bonds list
					bonds.add(bond);
				}
			}
		}
		//Return found bonds
		if(bonds.isEmpty())
			throw new InvalidInputFileStructureException();
		return bonds;
	}
	
	
	/*
	 *	Constructors
	 */
	//Default constructor
	protected InputParser() {
	}
}
