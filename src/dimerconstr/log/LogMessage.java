package dimerconstr.log;

public class LogMessage {
	/*
	 *	Properties
	 */
	//Log message
	public String message = null;
	//Oneliner flag
	public boolean isOneLiner = false;
	//Error flag
	public boolean isError = false;
	
	/*
	 *	Constructors
	 */
	public LogMessage(String msg) {
		message = msg;
	}
	
	public LogMessage(String msg, boolean oneliner, boolean error) {
		message = msg;
		isOneLiner = oneliner;
		isError = error;
	}
}
