package scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scheduler.interfaces.Message;

/**Selects the next right message 
 * from its MessageHandler's queue. Selection is 
 * based on the order of arrival of Message groups 
 * and on whether the termination message for a group
 * has already been processed.  
 */
public class MessageSelectorTerminationMessageAware {
	private List<Integer> groupsStarted; 
	private List<Integer> terminatedGroups; 
	
	public MessageSelectorTerminationMessageAware() {
		groupsStarted = new ArrayList<Integer>();
		terminatedGroups = new ArrayList<Integer>();
	}
	
	
	/**
	 * Selects the next eligible message from this MessageSelector's 
	 * MessageHandler queue. Selection is such that if processing has begun 
	 * for any group of messages, priority is given to the next message on the 
	 * queue within these groups. If a termination message for a group has 
	 * been processed, and a message from that group is found, a  
	 * TerminationMessageProcessedException is thrown.
	 * 
	 * @param messageQueue
	 * @return
	 * @throws TerminationMessageProcessedException
	 */
	public Message selectNextMessage(List<Message> messageQueue) 
	throws TerminationMessageProcessedException {
		
		Message nextMessage = null;
		
		if(messageQueue != null && !messageQueue.isEmpty()){
			
			if(groupsStarted.isEmpty() && (messageQueue.get(0) != null)) {
				//First message, check if it's a termination message
				Message currentMessage = messageQueue.get(0);
				Debug.debug("Starting new group: "+ currentMessage.getGroupID());
				
				groupsStarted.add(currentMessage.getGroupID());
				checkIfGroupIsTerminated(currentMessage);
				nextMessage = currentMessage;
				
			} else {
				nextMessage = selectNextEligibleMessage(messageQueue);
				
			}
			
		} else if (messageQueue == null){
			throw new IllegalArgumentException("Found null message queue");
			
		}
		
		return nextMessage;
	}
	
	
	/**
	 * Selects the next message from started groups or the next message at the 
	 * head of this MessageSelector's MessageHandler queue.
	 * @param messages
	 * @return
	 * @throws TerminationMessageProcessedException
	 */
	private Message selectNextEligibleMessage(List<Message> messages) 
	throws TerminationMessageProcessedException { 
		Message nextMessage = selectNextMessageFromStartedGroups(messages);
		
		if(nextMessage != null){
			Debug.debug("Found message in started group: " + nextMessage.getGroupID());
			checkIfGroupIsTerminated(nextMessage);
			
		} else {
			//if no message matches groups started, get head of queue and 
			//add that group.
			Debug.debug("Getting message in new group");
			nextMessage = messages.get(0);
			
			Debug.debug("Starting message in new group: " + nextMessage.getGroupID());
			checkIfGroupIsTerminated(nextMessage);
			groupsStarted.add(nextMessage.getGroupID());
		}
		
		return nextMessage;
	}
	
	
	/**
	 * Checks if a termination message for the message's group has 
	 * already been processed. If so, throws a 
	 * TerminationMessageProcessedException.
	 * @param message
	 * @throws TerminationMessageProcessedException
	 */
	private void checkIfGroupIsTerminated(Message message)  
	throws TerminationMessageProcessedException {
		
		int groupId = message.getGroupID();
		if (!terminatedGroups.contains(groupId) && 
			message instanceof TerminationMessage) {
			
			Debug.debug("Adding term message in group: " + groupId);
			terminatedGroups.add(groupId);
			
		} else if (terminatedGroups.contains(groupId)) {
			Debug.debug("Throwing termination message exception");
			throw new 
			TerminationMessageProcessedException("Terminal message for group " 
					                          + groupId + " already processed");
			
		}

	}
	
	/**Checks through this MessageSelector's MessageHandler queue for 
	 * the next Message belonging to a group that has already started.  
	 * */
	private Message selectNextMessageFromStartedGroups (List<Message> messageQueue) {
		Message nextMessage = null;
		//loop though groups started for the first matching message
		Iterator<Integer> groupsIterator = groupsStarted.iterator();
		
		groupsLoop:
			while(groupsIterator.hasNext()) {
				int currentGroup = groupsIterator.next();
				for (Message message : messageQueue) {
					if(message!= null && (message.getGroupID() == currentGroup)) {
						//found a first match. No need to continue.
						nextMessage = message;
						break groupsLoop;
						
					} else if (message == null){
						throw new IllegalArgumentException("Null message found in list");
						
					}
				}
			}
		
		return nextMessage;
		
	}
	
	
}
