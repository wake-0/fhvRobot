/*
 * TransportMock.h
 *
 *  Created on: 05.11.2015
 *      Author: Nicolaj Hoess
 */

#ifndef SOURCE_IMPL_MOCKS_TRANSPORTMOCK_H_
#define SOURCE_IMPL_MOCKS_TRANSPORTMOCK_H_

#include "../ConnectionLayer.h"

using namespace FhvRobotProtocolStack;

class TransportMock : public TransportLayer {
public:
	TransportMock() : TransportLayer() { }
	virtual ~TransportMock() { }

	bool Connect(const char* address, int port);
	bool Send(const char* msg, unsigned int len);

	bool TestMessageReceive(const char* testMsg, unsigned int len);
};

#endif /* SOURCE_IMPL_MOCKS_TRANSPORTMOCK_H_ */
