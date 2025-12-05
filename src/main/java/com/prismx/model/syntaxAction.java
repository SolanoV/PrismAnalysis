package com.prismx.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class syntaxAction {
    private HashMap<Integer, ArrayList<String>> processedTokens;
    private HashMap<Integer, ArrayList<String>> processedLexicals;
    private HashMap<Integer, String> errors;

    public syntaxAction(HashMap<Integer, ArrayList<String>> processedTokens, HashMap<Integer, ArrayList<String>> processedLexicals) {
        this.processedTokens = processedTokens;
        this.processedLexicals = processedLexicals;
        this.errors = new HashMap<>();
    }

    public void analyzeSyntax() {
        // Clear errors before starting a new analysis
        errors.clear();

        System.out.println("\n--- STARTING SYNTAX ANALYSIS ---");

        // SYMBOL TABLE: Keeps track of declared variables (e.g., "x", "count")
        // We reset this every time we analyze the code to ensure a clean scope.
        HashSet<String> declaredVariables = new HashSet<>();

        for (Map.Entry<Integer, ArrayList<String>> entry : processedTokens.entrySet()) {
            int lineNumber = entry.getKey();
            ArrayList<String> tokens = entry.getValue();
            ArrayList<String> lexemes = processedLexicals.get(lineNumber);

            // Skip empty lines
            if (tokens.isEmpty()) continue;

            try {
                String firstToken = tokens.get(0);

                if (firstToken.equals("<data_type>")) {
                    // Case 1: Declaration (e.g., "int x;" or "int x = 5;")
                    // Pass the symbol table to REGISTER the new variable
                    checkDeclaration(tokens, lexemes, declaredVariables);
                } else if (firstToken.equals("<identifier>")) {
                    // Case 2: Assignment (e.g., "x = 5;")
                    // Pass the symbol table to CHECK if the variable exists
                    checkAssignment(tokens, lexemes, declaredVariables);
                } else if (firstToken.equals("<error>")) {
                    throw new Exception("Lexical Error detected: Invalid symbol '" + lexemes.get(0) + "'");
                } else {
                    throw new Exception("Invalid Statement Start. Expected Data Type or Identifier, found '" + lexemes.get(0) + "'");
                }

                System.out.println("Line " + lineNumber + ": Syntax Correct ✅");

            } catch (Exception e) {
                // Store the error message for the Controller to display
                String errorMsg = e.getMessage();
                errors.put(lineNumber, errorMsg);
                System.err.println("Line " + lineNumber + " Error: " + errorMsg + " ❌");
            }
        }
    }

    // RULE: <data_type> <identifier> [ = <value> ] ;
    private void checkDeclaration(ArrayList<String> tokens, ArrayList<String> lexemes, HashSet<String> declaredVariables) throws Exception {
        // Minimum tokens needed: "int x;" (size 3)
        if (tokens.size() < 3) throw new Exception("Statement incomplete. Minimum declaration is 'type variable;'");

        if (!tokens.get(1).equals("<identifier>"))
            throw new Exception("Expected variable name, found: " + lexemes.get(1));

        // --- SYMBOL TABLE LOGIC ---
        // 1. Get the variable name
        String varName = lexemes.get(1);

        // 2. Add to valid variables list (The "Scope")
        // Note: You can add a check here if (declaredVariables.contains(varName)) to block duplicates.
        declaredVariables.add(varName);
        // ---------------------------

        // Check what comes next: Semicolon (End) or Assignment (Init)?
        String thirdToken = tokens.get(2);

        if (thirdToken.equals("<delimiter>")) {
            // Case A: "int x;"
            // Valid declaration without initialization.
            return;
        }
        else if (thirdToken.equals("<assignment_operator>")) {
            // Case B: "int x = 5;"
            if (tokens.size() < 5) throw new Exception("Initialization statement incomplete.");

            // Check Value
            if (!tokens.get(3).equals("<value>")) {
                if (tokens.get(3).equals("<delimiter>")) throw new Exception("Unexpected ';'. Expected a value.");
                throw new Exception("Expected a value, found: " + lexemes.get(3));
            }
            // Check Final Semicolon
            if (!tokens.get(4).equals("<delimiter>"))
                throw new Exception("Expected ';', found: " + lexemes.get(4));
        }
        else {
            // Case C: Error (e.g., "int x 5;")
            throw new Exception("Expected ';' or '=', found: " + lexemes.get(2));
        }
    }

    // RULE: <identifier> = <value> ;
    private void checkAssignment(ArrayList<String> tokens, ArrayList<String> lexemes, HashSet<String> declaredVariables) throws Exception {
        if (tokens.size() < 4) throw new Exception("Statement incomplete.");

        // --- SYMBOL TABLE LOGIC ---
        String varName = lexemes.get(0);
        // 1. Check if we have seen this variable before
        if (!declaredVariables.contains(varName)) {
            // This catches the "x = 1;" error if "x" wasn't declared previously
            throw new Exception("Variable '" + varName + "' has not been declared!");
        }

        if (!tokens.get(1).equals("<assignment_operator>"))
            throw new Exception("Expected '=', found: " + lexemes.get(1));

        if (!tokens.get(2).equals("<value>")) {
            if (tokens.get(2).equals("<delimiter>")) throw new Exception("Unexpected ';'. Expected a value.");
            throw new Exception("Expected a value, found: " + lexemes.get(2));
        }

        if (!tokens.get(3).equals("<delimiter>"))
            throw new Exception("Expected ';', found: " + lexemes.get(3));
    }

    public HashMap<Integer, String> getErrors() {
        return errors;
    }
}