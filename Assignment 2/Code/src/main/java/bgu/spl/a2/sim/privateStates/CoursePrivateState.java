package bgu.spl.a2.sim.privateStates;

import java.util.ArrayList;
import java.util.List;
import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{
	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		this.availableSpots = new Integer(0);
		this.registered = new Integer(0);
		this.regStudents = new ArrayList<>();
		this.prequisites = new ArrayList<>();
	}

	public Integer getAvailableSpots() {
		return availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public List<String> getPrequisites() {
		return prequisites;
	}

	/**
	 * Set available spots at course private state
	 * in order to register this course, availableSpots must be >0
	 * closed course means available spots = -1
	 * @param availableSpots - the value of the spots
	 */
	public void setAvailableSpots(Integer availableSpots) { this.availableSpots = availableSpots; }

	/**
	 * set registered students to this course
	 * @param registered - the value of registered students in this course
	 */
	public void setRegistered(Integer registered) {	this.registered = registered; }

	/**
	 * set prerequisites course list to this course
	 * in order to register to this course student must have all the prerequisites
	 * @param prequisites
	 */
	public void setPrequisites(List<String> prequisites) { this.prequisites = prequisites; }

	/**
	 * register student to this course
	 * update available spots to this course
	 * @param student - the student that registering to this course
	 */
	public void addStudent(String student){
		this.regStudents.add(student);
		setAvailableSpots(this.availableSpots - 1);
		setRegistered(this.registered + 1);
	}

	/**
	 * unregister student to this course
	 * update available spots to this course
	 * @param student - the student that unregistering to this course
	 */
	public void removeStudent(String student){
		this.regStudents.remove(student);
		setAvailableSpots(this.availableSpots + 1);
		setRegistered(this.registered - 1);
	}
}
