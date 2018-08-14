package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This action assists to the Administrative Check action by
 * checking if the student meet the obligation that was given.
 */
public class CheckAdministrativeObligationsAssistant extends Action<Boolean> {
    private Computer computer;
    private ArrayList<String> condition ;
    /**
     * Construct and initialize the Action - Administrative check Assistant
     * @param computer - a computer that was given by the promise's result that will set the signature
     * @param condition - a list of courses names that should be passed by the students
     */
    public CheckAdministrativeObligationsAssistant
            (Computer computer, ArrayList<String> condition){
        this.computer = computer;
        this.condition = condition;
        this.setActionName("Administrative Check Assistant");
    }

    /**
     * override the start method of callback interface .
     * This method check the condition list that the student need to pass and give
     * the suit signature.
     */
    @Override
    public void start(){
        HashMap<String,Integer> coursesGrades = ((StudentPrivateState)this.actorState).getGrades();
        ((StudentPrivateState)this.actorState).setSignature(computer.checkAndSign(condition, coursesGrades));
        if(((StudentPrivateState)this.actorState).getSignature() == computer.getSuccessSig() || ((StudentPrivateState)this.actorState).getSignature() == computer.getFailSig()){
            complete(true);
        }
        else{
            complete(false);
        }
    }
}

