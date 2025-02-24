JAVA=java
JAVAC=javac
#JFLEX=jflex
#CLASSPATH=-cp /usr/share/java/cup.jar:.
#CUP=cup
# JFLEX=~/Projects/jflex/bin/jflex
JFLEX=jflex
CUP=$(JAVA) java_cup.Main

all: Main.class

Main.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java Main.java

%.class: %.java
	$(JAVAC) $^

Lexer.java: tiny.flex
	$(JFLEX) tiny.flex

parser.java: tiny.cup
	#$(CUP) -dump -expect 3 tiny.cup
	$(CUP) -expect 3 tiny.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~
