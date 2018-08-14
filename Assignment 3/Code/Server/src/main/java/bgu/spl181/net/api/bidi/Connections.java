package bgu.spl181.net.api.bidi;

import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.util.Collection;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void broadcastToLoggedInUsers (Collection<Integer> loggedUsers, T msg);

    public void disconnect(int connectionId);

    void addConnection(int connectionId, ConnectionHandler<T> connectionHandler);

    void removeConnection(int connectionId);

    boolean isClientLoggedIn(Integer connectionId);

}
