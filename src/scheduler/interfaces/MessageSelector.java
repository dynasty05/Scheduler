package scheduler.interfaces;

import java.util.List;


/**The methods that a message selector must implement*/
public interface MessageSelector {
	
	public Message selectNextMessage(List<Message> messageQueue);
}
