JC = javac
JFLAGS =
SRCS = ${wildcard *.java}
# SRCS = Main.java
OBJS = ${SRCS:.java=.class}

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

all: $(OBJS)

# modify the zip command so it's appropriate for your project
submit:
	zip submit.zip $(SRCS) Makefile LICENSE  HONOR ChatGPT_Transcript openGrid.lay mediumGrid.lay smallGrid.lay tinyGrid.lay rnGrid.lay
clean:
	rm -f *.class

