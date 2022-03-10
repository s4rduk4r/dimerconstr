package dimerconstr.constructor.aux;

import java.util.List;

import dimerconstr.containers.molecule.IMolecule;

public interface IDimer {
	//Construct dimer
	public void construct();
	//Get constructed complexes
	public List<IMolecule> getComplexes() throws DimerNoComplexesConstructedException;
}
