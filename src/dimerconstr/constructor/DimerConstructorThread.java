/**
 *	DimerConstructorThread is a dimer construction algorithm implementation
 */
package dimerconstr.constructor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dimerconstr.app.jobs.IJobDescriptor;
import dimerconstr.app.jobs.InvalidJobParametersException;
import dimerconstr.app.jobs.JobDescriptor;
import dimerconstr.app.jobs.TerminatorJob;
import dimerconstr.constructor.aux.Dimer;
import dimerconstr.constructor.aux.Dimer2;
import dimerconstr.constructor.aux.IDimer;
import dimerconstr.containers.atom.IAtom;
import dimerconstr.containers.bond.HBond;
import dimerconstr.containers.bond.IHBond;
import dimerconstr.containers.molecule.IMolecule;
import dimerconstr.containers.molecule.Molecule;
import dimerconstr.log.ILogger;
import dimerconstr.log.LoggerThread;

public class DimerConstructorThread extends Thread {

	/*
	 *	Properties
	 */
	//Dimer constructor joblist holder
	protected IDimerConstructor jobList = null;
	//Logger
	protected ILogger logger = LoggerThread.getInstance();
	//Available CPUs
	protected final static int MAX_CPU = Runtime.getRuntime().availableProcessors();
	
	/*
	 *	In-class constants
	 */
	
	
	/*
	 *	Interface
	 */
	//Runnable
	//Thread logic
	@Override
	public void run() {
		IJobDescriptor job = null;
		try {
			//While jobs are in list - work on them
			while(TerminatorJob.instance != (job = jobList.getJob()))
			{
				int bondPairs = job.getHBonds().size() / 2;
				//Start construction threads
				logger.log(String.format("Processing job: %s\nBond pairs to process: %d", 
										 job.getJobName(), 
										 bondPairs));
				//FIXME IDimer dimer = new Dimer(job);
				/* FIXME Monothread code
				IDimer dimer = new Dimer2(job);
				dimer.construct();
				*/
				//Multithread code
				IDimer[] dimer = new IDimer[MAX_CPU];
				Thread[] t = new Thread[MAX_CPU];
				int bondPairsPerThread = bondPairs / MAX_CPU;
				int bondsLeft = bondPairs - bondPairsPerThread * MAX_CPU;
				Iterator<IHBond> iBond = job.getHBonds().iterator();
				for(int cpu = 0; cpu < MAX_CPU; ++cpu)
				{
					IMolecule mol1 = new Molecule((Molecule) job.getMonomer1());
					IMolecule mol2 = new Molecule((Molecule) job.getMonomer2());
					List<IHBond> bonds = new LinkedList<IHBond>();
					while(iBond.hasNext() && bonds.size() < 2 * bondPairsPerThread)
					{
						for(int i = 0; i < 2; ++i)
							bonds.add(fixHBond(iBond.next(), mol1, mol2));
					}
					if(MAX_CPU == cpu + 1 && bondsLeft > 0) {
						while(iBond.hasNext()) {
							bonds.add(fixHBond(iBond.next(), mol1, mol2));
						}
					}
												
					IJobDescriptor threadJob = new JobDescriptor(job.getJobName(), 
																 mol1, 
																 mol2, 
																 bonds);
					//Construct dimer
					dimer[cpu] = new Dimer2(threadJob);
					//Construct thread
					t[cpu] = new Thread((Runnable) dimer[cpu]);
					t[cpu].start();
				}
				//Wait for constructors to finish
				for(int cpu = 0; cpu < MAX_CPU; ++cpu)
				{
					t[cpu].join();
				}
			}
		}
		//Do nothing
		catch (InterruptedException e) { System.out.println("TID: " + super.getId() + " InterruptedException"); } 
		catch (InvalidJobParametersException e) { e.printStackTrace(); }
	}
	
	/*
	 *	Auxiliary methods
	 */
	//Fix bond
	protected IHBond fixHBond(IHBond bond, IMolecule mol1, IMolecule mol2) {
		IAtom acceptor = bond.getAcceptor();
		IAtom donor = bond.getDonor();
		//Fix acceptor
		IMolecule molecule = (acceptor.getMolecule().getName() == mol1.getName()) ? mol1 : mol2;
		acceptor = molecule.getAtom(acceptor.getName());
		//Fix donor
		donor = (molecule != mol1) ? mol1.getAtom(donor.getName()) : mol2.getAtom(donor.getName());
		return new HBond(acceptor, donor);
	}
	
	//Do algorithm
	protected void algorithm(IJobDescriptor job)
	{
		//Prepare worker threads
		IDimer dimer = new Dimer(job);
		dimer.construct();
	}
		
	/*
	 *	Constructor
	 */
	//Initialization constructor
	public DimerConstructorThread(IDimerConstructor dimerconstr) {
		this.jobList = dimerconstr;
	}
}
