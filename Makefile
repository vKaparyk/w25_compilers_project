JAVA=java
JAVAC=javac
#JFLEX=jflex
#CLASSPATH=-cp /usr/share/java/cup.jar:.
#CUP=cup
# JFLEX=~/Projects/jflex/bin/jflex
# TODO: modify CP properly s.t. it can run on school lunix server
JFLEX=jflex
CUP=$(JAVA) java_cup.Main

all: Main.class

Main.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java Main.java

%.class: %.java
	$(JAVAC) $^

Lexer.java: cm.flex
	$(JFLEX) cm.flex

parser.java: cm.cup.rules
	#$(CUP) -dump -expect 3 $^
	$(CUP) -expect 3 $^

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~
