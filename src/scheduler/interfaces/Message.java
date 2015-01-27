package scheduler.interfaces;

/**The methods that a Message must implement*/
public interface Message {
	
	public void completed();
	public int getGroupID ();
	public boolean isCompleted();
}
