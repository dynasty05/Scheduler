package scheduler;

/**
 * An Exception to indicate that a message in a group whose termination 
 * message has already been processed has been found.  
 * @author user
 *
 */
public class TerminationMessageProcessedException extends Exception{

	public TerminationMessageProcessedException(String message){
		super(message);
	}
}
