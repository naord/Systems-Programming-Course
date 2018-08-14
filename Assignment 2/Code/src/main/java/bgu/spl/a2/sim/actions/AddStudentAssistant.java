package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * This class assist to the action 'AddStudent' by communicating with the Student's Actor
 * from the Department's Actor without passing the rules of the assignment
 */
public class AddStudentAssistant extends Action<Boolean> {
    /**
     * Construct and initialize the Action - Add Student Assistant
     */
    public AddStudentAssistant (){
        this.setActionName("AddStudentAssistant");
    }

    /**
     * Override the start method of callback interface callback.
     */
    @Override
    protected void start(){
        complete(true);
    }

}
