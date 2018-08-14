package bgu.spl.a2.sim.privateStates;

import java.util.ArrayList;
import java.util.List;
import bgu.spl.a2.PrivateState;

/**
 * this class describe department's private state
 */
public class DepartmentPrivateState extends PrivateState{
	private List<String> courseList;
	private List<String> studentList;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public DepartmentPrivateState() {
		this.courseList = new ArrayList<>();
		this.studentList= new ArrayList<>();
	}

	public List<String> getCourseList() {
		return courseList;
	}

	public List<String> getStudentList() {
		return studentList;
	}

	/**
	 * add student to this department
	 * @param student - the student to add to this department
	 */
	public void addStudent(String student) { this.studentList.add(student); }

	/**
	 * add course to this department
	 * @param course - the course to add to this department
	 */
	public void addCourse(String course) { this.courseList.add(course); }

	/**
	 * remove course from this department
	 * @param course - the course to remove from this department
	 */
	public void removeCourse(String course) { this.courseList.remove(course); }
	
}
