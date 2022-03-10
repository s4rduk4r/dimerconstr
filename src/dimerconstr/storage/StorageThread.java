package dimerconstr.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import dimerconstr.aux.math.Vector3d;
import dimerconstr.containers.atom.IAtom;
import dimerconstr.containers.molecule.IMolecule;

public class StorageThread extends Thread implements IStorage {
	/*
	 *	Properties
	 */
	//Lock
	Lock lock = new ReentrantLock();
	//Semaphore
	Semaphore count = new Semaphore(0);
	//Kill flag
	boolean isTerminated = false;
	//Complexes to store
	protected Queue<IMolecule> storeList = new LinkedList<IMolecule>();
	//Instance
	static protected IStorage instance = new StorageThread(); 
	
	/*
	 *	In-class constants
	 */
	//Filename extension
	protected final String FILE_EXT = ".xyz";
	
	/*
	 *	Interface
	 */
	//Get singleton instance
	static public IStorage getInstance() {
		return instance;
	}
	
	//Runnable
	//Thread logic
	@Override
	public void run() {
		while(true)
		{
			try {
				count.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Terminate if required
			if(isTerminated && isEmpty())
				return;
			//Store complexes
			lock.lock();
			IMolecule molecule = storeList.remove();
			lock.unlock();
			FileWriter file;
			try {
				String filename = molecule.getName() + FILE_EXT;
				file = new FileWriter(filename);
				Iterator<IAtom> atoms = molecule.getAtoms();
				while(atoms.hasNext())
				{
					IAtom atom = atoms.next();
					Vector3d pos = atom.getPosition();
					String line = String.format("%d\t%f\t%f\t%f\n", atom.getIndex(), pos.x, pos.y, pos.z);
					file.write(line);
				}
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//IStorage
	//Store molecule to file
	public void store(IMolecule molecule) {
		//No more servings after termination request
		if(isTerminated)
			return;
		lock.lock();
		storeList.add(molecule);
		count.release();
		lock.unlock();
	}
	
	//Terminate storage
	public void terminate() {
		lock.lock();
		isTerminated = true;
		count.release();
		lock.unlock();
	}

	/*
	 *	Auxiliary methods
	 */
	//Check if job list is empty
	protected boolean isEmpty() {
		lock.lock();
		boolean isEmpty = storeList.isEmpty();
		lock.unlock();
		return isEmpty;
	}
	
	/*
	 *	Constructors
	 */
	//Default constructor
	protected StorageThread() {
	}
}
