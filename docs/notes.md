this doc will be used to keep the notes in order.
preferred format: date, followed by jot notes of stuffs

24/02/2025
 - started work on compilers
 - will try to get scanning done
 - reading over the doc/specifications/sample given






TODO: 
 - Current Tasks
    - symbol table
      - is a hashmap w/ linked lists as values
        - set up hashmap, set up nodes
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
      
      - figure out why the variables are stored taht way
        - override same name, i gathered
        - but sometimes, size overrides j. why?
      - consider error checking 
        - check if invalid scope
        - check if not defined at all
      - TODO: finalize looking at

    - type checking
      - TODO: haven't even looked at

    - command line args 
      - "-a"
      - "-s"
