package dimerconstr.log;

public interface ILogger {
	//Log message
	public void log(String message);
	//Log message but don't return
	public void log(String message, boolean isOneLine);
	//Log error message
	public void error(String message);
	//Log error message but don't return
	public void error(String message, boolean isOneLine);
	//Terminate logger
	public void terminate();
}
