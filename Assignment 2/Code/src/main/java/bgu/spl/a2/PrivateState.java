package bgu.spl.a2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * an abstract class that represents private states of an actor
 * it holds actions that the actor has executed so far 
 * IMPORTANT: You can not add any field to this class.
 */
public abstract class PrivateState implements Serializable {
	// holds the actions' name what were executed
	private List<String> history = new ArrayList<>();

	/**
	 * This method returns the history logs.
	 * @return history
	 */
	public List<String> getLogger(){
		return history;
	}
	
	/**
	 * add an action to the records
	 *  
	 * @param actionName - The name of the action that need to be added to the record.
	 */
	public void addRecord(String actionName){
		history.add(actionName);
	}
	
	
}
