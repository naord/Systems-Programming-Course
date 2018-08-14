package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;
import java.util.HashMap;

/**
 * represents a warehouse that holds a finite amount of computers
 *  and their suspended mutexes.
 */
public class Warehouse {
    private HashMap<String, Computer> computersMap;
    /**
     * Construct and initialize the Warehouse.
     * @param computersMap - Hash Map that contains the computer's type and the computer itself.
     */
	public Warehouse(HashMap<String,Computer> computersMap){
	    this.computersMap = computersMap;
    }
    /**
     * This method return promise for computer, if he is locked the program is not locked
     * @param computerType
     * @return - a promise to a Computer
     */
    public Promise<Computer> getComputer(String computerType){
	    return computersMap.get(computerType).mutex.down();
    }
}
