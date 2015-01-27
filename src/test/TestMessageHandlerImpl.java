package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import scheduler.MessageHandlerImpl;
import scheduler.MessageImpl;
import scheduler.interfaces.Message;
import static org.junit.Assert.*;

public class TestMessageHandlerImpl {

	@Test
	/**
	 * Description: One message with one resource configured.
	 * Expected: Message is forwarded and processed.
	 */
	public void test_sendMessages_oneResource_oneMessage () {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(1);
		messages.add(m1);
		
		assertFalse(m1.isCompleted());
		messageHandler.sendMessages(messages);
		
		//the message should now be processed
		Message processedMessage = messageHandler.getProcessedMessages().get(0);
		assertTrue(processedMessage.isCompleted());
		
	}
	
	
	@Test
	/**
	 * Description: Multiple messages in the same group with one 
	 * resource configured.
	 * Expected: Messages are forwarded in turn for processing. 
	 */
	public void test_sendMessages_oneResource_multipleMessages_sameGroup() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(1);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(1);
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		
		messageHandler.sendMessages(messages);
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		
		//verify all messages are processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(messages.size(), processedMessages.size());
		
	} 
	
	
	@Test
	/**
	 * Description: Two messages with two resources configured.
	 * Expected: Both messages are forwarded for processing. 
	 */
	public void test_sendMessages_twoResources_twoMessages() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(2);
		
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(1);
		MessageImpl m2 = new MessageImpl(1);
	
		messages.add(m1);
		messages.add(m2);
		
		messageHandler.sendMessages(messages);
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		
		//verify all messages are processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(messages.size(), processedMessages.size());

	} 
	
	
	@Test
	/**
	 * Description: Multiple messages in different groups arriving interleaved 
	 * with one resource configured.
	 * Expected: All messages should return processed, and in the order of 
	 * arrival of the groups 
	 */
	public void 
	test_sendMessages_oneResource_multipleMessages_differentGroupsArrivingInterleaved() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(4);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(1);
		MessageImpl m5 = new MessageImpl(3);
		MessageImpl m6 = new MessageImpl(1);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		messages.add(m6);
		
		messageHandler.sendMessages(messages);
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		
		//verify all messages are processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(messages.size(), processedMessages.size());
	}
	
	
	@Test
	/**
	 * Description: Multiple messages in different groups arriving in batches of
	 * their groups with one resource configured.
	 * Expected: Messages processed and returned in order of their arrival.
	 */
	public void 
	test_sendMessages_oneResource_multipleMessages_differentGroupsArrivingInOrder() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(4);
		MessageImpl m2 = new MessageImpl(4);
		MessageImpl m3 = new MessageImpl(4);
		MessageImpl m4 = new MessageImpl(1);
		MessageImpl m5 = new MessageImpl(1);
		MessageImpl m6 = new MessageImpl(3);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		messages.add(m6);
		
		messageHandler.sendMessages(messages);
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		verifyAllMessagesAreProcessed(processedMessages);
		
		//verify all messages are processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(messages.size(), processedMessages.size());
		
	}
	
	
	@Test
	/**
	 * Description: Multiple messages in different groups arriving interleaved 
	 * more than one resource configured.
	 * Expected: Messages processed and returned in order of arrival of their groups.
	 */
	public void 
	test_sendMessages_multipleResources_multipleMessages_differentGroupsArrivingInterleaved() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(3);
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(4);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(1);
		MessageImpl m5 = new MessageImpl(3);
		MessageImpl m6 = new MessageImpl(1);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		messages.add(m6);
		
		messageHandler.sendMessages(messages);
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		
		//verify all messages are processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(messages.size(), processedMessages.size());
	}
	
	
	@Test
	/**
	 * Description: Multiple messages in different groups arriving in the order 
	 * of their groups with more than one resource configured.
	 * Expected: Messages processed and returned in order of arrival of their groups.
	 */
	public void 
	test_sendMessages_multipleResources_multipleMessages_differentGroupsArrivingInOrder() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(3);
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(4);
		MessageImpl m2 = new MessageImpl(4);
		MessageImpl m3 = new MessageImpl(4);
		MessageImpl m4 = new MessageImpl(1);
		MessageImpl m5 = new MessageImpl(1);
		MessageImpl m6 = new MessageImpl(1);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		messages.add(m6);
		
		messageHandler.sendMessages(messages);
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		
		//verify all messages are processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(messages.size(), processedMessages.size());
		
	}
	
	
	@Test
	/**
	 * Description: Multiple messages in the same group with more than one 
	 * resource configured.
	 * Expected: Messages processed and returned in order of arrival of their groups.
	 */
	public void 
	test_sendMessages_multipleResources_multipleMessages_sameGroup() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(3);
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(5);
		MessageImpl m2 = new MessageImpl(5);
		MessageImpl m3 = new MessageImpl(5);
		MessageImpl m4 = new MessageImpl(5);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);

		messageHandler.sendMessages(messages);
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		
		//verify all messages are processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(messages.size(), processedMessages.size());
		
	}
	
	@Test
	/**
	 * Description: Fewer messages than available resources.  
	 * 
	 * Expected: Messages processed and returned in order of arrival of their 
	 * groups. No exceptions thrown
	 */
	public void test_sendMessages_fewerMessagesThanAvailableResources () {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(5);
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(5);
		MessageImpl m2 = new MessageImpl(5);
		MessageImpl m3 = new MessageImpl(5);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);

		try{
			messageHandler.sendMessages(messages);
			
		}catch(Exception e) {
			fail("Found exception " + e.getClass() + "when none was expected");
			
		}
		
		List<Message> processedMessages = messageHandler.getProcessedMessages();
		
		//verify all messages are processed
		verifyAllMessagesAreProcessed(processedMessages);
		assertEquals(messages.size(), processedMessages.size());
	}
	
	
	@Test
	/**
	 * Description: null message sent for processing  
	 * 
	 * Expected: IllegalArgumentException
	 */
	public void test_sendMessage_nullMessageInList() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		List<Message> messages = new ArrayList<Message>();
		
		messages.add(new MessageImpl(5));
		messages.add(null);
		messages.add(new MessageImpl(5));

		try{
			messageHandler.sendMessages(messages);
			
		}catch(IllegalArgumentException i) {
			
		}catch(Exception e) {
			fail("Expected IllegalArgumentException but found " + e.getClass()); 
			
		}
		
	}
	
	
	@Test
	/**
	 * Description: null message queue  
	 * 
	 * Expected: IllegalArgumentException
	 */
	public void test_sendMessage_nullList() {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		
		try{
			messageHandler.sendMessages(null);
			
		}catch(IllegalArgumentException i) {
			
		}catch(Exception e) {
			fail("Expected IllegalArgumentException but found " + e.getClass()); 
			
		}
		
	}
	
	
	@Test
	/**
	 * Description: add a processed message to the list of processed messages
	 * 
	 * Expected: message added to list.
	 */
	public void test_addProcessedMessage_processedMessage () {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		Message m1 = new MessageImpl(1);
		
		assertFalse(((MessageImpl)m1).isCompleted());
		
		//set as processed
		m1.completed();
		
		messageHandler.addProcessedMessage(m1);
		
		//expecting message in list of processed messages
		assertEquals(m1, messageHandler.getProcessedMessages().get(0));
	}
	
	@Test
	/**
	 * Description: add an unprocessed message to the list of processed  
	 * messages.
	 * 
	 * Expected: IllegalArgumentException 
	 */
	public void test_addProcessedMessage_unprocessedMessage () {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		
		try{
			messageHandler.addProcessedMessage(new MessageImpl(1)); 
			
		}catch(IllegalArgumentException i) {
			
		}catch(Exception e) {
			fail("Expected IllegalArgumentException but found " + e.getClass()); 
			
		}
		
	}
	
	
	@Test
	/**
	 * Description: add null message to list
	 * 
	 * Expected: NullPointerException
	 */
	public void test_addProcessedMessage_nullMessage () {
		MessageHandlerImpl messageHandler = new MessageHandlerImpl(1);
		
		try{
			messageHandler.addProcessedMessage(null); 
			
		}catch(NullPointerException i) {
			
		}catch(Exception e) {
			fail("Expected NullPointerException but found " + e.getClass()); 
			
		}
	}
	
	
	/**
	 * Verifies that all Messages in the list have been processed.
	 * */
	private void verifyAllMessagesAreProcessed(List<Message> messages) {
		for (Message m : messages) {
			assertTrue("Found unprocessed Message " + m + 
					    " among processed Messages!", 
					    ((MessageImpl)m).isCompleted());
		}
	}
}
