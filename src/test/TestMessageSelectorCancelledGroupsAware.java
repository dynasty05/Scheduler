package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import scheduler.MessageImpl;
import scheduler.MessageSelectorCancelledGroupsAware;
import scheduler.interfaces.Message;

public class TestMessageSelectorCancelledGroupsAware {
	
	
	@Test
	/**
	 * Description: Cancel a group and supply a queue with messages in that 
	 * group. 
	 * 
	 * Expected: Message in the next occurring group should be selected.
	 */
	public void test_selectNextMessage_sendMessageAfterGroupCancelled () {
		
		MessageSelectorCancelledGroupsAware messageSelector = 
		new MessageSelectorCancelledGroupsAware();
		
		List<Message> messageQueue = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(1);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(1);
		MessageImpl m4 = new MessageImpl(8);
		
		messageQueue.add(m1);
		messageQueue.add(m2);
		messageQueue.add(m3);
		messageQueue.add(m4);
		
		//expecting the first group 1 message
		assertEquals(m1, messageSelector.selectNextMessage(messageQueue));
		messageSelector.addCancelledGroup(1);
		
		//expecting message in group 8 instead of next group 1 message
		assertEquals(m4, messageSelector.selectNextMessage(messageQueue));
		
	}
	
	
	@Test
	/**
	 * Description:  Cancel a group and send a queue with messages in that group. 
	 * 
	 * Expected: message in the next occurring group in the queue
	 */
	public void test_selectNextMessage_cancelGroupBeforeSendingMessage () {
		List<Message> messageQueue = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(1);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(8);
		messageQueue.add(m1);
		messageQueue.add(m2);
		messageQueue.add(m3);
		messageQueue.add(m4);
		
		MessageSelectorCancelledGroupsAware messageSelector = 
		new MessageSelectorCancelledGroupsAware();
		
		//cancel group 1
		messageSelector.addCancelledGroup(1);
		
		//expecting message in group 3
		assertEquals(m3, messageSelector.selectNextMessage(messageQueue));
	}
	
	
	@Test
	/**
	 * Description:  Cancel a group and send a queue not containing messages 
	 * in that group. 
	 * 
	 * Expected: the next message according to occurrence of group id. 
	 */
	public void test_selectNextMessage_cancelGroupNotInMessageQueue() {
		List<Message> messageQueue = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(1);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(8);
		messageQueue.add(m1);
		messageQueue.add(m2);
		messageQueue.add(m3);
		messageQueue.add(m4);
		
		MessageSelectorCancelledGroupsAware messageSelector = 
		new MessageSelectorCancelledGroupsAware();
		
		//cancel group 5
		messageSelector.addCancelledGroup(5);
		
		//expecting message in group 1
		assertEquals(m1, messageSelector.selectNextMessage(messageQueue));
	}
	
	
	@Test
	/**
	 * Description: Cancel an invalid group id
	 * 
	 * Expected: IllegalArgumentException
	 */
	public void test_addCancelledGroup_invalidGroupId () {
		MessageSelectorCancelledGroupsAware messageSelector = 
			new MessageSelectorCancelledGroupsAware();
		
		try{
			messageSelector.addCancelledGroup(-10);
			
		} catch (IllegalArgumentException i) {
			
		}
		
	}

}
