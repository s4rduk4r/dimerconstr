/**
 *	IDimerConstructor is DimerConstructor's interface
 */
package dimerconstr.constructor;

import dimerconstr.app.jobs.IJobDescriptor;

public interface IDimerConstructor {
	//Start constructor
	public void startConstructor();
	//Wait for constructor
	public void waitConstructor();
	//Add job to list
	public void add(IJobDescriptor job);
	//Get a job
	public IJobDescriptor getJob() throws InterruptedException;
}
