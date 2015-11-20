/*
 * TransportMock.cpp
 *
 *  Created on: 05.11.2015
 *      Author: Nicolaj Hoess
 */

#include "../../../include/mocks/TransportMock.h"

#include <iostream>
#include <bitset>

using namespace std;

bool TransportMock::Connect(const char* address, int port) {

	cout << "Mock Transport: Connecting to: " << address << ":" << port << endl;
	return true;
}

bool TransportMock::Send(const char* msg, unsigned int len) {

	cout << "Mock Transport: Sending message:" << endl;

	for (unsigned int i = 0; i < len; i++) {
		char curr = *(msg + i);
		bitset<8> x(curr);
		cout << x << " ";
	}
	cout << endl;

	return true;
}

bool TransportMock::TestMessageReceive(const char* testMsg, unsigned int len) {
	if (callback != NULL) {
		callback->MessageReceived(testMsg, len);
		return true;
	}
	return false;
}
