this doc will be used to keep the notes in order.
preferred format: date, followed by jot notes of stuffs

24/02/2025
 - started work on compilers
 - will try to get scanning done
 - reading over the doc/specifications/sample given






TODO: 
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
      - TODO: finalize looking at

    - TODO: add helpers analyzing Dec dtype
      - isInteger()
      - isBool()
      - isVoid()
      - ...
    
    - type checking
      - TODO: haven't even looked at

    - command line args 
      - "-a"
      - "-s"
