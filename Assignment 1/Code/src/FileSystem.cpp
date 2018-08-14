//
// Created by startupx on 09/11/17.
//
#include <iostream>
#include "../include/FileSystem.h"
#include "../include/GlobalVariables.h"

using namespace std;

FileSystem::FileSystem():
        rootDirectory(new Directory(string("/"), nullptr)),
    workingDirectory(new Directory(string("/"), nullptr)){}

FileSystem::FileSystem(const FileSystem& other)
        :rootDirectory(other.rootDirectory),
         workingDirectory(other.workingDirectory){
    if(verbose % 2){
        cout << "FileSystem::FileSystem(const FileSystem& other)" << endl;
    }
}

FileSystem::FileSystem(FileSystem&& other)noexcept
    :rootDirectory(other.rootDirectory),
    workingDirectory(other.workingDirectory){
    other.workingDirectory = nullptr;
    other.rootDirectory = nullptr;

    if(verbose % 2){
        cout << "FileSystem::FileSystem(FileSystem&& other)" << endl;
    }
}

FileSystem& FileSystem::operator=(const FileSystem& other){
    delete workingDirectory;
    workingDirectory = other.workingDirectory;

    delete rootDirectory;
    rootDirectory = other.rootDirectory;

    if(verbose % 2){
        cout << "FileSystem& FileSystem::operator=(const FileSystem& other)" << endl;
    }

    return *this;
}

FileSystem& FileSystem::operator=(FileSystem&& other) noexcept {
    if(&other != this) {
        delete workingDirectory;
        workingDirectory = other.workingDirectory;
        other.workingDirectory = nullptr;

        delete rootDirectory;
        rootDirectory = other.rootDirectory;
        other.rootDirectory = nullptr;
    }

    if(verbose % 2){
        cout << "FileSystem& FileSystem::operator=(FileSystem&& other)" << endl;
    }
    return *this;
}

FileSystem::~FileSystem(){
    delete rootDirectory;
    delete workingDirectory;

    if(verbose % 2){
        cout << "FileSystem::~FileSystem()" << endl;
    }
}

Directory& FileSystem::getRootDirectory() const{
    return *rootDirectory;
}

Directory& FileSystem::getWorkingDirectory() const{
    return *workingDirectory;
}

void FileSystem::setWorkingDirectory(Directory *newWorkingDirectory){
    workingDirectory = newWorkingDirectory;
}
