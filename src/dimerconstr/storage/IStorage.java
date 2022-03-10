package dimerconstr.storage;

import dimerconstr.containers.molecule.IMolecule;

public interface IStorage {
	//Store molecule to file
	public void store(IMolecule molecule);
	//Terminate storage
	public void terminate();
}
