package scheduler.interfaces;


/**The methods that a Gateway must implement*/
public interface Gateway {

	public void send (Message message);
	
	public boolean isAvailable();

}
