//
// Created by yuval on 13/01/2018.
//

#include "connectionHandler.h"

#ifndef BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H
#define BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H


class KeyboardThread{
private:
    ConnectionHandler &connectionHandler;

public:
    explicit KeyboardThread(ConnectionHandler &connectionHandler);
    void operator()();
};

#endif //BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H