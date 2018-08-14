package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {

	String computerType;
	long failSig;
	long successSig;
	SuspendingMutex mutex;

	/**
	 * Construct and initialize the Computer.
	 * @param computerType
	 */
	public Computer(String computerType) {
		this.computerType = computerType;
	}

	/**
	 * set the computer signature
	 * @param failSig - fail signature to set
	 * @param successSig - success signature to set
	 */
	public void setSig(long failSig, long successSig){
		this.failSig = failSig;
		this.successSig = successSig;
	}

	/**
	 * set SuspendingMutex to this computer
	 * @param mutex - mutex to set
	 */
	public void setMutex (SuspendingMutex mutex){
		this.mutex = mutex;
	}

	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		boolean isFulfill = true;
		for (int i = 0; i<courses.size() && isFulfill; i++) {
			if (coursesGrades.containsKey(courses.get(i))) {//if contain the course
				if (coursesGrades.get(courses.get(i)) < 56) {//if didn't pass the course
					isFulfill = false;
				}
			} else {//don't contain the course
				isFulfill = false;
			}
		}
		if (isFulfill){//has all courses and pass all of them
			return this.successSig;
		}
		return this.failSig;
	}
	/**
	 * Getter to the success signature.
	 * @return success signature
	 */
	public long getSuccessSig() {
		return this.successSig;
	}
	/**
	 * Getter to the fail signature.
	 * @return fail signature
	 */
	public long getFailSig() {
		return this.failSig;
	}
	/**
	 * Release the computer.
	 */
	public void UpComputer(){
		this.mutex.up();
	}
}
