package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import scheduler.MessageImpl;
import scheduler.MessageSelectorImpl;
import scheduler.interfaces.Message;
import static org.junit.Assert.*;

public class TestMessageSelectorImpl {
	
	@Test
	/*Description: Messages in different groups, ordered in groups
	 *Expected: messages selected in order of arrival
	 * */
	public void test_selectNextMessage_multipleGroups_ArriveInGroups() {
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(3);
		MessageImpl m2 = new MessageImpl(3);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(4);
		MessageImpl m5 = new MessageImpl(4);
		MessageImpl m6 = new MessageImpl(4);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		messages.add(m6);
		
		List<Message> orderedMessages = selectMessagesFromQueue(messages);
		//messages should be ordered in the same order as the queue
		assertEquals(m1, orderedMessages.get(0));
		assertEquals(m2, orderedMessages.get(1));
		assertEquals(m3, orderedMessages.get(2));
		assertEquals(m4, orderedMessages.get(3));
		assertEquals(m5, orderedMessages.get(4));
		assertEquals(m6, orderedMessages.get(5));
		
		
	}
	
		
	@Test
	/*Description: Messages in different groups arrived interleaved.
	 *Expected: Messages returned arranged in the order of arrival of groups
	 * */
	public void test_selectNextMessage_multipleGroups_interleavedArrival() {
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(3);
		MessageImpl m2 = new MessageImpl(5);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(4);
		MessageImpl m5 = new MessageImpl(5);
		MessageImpl m6 = new MessageImpl(4);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		messages.add(m6);
		
		List<Message> orderedMessages = selectMessagesFromQueue(messages); 
		//order expected: group 3, 5, 4
		assertEquals(m1, orderedMessages.get(0));
		assertEquals(m3, orderedMessages.get(1));
		assertEquals(m2, orderedMessages.get(2));
		assertEquals(m5, orderedMessages.get(3));
		assertEquals(m4, orderedMessages.get(4));
		assertEquals(m6, orderedMessages.get(5));
	}
	

	@Test
	/*Description: All messages in the same group
	 *Expected:  messages returned in the same order*/
	public void test_selectNextMessage_allMessagesInSameGroup() {
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(3);
		MessageImpl m2 = new MessageImpl(3);
		MessageImpl m3 = new MessageImpl(3);
		MessageImpl m4 = new MessageImpl(3);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		
		List<Message> orderedMessages = selectMessagesFromQueue(messages);
		//expecting same order as in list sent
		assertEquals(m1, orderedMessages.get(0));
		assertEquals(m2, orderedMessages.get(1));
		assertEquals(m3, orderedMessages.get(2));
		assertEquals(m4, orderedMessages.get(3));

	}

	@Test	
	/**
	 *Description: null message in the queue
	 *Expected: IllegalArgumentException 
	 */
	public void test_selectNextMessage_nullMessageInQueue() {
		List<Message> messages = new ArrayList<Message>();
		messages.add(new MessageImpl(5));
		messages.add(new MessageImpl(6));
		messages.add(null);
		messages.add(new MessageImpl(1));
		
		try{
			selectMessagesFromQueue(messages);
			
		} catch(IllegalArgumentException i) {
			
		} catch (Exception e) {
			fail("Expected IllegalArgumentException but found "+ e.getClass());
			
		}
	}
	
	
	//null list
	@Test
	/**
	 * Description: null queue
	 * Expected: IllegalArgumentException
	 */
	public void test_selectNextMessage_nullQueue() {
		try{
			MessageSelectorImpl selector = new MessageSelectorImpl();
			selector.selectNextMessage(null);
			
		} catch(IllegalArgumentException i) {
			
		} catch (Exception e) {
			fail("Expected IllegalArgumentException but found "+ e.getClass());
			
		}
	}
	
	
	private List<Message> selectMessagesFromQueue(List<Message> messages) {
		MessageSelectorImpl selector = new MessageSelectorImpl();
		List<Message> selectedMessages = new ArrayList<Message>();
		
		while (messages.size() > 0) {
			Message nextMessage = selector.selectNextMessage(messages);
			selectedMessages.add(nextMessage);
			messages.remove(nextMessage);
		}
		
		return selectedMessages;
	}
}
