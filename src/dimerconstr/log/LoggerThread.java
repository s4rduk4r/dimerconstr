package dimerconstr.log;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoggerThread extends Thread implements ILogger {
	/*
	 *	Properties
	 */
	//Singleton
	protected static LoggerThread instance = new LoggerThread();
	//Mutex
	protected Lock lock = new ReentrantLock();
	//Semaphore
	protected Semaphore count = new Semaphore(0);
	//Kill flag
	protected boolean isTerminated = false;
	//Job list
	protected List<LogMessage> jobs = new LinkedList<LogMessage>();
	//Log file
	protected String logFilename = null;
	//First time logging flag
	protected boolean isFirstLog = true;
	
	/*
	 *	In-class constants
	 */
	//Date-time format
	protected final String dateFormat = "dd-MM-yyyy";
	protected final String timeFormat = "HH:mm:ss";
	protected final String firstLogEntry = "-----BREAK-LINE-----\n";
	protected final String error = "[ERROR]";
	
	/*
	 *	Interface
	 */
	//Singleton instance
	static public ILogger getInstance() {
		return instance;
	}
	
	//Thread
	@Override
	public void run() {
		while(true) {
			try {
				count.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Terminate
			if(this.isEmpty() && isTerminated)
				return;
			lock.lock();
			LogMessage message = jobs.remove(0);
			lock.unlock();
			//Print on screen
			String msg = message.message;
			if(message.isError) {
				System.err.printf(message.isOneLiner ? msg : msg.concat("\n"));
			} else {
				System.out.printf(message.isOneLiner ? msg : msg.concat("\n"));
			}
			//Log to file
			try {
				FileWriter logFile = new FileWriter(logFilename, true);
				//First log entry
				if(isFirstLog) {
					logFile.write(firstLogEntry);
					isFirstLog = false;
				}
				//Prepare message for logging to file
				if(message.isError) msg = msg.concat(error);
				msg = this.getTime() + msg + '\n';
				logFile.write(msg);
				logFile.close();
			} catch (IOException e) {
				System.err.println("ERROR: Unable to log into file");
			}
		}
	}
	//ILogger
	//Log message
	@Override
	public void log(String message) {
		//No jobs for terminated thread
		if(isTerminated)
			return;
		lock.lock();
		jobs.add(new LogMessage(message));
		count.release();
		lock.unlock();
	}
	//Log message but don't return
	public void log(String message, boolean isOneLine) {
		//No jobs for terminated thread
		if(isTerminated)
			return;
		lock.lock();
		jobs.add(new LogMessage(message, true, false));
		count.release();
		lock.unlock();
	}
	//Log error message
	public void error(String message) {
		//No jobs for terminated thread
		if(isTerminated)
			return;
		lock.lock();
		jobs.add(new LogMessage(message, false, true));
		count.release();
		lock.unlock();
	}
	//Log error message but don't return
	public void error(String message, boolean isOneLine) {
		//No jobs for terminated thread
		if(isTerminated)
			return;
		lock.lock();
		jobs.add(new LogMessage(message, true, true));
		count.release();
		lock.unlock();
	}
	//Terminate logger
	@Override
	public void terminate() {
		//If already termination request received - do nothing
		if(isTerminated)
			return;
		lock.lock();
		isTerminated = true;
		count.release();
		lock.unlock();
	}
	
	/*
	 *	Auxiliary methods
	 */
	//Check if list is empty
	protected boolean isEmpty() {
		lock.lock();
		boolean isEmpty = jobs.isEmpty(); 
		lock.unlock();
		return isEmpty;
	}
	//Get time
	protected String getTime() {
		return "[" + new SimpleDateFormat(timeFormat).format(Calendar.getInstance().getTime()) + "] ";
	}
	
	/*
	 *	Constructors
	 */
	//Singleton
	protected LoggerThread() {
		logFilename = new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime()) + ".log";
	}
}
