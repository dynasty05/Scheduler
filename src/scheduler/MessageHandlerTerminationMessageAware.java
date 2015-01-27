package scheduler;
import java.util.ArrayList;
import java.util.List;

import scheduler.interfaces.Gateway;
import scheduler.interfaces.Message;

/**
 * A MessageHandler that supports the processing of termination messages.
 * @author user
 *
 */
public class MessageHandlerTerminationMessageAware {
	
	private List<Message> unprocessedMessages;
	private ArrayList<Message> processedMessages;
	private List<Gateway> gateways;
	
	public MessageHandlerTerminationMessageAware(int numberOfGateways) {
		unprocessedMessages = new ArrayList<Message>();
		processedMessages = new ArrayList<Message>();
		setupGateways(numberOfGateways);
		
	}
	
	/**
	 * Instantiate this MessageHandler's gateways based on 
	 * the configured number.
	 * @param number
	 */
	private void setupGateways(int number){
		if (number > 0) {
			gateways = new ArrayList<Gateway>();
			for (int i = 1; i <= number; i++){
				gateways.add(new GatewayImpl(this));
			}
			
		}else {
			throw new IllegalArgumentException("Number of resources must be " +
												"greater than zero");
		}
	}
	
	
	/**
	 * Sends the messages to be processed to the configured resources 
	 * for processing as they become available. A message is sent for processing 
	 * if the termination message for that group has not already been processed.
	 * Queues up any messages
	 * that cannot be immediately processed.
	 */
	public void sendMessages(List<Message> messages) 
	throws TerminationMessageProcessedException{
		try {
			if (messages != null && !messages.isEmpty()) {
				//add the messages to this handler's queue
				addMessagesToQueue(messages);
				MessageSelectorTerminationMessageAware messageSelector = 
					new MessageSelectorTerminationMessageAware();
				
				while (!unprocessedMessages.isEmpty()) {	
					for (Gateway resource : gateways) { 
						if (resource.isAvailable()) {	
							//select next eligible message
							Message message = 
							messageSelector.selectNextMessage(unprocessedMessages);
							//if null, no more messages.
							if(message == null) {
								break;
							}
							unprocessedMessages.remove(message);
							sendMessageToGateway(resource, message);
							
						}
					}//for
					
				}//while
				
				System.out.println("No more messages to process.");
				
			} else if (messages == null){
				throw new IllegalArgumentException("Found null message in list");
				
			} else {
				//empty list
				System.out.println("Found empty list. No messages to process");
				
			}
			
		} catch(IllegalArgumentException n){
			throw n;
			
		}
		
	}
	
	
	/**
	 * Sends a selected message to the Gateway for processing in its own thread.
	 * @param resource
	 * @param message
	 */
	private void sendMessageToGateway(Gateway resource, Message message) {
		System.out.println("SENDING MESSAGE IN GROUP " + message.getGroupID() + " TO " +
				           "THE GATEWAY");
		
		resource.send(message);
		
		//start new thread to process message
		Thread t = new Thread ((Runnable)resource);
		t.start();
		
		//wait till thread returns the processed message
		while(!message.isCompleted()){
			synchronized(message) {
				try {
					message.wait();
					
				} catch (InterruptedException i) {
					i.printStackTrace();
				}			
			}//synchronized
		}//while
		
	}
	
	
	/**
	 * Adds the messages to this MessageHandler's message queue of messages 
	 * to be processed.
	 */
	private void addMessagesToQueue(List<Message> messages){
		for (Message message : messages){
			if(message != null){
				unprocessedMessages.add(message);
				
			} else{
				throw new IllegalArgumentException("Found null Message in list!");
				
			}
		}
	}


	/**
	 * Adds a processed message to this MessageHandler's list of 
	 * processed messages.
	 */
	public void addProcessedMessage(Message message) {
		if(message != null){
			synchronized(message) {
				if (((MessageImpl)message).isCompleted()) {
					processedMessages.add(message);
					message.notify();
						
				} else {
					throw new IllegalArgumentException("Cannot add unprocessed message " 
															+	"to processed list");
				}
			}
			
		} else{
			throw new IllegalArgumentException("Cannot add null message to processed list");
		}
	}
	
	
	/**
	 * Returns the list of messages that have been processed through 
	 * this MessageHandler.
	 */
	public List<Message> getProcessedMessages () {
			return this.processedMessages;
		
	}

}
