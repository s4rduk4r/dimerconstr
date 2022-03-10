package dimerconstr.app.jobs;

public class InvalidJobParametersException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4498528511457593988L;
	
	public InvalidJobParametersException() {
		super("Invalid job parameters provided");
	}
}
