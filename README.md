Compilers Project checkpoint 1
CIS*4650 - Compilers
By: 
    Benjamin Bliss, 1146484
    Uladzislau Kaparykha, 1096425
Date: 03/03/2025


How to Build/Run the program
	- make
	- java -cp $CLASSPATH:.:/usr/share/java/cup.jar Main <testfile.cm> 
	- optionally output can be redirected to an output file

example:
    java -cp $CLASSPATH:.:/usr/share/java/cup.jar Main test_files/1.cm

Notes:
	The Scanner.java, Main.java, Makefile and cm.flex, cm.cup, and all files in absyn/ are all modified 
    and updated version of the started code given by the prof