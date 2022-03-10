/**
 *	TerminatorJob is a class used as an end mark for constructor worker threads
 */
package dimerconstr.app.jobs;

import java.util.List;

import dimerconstr.containers.bond.IHBond;
import dimerconstr.containers.molecule.IMolecule;

public class TerminatorJob implements IJobDescriptor {
	/*
	 *	Properties
	 */
	public static final TerminatorJob instance = new TerminatorJob();
	
	/*
	 *	Interface
	 */
	//Get job name
	public String getJobName() {
		return null;
	}
	//Get 1st monomer
	public IMolecule getMonomer1() {
		return null;
	}
	//Get 2nd monomer
	public IMolecule getMonomer2() {
		return null;
	}
	//Get H-bonds list
	public List<IHBond> getHBonds() {
		return null;
	}
	
	/*
	 *	Constructor
	 */
	//Default constructor
	protected TerminatorJob() {
	}
}
