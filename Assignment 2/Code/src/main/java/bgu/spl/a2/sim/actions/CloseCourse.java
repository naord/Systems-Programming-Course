package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import java.util.ArrayList;
import java.util.List;
/**
 * This action should close a course.
 * Should unregister all the registered students in the course and remove the course
 * from the department course's list and from the grade sheets of the students.
 * The number of available spaces of the closed course should be updated to -1.
 * the actor is not removed.
 * After closing the course, all the request for registration should be denied.
 */
public class CloseCourse extends Action<Boolean> {
    private String courseId;

    /**
     * Construct and initialize the Action - Close Course
     * @param courseId - the id of the course that need to be closed
     */
    public CloseCourse(String courseId){
        this.courseId = courseId;
        this.setActionName("Close Course");
    }

    /**
     * Override the start method of callback interface.
     * This method send message, with the new action that was created- CloseCourseAssistant,
     * to the course's actor from the department's actor,
     * and remove the course from the department's courses list.
     */
    @Override
    public void start(){
        List<Action<Boolean>> actions = new ArrayList<>();
        Action<Boolean> assistant = new CloseCourseAssistant(courseId);
        actions.add(assistant);
        sendMessage(assistant, courseId, new CoursePrivateState());
        then(actions, ()->{
            if(actions.get(0).getResult().get()) {
                ((DepartmentPrivateState)this.actorState).removeCourse(courseId);
                complete(true);
            }
            else{complete(false); }
        });
    }
}
