package test;

import org.junit.Test;

import scheduler.GatewayImpl;
import scheduler.MessageHandlerImpl;
import scheduler.MessageHandlerTerminationMessageAware;
import scheduler.MessageImpl;
import scheduler.TerminationMessage;
import scheduler.interfaces.Gateway;
import scheduler.interfaces.MessageHandler;
import static org.junit.Assert.*;

public class TestGatewayImpl {
	
	@Test
	/*Description: Send an unprocessed Message
	 *Expected: no exception 
	 * */
	public void test_send_unprocessedMessage() {
		Gateway gateway = new GatewayImpl(new MessageHandlerImpl(1));
		
		MessageImpl message = new MessageImpl(3);
		try{
			gateway.send(message);
			
		} catch(Exception e) {
			fail("Found exception when none was expected");
		}
		
	}
	
	
	@Test
	/*Description: Send a processed Message
	 *Expected: IllegalArgumentException 
	 * */
	public void test_send_processedMessage() {
		Gateway gateway = new GatewayImpl(new MessageHandlerImpl(1));
		MessageImpl message = new MessageImpl(3);
		message.completed();
		
		try{
			gateway.send(message);
			
		} catch (IllegalArgumentException i) {
			String expectedMessage = "Found already processed message";
			assertEquals(expectedMessage, i.getMessage());
			
		} catch (Exception e) {
			
		}
	}
	
	
	@Test
	/*Description: Attempt sending termination message to a Gateway using   
	 * the default message handler.
	 *Expected: IllegalArgumentException.*/
	public void test_send_terminationMessageUsingDefaultMessageHandler() {
		MessageHandler messageHandler = new MessageHandlerImpl(1);
		GatewayImpl gateway = new GatewayImpl(messageHandler);
		TerminationMessage message = new TerminationMessage(5); 
		assertFalse(message.isCompleted());
		
		try{
			gateway.send(message);
			
		} catch (IllegalArgumentException i) {
			String expectedMessage = "Found unsupported message type: " +
                                     "TerminationMessage";
			
			assertEquals("Wrong exception found", expectedMessage, i.getMessage());
			
		} catch (Exception e) {
			fail("Got unexpected exception. Expecting IllegalArgumentException");
			
		}

	}
	
	
	@Test 
	/*Description: Send a non termination message to Gateway using 
	 * MessageHandlerTerminationMessageAware. 
	 *Expected: Processed message in handler's processed list.*/
	public void test_send_messageUsingMessageHandlerTerminationMessageAware() {
		MessageHandlerTerminationMessageAware messageHandler = 
			new MessageHandlerTerminationMessageAware(1);
		GatewayImpl gateway = new GatewayImpl(messageHandler);
		MessageImpl message = new MessageImpl(5); 
		try{
			gateway.send(message);
			
		} catch (Exception e) {
			fail("Found exception when none was expected");
		}

	}
	
	
	@Test
	/*Description: Process a Message using a  
	 * MessageHandler.
	 *Expected: Processed message in handler's processed list.*/
	public void test_run_processMessageUsingMessageHandler() {
		MessageHandler messageHandler = new MessageHandlerImpl(1);
		GatewayImpl gateway = new GatewayImpl(messageHandler);
		MessageImpl message = new MessageImpl(5); 
		assertFalse(message.isCompleted());
		
		gateway.send(message);
		gateway.run();
		
		//message should be processed
		assertTrue(message.isCompleted());
		assertEquals(message, messageHandler.getProcessedMessages().get(0));
	}
	
	
	@Test
	/*Description: Process a termination message using   
	 * the right message handler .
	 *Expected: Processed message in handler's processed list.*/
	public void 
	test_run_processTerminationMessageUsingMessageHandlerTerminatonMessageAware() {
		MessageHandlerTerminationMessageAware messageHandler = 
			new MessageHandlerTerminationMessageAware(1);
		GatewayImpl gateway = new GatewayImpl(messageHandler);
		TerminationMessage message = new TerminationMessage(5); 
		assertFalse(message.isCompleted());
		
		gateway.send(message);
		gateway.run();
		
		//message should be processed
		assertTrue(message.isCompleted());
		assertEquals(message, messageHandler.getProcessedMessages().get(0));
	}
	
	
	@Test
	/*Description: Process a MessageImpl using message handler that recognizes 
	 * termination messages.
	 *Expected: Processed message in handler's processed list.*/
	public void 
	test_run_processMessageImplWithMessageHandlerTerminationMessageAware() {
		MessageHandlerTerminationMessageAware messageHandler = 
			new MessageHandlerTerminationMessageAware(1);
		GatewayImpl gateway = new GatewayImpl(messageHandler);
		MessageImpl message = new MessageImpl(1);
		
		gateway.send(message);
		gateway.run();
		
		assertTrue(message.isCompleted());
		assertEquals(message, messageHandler.getProcessedMessages().get(0));
	}
	

}
