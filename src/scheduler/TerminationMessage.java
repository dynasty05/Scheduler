package scheduler;

/**
 * A termination message for a message group
 * @author user
 *
 */
public class TerminationMessage extends MessageImpl {
	
	public TerminationMessage (int groupID) {
		super (groupID);
	}

}
