JAVA=java -cp $(CLASSPATH)
JAVAC=javac -cp $(CLASSPATH)
#JFLEX=jflex
CLASSPATH=/usr/share/java/cup.jar:.
CUP=cup
# JFLEX=~/Projects/jflex/bin/jflex
JFLEX=jflex
CUP=$(JAVA) java_cup.Main

all: Main.class

Main.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Main.java CustomParser.java SemanticAnalyzer.java SymbolTable.java

%.class: %.java
	$(JAVAC) $^

Lexer.java: cm.flex
	$(JFLEX) cm.flex

parser.java: cm.cup
	#$(CUP) -dump -nonterms -expect 5 $^
	$(CUP) -expect 5 $^

clean:
	rm -rf parser.java Lexer.java sym.java *~;\
	find . -name "*.class" -type f -delete;\
	find . -name "*.sym" -type f -delete;\
	find . -name "*.abs" -type f -delete;\
