//
// Created by yuval on 13/01/2018.
//

#ifndef BOOST_ECHO_CLIENT_SOCKETTHREAD_H
#define BOOST_ECHO_CLIENT_SOCKETTHREAD_H

#include "connectionHandler.h"

class SocketThread{
private:
    ConnectionHandler &connectionHandler;

public:
    SocketThread(ConnectionHandler &connectionHandler);
    void operator()();
};
#endif //BOOST_ECHO_CLIENT_SOCKETTHREAD_H
