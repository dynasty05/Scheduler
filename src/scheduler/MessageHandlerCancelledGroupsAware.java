package scheduler;
import java.util.List;

import scheduler.interfaces.Message;

/**
 * A MessageHandler that supports the cancellation of groups.
 *
 */
public class MessageHandlerCancelledGroupsAware extends MessageHandlerImpl{
	
	private MessageSelectorCancelledGroupsAware messageSelector;
	
	public MessageHandlerCancelledGroupsAware(int numberOfGateways) {
		super(numberOfGateways);
		messageSelector = new MessageSelectorCancelledGroupsAware(); 
	}
	
	@Override
	/**
	 * Sends the messages to be processed to the configured resources 
	 * for processing as they become available. A message is sent for processing 
	 * only if the group for that message has not been cancelled. 
	 * Queues up any messages
	 * that cannot be immediately processed.
	 */
	public void sendMessages(List<Message> messages) {
		if (messages != null && !messages.isEmpty()) {
			//add messages to queue
			addToQueue(messages);
			
			unprocessedLoop:
			while (!unprocessedMessages.isEmpty()) {				
				for (GatewayImpl resource : gateways) {
					if (resource.isAvailable()) {	
						//select next eligible message that has not been 
						//cancelled.
						MessageImpl message = 
							(MessageImpl)messageSelector.selectNextMessage(unprocessedMessages);
						
						if (message != null) {
							unprocessedMessages.remove(message);
							sendMessageToGateway(resource, message);
							
						} else {
							//no more messages to process
							break unprocessedLoop;
						}
						
					}
				}//for
			}//while
			
			System.out.println("Processing complete.");
			System.out.println("");
			
		} else if (messages == null){
			throw new IllegalArgumentException("Null message list found");
			
		}
	}
	
	
	/**
	 * Indicates to this MessageHandler that messages in the groupId 
	 * should not get sent to the Gateway.
	 * @param groupId
	 */
	public void cancelGroup(int groupId) {
		if(groupId < 0) {
			throw new IllegalArgumentException("Invalid group id");
		}
		
		messageSelector.addCancelledGroup(groupId);
		System.out.println("Cancelled group " + groupId);
	}

}
