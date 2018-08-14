//
// Created by startupx on 11/11/17.
//

#include <algorithm>
#include <iostream>
#include <typeinfo>
#include "../include/Commands.h"
#include "../include/GlobalVariables.h"

using namespace std;


BaseCommand::BaseCommand(string args):
        args(args){}

string BaseCommand::getArgs(){
    return args;
}

BaseCommand::~BaseCommand(){}

CdCommand::CdCommand(string args):BaseCommand(move(args)){
}

void CdCommand::execute(FileSystem & fs){
    Directory *currentDirectory = dynamic_cast<Directory*>(
            fs.getWorkingDirectory().getChildRecursive(getArgs()));
    if(currentDirectory) {
        fs.setWorkingDirectory(currentDirectory);
    }
    else {
        cout << "The system cannot find the path specified" << endl;
    }
}

string CdCommand::toString(){
    return "cd " + getArgs();
}

CpCommand::CpCommand(string args):BaseCommand(move(args)){
}

void CpCommand::execute(FileSystem & fs){
    string arguments = getArgs();
    string sourcePath = arguments.substr(0, arguments.find(' '));
    string destinationPath = arguments.substr(arguments.find(' ') + 1);
    BaseFile *sourceBaseFile = fs.getWorkingDirectory().getChildRecursive(sourcePath);

    Directory *destDirectory = dynamic_cast<Directory*>(
            fs.getWorkingDirectory().getChildRecursive(destinationPath));

    if(!sourceBaseFile || !destDirectory){
        cout << "No such file or directory" << endl;
        return;
    }

    if(typeid(Directory) == typeid(*sourceBaseFile)){
        auto sourceDirectory = new Directory(*dynamic_cast<Directory*>(sourceBaseFile));
        sourceDirectory->setParent(destDirectory);
        destDirectory->addFile(sourceDirectory);
    }
    else{
        auto sourceFile = new File(*dynamic_cast<File*>(sourceBaseFile));
        destDirectory->addFile(sourceFile);
    }
}

string CpCommand::toString(){
    return "cp " + getArgs();
}


ErrorCommand::ErrorCommand(string args):
        BaseCommand(move(args)){
}

void ErrorCommand::execute(FileSystem & fs){
    string fullCommand = getArgs();
    if(count(fullCommand.begin(),fullCommand.end(),' ') > 0) {
        cout << fullCommand.substr(0, fullCommand.find(' '));
    }
    else{
        cout << fullCommand;
    }
    cout << ": Unknown command" << endl;
}

string ErrorCommand::toString(){
    return getArgs();
}

ExecCommand::ExecCommand(string args, const vector<BaseCommand *> & history):
        BaseCommand(move(args)),
        history(history){}

void ExecCommand::execute(FileSystem & fs){
    int commandNumber = atoi(getArgs().c_str());
    if(commandNumber >=0 && commandNumber < (int)history.size()){
        history[commandNumber]->execute(fs);
    }
    else{
        cout << "Command not found" << endl;
    }
}

string ExecCommand::toString(){
    return "exec " + getArgs();
}

HistoryCommand::HistoryCommand(string args, const vector<BaseCommand *> & history):
        BaseCommand(move(args)),
        history(history){}

void HistoryCommand::execute(FileSystem & fs){
    for(unsigned int i=0; i<history.size(); ++i){
        cout << i << "\t" << history[i]->toString() << endl;
    }
}

string HistoryCommand::toString(){
    return "history " + getArgs();
}


LsCommand::LsCommand(string args):BaseCommand(move(args)){

}

void LsCommand::execute(FileSystem & fs){
    string path, flags, arguments = getArgs();
    Directory *currentDirectory;

    if(count(arguments.begin(), arguments.end(), ' ') == 1){
        flags = arguments.substr(0, arguments.find(' '));
        path = arguments.substr(arguments.find(' ') + 1);
    }
    else{
        if(arguments == "-s"){
            flags = "-s";
            path = "";
        }
        else{
            flags = "";
            path = arguments;
        }
    }

    if(path.empty()){
        currentDirectory = &fs.getWorkingDirectory();
    }
    else{
        currentDirectory = dynamic_cast<Directory*>(
                fs.getWorkingDirectory().getChildRecursive(path));
    }

    if(!currentDirectory){
        cout << "The system cannot find the path specified" << endl;
        return;
    }

    currentDirectory->sortByName();
    if("-s" == flags){
        currentDirectory->sortBySize();
    }

    for (auto &i : currentDirectory->getChildren()) {
        if(typeid(*i) == typeid(Directory)){
            cout << "DIR\t";
        }
        else{
            cout << "FILE\t";
        }

        cout << i->getName() << "\t" << i->getSize()<<endl;
    }
}

string LsCommand::toString(){
    return "ls " + getArgs();
}



MkdirCommand::MkdirCommand(string args):BaseCommand(move(args)){
}

void MkdirCommand::execute(FileSystem & fs){
    vector<string> mkdirList = vector<string>(0);
    string dirName, dirPath = getArgs();
    Directory *newDirectory = dynamic_cast<Directory*>(
            fs.getWorkingDirectory().getChildRecursive(dirPath));

    if(fs.getWorkingDirectory().getChildRecursive(dirPath)){
        cout << "The directory already exists" << endl;
        return;
    }

    if(count(dirPath.begin(), dirPath.end(), '/') == 0){
        newDirectory = &fs.getWorkingDirectory();
        mkdirList.push_back(dirPath);
    }
    else {
        while (!newDirectory) {
            dirName = dirPath.substr(dirPath.find_last_of('/') + 1);
            dirPath = dirPath.substr(0, dirPath.find_last_of('/'));
            if (dirPath.empty()) { dirPath = "/"; }

            if(!count(dirPath.begin(), dirPath.end(), '/') &&
               !dynamic_cast<Directory *>(fs.getWorkingDirectory().getChildRecursive(dirPath))){

                mkdirList.push_back(dirName);
                mkdirList.push_back(dirPath);
                newDirectory = &fs.getWorkingDirectory();
                break;
            }

            mkdirList.push_back(dirName);

            newDirectory = dynamic_cast<Directory *>(
                    fs.getWorkingDirectory().getChildRecursive(dirPath));
        }
    }

    Directory *temp;
    while(!mkdirList.empty()){
        temp = new Directory(mkdirList.back(), newDirectory);
        mkdirList.pop_back();
        newDirectory->addFile(temp);
        newDirectory=temp;
    }
}

string MkdirCommand::toString(){
    return "mkdir " + getArgs();
}

MkfileCommand::MkfileCommand(string args):BaseCommand(move(args)){
}

void MkfileCommand::execute(FileSystem & fs){
    string fileName = getArgs().substr(0, getArgs().find(' '));
    int fileSize = atoi(getArgs().substr(getArgs().find(' ') + 1).c_str());
    Directory *sourceParentDirectory = &fs.getWorkingDirectory();

    if(fs.getWorkingDirectory().getChildRecursive(fileName)){
        cout << "File already exists" << endl;
        return;
    }

    if(count(fileName.begin(), fileName.end(), '/')){
        string sourceParentPath = fileName.substr(0, fileName.find_last_of('/'));
        sourceParentPath = sourceParentPath == fileName || sourceParentPath.empty() ? "/" : sourceParentPath;

        sourceParentDirectory = dynamic_cast<Directory*>(
                fs.getWorkingDirectory().getChildRecursive(sourceParentPath));

        if(!sourceParentDirectory){
            cout << "The system cannot find the path specified" << endl;
            return;
        }

        fileName = fileName.substr(fileName.find_last_of('/') + 1);
    }

    BaseFile *newFile = new File(fileName, fileSize);
    sourceParentDirectory->addFile(newFile);
}

string MkfileCommand::toString(){
    return "mkdir " + getArgs();
}



MvCommand::MvCommand(string args):BaseCommand(move(args)){
}

void MvCommand::execute(FileSystem & fs){
    string arguments = getArgs();
    string sourcePath = arguments.substr(0, arguments.find(' '));
    string sourceParentPath = sourcePath.substr(0, sourcePath.find_last_of('/'));
    sourceParentPath = sourceParentPath == sourcePath || sourceParentPath.empty() ? "/" : sourceParentPath;
    string destinationPath = arguments.substr(arguments.find(' ') + 1);
    BaseFile *sourceBaseFile = fs.getWorkingDirectory().getChildRecursive(sourcePath);

    Directory *sourceParentDirectory = dynamic_cast<Directory*>(
            fs.getWorkingDirectory().getChildRecursive(sourceParentPath));

    Directory *destDirectory = dynamic_cast<Directory*>(
            fs.getWorkingDirectory().getChildRecursive(destinationPath));

    if(!sourceBaseFile || !destDirectory){
        cout << "No such file or directory" << endl;
        return;
    }

    string sourceFullPath = sourceParentDirectory->getAbsolutePath() + '/' + sourceBaseFile->getName();
    string currentProportionalPath =
            fs.getWorkingDirectory().getAbsolutePath().substr(0, sourceFullPath.length());
    bool contains = currentProportionalPath == sourceFullPath;
    if(contains || &fs.getRootDirectory() == sourceBaseFile){
        cout << "Can't move directory" << endl;
        return;
    }
    destDirectory->addFile(sourceBaseFile);
    sourceParentDirectory->removeFile(sourceBaseFile);

    auto sourceDirectory = dynamic_cast<Directory*>(sourceBaseFile);
    if(sourceDirectory){
        sourceDirectory->setParent(destDirectory);
    }

}

string MvCommand::toString(){
    return "mv " + getArgs();
}

PwdCommand::PwdCommand(string args):BaseCommand(move(args)){
}

void PwdCommand::execute(FileSystem & fs){
    cout << fs.getWorkingDirectory().getAbsolutePath() <<endl;
}

string PwdCommand::toString(){
    return "pwd " + getArgs();
}


RenameCommand::RenameCommand(string args):BaseCommand(move(args)){
}

void RenameCommand::execute(FileSystem & fs){
    string oldName = getArgs().substr(0, getArgs().find(' '));
    string newName = getArgs().substr(getArgs().find(' ') + 1);
    BaseFile *baseFile = fs.getWorkingDirectory().getChildRecursive(oldName);
    if(baseFile){
        baseFile->setName(newName);
    }
    else{
        cout << "No such file or directory" << endl;
    }
}

string RenameCommand::toString(){
    return "rename " + getArgs();
}


RmCommand::RmCommand(string args):BaseCommand(move(args)){
}

void RmCommand::execute(FileSystem & fs){
    string arguments = getArgs();
    BaseFile *baseFile = fs.getWorkingDirectory().getChildRecursive(arguments);

    if(nullptr == baseFile){
        cout << "No such file or directory" << endl;
        return;
    }

    if(baseFile == &fs.getWorkingDirectory() || baseFile == &fs.getRootDirectory()){
        cout << "Can't remove directory" << endl;
        return;
    }

    if(count(arguments.begin(), arguments.end(), '/') == 0){
        fs.getWorkingDirectory().removeFile(baseFile);
    }
    else{
        string baseFolderPath =  getArgs().substr(0, getArgs().find_last_of('/'));
        dynamic_cast<Directory*>(fs.getWorkingDirectory().getChildRecursive(
                baseFolderPath))->removeFile(baseFile);
    }
}

string RmCommand::toString(){
    return "rm " + getArgs();
}

VerboseCommand::VerboseCommand(string args):BaseCommand(move(args)){
}

void VerboseCommand::execute(FileSystem & fs){
    if(getArgs().length() > 1){
        cout << "Wrong verbose input" << endl;
        return;
    }

    switch(getArgs()[0]){
        case '0':
        case '1':
        case '2':
        case '3':
            verbose = (unsigned)(getArgs()[0]-'0');
            break;
        default:
            cout << "Wrong verbose input" << endl;
    }
}

string VerboseCommand::toString(){
    return "verbose " + getArgs();
}