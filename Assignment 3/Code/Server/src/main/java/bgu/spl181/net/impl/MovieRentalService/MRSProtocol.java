package bgu.spl181.net.impl.MovieRentalService;

import bgu.spl181.net.impl.UserService.JsonUserHandler;
import bgu.spl181.net.impl.UserService.USTBaseProtocol;
import java.util.ArrayList;

public class MRSProtocol extends USTBaseProtocol<MRSProtocolShardData> {

    public MRSProtocol(MRSProtocolShardData shardData){
        super(shardData);
    }

    /**
     * process the register request
     * if data block does not fit service requirements return ERROR
     * if data block fit service requirements, register the user and return ACK
     * @param username - The user name.
     * @param password - the password
     * @param dataBlock - user country
     */
    @Override
    protected void processRegister(String username, String password, String dataBlock){
        JsonUserHandler userHandler = shardData.getUsersHandler();
        if(dataBlock.length() > 8 && dataBlock.substring(0,8).compareTo("country=") == 0){
            String[] splitData = dataBlock.split("\"");//[country=] [countryName]
            if(splitData.length == 2) {//only one country
                String countryName = splitData[1];
                userHandler.addUser(username, password, countryName);
                connections.send(connectionId, "ACK registration succeeded");
                return;
            }
        }
        connections.send(connectionId, "ERROR registration failed");
    }

    /**
     * process the request command
     * if command fit service requirements, execute the request and return ACK
     * if command does not fit service requirements, return ERROR
     * @param wordArray- command words in array
     * @param message- the original command
     */
    @Override
    protected void processRequest(String[] wordArray, String message){
        JsonMoviesHandler movieHandler = shardData.getMovieHandler();
        JsonUserHandler userHandler = shardData.getUsersHandler();
        String [] nameArray = message.split("\"");
        String movieName;
        String userName;
        if(wordArray.length > 1){
            switch(wordArray[1]) {
                case "info":
                    if (shardData.isClientLoggedIn(connectionId)) {
                        if (wordArray.length == 2) {//REQUEST info
                            String moviesList = movieHandler.movieListToString();
                            connections.send(connectionId, "ACK info " + moviesList);
                            return;
                        }
                        if (wordArray.length > 2) {//REQUEST info "[movie name]"
                            movieName = nameArray[1];// [REQUEST info ],[movie name]
                            if (movieHandler.isMovieExists(movieName)) {
                                String movieInfo = movieHandler.movieTemplateToString(movieName);
                                connections.send(connectionId, "ACK info " + movieInfo);
                                return;
                            }
                        }
                    }
                    connections.send(connectionId, "ERROR request info failed");
                    break;
                case "rent":
                    synchronized (shardData.getUsers()) {
                        synchronized (shardData.getMovies()) {
                            if (shardData.isClientLoggedIn(connectionId)) {
                                userName = shardData.getUserName(connectionId);
                                if (wordArray.length > 2) {//REQUEST rent "<movie name>"
                                    movieName = nameArray[1];// [REQUEST rent ],[movie name]
                                    if (movieHandler.isMovieExists(movieName) && movieHandler.getNumberOfAvailableAmount(movieName) > 0
                                            && movieHandler.getPrice(movieName) < userHandler.getUserBalance(userName) &&
                                            !userHandler.HadMovie(userName, movieName) &&
                                            !movieHandler.isBannedCountry(movieName, userHandler.getUserCountry(userName))) {
                                        movieHandler.reduceAvilbleAmountByOne(movieName);
                                        int price = movieHandler.getPrice(movieName);
                                        userHandler.addMovieToUser(userName, movieHandler.getMovieId(movieName), movieName, price);
                                        connections.send(connectionId, "ACK rent " + "\"" + movieName + "\"" + " success");
                                        int copies = movieHandler.getNumberOfAvailableAmount(movieName);
                                        connections.broadcastToLoggedInUsers(shardData.getLoggedInUsers(), "BROADCAST movie " + "\"" + movieName + "\" " + copies + " " + price);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    connections.send(connectionId, "ERROR request rent failed");
                    break;
                case "return":
                    synchronized (shardData.getUsers()) {
                        synchronized (shardData.getMovies()) {
                            if (shardData.isClientLoggedIn(connectionId)) {
                                userName = shardData.getUserName(connectionId);
                                if (wordArray.length > 2) {//REQUEST return "<movie name>"
                                    movieName = nameArray[1];// [REQUEST return ],[movie name]
                                    if (movieHandler.isMovieExists(movieName) && userHandler.HadMovie(userName, movieName)) {
                                        userHandler.removeMovieFromUserRentedMovieList(userName, movieHandler.getMovieId(movieName), movieName);
                                        movieHandler.increaseAvilbleAmountByOne(movieName);
                                        connections.send(connectionId, "ACK return " + "\"" + movieName + "\"" + " success");
                                        int price = movieHandler.getPrice(movieName);
                                        int copies = movieHandler.getNumberOfAvailableAmount(movieName);
                                        synchronized (connections) {
                                            connections.broadcastToLoggedInUsers(shardData.getLoggedInUsers(), "BROADCAST movie " + "\"" + movieName + "\" " + copies + " " + price);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    connections.send(connectionId, "ERROR request return failed");
                    break;
                case "balance":
                    if(shardData.isClientLoggedIn(connectionId)){
                        userName = shardData.getUserName(connectionId);
                        if (wordArray.length > 2) {
                            if (wordArray[2].equalsIgnoreCase("info")) {//REQUEST balance info
                                int balance = userHandler.getUserBalance(userName);
                                connections.send(connectionId, "ACK balance "+balance);
                                return;
                            }
                            synchronized (shardData.getUsers()) {
                                if (wordArray[2].equalsIgnoreCase("add") && wordArray.length > 3) {//REQUEST balance add <amount>
                                    int amount = Integer.parseInt(wordArray[3]);
                                    if (amount > 0) {
                                        userHandler.increaseBalance(userName, amount);
                                        connections.send(connectionId, "ACK balance " + userHandler.getUserBalance(userName) + " added " + amount);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    connections.send(connectionId, "ERROR request balance failed");
                    break;
                case "addmovie"://REQUEST addmovie <”movie name”> <amount> <price> [“banned country”,…]
                    if(shardData.isClientLoggedIn(connectionId)){
                        userName = shardData.getUserName(connectionId);
                        if(userHandler.isUserAdmin(userName)) {
                            if (nameArray.length > 2) {
                                movieName = nameArray[1];//[REQUEST addmovie] ,[movie name], [amount> <price>], [“banned country”],[…]
                                synchronized (shardData.getMovies()) {
                                    if (!movieHandler.isMovieExists(movieName)) {
                                        String[] amountAndPrice = nameArray[2].split(" ");
                                        if (amountAndPrice.length == 3) {
                                            int amount = Integer.parseInt(amountAndPrice[1]);
                                            int price = Integer.parseInt(amountAndPrice[2]);
                                            if (amount > 0 && price > 0) {
                                                ArrayList<String> bannedCountries = new ArrayList<String>();
                                                for (int i = 3; i < nameArray.length; i = i + 2) {
                                                    bannedCountries.add(nameArray[i]);
                                                }
                                                movieHandler.addMovie(movieName, amount, price, bannedCountries);
                                                connections.send(connectionId, "ACK addmovie \"" + movieName + "\" success");
                                                synchronized (connections) {
                                                    connections.broadcastToLoggedInUsers(shardData.getLoggedInUsers(), "BROADCAST movie \"" + movieName + "\" " + amount + " " + price);
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    connections.send(connectionId, "ERROR request addmovie failed");
                    break;
                case "remmovie"://REQUEST remmovie <”movie name”>
                    if(shardData.isClientLoggedIn(connectionId)) {
                        userName = shardData.getUserName(connectionId);
                        if(userHandler.isUserAdmin(userName)) {
                            if (nameArray.length == 2) {
                                movieName = nameArray[1];//[REQUEST remmovie ] [”movie name”]
                                synchronized (shardData.getMovies()) {
                                    synchronized (shardData.getUsers()) {
                                        if (movieHandler.isMovieExists(movieName) && movieHandler.isPossibleToRemoveMovie(movieName)) {
                                            movieHandler.removeMovie(movieName);
                                            connections.send(connectionId, "ACK remmovie \"" + movieName + "\" success");
                                            synchronized (connections) {
                                                connections.broadcastToLoggedInUsers(shardData.getLoggedInUsers(), "BROADCAST movie \"" + movieName + "\" removed");
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    connections.send(connectionId, "ERROR request remmovie failed");
                    break;
                case "changeprice"://REQUEST changeprice <”movie name”> <price>
                    if(shardData.isClientLoggedIn(connectionId)) {
                        userName = shardData.getUserName(connectionId);
                        if(userHandler.isUserAdmin(userName)) {
                            synchronized (shardData.getMovies()) {
                                if (nameArray.length == 3) {
                                    movieName = nameArray[1];//[REQUEST changeprice ] [<”movie name”>] [<price>]
                                    int price = Integer.parseInt(nameArray[2].substring(1));//remove space
                                    if (movieHandler.isMovieExists(movieName) && price > 0) {
                                        movieHandler.setPrice(movieName, price);
                                        int amount = movieHandler.getNumberOfAvailableAmount(movieName);
                                        connections.send(connectionId, "ACK changeprice \"" + movieName + "\" success");
                                        synchronized (connections) {
                                            connections.broadcastToLoggedInUsers(shardData.getLoggedInUsers(), "BROADCAST movie \"" + movieName + "\" " + amount + " " + price);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    connections.send(connectionId, "ERROR request changeprice failed");
                    break;
                default:
                   break;
            }
        }
    }

}
