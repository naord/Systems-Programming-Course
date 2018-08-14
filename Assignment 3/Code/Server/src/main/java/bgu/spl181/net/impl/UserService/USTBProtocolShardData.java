package bgu.spl181.net.impl.UserService;


import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class USTBProtocolShardData {
    private JsonUserHandler usersHandler;
    private ConcurrentHashMap<Integer, String> loggedUsersMap;

    public USTBProtocolShardData(JsonUserHandler usersHandler){
        this.usersHandler = usersHandler;
        this.loggedUsersMap = new ConcurrentHashMap<>();
    }

    /**
     * JsonUserHandler getter
     * @return JsonUserHandler
     */
    public JsonUserHandler getUsersHandler() { return usersHandler; }

    public UsersList getUsers(){
       return usersHandler.getUsers();
    }

    public ConcurrentHashMap<Integer, String> getLoggedUsersMap() {
        return loggedUsersMap;
    }

    /**
     *
     * @return collection of connection id of all logged in users
     */
    public Collection<Integer> getLoggedInUsers(){ return loggedUsersMap.keySet();}

    public Collection<String> getLoggedInName(){ return loggedUsersMap.values();}

    /**
     *
     * @param userName - the user name
     * @return true if user is logged in else false
     */
    public boolean isUserLoggedIn(String userName){
        return loggedUsersMap.containsValue(userName);
    }

    public boolean isClientLoggedIn(Integer connectionId){
        return loggedUsersMap.containsKey(connectionId);
    }

    /**
     *
     * @param connectionId - the user connection id
     * @return user name
     */
    public String getUserName(Integer connectionId){
        return loggedUsersMap.get(connectionId);
    }

    /**
     * add user to logged in users map
     * @param connectionId - the user connection id
     * @param userName - the user id
     */
    public void addLoggedInUser(Integer connectionId, String userName){
        loggedUsersMap.put(connectionId, userName);
    }

    /**
     * remove user from logged in users map
     * @param connectionId - the user id
     */
    public void removeLoggedInClient(Integer connectionId){
        loggedUsersMap.remove(connectionId);
    }

}
