package test;

import java.util.ArrayList;
import java.util.List;


import scheduler.MessageHandlerTerminationMessageAware;
import scheduler.MessageImpl;
import scheduler.TerminationMessage;
import scheduler.TerminationMessageProcessedException;
import scheduler.interfaces.Message;

import org.junit.Test;
import static org.junit.Assert.*;


public class TestMessageHandlerTerminationMessageAware {

	@Test 
	/**Description: Message in a group occurs after a TerminationMessage for that 
	 * group.
	 * Expected: Should throw TerminationMessageProcessedException*/
	public void test_sendMessages_messageEncounteredAfterTerminationMessageProcessed(){
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(2);
		
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		unprocessedMessages.add(new MessageImpl(2));
		unprocessedMessages.add(new MessageImpl(1));
		unprocessedMessages.add(new TerminationMessage(2));
		unprocessedMessages.add(new MessageImpl(2));
		unprocessedMessages.add(new MessageImpl(1));
		
		try{
			messageHandler.sendMessages(unprocessedMessages);
			
		} catch (Exception e) {
			assertEquals(TerminationMessageProcessedException.class, e.getClass());
			
		}		
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		verifyAllMessagesAreProcessed(processedMessages);
		//only two messages should be processed
		assertEquals(2, processedMessages.size());
		
	}
	
	
	@Test
	/**Description: TerminationMessage for a group occurs last for that group
	 * Expected: no exception*/
	public void test_sendMessages_terminationMessageOccursLast() {
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(2));
		unprocessedMessages.add(new MessageImpl(1));
		unprocessedMessages.add(new TerminationMessage(2));
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(5));
		
		try{
			messageHandler.sendMessages(unprocessedMessages);
			
		} catch (Exception e) {
			fail("Exception not expected. Expected all messages to be processed");
			
		}
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		verifyAllMessagesAreProcessed(processedMessages);
		//all messages should be processed
		assertEquals(unprocessedMessages.size(), processedMessages.size());
		
	}
	
	@Test
	/**Description: The TerminationMessage for a group occurs first in the batch.
	 * Expected: TerminationMessageProcessedException after the termination 
	 *message is processed.*/
	public void test_sendMessages_terminationMessageOccursFirstInBatch () {
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		unprocessedMessages.add(new TerminationMessage(2));
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(2));
		unprocessedMessages.add(new MessageImpl(1));
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(5));
		
		try{
			messageHandler.sendMessages(unprocessedMessages);
			
		} catch (Exception e) {
			//expecting TerminationMessageProcessedException
			assertEquals(TerminationMessageProcessedException.class, e.getClass());
			
		}
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		//expecting only the termination message to be processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(1, processedMessages.size());
	
	}
	
	
	@Test
	/**Description: The TerminationMessage for a group occurs first.
	 * Expected: TerminationMessageProcessedException after the termination 
	 *message is processed. All eligible messages before this 
	 *should be processed. */
	public void test_sendMessages_terminationMessageOccursFirstForGroup () {
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(1));
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new TerminationMessage(2));
		unprocessedMessages.add(new MessageImpl(2));
		
		try{
			messageHandler.sendMessages(unprocessedMessages);
			
		} catch (Exception e) {
			//expecting TerminationMessageProcessedException
			assertEquals(TerminationMessageProcessedException.class, e.getClass());
			
		}
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		//expecting messages up to the termination message to be processed
		verifyAllMessagesAreProcessed(processedMessages);
		int expectedSize = unprocessedMessages.size() - 1;
		assertEquals(expectedSize, processedMessages.size());
	
	}
	
	
	@Test
	 /**Description: No Termination message in batch
	 * Expected: All messages should be processed without exception*/
	public void test_sendMessages_noTerminationMessage() {
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(1));
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(2));
		
		try{
			messageHandler.sendMessages(unprocessedMessages);
			
		} catch (Exception e) {
			//no exception expected
			fail("Exception not expected. Expected all messages to be processed");
			
		}
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		//all messages should be processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(unprocessedMessages.size(), processedMessages.size());
	
	}
	
	@Test
	/**Description: Null message occurs in the list to be processed.
	 *Expected: InvalidArgumentException.*/
	public void test_sendMessages_nullMessageInBatch() {
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(1));
		unprocessedMessages.add(null);
		unprocessedMessages.add(new MessageImpl(5));
		unprocessedMessages.add(new MessageImpl(2));
		
		try{
			messageHandler.sendMessages(unprocessedMessages);
			
		} catch (Exception e) {
			//expecting an IllegalArgumentException
			assertTrue(e instanceof IllegalArgumentException);			
		}
		
	}
	
	@Test
	/**Description: Check that empty list is correctly handled
	 *Expected: No exception, no messages returned. */
	public void test_sendMessages_emptyList() {
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		List<Message> unprocessedMessages = new ArrayList<Message> ();
		try {
			messageHandler.sendMessages(unprocessedMessages);
			
		}catch (Exception e) {
			fail("Exception not expected.");
			
		}
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		assertEquals(0, processedMessages.size());
	}
	
	
	@Test
	/**Description: Add a processed message
	 *Expected: Message should be added to processed messages list */
	public void test_addProcessedMessage_processedMessage() {
		Message message = new MessageImpl(1);
		message.completed();
		
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		try {
			messageHandler.addProcessedMessage(message);
			
		} catch (Exception e) {
			fail("Exception not expected");
			
		}
		
		assertEquals(1, messageHandler.getProcessedMessages().size());
		
	}
	
	@Test
	/**Description: Attempt to add an unprocessed message
	 *Expected: IllegalArgumentException */
	public void test_addProcessedMessage_unprocessedMessage() {
		Message message = new MessageImpl(1);
		
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		try {
			messageHandler.addProcessedMessage(message);
			
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			
		}
		
	}
	
	@Test
	/**Description: Attempt to add a null message
	 *Expected: IllegalArgumentException */
	public void test_addProcessedMessage_nullMessage() {
		
		MessageHandlerTerminationMessageAware messageHandler =
			new MessageHandlerTerminationMessageAware(1);
		
		try {
			messageHandler.addProcessedMessage(null);
			
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			
		}
	}
	
	@Test
	/**Description: Invalid argument in constructor.
	 *Expected: IllegalArgumentException
	 * */
	public void test_constructor_invalidArgument() {
		try {
	
			new MessageHandlerTerminationMessageAware(-1);
			
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			
		}
		
	}
	
	
	/*Verifies that all Messages in the list have been processed.*/
	private void verifyAllMessagesAreProcessed(List<Message> messages) {
		for (Message m : messages) {
			assertTrue("Found unprocessed Message " + m + 
					    " among processed Messages!", 
					    ((MessageImpl)m).isCompleted());
		}
	}
}
