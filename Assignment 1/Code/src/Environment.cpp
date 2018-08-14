#include <iostream>
#include <algorithm>
#include <map>
#include "../include/Environment.h"
#include "../include/GlobalVariables.h"

using namespace std;

Environment::Environment()
        :fs(FileSystem()),
         commandsHistory(vector<BaseCommand*>(0)) {}

Environment::Environment(const Environment& other)
        :fs(other.fs),
         commandsHistory(other.commandsHistory){
    if(verbose % 2){
        cout << "Environment::Environment(const Environment& other)" << endl;
    }
}

Environment::Environment(Environment&& other) noexcept:
        fs(move(other.fs)),
        commandsHistory(move(other.commandsHistory)){
    if(verbose % 2){
        cout << "Environment::Environment(Environment&& other)" << endl;
    }
}

Environment& Environment::operator=(const Environment& other){
    while(!commandsHistory.empty()){
        delete commandsHistory.back();
        commandsHistory.pop_back();
    }
    fs = other.fs;
    commandsHistory = other.commandsHistory;

    if(verbose % 2){
        cout << "Environment& Environment::operator=(const Environment& other)" << endl;
    }
    return *this;
}

Environment& Environment::operator=(Environment&& other) noexcept {
    if (&other != this) {
        while(!commandsHistory.empty()){
            delete commandsHistory.back();
            commandsHistory.pop_back();
        }

        fs = move(other.fs);
        commandsHistory = move(other.commandsHistory);
    }
    if(verbose % 2){
        cout << "Environment& Environment::operator=(Environment&& other)" << endl;
    }
    return *this;
}

Environment::~Environment(){
    while(!commandsHistory.empty()){
        delete commandsHistory.back();
        commandsHistory.pop_back();
    }

    if(verbose % 2){
        cout << "Environment::~Environment()" << endl;
    }
}

void Environment::start()
{
    string command, commandName, commandArgs;
    BaseCommand *baseCommand = nullptr;

    while(true){
        cout << fs.getWorkingDirectory().getAbsolutePath() << ">";
        getline(cin, command);

        if("exit" == command){
            break;
        }
        if(count(command.begin(),command.end(),' ') > 0) {
            commandName = command.substr(0, command.find(' '));
            commandArgs = command.substr(command.find(' ') + 1);
        }
        else{
            commandName = command;
            commandArgs = "";
        }

        baseCommand = nullptr;

        if(commandName == "ls"){
            baseCommand = new LsCommand(commandArgs);
        }
        if(commandName == "cd"){
            baseCommand = new CdCommand(commandArgs);
        }
        if(commandName == "pwd"){
            baseCommand = new PwdCommand(commandArgs);
        }
        if(commandName == "mkdir"){
            baseCommand = new MkdirCommand(commandArgs);
        }
        if(commandName == "mkfile"){
            baseCommand = new MkfileCommand(commandArgs);
        }
        if(commandName == "exec"){
            baseCommand = new ExecCommand(commandArgs, commandsHistory);
        }
        if(commandName == "history"){
            baseCommand = new HistoryCommand(commandArgs, commandsHistory);
        }
        if(commandName == "rename"){
            baseCommand = new RenameCommand(commandArgs);
        }
        if(commandName == "rm"){
            baseCommand = new RmCommand(commandArgs);
        }
        if(commandName == "mv"){
            baseCommand = new MvCommand(commandArgs);
        }
        if(commandName == "cp"){
            baseCommand = new CpCommand(commandArgs);
        }
        if(commandName == "verbose"){
            baseCommand = new VerboseCommand(commandArgs);
        }

        if(nullptr == baseCommand) {
            baseCommand = new ErrorCommand(command);
        }

        if(verbose > 1){
            cout << baseCommand->toString() << endl;
        }

        baseCommand->execute(fs);

        addToHistory(baseCommand);
        command = "";
    }
}

FileSystem& Environment::getFileSystem() const{
    return const_cast<FileSystem&>(fs);
}

void Environment::addToHistory(BaseCommand *command){
    commandsHistory.push_back(command);
}

const vector<BaseCommand*>& Environment::getHistory() const{
    return commandsHistory;
}
