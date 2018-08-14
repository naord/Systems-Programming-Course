package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.ArrayList;
import java.util.List;
/**
 * This action should try to register the student in the course,
 * if it succeeds, should add the course to the grades sheet of the student,
 * and give him a grade if supplied.
 */
public class ParticipateInCourse extends Action<Boolean> {
    private String courseName;
    private String studentName;
    private Integer grade;

    /**
     * Construct and initialize the Action - Participate In Course.
     * @param courseName - The name of course to be registered to.
     * @param studentName - Student's name.
     * @param grade - The given grade to the course.
     */
    public ParticipateInCourse(String courseName, String studentName, Integer grade){
        this.courseName = courseName;
        this.studentName = studentName;
        this.grade = grade;
        this.setActionName("Participate In Course");
    }

    /**
     * Override the start method of callback interface callback.
     * This method creates a new action named ParticipateInCourseAssistant.
     * It sends a message to the student's actor and ask him to check all prerequisites,
     * and if succeed it return true result.
     * When the ParticipateInCourseAssistant complete the course's actor check if there
     * are any available spots and if there are it adds the student to
     * the registered students list.
     */
    public void start(){
        CoursePrivateState coursePrivateState = (CoursePrivateState)this.actorState;
        List<Action<Boolean>> actions = new ArrayList<>();
        Action<Boolean> assistant = new ParticipateInCourseAssistant(courseName, grade,((CoursePrivateState)this.actorState).getPrequisites());
        actions.add(assistant);
        if(coursePrivateState.getAvailableSpots()>0
                && !coursePrivateState.getRegStudents().contains(studentName)){//if has available spots & student not registered
            sendMessage(assistant, studentName, new StudentPrivateState());
            then(actions, ()->{
                if(actions.get(0).getResult().get()) {
                    if(coursePrivateState.getAvailableSpots()>0){//if this course still have available spots
                        ((CoursePrivateState)this.actorState).addStudent(studentName);
                        complete(true);
                    }
                    else{//course don't have available spots
                        Action<Boolean> unregister = new UnregisterAssistant(courseName);
                        List<Action<Boolean>> actions1 = new ArrayList<>();
                        actions1.add(unregister);
                        sendMessage(unregister, studentName, new StudentPrivateState());//unregister the student
                        complete(false);
                    }
                }
                else{//in case student does not have prerequisites
                    complete(false);
                }
            });
        }
        else{//in case the course does not have available spots
            complete(false);
        }
    }
}
