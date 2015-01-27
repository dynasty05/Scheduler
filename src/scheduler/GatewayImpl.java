package scheduler;

import scheduler.interfaces.Gateway;
import scheduler.interfaces.Message;
import scheduler.interfaces.MessageHandler;


/**
 * The resource used to send messages 
 * to the Gateway.
 */
public class GatewayImpl implements Gateway, Runnable{
	private Message message;
	private Object messageHandler;
	private boolean isAvailable;
		
	public GatewayImpl(Object handler) {
		this.isAvailable = true;
		validateHandler(handler);
		
	}
	
	/**
	 * Validates that the Object is either an instance of MessageHandler or
	 * MessageHandlerTerminationMessageAware.
	 * @param handler
	 * @throws IllegalArgumentException
	 */
	private void validateHandler (Object handler) throws IllegalArgumentException {
		if(handler != null) {
			
			if(handler instanceof MessageHandler || 
			   handler instanceof MessageHandlerTerminationMessageAware) {
				
				messageHandler = handler;
				
			}else {
				throw new IllegalArgumentException("Unexpected handler type found");
				
			}
			
		} else {
			throw new IllegalArgumentException ("Cannot instantiate Gateway with " +
					                        "null message handler"); 
			
		}
	}
	
	
	@Override
	/**
	 * Sends the Message to this Gateway for processing.
	 */
	public void send(Message message) {
		if (message != null && !((MessageImpl)message).isCompleted()) {
			validateMessage(message);
			this.message = message;
			
		} else if (message == null) {
			throw new IllegalArgumentException("Cannot process a null message");
			
		} else {
			throw new IllegalArgumentException("Found already processed message");
		}
	}
	
	
	/**
	 * Checks that message is not of an unsupported type for this Gateway's 
	 * MessageHandler.
	 * @param message
	 */
	private void validateMessage(Message message) {
		if (message instanceof TerminationMessage && 
				messageHandler instanceof MessageHandler) {
			
			throw new IllegalArgumentException("Found unsupported message type: " +
					                           "TerminationMessage");
		}
		
	}
	
	
	@Override
	public boolean isAvailable() {
		return this.isAvailable;
	}

	
	@Override
	/**
	 * Processes this Gateway's message and returns it to its MessageHandler.
	 */
	public void run() {
		//make Gateway unavailable
		this.isAvailable = false;
		message.completed();
		
		if(messageHandler instanceof MessageHandler) {
			MessageHandlerImpl mh = (MessageHandlerImpl)messageHandler;
			mh.addProcessedMessage(message);
			
		} else {
			MessageHandlerTerminationMessageAware mh = 
				(MessageHandlerTerminationMessageAware)messageHandler;
			mh.addProcessedMessage(message);
				
		}
		
		this.isAvailable = true;
		
	}


}
