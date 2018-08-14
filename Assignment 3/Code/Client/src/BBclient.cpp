#include <stdlib.h>
#include <iostream>
#include "../include/connectionHandler.h"
#include "../include/keyboardThread.h"
#include "../include/socketThread.h"
#include <boost/thread.hpp>
#include <boost/chrono.hpp>
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {//argc will be the number of strings pointed to by argv.
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = "127.0.0.1";//argv[1] todo need to be changed ?
    short port = 7777; //atoi(argv[2]); -atoi - convert string to double// todo need to be changes ?
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    KeyboardThread kt(connectionHandler);
    SocketThread st(connectionHandler);
    boost::thread threadForKeyboard(kt);
    boost::thread threadForSocket(st);
    threadForSocket.join();
    return 0;
}
