/**
 *	Interface for StateHolder
 */
package dimerconstr.app.jobs;

import java.util.List;

import dimerconstr.containers.bond.IHBond;
import dimerconstr.containers.molecule.IMolecule;

public interface IJobDescriptor {
	//Get job name
	public String getJobName();
	//Get 1st monomer
	public IMolecule getMonomer1();
	//Get 2nd monomer
	public IMolecule getMonomer2();
	//Get H-bonds list
	public List<IHBond> getHBonds();
}
