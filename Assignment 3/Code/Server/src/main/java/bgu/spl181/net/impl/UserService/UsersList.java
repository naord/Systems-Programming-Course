package bgu.spl181.net.impl.UserService;

import java.util.List;

public class UsersList{
    private List<UserTemplate> users;

    public boolean containsUser(String username) {
        for (UserTemplate user:users) {
            if(user.getUsername().equalsIgnoreCase(username)){
                return true;
            }
        }
        return false;
    }

    public UserTemplate getUser(String username) {
        for (UserTemplate user:users) {
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

    public void addUser(UserTemplate userTemplate) {
           users.add(userTemplate);
    }

}
