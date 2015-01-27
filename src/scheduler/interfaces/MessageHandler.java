package scheduler.interfaces;

import java.util.List;

/**The methods that a message handler must implement*/
public interface MessageHandler {
	
	public void sendMessages(List<Message> messages);

	public void addProcessedMessage(Message message);
	
	public List<Message> getProcessedMessages ();

}
