package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import java.util.HashMap;

/**
 * This action created to assist to 'PreferencesList' action
 */
public class PreferencesListAssistant extends Action<Boolean>{
    private String studentName;
    private HashMap<String, Integer> gradesSheet;

    /**
     * Construct and initialize the Action - Register With Preferences Assistant.
     * @param studentName - The student that need to be added the course.
     * @param gradesSheet - The student's grade sheet
     */
    public PreferencesListAssistant(String studentName,HashMap<String, Integer> gradesSheet){
        this.studentName = studentName;
        this.gradesSheet = gradesSheet;
        this.setActionName("Register With Preferences Assistance");
    }
    /**
     * Override the start method of callback interface.
     * This method check all prerequisites, and if succeed it return true result.
     */
    @Override
    public void start(){
        boolean canRegister = false;
        CoursePrivateState privateState = (CoursePrivateState)this.actorState;
        if(privateState.getAvailableSpots()>0 && !(privateState.getRegStudents().contains(studentName))){//if has available spots
            canRegister = true;
            for(int i=0; i<privateState.getPrequisites().size() && canRegister;i++){
                if(!gradesSheet.containsKey(privateState.getPrequisites().get(i))){
                    canRegister = false;
                }
            }
        }
        if(canRegister){
            privateState.addStudent(studentName);//register the student
            complete(true);
        }
        else{
            complete(false);
        }
    }
}
