PROJECT=fhvrobot

# Two additional CFLAGS must be used for Angstrom
# They must not be used for Debian or Ubuntu. I couldn't find out why. 
# The hint came from C:\gcc-linaro\share\doc\gcc-linaro-arm-linux-gnueabihf\README.txt 
#
# Uncomment the following line if you use Angstrom on your BeagleBone
#TARGET=angstrom

# Directory for C-Source
vpath %.c ./source

SUBDIRS := $(wildcard ./source)
SOURCES := $(wildcard $(addsuffix *.cpp,$(SUBDIRS)))

# Directory for includes
CINCLUDE = ./include  

# Directory for object files
OBJDIR = ./object

# Other dependencies
DEPS = \
 Makefile \
 source/dmcc_lib/dmcc.h \
 include/Robot.h \
 include/ConnectionLayer.h \
 include/mocks/TransportMock.h \
 include/Controller.h \
 include/ConnectionAPI.h \
 include/configuration/ini.h \
 include/configuration/INIReader.h \
 include/GPIO/GPIOConst.h \
 include/GPIO/GPIOManager.h \
 include/sensors/Lidar.h \
 include/sensors/MPU9150.h \
 include/sensors/FusionFilter.h \
 include/sensors/I2C.h
 

# Compiler object files 
COBJ = \
 $(OBJDIR)/dmcc_lib/dmcc.o \
 $(OBJDIR)/impl/configuration/ini.o

 
CPPOBJ = \
 $(OBJDIR)/impl/Robot.o \
 $(OBJDIR)/impl/ConnectionLayer.o \
 $(OBJDIR)/tests/TransportMock.o \
 $(OBJDIR)/impl/Controller.o \
 $(OBJDIR)/impl/ConnectionAPI.o \
 $(OBJDIR)/impl/FusionFilter.o \
 $(OBJDIR)/impl/INIReader.o \
 $(OBJDIR)/impl/Lidar.o \
 $(OBJDIR)/impl/MPU9150.o \
 $(OBJDIR)/impl/GPIOConst.o \
 $(OBJDIR)/impl/GPIOManager.o \
 $(OBJDIR)/impl/I2C.o
 
CPPSRC = \
 ./source/main.cpp \
 ./source/impl/Robot.cpp \
 ./source/impl/ConnectionLayer.cpp \
 ./source/impl/Controller.cpp \
 ./source/impl/sensors/I2C.cpp \
 ./source/impl/sensors/Lidar.cpp \
 ./source/impl/sensors/MPU9150.cpp \
 ./source/impl/sensors/FusionFilter.cpp \
 ./source/impl/GPIO/GPIOConst.cpp \
 ./source/impl/GPIO/GPIOManager.cpp \
 ./source/impl/configuration/INIReader.cpp \
 ./source/impl/ConnectionAPI.cpp

# gcc binaries to use
CC = "C:\Linaro-gcc\bin\arm-linux-gnueabihf-gcc.exe"
CXX = "C:\Linaro-gcc\bin\arm-linux-gnueabihf-g++.exe"
LD = "C:\Linaro-gcc\bin\arm-linux-gnueabihf-g++.exe"

SHELL = cmd
REMOVE = rm -f

# Compiler options
# Two additional flags neccessary for Angstrom Linux. Don't use them with Ubuntu or Debian  
CFLAGS = -marm
ifeq ($(TARGET),angstrom)
CFLAGS += -march=armv4t
CFLAGS += -mfloat-abi=soft
endif
CFLAGS += -O0 
CFLAGS += -g 
CFLAGS += -I.
CFLAGS += -I$(CINCLUDE)
CFLAGS += $(CDEFINE)
CFLAGS += -pthread

# for a better output
MSG_EMPTYLINE = ----------------------------------    
MSG_COMPILING = ---COMPILE--- 
MSG_LINKING = ---LINK--- 
MSG_SUCCESS = ---SUCCESS--- 

# Main project
all: $(PROJECT) $(CPPSRC) $(DEPS) $(SOURCES) $(CPPOBJ) ./source/impl/* ./source/tests/testsuite.cpp ./source/tests/sensor_testsuite.cpp

.PHONY: all test clean

# Linker call
$(PROJECT): $(COBJ) $(SOURCES)
	$(CXX) -c ./source/main.cpp -o $(OBJDIR)/main.o -Wall
	$(CXX) -c ./source/impl/Robot.cpp -o $(OBJDIR)/impl/Robot.o -Wall
	$(CXX) -c ./source/impl/Controller.cpp -o $(OBJDIR)/impl/Controller.o -Wall
	$(CXX) -c ./source/impl/ConnectionLayer.cpp -o $(OBJDIR)/impl/ConnectionLayer.o -Wall
	$(CXX) -c ./source/impl/ConnectionAPI.cpp -o $(OBJDIR)/impl/ConnectionAPI.o -Wall
	$(CXX) -c ./source/impl/sensors/I2C.cpp -o $(OBJDIR)/impl/I2C.o -Wall
	$(CXX) -c ./source/impl/sensors/Lidar.cpp -o $(OBJDIR)/impl/Lidar.o -Wall
	$(CXX) -c ./source/impl/sensors/MPU9150.cpp -o $(OBJDIR)/impl/MPU9150.o -Wall
	$(CXX) -c ./source/impl/sensors/FusionFilter.cpp -o $(OBJDIR)/impl/FusionFilter.o -Wall
	$(CXX) -c ./source/impl/GPIO/GPIOConst.cpp -o $(OBJDIR)/impl/GPIOConst.o -Wall
	$(CXX) -c ./source/impl/GPIO/GPIOManager.cpp -o $(OBJDIR)/impl/GPIOManager.o -Wall
	$(CXX) -c ./source/impl/configuration/INIReader.cpp -o $(OBJDIR)/impl/INIReader.o -Wall
	$(CXX) -c ./source/impl/mocks/TransportMock.cpp -o $(OBJDIR)/tests/TransportMock.o -Wall
	$(CXX) -c ./source/tests/testsuite.cpp -o $(OBJDIR)/tests/testsuite.o -Wall
	$(CXX) -c ./source/tests/sensor_testsuite.cpp -o $(OBJDIR)/tests/sensor_testsuite.o -Wall
	$(CXX) -c ./source/streaming/ClCamIp.c -o $(OBJDIR)/streaming.o -Wall
	
	@echo $(MSG_EMPTYLINE)
	@echo $(MSG_LINKING)
	$(LD) -o $@ $^ $(CPPOBJ) $(OBJDIR)/main.o $(CFLAGS)
	$(LD) -o $@_test $^ $(CPPOBJ) $(OBJDIR)/tests/testsuite.o $(CFLAGS)
	$(LD) -o $@_test_sensors $^ $(CPPOBJ) $(OBJDIR)/tests/sensor_testsuite.o $(CFLAGS)
	$(LD) -o $@_streaming $^ $(OBJDIR)/streaming.o $(CFLAGS)
	@echo $(MSG_EMPTYLINE)
	@echo $(MSG_SUCCESS) $(PROJECT)

# Compiler call
$(COBJ): $(OBJDIR)/%.o: %.c $(DEPS)
	@echo $(MSG_EMPTYLINE)
	@echo $(MSG_COMPILING) $<
	$(CC) -c -o $@ $< $(CFLAGS)

clean:
	$(REMOVE) $(OBJDIR)/*.o
	$(REMOVE) $(OBJDIR)/dmcc_lib/*.o
	$(REMOVE) $(OBJDIR)/impl/*.o
	$(REMOVE) $(OBJDIR)/impl/configuration/*.o
	$(REMOVE) $(OBJDIR)/tests/*.o
	$(REMOVE) $(PROJECT)
	
echo:
	@echo "Hey"
	