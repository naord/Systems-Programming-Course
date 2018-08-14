//
// Created by yuval on 13/01/2018.
//

#include "../include/connectionHandler.h"
#include "../include/keyboardThread.h"

KeyboardThread::KeyboardThread(ConnectionHandler &connectionHandler): connectionHandler(connectionHandler) {
}

void KeyboardThread::operator()(){
    while (!std::cin.eof()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if (!connectionHandler.sendLine(line)) {
            break;
        }
    }
}