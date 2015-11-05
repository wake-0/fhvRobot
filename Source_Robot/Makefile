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
 include/mocks/TransportMock.h

# Compiler object files 
COBJ = \
 $(OBJDIR)/dmcc_lib/dmcc.o
 
CPPOBJ = \
 $(OBJDIR)/impl/Robot.o \
 $(OBJDIR)/impl/ConnectionLayer.o \
 $(OBJDIR)/tests/TransportMock.o
 
CPPSRC = \
 ./source/main.cpp \
 ./source/impl/Robot.cpp \
 ./source/impl/ConnectionLayer.cpp

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

# for a better output
MSG_EMPTYLINE = ----------------------------------    
MSG_COMPILING = ---COMPILE--- 
MSG_LINKING = ---LINK--- 
MSG_SUCCESS = ---SUCCESS--- 

# Main project
all: $(PROJECT) $(CPPSRC) $(DEPS)

# Linker call
$(PROJECT): $(COBJ)
	$(CXX) -c ./source/main.cpp -o $(OBJDIR)/main.o -Wall
	$(CXX) -c ./source/impl/Robot.cpp -o $(OBJDIR)/impl/Robot.o -Wall
	$(CXX) -c ./source/impl/ConnectionLayer.cpp -o $(OBJDIR)/impl/ConnectionLayer.o -Wall

	$(CXX) -c ./source/impl/mocks/TransportMock.cpp -o $(OBJDIR)/tests/TransportMock.o -Wall
	$(CXX) -c ./source/tests/testsuite.cpp -o $(OBJDIR)/tests/testsuite.o -Wall
	
	@echo $(MSG_EMPTYLINE)
	@echo $(MSG_LINKING)
	$(LD) -o $@ $^ $(CPPOBJ) $(OBJDIR)/main.o $(CFLAGS)
	$(LD) -o $@_test $^ $(CPPOBJ) $(OBJDIR)/tests/testsuite.o $(CFLAGS)
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
	$(REMOVE) $(OBJDIR)/tests/*.o
	$(REMOVE) $(PROJECT)
	
echo:
	@echo "Hey"
	