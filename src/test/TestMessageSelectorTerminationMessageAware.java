package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import scheduler.MessageHandlerTerminationMessageAware;
import scheduler.MessageImpl;
import scheduler.MessageSelectorTerminationMessageAware;
import scheduler.TerminationMessage;
import scheduler.TerminationMessageProcessedException;
import scheduler.interfaces.Message;
import static org.junit.Assert.*;

public class TestMessageSelectorTerminationMessageAware {
	
	@Test
	/*Description: A non termination message for a  group occurs after a 
	 * termination message for that group.
	 *Expected: TerminationMessageProcessedException */
	public void test_selectNextMessage_messageAfterTerminationMessage() {		
		//termination message in group 2.
		List<Message> messages = new ArrayList<Message>();
		Message m1 = new MessageImpl(2);
		Message m2 = new MessageImpl(3);
		Message m3 = new TerminationMessage(2);
		Message m4 = new MessageImpl(3);
		Message m5 = new MessageImpl(2);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		
		MessageSelectorTerminationMessageAware selector = 
			new MessageSelectorTerminationMessageAware();
		try{
			Message nextMessage = selector.selectNextMessage(messages);
			assertEquals(m1, nextMessage);
			messages.remove(m1);
			
			nextMessage = selector.selectNextMessage(messages);
			//expecting termination message in group 2
			assertEquals(m3, nextMessage);
			messages.remove(nextMessage);
			
			//should find last message in group 2 and throw exception
			nextMessage = selector.selectNextMessage(messages);
			
		} catch (TerminationMessageProcessedException t) {
			
		} catch (Exception e) {
			fail("Expected TerminationMessageProcessedException but found "
					+ e.getClass());
		}
		
	}
	
	
	
	@Test
	/*Description: A termination message for a group occurs first 
	 * 
	 *Expected: TerminationMessageProcessedException */
	public void test_selectNextMessage_terminationMessageOccursFirstForGroup() {
		//termination message in group 2.
		List<Message> messages = new ArrayList<Message>();
		Message m1 = new TerminationMessage(2);
		Message m2 = new MessageImpl(3);
		Message m3 = new MessageImpl(2);
		Message m4 = new MessageImpl(3);
		Message m5 = new MessageImpl(2);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		
		MessageSelectorTerminationMessageAware selector = 
			new MessageSelectorTerminationMessageAware();
		try{
			Message nextMessage = selector.selectNextMessage(messages);
			assertEquals(m1, nextMessage);
			messages.remove(nextMessage);
			
			nextMessage = selector.selectNextMessage(messages);
			
		}catch (TerminationMessageProcessedException t) {
			
		} catch (Exception e) {
			fail("Expected TerminationMessageProcessedException but found "
					+ e.getClass());
		}
	}
	
	
	@Test
	/*Description: A termination message for a group occurs last for that group 
	 * 
	 *Expected: Messages selected in order of appearance of group. */
	public void test_selectNextMessage_terminationMessageOccursLastForGroup() {
		//last message in group 2 is termination message
		Message m1 = new MessageImpl(2);
		Message m2 = new MessageImpl(3);
		Message m3 = new MessageImpl(2);
		Message m4 = new TerminationMessage(2);
		Message m5 = new MessageImpl(3);
		
		List<Message> messages = new ArrayList<Message>();
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		
		MessageSelectorTerminationMessageAware selector = 
			new MessageSelectorTerminationMessageAware();
		
		try{
			List<Message> selectedMessages = new ArrayList<Message> ();
			Message nextMessage;
			//Iterator<Message> iterator = messages.iterator();
			while (messages.size() > 0) {
				nextMessage = selector.selectNextMessage(messages);
				selectedMessages.add(nextMessage);
				messages.remove(nextMessage);
		
			}
			
			//verify the order of selection
			assertEquals(m1, selectedMessages.get(0));
			assertEquals(m3, selectedMessages.get(1));
			assertEquals(m4, selectedMessages.get(2));
			assertEquals(m2, selectedMessages.get(3));
			assertEquals(m5, selectedMessages.get(4));
			
		} catch (Exception e) {
			fail("Found exception " + e.getClass() + " when none was expected");
			
		}
	}
	
	
	@Test
	/*Description: No termination message in list 
	 * 
	 *Expected: Messages selected in order of appearance of group. */
	public void test_selectNextMessage_noTerminationMessage() {
		List<Message> messages = new ArrayList<Message>();
		MessageImpl m1 = new MessageImpl(5);
		MessageImpl m2 = new MessageImpl(1);
		MessageImpl m3 = new MessageImpl(1);
		MessageImpl m4 = new MessageImpl(2);
		MessageImpl m5 = new MessageImpl(5);
		
		messages.add(m1);
		messages.add(m2);
		messages.add(m3);
		messages.add(m4);
		messages.add(m5);
		
		MessageSelectorTerminationMessageAware selector = 
			new MessageSelectorTerminationMessageAware();
		Message nextMessage;
		try{
			List<Message> selectedMessages = new ArrayList<Message>();
			while(messages.size() > 0){
				nextMessage = selector.selectNextMessage(messages);
				selectedMessages.add(nextMessage);
				messages.remove(nextMessage);
				
			}
			
			//verify messages were selected in the right order
			assertEquals(m1, selectedMessages.get(0));
			assertEquals(m5, selectedMessages.get(1));
			assertEquals(m2, selectedMessages.get(2));
			assertEquals(m3, selectedMessages.get(3));
			assertEquals(m4, selectedMessages.get(4));
			
		} catch (Exception e) {
			fail("Found exception " + e.getClass() + " when none was expected");
			
		}
		
	}
	
	
	
	@Test
	/*Description: null message in list 
	 * 
	 *Expected: IllegalArgumentException */
	public void test_selectNextMessage_nullMessageInList() {
		List<Message> messages = new ArrayList<Message>();
		messages.add(new MessageImpl(1));
		messages.add(null);
		messages.add(new MessageImpl(1));
		messages.add(new MessageImpl(1));
		messages.add(new MessageImpl(1));
		
		try{
			MessageSelectorTerminationMessageAware selector = 
				new MessageSelectorTerminationMessageAware();
			Message nextMessage;
			while(messages.size() > 0){
				nextMessage = selector.selectNextMessage(messages);
				messages.remove(nextMessage);
			}
			
		} catch (IllegalArgumentException i) {
			
		} catch (Exception e) {
			fail("Expected IllegalArgumentException but found "+ e.getClass());
		}
 	}
	
	
}
