package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
/**
 * This action assist in closing the course it changes the course parameters respectively,
 * and returns true result when finishes.
 */
public class CloseCourseAssistant extends Action<Boolean> {
    private String courseId;

    /**
     * Construct and initialize the Action - Close Course Assistant.
     * @param courseId - the course that need to be closed.
     */
    public CloseCourseAssistant(String courseId) {
        this.courseId = courseId;
        this.setActionName("Close Course Assistant");
    }
    /**
     * Override the start method of callback interface.
     * This method going through all the registered students in the course,
     * and remove the course from their grades sheet,
     * changes the Available Spots to '-1' and the number of registered student to '0'.
     */
    public void start(){
        ((CoursePrivateState)this.actorState).setAvailableSpots(-1);
        ((CoursePrivateState)this.actorState).setRegistered(0);
        for (String registeredStudent:((CoursePrivateState)this.actorState).getRegStudents()){
            ((StudentPrivateState)this.pool.getPrivateState(registeredStudent)).removeCourse(courseId);
        }
        ((CoursePrivateState)this.actorState).getRegStudents().clear();
        complete(true);
    }
}