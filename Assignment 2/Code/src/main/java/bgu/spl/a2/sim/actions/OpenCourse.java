package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import java.util.ArrayList;
import java.util.List;
/**
 * This action opens a new course in a specified department.
 * The course has an initially available spaces and a list of prerequisites.
 */
public class OpenCourse extends Action<Boolean>{
    private String courseName;
    private Integer availableSpots;
    private ArrayList<String> prerequisites;

    /**
     * Construct and initialize the Action - Open Course.
     * @param courseName - the name of the course that need to be opened.
     * @param availableSpots - the number of available spots in the course.
     * @param prerequisites - the list of prerequisites from the course.
     */
    public OpenCourse(String courseName, Integer availableSpots, ArrayList<String> prerequisites){
        this.courseName = courseName;
        this.availableSpots = availableSpots;
        this.prerequisites = prerequisites;
        this.setActionName("Open Course");
    }

    /**
     * Override the start method of callback interface.
     * creating new action (OpenCourseAssistant) that we send by sendMessage to Course Actor
     * by sending a message a new course is opened (if not exist)
     * we use then to make sure after new course was created we add his name to department private state.
     */
    @Override
    public void start(){
        List<Action<Boolean>> actions = new ArrayList<>();
        Action<Boolean> assistant = new OpenCourseAssistant(availableSpots, prerequisites);
        actions.add(assistant);
        sendMessage(assistant, courseName, new CoursePrivateState());
        then(actions, ()->{
            if(actions.get(0).getResult().get()) {
                ((DepartmentPrivateState)this.actorState).addCourse(courseName);
                complete(true);
            }
            else{
                complete(false);
            }
        });
    }


}
