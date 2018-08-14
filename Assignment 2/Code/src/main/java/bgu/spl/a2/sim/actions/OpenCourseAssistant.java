package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import java.util.ArrayList;

/**
 * This action assist the Open Course action.
 */
public class OpenCourseAssistant extends Action<Boolean> {
    private Integer availableSpots;
    private ArrayList<String> prerequisites;

    /**
     * Construct and initialize the Action - Open Course Assistant.
     * @param availableSpots - The number of available spots in the course.
     * @param prerequisites - The list of prerequisites from the course.
     */
    public OpenCourseAssistant(Integer availableSpots, ArrayList<String> prerequisites){
        this.availableSpots = availableSpots;
        this.prerequisites = prerequisites;
        this.setActionName("Open Course Assistant");
    }

    /**
     * Override the start method of callback interface.
     * Sets the prerequisites list and the number of available spots and returns true when complete.
     */
    @Override
    public void start(){
        ((CoursePrivateState)this.actorState).setPrequisites(this.prerequisites);
        ((CoursePrivateState)this.actorState).setAvailableSpots(this.availableSpots);
        complete(true);
    }
}
