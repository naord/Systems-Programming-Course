#ifndef FILES_H_
#define FILES_H_

#include <string>
#include <vector>

using namespace std;
class BaseFile {
private:
	string name;
	
public:
	BaseFile(string name);
    virtual ~BaseFile();
	string getName() const;
	void setName(string newName);
	virtual int getSize() = 0;
};

class File : public BaseFile {
private:
	int size;
		
public:
	File(string name, int size); // Constructor
	int getSize(); // Return the size of the file
};

class Directory : public BaseFile {
private:
	Directory *parent;
	vector<BaseFile*> children;

public:
	Directory(string name, Directory *parent); // Constructor
    Directory(const Directory& other);
    Directory(Directory&& other) noexcept ;
    Directory& operator=(const Directory& other);
    Directory& operator=(Directory&& other) noexcept ;
    ~Directory();
	Directory *getParent() const; // Return a pointer to the parent of this directory
	void setParent(Directory *newParent); // Change the parent of this directory
	void addFile(BaseFile* file); // Add the file to children
	void removeFile(string name); // Remove the file with the specified name from children
	void removeFile(BaseFile* file); // Remove the file from children
	void sortByName(); // Sort children by name alphabetically (not recursively)
	void sortBySize(); // Sort children by size (not recursively)
	vector<BaseFile*> getChildren(); // Return children
	File *getFileChild(const string &name); // Return specified file child
    Directory *getDirectoryChild(const string &name); // Return specified dir child
    BaseFile *getChildRecursive(string absPath); // return child recursively
	int getSize(); // Return the size of the directory (recursively)
	string getAbsolutePath();  //Return the path from the root to this
	
};

#endif