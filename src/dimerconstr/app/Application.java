/**
 *	Application entry point
 */
package dimerconstr.app;

import java.io.IOException;

import dimerconstr.app.inputparser.IInputParser;
import dimerconstr.app.inputparser.InputParser;
import dimerconstr.app.inputparser.InvalidInputFileStructureException;
import dimerconstr.app.jobs.IJobDescriptor;
import dimerconstr.app.jobs.TerminatorJob;
import dimerconstr.constructor.DimerConstructor;
import dimerconstr.constructor.IDimerConstructor;
import dimerconstr.containers.atom.InvalidAtomParametersException;
import dimerconstr.log.ILogger;
import dimerconstr.log.LoggerThread;
import dimerconstr.storage.StorageThread;

public class Application {
	/*
	 *	Properties
	 */
	//Singleton instance
	protected static final Application instance = new Application();
	protected final ILogger log = LoggerThread.getInstance();

	/*
	 *	In-class constants
	 */
	//Input file format help
	protected static final String help = 
			"Input file format:\n" +
			"--------------------\n" +
			"Input file consists of commentary lines and 3 sections: TITLE, GEOMETRY, BONDS\n" +
			"Each section is terminated by an empty line. \n" +
			"Each commentary begins with '#' character. " +
			"Example of commentary line is given below:\n\n" +
			"# This is a commentary line\n\n" +
			" TITLE section consists of any number alphanumeric characters. No special characters allowed.\n\n" +
			" GEOMETRY section consists of 2 monomer descriptions. \n" +
			"Monomer description starts with molecule name on a separate line. " +
			"Then goes any number of atomic descriptions in format:\n" +
			"\t\tIMN	X	Y	Z	DAM\n" +
			"where\tIMN - intramolecular name. E.g. N1, N3, H4-1, etc.\n" +
			"\tX, Y, Z - floating point atomic coordinates in decard coordinate system\n" +
			"\tDAM - donor-acceptor marker. " +
			"If atom is donor - then it must be marked by 'D' or 'd' character.\n" +
			"If atom is acceptor - then it must be marked by 'A' or 'a' character. " +
			"If atom is neither donor or acceptor - then no marker is needed\n" +
			"\tP - molecule plane atom. If atom marked with 'P' or 'p' character, " +
			"then it is considered as belonging to molecule plane.\n\n" +
			" BONDS section consists of H-bonds pairs in format: IMN1:IMN2 IMN3:IMN4\n" +
			"where\tIMN1 and IMN3 are 1st monomer's atoms.\n" +
			"\tIMN2 and IMN4 are 2nd monomer's atoms.\n";
	
	/*
	 *	Interface
	 */
	//Get instance
	public static Application getInstance() {
		return instance;
	}
	//Run application
	public void run(String args[])
	{
		//USAGE
		if(0 == args.length) {
			this.usage();
			return;
		}
		//Help
		if(args[0].equals("-help")) {
			this.help();
			return;
		}
		//Main cycle
		log.log(String.format("Found %d jobs to process", args.length));
		//Create dimer constructor worker
		IDimerConstructor constructor = createDimerConstructor();
		//Create storage thread
		new Thread((Runnable)StorageThread.getInstance()).start();
		//Create logger thread
		new Thread((Runnable)LoggerThread.getInstance()).start();
		for(String file : args)
		{
			//Parse all input files
			IInputParser parser = InputParser.getInstance();
			try {
				//Parse input file to form a job
				IJobDescriptor job = parser.parse(file);
				//Add this job to list
				constructor.add(job);
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch(InvalidInputFileStructureException e)
			{
				e.printStackTrace();
			} catch(InvalidAtomParametersException e)
			{
				e.printStackTrace();
			}
		}
		//Send terminator object
		constructor.add(TerminatorJob.instance);
		//Wait for worker threads to finish their jobs
		try {
			waitForConstructor(constructor);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Terminate storage thread
		StorageThread.getInstance().terminate();
		//Terminate logger thread
		log.terminate();
	}
	
	/*
	 *	Auxiliary methods
	 */
	//Usage string
	protected void usage() {
		System.out.println("USAGE: dimerconstr inputfile1 [inputfile2] [...]");
		System.out.println("For input file format call with -help key");
	}
	//Input file format help
	protected void help() {
		System.out.println(help);
	}
	//Prepare dimer constructor
	protected IDimerConstructor createDimerConstructor() {
		IDimerConstructor constructor = DimerConstructor.getInstance();
		constructor.startConstructor();
		return constructor;
	}
	//Wait for worker threads
	protected void waitForConstructor(IDimerConstructor constructor) throws InterruptedException {
		constructor.waitConstructor();
	}
	
	
	/*
	 *	Constructors
	 */
	
	
	
	/*
	 *	Entry point
	 */
	public static void main(String args[]) {
		Application app = Application.getInstance();
		app.run(args);
	}
}
