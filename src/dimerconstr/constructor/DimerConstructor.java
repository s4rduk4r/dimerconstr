package dimerconstr.constructor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import dimerconstr.app.jobs.IJobDescriptor;
import dimerconstr.app.jobs.TerminatorJob;


public class DimerConstructor extends ThreadGroup implements IDimerConstructor {
	
	/*
	 *	Properties
	 */
	//Singleton instance
	static final protected IDimerConstructor instance = new DimerConstructor();
	//Job list for worker threads
	protected List<IJobDescriptor> jobList = new LinkedList<IJobDescriptor>();
	//Dimer constructor thread
	protected Thread constructor = null;
	//Mutex
	Lock lock = new ReentrantLock();
	//Semaphore
	Semaphore count = new Semaphore(0);
	
	/*
	 *	In-class constants
	 */
	//Thread group name
	protected final static String TG_NAME = "Constructors";
	//Waiting timeout, ms
	protected final static long WAIT_TIMEOUT = 10;
	
	
	/*
	 *	Interface
	 */
	//Get singleton instance
	static public IDimerConstructor getInstance() {
		return instance;
	}
	
	//IDimerConstructor
	//Start constructor
	public void startConstructor() {
		constructor = new DimerConstructorThread(this);
		constructor.start();
	}
	//Wait for constructor
	public void waitConstructor() {
		try {
			constructor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	//Add job to list
	public void add(IJobDescriptor job) {
		lock.lock();
		jobList.add(job);
		count.release();
		lock.unlock();
	}
	//Get a job
	public IJobDescriptor getJob() {		
		IJobDescriptor job = null;
		try {
			count.acquire();
		} catch (InterruptedException e) { e.printStackTrace(); }
		lock.lock();
		job = jobList.get(0);
		if(TerminatorJob.instance != job)
			jobList.remove(0);
		lock.unlock();
		return job;
	}
	
	/*
	 *	Constructors
	 */
	//Initialization constructor
	protected DimerConstructor() {
		super(TG_NAME);
	}	
}
