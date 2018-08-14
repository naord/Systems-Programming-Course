package bgu.spl181.net.srv;

import bgu.spl181.net.srv.bidi.ConnectionHandler;
import bgu.spl181.net.api.bidi.Connections;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This method implements the implement Connections.
 * @param <T>
 */
public class ConnectionsImplement<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectionsHashMap = new ConcurrentHashMap<>(9);

    /**
     * This method sends a message T to client represented by the given connection Id.
     * if client is not connected the massage is not send
     * @param connectionId - Client's connection id.
     * @param msg - Message that need to be sent.
     * @return  true if massage was sent or false if not
     */
    @Override
    public boolean send(int connectionId, T msg){
        if(connectionsHashMap.containsKey(connectionId)){
            ConnectionHandler connectionHandler = connectionsHashMap.get(connectionId);
            connectionHandler.send(msg);
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * sends a message T to all active clients.
     * This includes clients that has not yet completed log-in by the User service text
     * based protocol.
     * Remember, Connections<T> belongs to the server pattern
     * implementation, not the protocol.
     * @param msg - - Message that need to be sent.
     */
    @Override
    public void broadcast(T msg){
        if(!connectionsHashMap.isEmpty()){
            connectionsHashMap.forEach((connectionId,connectionHandler)->{
                connectionHandler.send(msg);
            });
        }
    }

    /**
     * broad cast to all logged users by sending message to all logged in users
     * @param msg - message to send
     */
    public void broadcastToLoggedInUsers (Collection<Integer> loggedUsers, T msg){
        if(!loggedUsers.isEmpty()){
            for( Integer loggedInUserId : loggedUsers){
                if(connectionsHashMap.containsKey(loggedInUserId)){
                    ConnectionHandler handler = connectionsHashMap.get(loggedInUserId);
                    if(handler != null){
                        handler.send(msg);
                    }
                }
            }
        }
    }
    /**
     * This method removes active client connId from map.
     * @param connectionId - the client's id that need to be removed from connectionsHashMap.
     */
    @Override
    public void disconnect(int connectionId){
        if(connectionsHashMap.containsKey(connectionId)){
            try {
                ConnectionHandler connectionHandler = connectionsHashMap.get(connectionId);
                connectionHandler.close();
                connectionsHashMap.remove(connectionId, connectionHandler);
            }
            catch (IOException ex){}
        }
    }

    /**
     * add connection to connections HashMap
     * @param connectionId - client connection id
     * @param connectionHandler - clients connection handler
     */
    public void addConnection(int connectionId, ConnectionHandler<T> connectionHandler){
        connectionsHashMap.put(connectionId, connectionHandler);
    }

    public void removeConnection(int connectionId){
        ConnectionHandler connectionHandler = connectionsHashMap.get(connectionId);
        connectionsHashMap.remove(connectionId, connectionHandler);
    }
    /**
     * check if client is logged on
     * @param connectionId - client connection id
     * @return true if client is logged on else return false
     */
    public boolean isClientLoggedIn(Integer connectionId){
        return connectionsHashMap.containsKey(connectionId);
    }

}
