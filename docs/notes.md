this doc will be used to keep the notes in order.
preferred format: date, followed by jot notes of stuffs

24/02/2025
 - started work on compilers
 - will try to get scanning done
 - reading over the doc/specifications/sample given





- input/output should be added to hashmap, but throw errror if encoutered ("you weren't supposed to call that")
- fix and replace as much as possible
- if return void, but returns different; is ok, but report as error



 - Current Tasks
    - symbol table
      - is stack of hashmaps
      - post order traversal
        - new Class SemanticAnalyzer in _.java
        - set up recursive function to go into the scopes
          - anywhere where variable declaration is called (var_dec)
            - function args
            - compound statement
          - anywhere where variable is called
            - expression
          - those 2 must be different, implement different stuffs
            - declare variable; vs 
              - create/modify hashmap
            - refer to variable
              - mostly for error checking
        - still using the same "collection of java objects returned from parser"
      - hasmmap will store data
        - SymbolTable class
        - stores Varibale class
          - type
            - is enum, probalby
              - defined as {type: int} {is array: bool}
          - name
          - value: Object
          - scope: integer
        - stack of hasmaps <String, Sym>

      - consider error checking 
        - check if not defined at all
        - redefinition within same scope


    - TODO: update readme (not just update with new content, but also expand on. refer to C1 evaluation)
      - mention the follwoing
        - "{save .abs to file} if the program runs to completion"
          - we don't do that, we save whatever we scavenged; but don't prceed to next step. some livbertyy taken
    - TODO: add test cases for type checking AS YOU UPDATE THE VISITOR
      - add testcase: if lexing/parsing failed, .sym isn't generated
    - TODO: go throgu the files, and rid of all/most TODOs
    - TODO: go through marking rubrik and checkoff that we did all that needed
    - TODO: change expList for params and set proper row/column
    - TODO: fix compund statement nesting. add print statements and stuff



asked the following
  - implementation of hashmap stask for variables; yay/nay, or should switch to list?
    - yep
  - can AssignExp implicitly convert from Int to Bool?
    - yep
  - error if:
    - funDec returns void, but has non-void return statement
      - treat all warnings as errors, including this
  - verify that all branches of the function have a return?
    - wont be tested for
  - assignment of arrays
    - absolutely not