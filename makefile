SRCDIR = src
BINDIR = bin
DOCDIR = doc

JAVAC = javac
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR)

vpath %.java $(SRCDIR)
vpath %.class $(BINDIR)

# define general build rule for java sources
.SUFFIXES:  .java  .class

.java.class:
	$(JAVAC)  $(JFLAGS)  $<

#default rule - will be invoked by make

all: 	ClSv.class\
	Server.class\
	ClientThread.class\


# Rules for clean
clean:
	@rm -f  $(BINDIR)/*.class
