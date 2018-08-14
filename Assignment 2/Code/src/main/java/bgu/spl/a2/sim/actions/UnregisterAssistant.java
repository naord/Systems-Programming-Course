package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.HashMap;

/**
 * This action assists Unregister action.
 */
public class UnregisterAssistant extends Action<Boolean> {
    private String courseName;

    /**
     * Construct and initialize the Action - Unregister Assistant.
     * @param courseName - The name of the course the student need to be unregistered from.
     */
    public UnregisterAssistant(String courseName){
        this.courseName = courseName;
        this.setActionName("Unregister Assistant");
    }

    /**
     * Override the start method of callback interface.
     * This method remove the course from the student's grade sheet and return true if succeed.
     */
    @Override
    public void start(){
        HashMap<String, Integer> grads = ((StudentPrivateState)this.actorState).getGrades();
        if(grads.containsKey(this.courseName)){//if student registered
            ((StudentPrivateState)this.actorState).removeCourse(courseName);
            complete(true);
        }
        else {//student was not registered
            complete(false);
        }

    }
}
