package scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scheduler.interfaces.Message;

/**Selects the next right message 
 * from its MessageHandler's queue. Selection is 
 * based on the order of arrival of Message groups.  
 */
public class MessageSelectorImpl {
	
	private List<Integer> groupsStarted; 
	
	public MessageSelectorImpl() {
		groupsStarted = new ArrayList <Integer> ();

	}
	

	/**
	 * Selects the next eligible message from this MessageSelector's 
	 * MessageHandler queue. Selection is such that if processing has begun 
	 * for any group of messages, priority is given to the next message on the 
	 * queue within these groups.
	 */
	public Message selectNextMessage(List<Message> messageQueue) {
		Message nextMessage = null;
		if (messageQueue != null && !messageQueue.isEmpty()) {
			
			//for the very first Message, save the group id
			if(groupsStarted.isEmpty()) {
				Message m = messageQueue.get(0);
				groupsStarted.add(m.getGroupID());
				nextMessage = m;
				
			} else { 
				nextMessage = selectNextEligibleMessage(messageQueue);  		
				
			}
			
		} else if (messageQueue == null){
			throw new IllegalArgumentException("Cannot select a message from null list");
			
		}
		return nextMessage;
		
	}
	
	
	/**
	 * Selects the next message from started groups or the next message at the 
	 * head of this MessageSelector's MessageHandler queue.
	 * @param messages
	 * @return
	 */
	private Message selectNextEligibleMessage(List<Message> messages) {
		Message nextMessage = selectNextMessageFromStartedGroups(messages);
		//if none found, get the head of the queue and add new group 
		if (nextMessage == null && (messages.get(0) != null)) {
			Message m = messages.get(0);
			groupsStarted.add(m.getGroupID());
			nextMessage = m;
			
		} else if (messages.get(0) == null) {
			throw new IllegalArgumentException("Found null message in list");
		}
		
		return nextMessage;
	}
	

	/**Checks through this MessageSelector's MessageHandler queue for 
	 * the next Message belonging to a group that has already started.  
	 * */
	private Message selectNextMessageFromStartedGroups(List<Message> messageQueue) {
		Message nextMessage = null;
		Iterator<Integer> groupsIterator = groupsStarted.iterator();
		
		groupsLoop:
			while (groupsIterator.hasNext()) {
				int currentId = groupsIterator.next();
				//loop through messages for a group match
				for (Message m : messageQueue) {
					if(m != null) {
						int groupId = m.getGroupID();
						if (groupId == currentId) {
							//found a match, exit loop
							nextMessage = m;
							break groupsLoop;
							
						} 
						
					} else {
						throw new IllegalArgumentException("Found null message in list");
						
					}
									
				}//for
			
			}//while
		
		return nextMessage;
	}
	
}
