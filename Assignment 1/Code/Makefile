CC=g++
CFLAGS=-g -Wall -Weffc++ -std=c++11
all:
	rm -f bin/*
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Commands.o src/Commands.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Files.o src/Files.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/FileSystem.o src/FileSystem.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Environment.o src/Environment.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/main.o src/main.cpp

	g++ -o bin/fs bin/Commands.o bin/Files.o bin/FileSystem.o bin/Environment.o bin/main.o

