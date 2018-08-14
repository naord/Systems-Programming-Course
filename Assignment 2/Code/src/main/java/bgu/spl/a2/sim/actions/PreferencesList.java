package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** This Action gets a Preferences List supplied by the student.
 * The Action going through his preferenceList until it finds
 * an available and legal course that student can participate in
 * and attach it the correct grade from the gradeList.
 */
public class PreferencesList extends Action<Boolean>{
    private String studentName;
    private ArrayList<String> preferencesList;
    private ArrayList<Integer> gradesList;
    private Boolean registerSucceededFlag;
    private String currentPreference;
    private Integer currentGrade;
    private Integer currentIndex;

    /**
     * Construct and initialize the Action - Register With Preferences.
     * @param studentName - The id of the student that need to be registered.
     * @param preferencesList - The student's preference List.
     * @param gradesList - The student's grades list that match to each course in his preference list.
     */
    public PreferencesList(String studentName, ArrayList<String> preferencesList, ArrayList<Integer> gradesList){
        this.studentName = studentName;
        this.preferencesList = preferencesList;
        this.gradesList = gradesList;
        this.registerSucceededFlag = false;
        this.currentPreference = preferencesList.get(0);
        this.currentGrade = gradesList.get(0);
        this.currentIndex = 0;
        this.setActionName("Register With Preferences");
    }
    /**
     * Override the start method of callback interface.
     * Creating a new action called - 'PreferencesListAssistant'.
     * We send message with the new action we created to the Course's actor,
     * inorder to be able to add a new student to the course.
     * When the callback returns with positive result the course will be added to the
     * student's course list with the suit grade.
     * Note: This function works by recursion.
     */
    @Override
    public void start(){
        HashMap<String, Integer> gradesHashMap = ((StudentPrivateState)this.actorState).getGrades();//getting student grades hash map
        currentGrade = gradesList.get(currentIndex); //getting course's grade from gradesList
        currentPreference = preferencesList.get(currentIndex); //getting course name from preferenceList
        if(!registerSucceededFlag){
            List<Action<Boolean>> actions = new ArrayList<>();
            Action<Boolean> assistant = new PreferencesListAssistant(studentName, gradesHashMap);
            actions.add(assistant);
            sendMessage(assistant, currentPreference, new CoursePrivateState());
            then(actions, ()->{
                if(actions.get(0).getResult().get()) {
                    ((StudentPrivateState)this.actorState).addCourse(currentPreference, currentGrade);
                    registerSucceededFlag = true;
                    complete(true);
                }
                else{
                    if(preferencesList.size() > (currentIndex + 1)){
                        currentIndex++;
                        this.start();
                    }
                    else{
                        complete(false);
                    }
                }
            });
        }
    }
}