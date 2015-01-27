package scheduler;

import java.util.ArrayList;
import java.util.List;

import scheduler.interfaces.Gateway;
import scheduler.interfaces.Message;
import scheduler.interfaces.MessageHandler;

/**
 * Schedules messages for processing using a configured number 
 * of resources.
 * @author user
 *
 */
public class MessageHandlerImpl implements MessageHandler{
	protected List<Message> unprocessedMessages;
	private ArrayList<Message> processedMessages;
	protected List<GatewayImpl> gateways; 
	
	public MessageHandlerImpl (int numberOfGateways) {
		unprocessedMessages = new ArrayList<Message> ();
		processedMessages = new ArrayList<Message> ();
		//the Gateway instances
		setUpGateways(numberOfGateways);	
	}
	
	/**
	 * Instantiate this MessageHandler's gateways based on 
	 * the configured number.
	 * @param number
	 */
	private void setUpGateways (int number) {
		if (number > 0) {
			gateways = new ArrayList<GatewayImpl> ();
			for (int i = 1; i <= number; i++) {
				gateways.add(new GatewayImpl (this));
			}
		}else {
			throw new IllegalArgumentException ("Number of resources must be " +
												"greater than zero");
		}
	}
	
	/**
	 * Sends the messages to be processed to the configured resources 
	 * for processing as they become available. Queues up any messages
	 * that cannot be immediately processed.
	 */
	public void sendMessages(List<Message> messages) {
		if (messages != null && !messages.isEmpty()) {
			//add the messages to the queue
			addToQueue(messages);
			MessageSelectorImpl messageSelector = 
				new MessageSelectorImpl();
			
			while (!unprocessedMessages.isEmpty()) {				
				for (GatewayImpl resource : gateways) {
					if (resource.isAvailable()) {	
						//get the next eligible message
						MessageImpl message = 
							(MessageImpl)messageSelector.selectNextMessage(unprocessedMessages);
						//if null, no more messages.
						if(message == null) {
							break;
						}
						
						unprocessedMessages.remove(message);
						sendMessageToGateway(resource, message);
						
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
	 * Sends a selected message to the Gateway for processing in its own thread.
	 * @param resource
	 * @param message
	 */
	protected void sendMessageToGateway(GatewayImpl resource, MessageImpl message) {
		System.out.println("SENDING MESSAGE IN GROUP " + message.getGroupID() + " TO " +
				           "THE GATEWAY");
		
		resource.send(message);
		
		//start new thread to process message
		Thread t = new Thread (resource);
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
	protected void addToQueue(List<Message> messages) {	
		for (Message m : messages) {
			if (m != null) {		
				unprocessedMessages.add(m);
				
			} else {
				throw new IllegalArgumentException ("Null Message found in list!");
				
			}
			
		}//for
		
	}	
	
	
	/**
	 * Adds a processed message to this MessageHandler's list of 
	 * processed messages.
	 */
	public void addProcessedMessage(Message message) {
		synchronized (message) {
			if (((MessageImpl)message).isCompleted()) {
				processedMessages.add(message);
				message.notify();
				
			} else {
				throw new IllegalArgumentException("Cannot add unprocessed message " +
													"to processed list");
			}
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
