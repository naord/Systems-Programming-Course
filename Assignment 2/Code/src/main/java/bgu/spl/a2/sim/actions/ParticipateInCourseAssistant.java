package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.List;

/**
 * This action assista ParticipateInCourse action.
 */
public class ParticipateInCourseAssistant extends Action<Boolean> {
    private String courseName;
    private int grade;
    private List<String> prerequisites;

    /**
     * Construct and initialize the Action - Participate In Course Assistant.
     * @param courseName - The name of course to be registered to.
     * @param grade - The given grade to the course.
     * @param prerequisites - The list of prerequisites.
     */
    public ParticipateInCourseAssistant(String courseName, int grade,  List<String> prerequisites){
        this.courseName = courseName;
        this.grade = grade;
        this.prerequisites = prerequisites;
        setActionName("Participate In Course Assistant");
    }

    /**
     * Override the start method of callback interface callback.
     * This method check all prerequisites, and if succeed it return true result.
     */
    public void start(){
        boolean canRegister = true;
        StudentPrivateState privateState = (StudentPrivateState)this.actorState;//making easier to read code
        if(privateState.getGrades().containsKey(courseName)){//in case student already registered
            canRegister = false;
        }
        for(int i=0; i<prerequisites.size() && canRegister;i++){//check if has all prerequisites
            if(!privateState.getGrades().containsKey(prerequisites.get(i))){
                canRegister = false;
            }
         }
        if(canRegister){
            privateState.addCourse(courseName, grade);//register the student
            complete(true);
        }
        else{
            complete(false);
        }
    }
}
