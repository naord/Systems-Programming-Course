package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

/**
 * This action should increase the number of available spaces for the course.
 */
public class OpeningNewPlaceInCourse extends Action<Boolean> {
    private Integer amountOfNewSpotsToAdd;

    /**
     * Construct and initialize the Action - Add Spaces.
     * @param amountOfNewSpotsToAdd - The number of new spaced to add.
     */
    public OpeningNewPlaceInCourse(Integer amountOfNewSpotsToAdd){
        this.amountOfNewSpotsToAdd= amountOfNewSpotsToAdd;
        this.setActionName("Add Spaces");
    }
    /**
     * Override the start method of callback interface callback.
     * This method increase the number of spots in the given course.
     */
    @Override
    public void start(){
        CoursePrivateState coursePrivateState = (CoursePrivateState)this.actorState;
        if( coursePrivateState.getAvailableSpots() >= 0){
            coursePrivateState.setAvailableSpots(coursePrivateState.getAvailableSpots() + amountOfNewSpotsToAdd);
            complete(true);
        }
        else {
            complete(false);
        }
    }
}
