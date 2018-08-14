package bgu.spl181.net.impl.UserService;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;

public abstract class USTBaseProtocol<N extends USTBProtocolShardData> implements BidiMessagingProtocol<String> {
    protected Boolean shouldTerminate = false;
    protected Connections<String> connections;
    protected N shardData;
    protected int connectionId;

    public USTBaseProtocol(N shardData){
        this.shardData = shardData;
    }

    /**
     * initiate the protocol with the active connections structure of the server and saves the
     * owner client’s connection id.
     * @param connectionId owner client’s connection id
     * @param connections active connections
     */
    public void start(int connectionId, Connections<String> connections){
        this.connections = connections;
        this.connectionId = connectionId;
    }

    /**
     * processes a given message.
     * responses are sent via the connections object send/broadcast function.
     * if message fit requirements send ACK if not send ERROR
     * @param message the received message
     */
    public void process(String message){
        JsonUserHandler userHandler = shardData.getUsersHandler();
        String [] wordArray = message.toString().split(" ");
        if (wordArray.length > 0){
            switch(wordArray[0]){
                case "REGISTER":
                    synchronized (shardData.getUsers()) {
                        if (wordArray.length > 2) {
                            String username = wordArray[1];
                            String password = wordArray[2];
                            String dataBlock = "";
                            for (int i = 3; i < wordArray.length; i++) {
                                dataBlock = dataBlock + wordArray[i] + " ";
                            }
                            if (dataBlock.length() > 1) {
                                dataBlock = dataBlock.substring(0, dataBlock.length() - 2);
                            }
                            if (!userHandler.isUserExists(username) && !shardData.isClientLoggedIn(connectionId)) {
                                processRegister(username, password, dataBlock);
                                return;
                            }
                        }
                        connections.send(connectionId, "ERROR registration failed");
                    }
                    break;
                case "LOGIN":
                    synchronized (shardData.getUsers()) {
                        synchronized (shardData.getLoggedUsersMap()) {
                            if (wordArray.length == 3) {
                                String username = wordArray[1];
                                String password = wordArray[2];
                                if (!shardData.isClientLoggedIn(connectionId) &&
                                        !shardData.isUserLoggedIn(username) &&
                                        userHandler.isUserExists(username) &&
                                        userHandler.checkIfPasswordIsCorrect(username, password)) {
                                    shardData.addLoggedInUser(connectionId, username);
                                    connections.send(connectionId, "ACK login succeeded");
                                    return;
                                }
                            }
                            connections.send(connectionId, "ERROR login failed");
                        }
                    }
                    break;
                case "SIGNOUT":
                    synchronized (connections) {
                        synchronized (shardData.getLoggedInUsers()) {
                            if (wordArray.length == 1 && shardData.isClientLoggedIn(connectionId)) {
                                connections.send(connectionId, "ACK signout succeeded");
                                shardData.removeLoggedInClient(connectionId);
                                connections.removeConnection(connectionId);
                                shouldTerminate = true;
                            } else {
                                connections.send(connectionId, "ERROR signout failed");
                            }
                        }
                    }
                    break;
                case "REQUEST":
                    processRequest(wordArray, message.toString());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){ return shouldTerminate;}

    protected abstract void processRequest(String[] wordArray, String message);

    /**
     * register user and send ACK
     * @param username - The user name.
     * @param password - the password.
     * @param dataBlock - additional information that may be used by the service.
     */
    protected void processRegister(String username, String password, String dataBlock){
        shardData.getUsersHandler().addUser(username, password, dataBlock);
        connections.send(connectionId, "ACK registration succeeded");
    }
}
