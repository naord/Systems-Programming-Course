package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.ArrayList;
import java.util.List;

/**
 * This action adds a new student to a specified department.
 */
public class AddStudent extends Action<Boolean>{
    private String studentId;
    private String departmentId;

    /**
     * Construct and initialize the Action - AddStudent.
     * @param studentId - student's Id we want to add to the department.
     * @param departmentId - the department we want the student will we added to.
     */
    public AddStudent (String studentId, String departmentId){
        this.studentId = studentId;
        this.departmentId = departmentId;
        this.setActionName("Add Student");
    }

    /**
     * Override the start method of callback interface .
     * Creating a new action called AddStudentAssistant.
     * We send message with the new action we created to the Student's actor
     * inorder to be able to add a new student.
     * When the callback returns with positive result the student is added to the department.
     */
    @Override
    protected void start(){
        List<Action<Boolean>> actions = new ArrayList<>();
        Action<Boolean> assist = new AddStudentAssistant();
        actions.add(assist);
        sendMessage(assist, studentId, new StudentPrivateState());
        then(actions, ()->{
            if(actions.get(0).getResult().get()){
                complete( true);
                ((DepartmentPrivateState)this.actorState).addStudent(studentId);
            }
            else {
                complete(false);
            }
        });
    }
}