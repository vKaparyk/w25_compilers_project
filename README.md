Compilers Project checkpoint 2
CIS*4650 - Compilers
By: 
    Benjamin Bliss, 1146484
    Uladzislau Kaparykha, 1096425
Date: 03/17/2025


How to Build/Run the program
	- make
	- java -cp $CLASSPATH:.:/usr/share/java/cup.jar Main <testfile.cm> <flag>

Flags:
    the flags we have implemented are 
     -a     which outputs the outputs the AST to <testfile.abs>
     -s     which outputs the outputs the AST to <testfile.abs> and the symbol table to <testfile.sym>
     -c     which outputs the outputs the AST to <testfile.abs> and the symbol table to <testfile.sym>; and will later (Checkpoint 3) compile into Assembly Code

    If no flags are specified, AST will be created and visited; but not printed nor semantically analyzed.

example:
    java -cp $CLASSPATH:.:/usr/share/java/cup.jar Main test_files/1.cm


Assumptions/Limitations
	With regards to the progress made in this checkpoint, there are many assumptions and limitations, 
    however most are not required per the assignment description, however it is worth mentioning anyways.  
    These include, called functions will not check if there is an implementation (only a prototype). We 
    include the input() and output() functions in our symbol table from the start so no error is raised 
    when they are called as long as the arguments are correct. We implemented int to bool conversion 
    similar to C.  Our program does not check all branches of a function for a return. The compiler will 
    only give errors and no warning.  Our program checks if there is a main function available as it is 
    required by the C- language.  Finally, when running the compiler, one of the following flags (-a, -s) 
    must be provided to choose what the compiler is to do, -a outputs the AST and -s outputs the AST and Symbol 
    table.  

Possible Improvements
    Possible improvements would be to resolve the previously mentioned function call limitation, adding a 
    warning if prototypes do not have implementations and raising an error if function calls lack implementations. 
    From the optimization side of it, our symbol table is less memory efficient when compared to a hashmap<string, arraylist> 
    implementation, so changing it to that would provide a more optimal symbol table.


Notes:
	The Scanner.java, Main.java, Makefile and cm.flex, cm.cup, and all files in absyn/ are all modified and updated 
    version of the started code given by the prof