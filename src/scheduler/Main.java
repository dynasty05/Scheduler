package scheduler;

import java.util.ArrayList;
import java.util.List;

import scheduler.interfaces.Message;
import scheduler.interfaces.MessageHandler;

public class Main {

	public static void main(String [] args) {
	
		sendOneMessageUsingOneResource();
		
		sendTwoMessagesUsingTwoResources();
		
		sendMultipleMessagesUsingOneResource();
		
		sendMultipleMessagesUsingMultipleResources();
		
		sendMessagesInCancelledGroup();
		
		sendMessagesWithTerminationMessage();

	}
	
	/**
	 * Forward one message to the gateway for a single resource
	 */
	private static void sendOneMessageUsingOneResource() {
		MessageImpl message = new MessageImpl(2);
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		unprocessedMessages.add(message);
		
		displayMessageQueue(unprocessedMessages);
		
		MessageHandler messageHandler = new MessageHandlerImpl(1);
		messageHandler.sendMessages(unprocessedMessages);
		
	}
	
	
	/**
	 * Forward two message to the gateway for two resources
	 */
	private static void sendTwoMessagesUsingTwoResources() {
		
		MessageImpl message1 = new MessageImpl(2);
		MessageImpl message2 = new MessageImpl(2);
		
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		unprocessedMessages.add(message1);
		unprocessedMessages.add(message2);
		
		//send two messages
		displayMessageQueue(unprocessedMessages);
		
		MessageHandler messageHandler = new MessageHandlerImpl(2);
		messageHandler.sendMessages(unprocessedMessages);
		
	}
	
	
	/**
	 * Send multiple messages using one resource, so that ineligible messages
	 * are queued. 
	 */
	private static void sendMultipleMessagesUsingOneResource() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		List<Message> unprocessedMessages = new ArrayList<Message>();
		//groups arriving interleaved
		MessageImpl m1 = new MessageImpl(4);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(1);
		MessageImpl m5 = new MessageImpl(3);
		MessageImpl m6 = new MessageImpl(1);
		
		unprocessedMessages.add(m1);
		unprocessedMessages.add(m2);
		unprocessedMessages.add(m3);
		unprocessedMessages.add(m4);
		unprocessedMessages.add(m5);
		unprocessedMessages.add(m6);
		
		displayMessageQueue(unprocessedMessages);
		
		messageHandler.sendMessages(unprocessedMessages);
		
		//batched groups
		MessageHandlerImpl messageHandler2 = new MessageHandlerImpl(1);
		List<Message> unprocessedMessages2 = new ArrayList<Message>();
		//groups arriving in order
		MessageImpl m7 = new MessageImpl(4);
		MessageImpl m8 = new MessageImpl(1);
		MessageImpl m9 = new MessageImpl(1);
		MessageImpl m10 = new MessageImpl(1);
		MessageImpl m11 = new MessageImpl(3);
		MessageImpl m12 = new MessageImpl(3);
		
		unprocessedMessages2.add(m7);
		unprocessedMessages2.add(m8);
		unprocessedMessages2.add(m9);
		unprocessedMessages2.add(m10);
		unprocessedMessages2.add(m11);
		unprocessedMessages2.add(m12);
		
		displayMessageQueue(unprocessedMessages2);
		
		messageHandler2.sendMessages(unprocessedMessages2);
	
	}
	
	/**
	 * Sends multiple messages using limited (fewer) resources.
	 */
	private static void sendMultipleMessagesUsingMultipleResources() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(3);
		List<Message> unprocessedMessages = new ArrayList<Message>();
		//groups arriving interleaved
		MessageImpl m1 = new MessageImpl(4);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(1);
		MessageImpl m5 = new MessageImpl(3);
		MessageImpl m6 = new MessageImpl(1);
		
		unprocessedMessages.add(m1);
		unprocessedMessages.add(m2);
		unprocessedMessages.add(m3);
		unprocessedMessages.add(m4);
		unprocessedMessages.add(m5);
		unprocessedMessages.add(m6);
		
		displayMessageQueue(unprocessedMessages);
		
		messageHandler.sendMessages(unprocessedMessages);
		
		
		//batched groups
		MessageHandlerImpl messageHandler2 = new MessageHandlerImpl(1);
		List<Message> unprocessedMessages2 = new ArrayList<Message>();
		//groups arriving in order
		MessageImpl m7 = new MessageImpl(4);
		MessageImpl m8 = new MessageImpl(1);
		MessageImpl m9 = new MessageImpl(1);
		MessageImpl m10 = new MessageImpl(1);
		MessageImpl m11 = new MessageImpl(3);
		MessageImpl m12 = new MessageImpl(3);
		
		unprocessedMessages2.add(m7);
		unprocessedMessages2.add(m8);
		unprocessedMessages2.add(m9);
		unprocessedMessages2.add(m10);
		unprocessedMessages2.add(m11);
		unprocessedMessages2.add(m12);
		
		displayMessageQueue(unprocessedMessages2);
		
		messageHandler2.sendMessages(unprocessedMessages2);
		
	}
	
	
	/**
	 * Sends messages for processing, and cancels a message group
	 */
	private static void sendMessagesInCancelledGroup() {
		List<Message> batch1 = new ArrayList<Message>();
		batch1.add(new MessageImpl(1));
		batch1.add(new MessageImpl(4));
		batch1.add(new MessageImpl(4));
		batch1.add(new MessageImpl(1));
		batch1.add(new MessageImpl(1));
		
		MessageHandlerCancelledGroupsAware messageHandler = 
		new MessageHandlerCancelledGroupsAware(1);
		
		messageHandler.sendMessages(batch1);
		
		//cancel group 4
		messageHandler.cancelGroup(4);
		
		//send more messages in group 4
		List<Message> batch2 = new ArrayList<Message>();
		batch2.add(new MessageImpl(1));
		batch2.add(new MessageImpl(4));
		batch2.add(new MessageImpl(4));
		batch2.add(new MessageImpl(1));
		batch2.add(new MessageImpl(1));
		
		messageHandler.sendMessages(batch2);
	}
	
	/**
	 * Sends messages for processing which includes a termination message 
	 * for one of the message groups.
	 */
	private static void sendMessagesWithTerminationMessage () {
		MessageHandlerTerminationMessageAware handler3 =
			new MessageHandlerTerminationMessageAware(2);
		
		//send termination message for group 2
		List<Message> messages = new ArrayList<Message> ();
		messages.add(new MessageImpl(2));
		messages.add(new MessageImpl(1));
		messages.add(new TerminationMessage(2));
		messages.add(new MessageImpl(2));
		
		displayMessageQueue(messages);
		
		try {
			handler3.sendMessages(messages);
			
		} catch (TerminationMessageProcessedException t) {
			System.out.println(t.getMessage());
			t.printStackTrace();
		}
		
	}
	
	/**
	 * Displays the messages to be sent for processing
	 * @param unprocessedMessages
	 */
	private static void displayMessageQueue(List<Message> unprocessedMessages) {
		System.out.println("MESSAGES SENT TO SCHEDULER : ");
		for (Message m : unprocessedMessages) {
			System.out.println(m + " IN GROUP " + m.getGroupID());
			
		}
		System.out.println("");
	}
	
}
