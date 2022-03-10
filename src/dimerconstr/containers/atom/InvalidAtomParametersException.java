package dimerconstr.containers.atom;

public class InvalidAtomParametersException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5280842875305603856L;

	public InvalidAtomParametersException() {
		super("Invalid atomic name. Valid atomic name has to be in XxNn format, " +
				"where Xx - atomic symbol from Periodic Universal Table. " +
				"Nn - intramolecular atomic number");
	}
}
