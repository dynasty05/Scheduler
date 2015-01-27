package test;
import static org.junit.Assert.fail;
import org.junit.Test;

import scheduler.MessageImpl;

public class TestMessageImpl {
	
	@Test
	/**
	 * Description: instantiate message with negative group id
	 * 
	 * Expected: IllegalArgumentException
	 */
	public void test_Constructor_invalidGroupId () {
		try {
			MessageImpl message = new MessageImpl(-1);
			
		} catch (IllegalArgumentException i) {
			
		} catch (Exception e) {
			fail("Expected IllegalArgumentException but found " + e.getClass());
			
		}
		 
	}
}
