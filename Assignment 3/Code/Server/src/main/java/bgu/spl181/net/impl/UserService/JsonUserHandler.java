package bgu.spl181.net.impl.UserService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.io.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonUserHandler {

    private UsersList users;
    private ReadWriteLock locker;
    private String userFilePath;
    private Reader reader;
    private Gson gson =  new GsonBuilder().registerTypeAdapter(Integer.class,
            (JsonSerializer<Integer>)(integer, type, jsonSerializationContext) ->
                    new JsonPrimitive(integer.toString())).setPrettyPrinting().create();


    public JsonUserHandler(String userFilePath){
        this.users = new UsersList();
        this.locker = new ReentrantReadWriteLock();//ReadWriteLock is the interface while ReentrantReadWriteLock is the implementation
        this.userFilePath = userFilePath;
        try{this.reader = new FileReader(userFilePath);}
        catch (FileNotFoundException ex){}
        users = gson.fromJson(reader , users.getClass());//reads from Json
    }


    public boolean isUserExists(String username) {
        locker.readLock().lock();
        boolean ans = users.containsUser(username);
        locker.readLock().unlock();
        return ans;
    }

    /**
     * This method adds a user to usersList if it doesn't already exists.
     * @return true if the user was added successfully
     */
    public void addUser(String username, String password, String country){
        locker.writeLock().lock();
        UserTemplate userTemplate = new UserTemplate(username, password, country);
        users.addUser(userTemplate);
        updateJson();
        locker.writeLock().unlock();
    }
    public boolean HadMovie(String userName, String movieName){
        locker.readLock().lock();
        boolean ans =  getUser(userName).hasMovie(movieName);
        locker.readLock().unlock();
        return ans;
    }

    public boolean isUserAdmin(String userName){
        return getUser(userName).isUserAdmin();
    }

    public void addMovieToUser(String username, String movieId, String movieName, int price){
        locker.writeLock().lock();
        users.getUser(username).addARentedMovieToUser(movieId, movieName, price);
        updateJson();
        locker.writeLock().unlock();
    }

    public void removeMovieFromUserRentedMovieList(String username, String movieId, String movieName){
        locker.writeLock().lock();
        users.getUser(username).removeFromRentedMovies(movieId, movieName);
        updateJson();
        locker.writeLock().unlock();
    }
    public int getUserBalance(String userName){
        locker.readLock().lock();
        int ans = getUser(userName).getBalance();
        locker.readLock().unlock();
        return ans;
    }
    public String getUserCountry(String userName){
        return getUser(userName).getCountry();
    }

    public UserTemplate getUser(String username){
        locker.readLock().lock();
        UserTemplate user = users.getUser(username);
        locker.readLock().unlock();
        return user;
    }

    public boolean checkIfPasswordIsCorrect(String username, String pass){
        return users.getUser(username).getPassword().equals(pass);
    }

    public void increaseBalance(String username, int amountToAddToBalance){
        locker.writeLock().lock();
        if(users.containsUser(username)){
            users.getUser(username).addToBalance(amountToAddToBalance);
        }
       updateJson();
        locker.writeLock().unlock();
    }

    public void updateJson() {
        locker.writeLock().lock();
        try(FileWriter writer = new FileWriter(userFilePath)){
            gson.toJson(users, UsersList.class, writer);
        }
        catch (IOException ex){}
        finally{//finally makes sure that the the commands below will be execute no matter what exception is thrown.
            locker.writeLock().unlock();
        }
    }

    public UsersList getUsers(){
        return this.users;
    }
}
