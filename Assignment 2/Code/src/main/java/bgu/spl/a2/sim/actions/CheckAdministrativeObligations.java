package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.ArrayList;
import java.util.List;
/**
 * This Action first check for each student if he meets some administrative obligations.
 */
public class CheckAdministrativeObligations extends Action<Boolean>{
    private Warehouse warehouse;
    private ArrayList<String> studentsList;
    private ArrayList<String> conditions ;
    private String computerId;

    /**
     * Construct and initialize the Action - Administrative check.
     * @param studentsList - a list of student that need to be checked
     * @param computerId - the computer type
     * @param conditions - a list of courses names that should be passed by the students
     * @param warehouse - a warehouse that contains all the computers
     */
    public CheckAdministrativeObligations(ArrayList<String> studentsList, String computerId, ArrayList<String> conditions, Warehouse warehouse){
        this.warehouse = warehouse;
        this.conditions= conditions;
        this.studentsList = studentsList;
        this.computerId = computerId;
        this.setActionName("Administrative Check");
    }

    /**
     * Override the start method of callback interface callback.
     * This Action first check if the department's secretary have to allocate one
     * of the computers available in the warehouse. When there is a free computer
     * the Depratment send message (with the new action that was created- CheckAdministrativeObligationsAssistant)
     * to each of the student's actor in order to check if the student meets the obligations.
     * The computer generates a signature and save it in the private state of the students,
     * which indicates on the academic status of the student.
     */
    @Override
    public void start(){
        Promise<Computer> promiseComputer =  warehouse.getComputer(computerId);
        promiseComputer.subscribe(()->{
            List<Action<Boolean>> actions = new ArrayList<>();
            Computer computer = promiseComputer.get();
            for (String student:studentsList){
                Action<Boolean> assistant = new CheckAdministrativeObligationsAssistant
                        (computer, conditions);
                actions.add(assistant);
                sendMessage(assistant, student, new StudentPrivateState());
            }
            then(actions, ()->{
                Boolean result = true;
                computer.UpComputer();//to wave on the computer so another action cloud use it
                for (Action action: actions) {
                    if(!((CheckAdministrativeObligationsAssistant)action).getResult().get()){
                        result = false;
                    }
                }
                if(result){
                    complete(true);
                }
                else{
                    complete(false);
                }
            });
        });
    }
}