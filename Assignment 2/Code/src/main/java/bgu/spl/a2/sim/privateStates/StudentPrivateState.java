package bgu.spl.a2.sim.privateStates;

import java.util.HashMap;
import bgu.spl.a2.PrivateState;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState{

	private HashMap<String, Integer> grades;
	private long signature;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public StudentPrivateState() {
		this.grades = new HashMap<String,Integer>();
		this.signature = 0;
	}

	public HashMap<String, Integer> getGrades() {
		return grades;
	}

	public long getSignature() {
		return signature;
	}

	/**
	 * set signature to this student
	 * @param signature - the signature to set to the student
	 */
	public void setSignature(long signature){ this.signature = signature; }
	/**add course*/
	/**
	 * register student to course and update his grade sheet
	 * @param course - the course to register the student
	 * @param grade - the grade of the course to register
	 */
	public void addCourse (String course, Integer grade){ this.grades.put(course, grade); }

	/**
	 * unregister student to course and update his grade sheet
	 * @param course - the course to unregister the student
	 */
	public void removeCourse (String course){ this.grades.remove(course); }
}
