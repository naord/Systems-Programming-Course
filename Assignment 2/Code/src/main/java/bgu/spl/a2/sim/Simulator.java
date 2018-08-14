/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.*;
import java.util.*;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.VersionMonitor;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	private static Warehouse warehouse;
	private static VersionMonitor phaseLach;
	private static JsonObject jsonObject;
	public static ActorThreadPool actorThreadPool;

	/**
	 * Begin the simulation Should not be called before attachActorThreadPool()
	 */
	public static void start(){
		actorThreadPool.start();
		setActions(jsonObject, "Phase 1");
		setActions(jsonObject, "Phase 2");
		setActions(jsonObject, "Phase 3");
		HashMap<String,PrivateState> result = new HashMap<>();
		result = end();
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("result.ser");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(result);
		}
		catch (FileNotFoundException ex){
		}
		catch (IOException ex){
		}
	}

	/**
	 * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	 * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	 */
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){//call after parse the json and before start
		actorThreadPool = myActorThreadPool;

	}

	/**
	 * shut down the simulation
	 * @return list of private states
	 */
	public static HashMap<String,PrivateState> end(){
		try {
			actorThreadPool.shutdown();
		}
		catch (InterruptedException ex){
			Thread.currentThread().interrupt();
		}
		HashMap<String,PrivateState> result = new HashMap<>();
		result.putAll(actorThreadPool.getActors());//Copies all of the mappings from actors(ConcurrentHashMap) to result (HashMap).
		return result;
	}

	/**
	 * This method initialize and call start().
	 * @param args
	 */
	public static void main(String [] args){//parse the json first
		FileReader reader = null;
		try {
			reader = new FileReader(args[0]);
		}
		catch (FileNotFoundException ex){}
		JsonParser jsonParser = new JsonParser();
		jsonObject = jsonParser.parse(reader).getAsJsonObject();//save the json file
		setActorThreadPool(jsonObject);
		setWarehouse(jsonObject);
		start();
	}
	/**
	 * Get number of threads from json file and set this ActorThreadPool
	 * @param json
	 */
	private static void setActorThreadPool(JsonObject json){
		int treads = json.get("threads").getAsInt();
		ActorThreadPool pool = new ActorThreadPool(treads);
		attachActorThreadPool(pool);
	}
	/**
	 * get computers array from json file and set this Warehouse.
	 * @param json - the input file
	 */
	private static void setWarehouse(JsonObject json){
		JsonArray computers = json.get("Computers").getAsJsonArray();
		HashMap<String,Computer> computersMap = new HashMap<>();
		for(int i=0; i<computers.size(); i++){
			JsonObject jsonComputer = computers.get(i).getAsJsonObject();
			String computerType = jsonComputer.get("Type").getAsString();
			long successSig = jsonComputer.get("Sig Success").getAsLong();
			long failSig = jsonComputer.get("Sig Fail").getAsLong();
			Computer computer = new Computer(computerType);
			computer.setSig(failSig, successSig);
			SuspendingMutex suspendingMutex = new SuspendingMutex(computer);
			computer.setMutex(suspendingMutex);
			computersMap.put(computerType, computer);
		}
		warehouse = new Warehouse(computersMap);
	}
	/**
	 * Get actions array for each phase from json file,
	 * submit the actions and wait until all of them are done.
	 * @param json - the input file
	 * @param phase - the current phase in the program
	 */
	private static void setActions(JsonObject json, String phase){
		JsonArray actions = json.get(phase).getAsJsonArray();
		List<Action> actionsList = new ArrayList<>();
		String courseName;
		String studentName;
		String departmentName;
		String computerType;
		for (int i=0; i<actions.size(); i++){
			JsonObject jsonAction = actions.get(i).getAsJsonObject();
			String actionName = jsonAction.get("Action").getAsString();
			switch (actionName){
				case "Open Course":
					departmentName = jsonAction.get("Department").getAsString();
					courseName = jsonAction.get("Course").getAsString();
					int courseSpace = jsonAction.get("Space").getAsInt();
					ArrayList<String> prerequisites = new ArrayList<>();
					JsonArray jasonPrerequisites = jsonAction.get("Prerequisites").getAsJsonArray();
					for (int j=0; j<jasonPrerequisites.size(); j++){
						prerequisites.add(jasonPrerequisites.get(j).getAsString());
					}
					Action<Boolean> openCourse = new OpenCourse(courseName, courseSpace, prerequisites);
					actionsList.add(openCourse);
					actorThreadPool.submit(openCourse, departmentName, new DepartmentPrivateState());//submit the action to thread pool
					break;
				case "Add Student":
					departmentName = jsonAction.get("Department").getAsString();
					studentName = jsonAction.get("Student").getAsString();
					Action<Boolean> addStudent = new AddStudent(studentName, departmentName);
					actionsList.add(addStudent);
					actorThreadPool.submit(addStudent, departmentName, new DepartmentPrivateState());
					break;
				case "Participate In Course":
					int grade;
					studentName = jsonAction.get("Student").getAsString();
					courseName = jsonAction.get("Course").getAsString();
					String gradeAsString = jsonAction.get("Grade").getAsString();
					if(gradeAsString.equals("-")){//if we don't get grade "-" mean -1 by forum
						grade = -1;
					}
					else{//if we get grade
						grade = jsonAction.get("Grade").getAsInt();
					}
					Action<Boolean> participateInCourse = new ParticipateInCourse(courseName, studentName, grade);
					actionsList.add(participateInCourse);
					actorThreadPool.submit(participateInCourse, courseName, new CoursePrivateState());
					break;
				case "Unregister":
					studentName = jsonAction.get("Student").getAsString();
					courseName = jsonAction.get("Course").getAsString();
					Action<Boolean> unregister = new Unregister(courseName,studentName);
					actionsList.add(unregister);
					actorThreadPool.submit(unregister, courseName, new CoursePrivateState());
					break;
				case "Close Course":
					departmentName = jsonAction.get("Department").getAsString();
					courseName = jsonAction.get("Course").getAsString();
					Action<Boolean> closeCourse = new CloseCourse(courseName);
					actionsList.add(closeCourse);
					actorThreadPool.submit(closeCourse, departmentName, new DepartmentPrivateState());
					break;
				case "Add Spaces":
					courseName = jsonAction.get("Course").getAsString();
					int spaceToAdd = jsonAction.get("Number").getAsInt();
					Action<Boolean> openingNewPlaceInCourse = new OpeningNewPlaceInCourse(spaceToAdd);
					actionsList.add(openingNewPlaceInCourse);
					actorThreadPool.submit(openingNewPlaceInCourse, courseName, new CoursePrivateState());
					break;
				case "Administrative Check":
					departmentName = jsonAction.get("Department").getAsString();
					ArrayList<String> students = new ArrayList<>();
					JsonArray jasonStudents = jsonAction.get("Students").getAsJsonArray();
					for (int k=0; k<jasonStudents.size(); k++){
						students.add(jasonStudents.get(k).getAsString());
					}
					computerType = jsonAction.get("Computer").getAsString();
					ArrayList<String> conditions = new ArrayList<>();
					JsonArray jasonConditions = jsonAction.get("Conditions").getAsJsonArray();
					for (int m=0; m<jasonConditions.size(); m++){
						conditions.add(jasonConditions.get(m).getAsString());
					}
					Action<Boolean> checkAdministrativeObligations = new CheckAdministrativeObligations(students, computerType, conditions, warehouse);
					actionsList.add(checkAdministrativeObligations);
					actorThreadPool.submit(checkAdministrativeObligations, departmentName, new DepartmentPrivateState());
					break;
				case "Register With Preferences":
					studentName = jsonAction.get("Student").getAsString();
					ArrayList<String> preferences = new ArrayList<>();
					JsonArray jasonPreferences = jsonAction.get("Preferences").getAsJsonArray();
					for (int j=0; j<jasonPreferences.size(); j++){
						preferences.add(jasonPreferences.get(j).getAsString());
					}
					ArrayList<Integer> grades = new ArrayList<>();
					JsonArray jasonGrades = jsonAction.get("Grade").getAsJsonArray();
					for (int j=0; j<jasonGrades.size(); j++){
						grades.add(jasonGrades.get(j).getAsInt());
					}
					Action<Boolean> preferencesList = new PreferencesList(studentName, preferences, grades);
					actionsList.add(preferencesList);
					actorThreadPool.submit(preferencesList, studentName, new StudentPrivateState());
					break;
				default:
					break;
			}
		}
		phaseLach = new VersionMonitor();
		for (Action action: actionsList){//add call back to each action promise to inc the lach
			action.getResult().subscribe(()-> phaseLach.inc());
		}
		try{
			phaseLach.await(actionsList.size()-1);//wait until all actions in phase are done.
		}
		catch (InterruptedException ex){
			Thread.currentThread().interrupt();
		}
	}
}
