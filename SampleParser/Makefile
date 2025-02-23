JAVA=java
JAVAC=javac
#JFLEX=jflex
#CLASSPATH=-cp /usr/share/java/cup.jar:.
#CUP=cup
JFLEX=~/Projects/jflex/bin/jflex
CLASSPATH=-cp ~/Projects/java-cup-11b.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main

all: Main.class

Main.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java Main.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: tiny.flex
	$(JFLEX) tiny.flex

parser.java: tiny.cup
	#$(CUP) -dump -expect 3 tiny.cup
	$(CUP) -expect 3 tiny.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~
