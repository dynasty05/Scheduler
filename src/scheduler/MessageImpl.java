package scheduler;

import scheduler.interfaces.Message;

/**
 * A message that can be processed through a MessageHandler 
 */
public class MessageImpl implements Message {
	private int groupID;
	private boolean completed;
	
	public MessageImpl(int groupID) {
		if (groupID >= 0) {
			this.groupID = groupID;
			
		} else {
			throw new IllegalArgumentException ("Message group id cannot be " +
					                            "negative");
		}
		
	}

	
	/**
	 * Indicates that processing has been completed 
	 * on this Message
	 */
	public void completed() {
		this.completed = true;
		
	}
	
	/**
	 * Returns whether this Message has been processed or not
	 */
	public boolean isCompleted() {
		return this.completed;
	}
	
	/**
	 * Returns the group id of this message.
	 */
	public int getGroupID() {
		return this.groupID;
	}
	
}
