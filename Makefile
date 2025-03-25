JAVA=java -cp $(CLASSPATH)
JAVAC=javac -cp $(CLASSPATH)
#JFLEX=jflex
CLASSPATH=/usr/share/java/cup.jar:.
CUP=cup
# JFLEX=~/Projects/jflex/bin/jflex
JFLEX=jflex
CUP=$(JAVA) java_cup.Main

all: CM.class

CM.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java CM.java CustomParser.java SemanticAnalyzer.java SymbolTable.java CodeGenerator.java

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
