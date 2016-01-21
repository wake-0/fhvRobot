#ifndef UTIL_DEBUGGER_H_
#define UTIL_DEBUGGER_H_

#include <string>
#include <iostream>

#define _DEBUG
#define CAKE_DebugLevel INFO

#define ERROR 0
#define WARNING 2
#define INFO 5
#define VERBOSE 10

class Debugger {
public:
    Debugger(int level)
#ifdef _DEBUG
    : m_output( level <= CAKE_DebugLevel )
#endif
    { }
    template<typename T>
    Debugger& operator<<(T t) {
        #ifdef _DEBUG
        if (m_output) {
            std::cout << t;
            return *this;
        } else
        #endif
        return *this;
    }
private:
#ifdef _DEBUG
    bool m_output;
#endif
};

#endif /* UTIL_DEBUGGER_H_ */
