package scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import scheduler.interfaces.Message;
import scheduler.interfaces.MessageSelector;

/**Selects the next right message 
 * from its MessageHandler's queue. Selection is 
 * based on the order of arrival of Message groups
 * and on whether the group has been cancelled or not.  
 */
public class MessageSelectorCancelledGroupsAware implements MessageSelector{  
	private List<Integer> groupsStarted;
	private HashSet<Integer> cancelledGroups;
	
	public MessageSelectorCancelledGroupsAware() {
		groupsStarted = new ArrayList <Integer> ();

	}
	
	
	/**
	 * Selects the next eligible message from this MessageSelector's 
	 * MessageHandler queue. Selection is such that if processing has begun 
	 * for any group of messages, priority is given to the next message on the 
	 * queue within these groups. However, a message is not selected if its group 
	 * has been cancelled.
	 */
	public Message selectNextMessage(List<Message> messageQueue) {
		if (messageQueue != null) {
			Message nextMessage = null;
			//remove any cancelled messages
			removeCancelledMessages(messageQueue);
			
			if (!messageQueue.isEmpty() && groupsStarted.isEmpty()) {
				//for first message, save the group id
				Message message = messageQueue.get(0);
				groupsStarted.add(message.getGroupID());
				nextMessage = message;
				
			} else if (!messageQueue.isEmpty()) {
				//get the next eligible message if any
				nextMessage = selectNextEligibleMessage(messageQueue);
				
			}//if
			return nextMessage;
			
		} else {
			throw new IllegalArgumentException("Cannot select message from null list");
			
		}		
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
	
	
	/**
	 * Removes messages in cancelled groups from this MessageSelector's
	 * MessageHandler queue.
	 * 
	 * @param messageQueue
	 */
	private void removeCancelledMessages(List<Message> messageQueue) {
		Iterator<Message> messageIterator = messageQueue.iterator();
		//remove any message in the cancelled groups
		while (messageIterator.hasNext()) {
			Message message = messageIterator.next();
			int groupId = message.getGroupID();
			
			if (isGroupCancelled(groupId)) {
				messageIterator.remove();
			}
		}
	}
	
	
	/**
	 * Indicates that messages in groupId should no longer 
	 * be eligible for selection.
	 */
	public void addCancelledGroup(int groupId) {
		if (this.cancelledGroups == null && groupId >= 0) {
			this.cancelledGroups = new HashSet<Integer> ();
			this.cancelledGroups.add(groupId);
			
		} else if(groupId >= 0) {
			this.cancelledGroups.add(groupId);
			
		} else {
			throw new IllegalArgumentException("Invalid group id");
		}
		
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
				
				//loop through the messages for a matching id
				Iterator<Message> messageIterator = messageQueue.iterator();
				while(messageIterator.hasNext()) {
					Message currentMessage = messageIterator.next();
					int groupId = currentMessage.getGroupID();
					if (groupId == currentId) {
						nextMessage = currentMessage;
						//found message, no need to continue
						break groupsLoop;
					}
					
				}//while
			
			}//while
		
		return nextMessage;
	}
	
	
	/**
	 * Returns true if groupId has been cancelled.
	 * */
	private boolean isGroupCancelled(int groupId) {
		if (cancelledGroups != null) {
			return cancelledGroups.contains(groupId);
			
		} else {
			return false;
			
		}
	}
	

}
