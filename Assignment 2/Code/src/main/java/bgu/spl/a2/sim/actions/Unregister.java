package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.ArrayList;
import java.util.List;

/**
 * This Action Unregister a student from the given course
 * list and remove the course from the grades sheet of the student and increases the
 * number of available spaces).
 */
public class Unregister extends Action<Boolean> {
    private String courseName;
    private String studentName;

    /**
     * Construct and initialize the Action - Unregister.
     * @param courseName - The name of the course the student need to be unregistered from.
     * @param studentName - The student Id of the student that need to be unregistered.
     */
    public Unregister(String courseName,String studentName){
        this.courseName = courseName;
        this.studentName = studentName;
        this.setActionName("Unregister");
    }

    /**
     * Override the start method of callback interface.
     * This method creates a new action named - UnregisterAssistant.
     * It check if the course contains the given student,
     * send message to student actor and wait for response.
     * When it get a true result it remove the student from the course actor.
     */
    public void start(){
        List<Action<Boolean>> actions = new ArrayList<>();
        Action<Boolean> assistant = new UnregisterAssistant(courseName);
        actions.add(assistant);
        sendMessage(assistant, studentName, new StudentPrivateState());
        then(actions, ()->{
            if(actions.get(0).getResult().get()) {
                ((CoursePrivateState)this.actorState).removeStudent(studentName);
                complete(true);
            }
            else{
                complete(false);
            }
        });
    }
}
