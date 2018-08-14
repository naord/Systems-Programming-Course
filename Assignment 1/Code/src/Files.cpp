//
// Created by startupx on 09/11/17.
//

#include <utility>
#include <iostream>
#include <typeinfo>
#include <algorithm>
#include "../include/Files.h"
#include "../include/GlobalVariables.h"

using namespace std;

BaseFile::BaseFile(string name):
        name(name) {}

BaseFile::~BaseFile(){}

string BaseFile::getName() const{
    return name;
}

void BaseFile::setName(string newName){
    name = move(newName);
}

File::File(string name, int size)
        :BaseFile(move(name)),
         size(size) {}

int File::getSize(){
    return size;
}

Directory::Directory(string name, Directory *parent)
        : BaseFile(move(name)),
          parent(parent),
          children(vector<BaseFile*>(0)){}

Directory::Directory(const Directory& other)
        : BaseFile(other),
          parent(other.parent),
          children(vector<BaseFile*>(0)){
    for(auto &i : other.children){
        if(typeid(*i) == typeid(Directory)){
            children.push_back(new Directory(*dynamic_cast<Directory*>(i)));
        }
        else{
            children.push_back(new File(*dynamic_cast<File*>(i)));
        }
    }

    if(verbose % 2){
        cout << "Directory::Directory(const Directory& other)" << endl;
    }
}

Directory::Directory(Directory&& other) noexcept
        : BaseFile(other),
          parent(other.parent),
          children(move(other.children)){
    other.parent = nullptr;

    if(verbose % 2){
        cout << "Directory::Directory(Directory&& other)" << endl;
    }
}

Directory& Directory::operator=(const Directory& other){
    BaseFile::setName(other.getName());
    parent = other.parent;

    for(auto &i : other.children){
        if(typeid(*i) == typeid(Directory)){
            children.push_back(new Directory(*dynamic_cast<Directory*>(i)));
        }
        else{
            children.push_back(new File(*dynamic_cast<File*>(i)));
        }
    }

    children = other.children;

    if(verbose % 2){
        cout << "Directory& Directory::operator=(const Directory& other)" << endl;
    }

    return *this;
}

Directory& Directory::operator=(Directory&& other) noexcept{
    if(&other != this){
        BaseFile::setName(other.getName());
        parent = other.parent;
        other.parent = nullptr;
        children = move(other.children);
    }

    if(verbose % 2){
        cout << "Directory& Directory::operator=(Directory&& other)" << endl;
    }

    return *this;
}

Directory::~Directory(){
    parent = nullptr;
    while(!children.empty()){
        delete children.back();
        children.pop_back();
    }

    if(verbose % 2){
        cout << "Directory::~Directory()" << endl;
    }
}

Directory *Directory::getParent() const{
    return parent;
}

void Directory::setParent(Directory *newParent){
    parent = newParent;
}

void Directory::addFile(BaseFile* file){
    children.push_back(file);
}

void Directory::removeFile(string name){
    for (unsigned i=0; i<children.size(); ++i){
        if(children[i]->getName() == name){
            children.erase(children.begin() + i);
            break;
        }
    }
}

void Directory::removeFile(BaseFile* file){
    for (unsigned i=0; i<children.size(); ++i){
        if(children[i] == file){
            children.erase(children.begin() + i);
            break;
        }
    }
}

void Directory::sortByName(){
    sort (children.begin(), children.end(),
          [](BaseFile *i, BaseFile*j){return i->getName() < j->getName();});
}

void Directory::sortBySize(){
    sort (children.begin(), children.end(),
          [](BaseFile *i, BaseFile*j){return i->getSize() < j->getSize();});
}

vector<BaseFile*> Directory::getChildren(){
    return children;
}

File *Directory::getFileChild(const string &name){
    File *nextFile = nullptr;
    unsigned long childrenNumber = children.size();

    if(name.empty()){return nextFile;}

    for(unsigned int i=0; i<childrenNumber; ++i){
        nextFile = dynamic_cast<File*>(children[i]);
        if(nextFile && children[i]->getName() == name){
            return nextFile;
        }
    }
    return nullptr;
}

Directory *Directory::getDirectoryChild(const string &name){
    Directory *nextDirectory = nullptr;
    unsigned long childrenNumber = children.size();

    if(name.empty()){return nextDirectory;}

    if(name.length() >= 2 && name.substr(0,2) == ".."){
        return parent;
    }

    for(unsigned int i=0; i<childrenNumber; ++i){
        nextDirectory = dynamic_cast<Directory*>(children[i]);
        if(nextDirectory && children[i]->getName() == name){
            return nextDirectory;
        }
    }
    return nullptr;
}

BaseFile *Directory::getChildRecursive(string absPath){
    string prefixArgs, suffixArgs = absPath;
    BaseFile* finalBaseFile;
    Directory *currentDirectory = this;
    long hierarchyCount = count(absPath.begin() + 1,absPath.end(), '/');

    if(count(absPath.begin(),absPath.end(), '/')) {

        if (absPath[0] == '/') {
            while (nullptr != currentDirectory->getParent()) {
                currentDirectory = currentDirectory->getParent();
            }
            absPath = absPath.substr(1);
            if(absPath.empty()){ return currentDirectory; }
        }

        prefixArgs = absPath.substr(0, absPath.find('/'));
        suffixArgs = absPath.substr(absPath.find('/') + 1);

        for (int i = 0; i < hierarchyCount; ++i) {
            currentDirectory = currentDirectory->getDirectoryChild(prefixArgs);
            if (nullptr == currentDirectory) { return currentDirectory; }

            if (suffixArgs.find('/') == std::string::npos) {
                break;
            }

            prefixArgs = suffixArgs.substr(0, suffixArgs.find('/'));
            suffixArgs = suffixArgs.substr(suffixArgs.find('/') + 1);
        }
    }

    finalBaseFile = currentDirectory->getFileChild(suffixArgs);
    if(nullptr == finalBaseFile){
        return currentDirectory->getDirectoryChild(suffixArgs);
    }
    else{ return finalBaseFile; }
}

int Directory::getSize(){
    int sizeSum = 0;
    for (auto &i : children) {
        sizeSum += i->getSize();
    }
    return sizeSum;
}

string Directory::getAbsolutePath() {
    string absolutePath = Directory::getName();
    Directory *temp = parent;
    if(!temp){
        return "/";
    }

    while (temp->parent != nullptr) {
        absolutePath = temp->getName() + "/" + absolutePath;
        temp = temp->parent;
    }
    return "/" + absolutePath;
}