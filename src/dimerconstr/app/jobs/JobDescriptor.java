/**
 *	JobDescriptor class is a job parameters holder
 */
package dimerconstr.app.jobs;

import java.util.List;

import dimerconstr.containers.bond.IHBond;
import dimerconstr.containers.molecule.IMolecule;

public class JobDescriptor implements IJobDescriptor {
	/*
	 *	Properties
	 */
	//Job name
	protected String name = null;
	//Monomers
	protected IMolecule monomer1 = null;
	protected IMolecule monomer2 = null;
	//H-bonds list
	protected List<IHBond> hbonds = null;
	
	/*
	 *	Interface
	 */
	//Get job name
	@Override
	public String getJobName() {
		return this.name;
	}
	//Get 1st monomer
	@Override
	public IMolecule getMonomer1() {
		return this.monomer1;
	}
	//Get 2nd monomer
	@Override
	public IMolecule getMonomer2() {
		return this.monomer2;
	}
	//Get H-bonds list
	@Override
	public List<IHBond> getHBonds() {
		return hbonds;
	}
	
	/*
	 *	Constructors
	 */
	//Constructor
	public JobDescriptor(String jobName, IMolecule mol1, IMolecule mol2, List<IHBond> bonds) throws InvalidJobParametersException {
		if(null == jobName || null == mol1 || null == mol2 || null == bonds)
			throw new InvalidJobParametersException();
		this.name = jobName;
		this.monomer1 = mol1;
		this.monomer2 = mol2;
		this.hbonds = bonds;
	}
	
}
