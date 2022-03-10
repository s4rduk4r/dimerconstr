package dimerconstr.constructor.aux;

public class DimerNoComplexesConstructedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8290898234417065373L;

	/*
	 *	Constructor
	 */
	public DimerNoComplexesConstructedException() {
		super(String.format("No complexes found!"));
	}
}
