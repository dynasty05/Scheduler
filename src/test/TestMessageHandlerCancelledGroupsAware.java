package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import scheduler.MessageHandlerCancelledGroupsAware;
import scheduler.MessageImpl;
import scheduler.interfaces.Message;
import static org.junit.Assert.*;


public class TestMessageHandlerCancelledGroupsAware {

	@Test
	/**
	 * Description: Cancel a group that has already started
	 * 
	 * Expected: no more messages from the canceled group expected.
	 */
	public void test_sendMessages_cancelStartedGroup_addMessagesInCancelledGroup() {
		List<Message> batch1 = new ArrayList<Message>();
		batch1.add(new MessageImpl(1));
		batch1.add(new MessageImpl(4));
		batch1.add(new MessageImpl(4));
		batch1.add(new MessageImpl(1));
		batch1.add(new MessageImpl(1));
		
		MessageHandlerCancelledGroupsAware messageHandler = 
		new MessageHandlerCancelledGroupsAware(1);
		
		messageHandler.sendMessages(batch1);
		//verify all messages were processed
		assertEquals(batch1.size(), messageHandler.getProcessedMessages().size());
		
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
		
		//expect only 3 additional messages
		int expectedSize = batch1.size() + 3;
		assertEquals(expectedSize, messageHandler.getProcessedMessages().size());
		
	}
	
	
	@Test
	/**
	 * Description: Cancel a group before the first message in that group
	 * 
	 * Expected: no messages from the canceled group expected.
	 */
	public void test_sendMessages_cancelGroupNotStarted() {
		
		MessageHandlerCancelledGroupsAware messageHandler = 
			new MessageHandlerCancelledGroupsAware(1);
		
		//cancel group 1
		messageHandler.cancelGroup(1);
		
		List<Message> batch1 = new ArrayList<Message>();
		batch1.add(new MessageImpl(1));
		batch1.add(new MessageImpl(4));
		batch1.add(new MessageImpl(4));
		batch1.add(new MessageImpl(1));
		batch1.add(new MessageImpl(1));
		
		messageHandler.sendMessages(batch1);
		
		//expecting only the two messages in group 4
		int expectedSize = batch1.size() - 3;
		assertEquals(expectedSize, messageHandler.getProcessedMessages().size());
	}
	
	
	@Test
	/**
	 * Description: Cancel a group whose id does not match any of the messages.
	 * 
	 * Expected: all messages should be processed
	 */
	public void test_sendMessages_cancelGroupNotInSentMessages() {
		List<Message> batch1 = new ArrayList<Message>();
		batch1.add(new MessageImpl(1));
		batch1.add(new MessageImpl(4));
		batch1.add(new MessageImpl(1));
		
		MessageHandlerCancelledGroupsAware messageHandler = 
			new MessageHandlerCancelledGroupsAware(1);
			
		messageHandler.sendMessages(batch1);
			
		//verify all messages were processed
		assertEquals(batch1.size(), messageHandler.getProcessedMessages().size());
			
		//cancel group 4
		messageHandler.cancelGroup(10);
			
		//send more messages 
		List<Message> batch2 = new ArrayList<Message>();
		batch2.add(new MessageImpl(2));
		batch2.add(new MessageImpl(4));
		batch2.add(new MessageImpl(4));
			
		messageHandler.sendMessages(batch2);
		
		//expecting all sent messages
		int expectedSize = batch1.size() + batch2.size();
		assertEquals(expectedSize, messageHandler.getProcessedMessages().size());
			
	}
	
	
	@Test
	/**
	 * Description: Cancel an invalid group id
	 * 
	 * Expected: IllegalArgumentException
	 */
	public void test_cancelGroup_invalidGroupId() {
		MessageHandlerCancelledGroupsAware messageHandler = 
		new MessageHandlerCancelledGroupsAware(1);
		
		try{
			messageHandler.cancelGroup(-1);
			
		} catch (IllegalArgumentException i) {
			
		} catch (Exception e) {
			fail("Expected IllegalArgumentException but found " + e.getClass());
			
		}
		
	}
	
}
