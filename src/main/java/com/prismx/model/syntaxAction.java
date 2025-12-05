package com.prismx.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class syntaxAction {
    private HashMap<Integer, ArrayList<String>> processedTokens;
    private HashMap<Integer, ArrayList<String>> processedLexicals;
    private HashMap<Integer, String> errors;
    private StringBuilder content;

    public syntaxAction(HashMap<Integer, ArrayList<String>> processedTokens, HashMap<Integer, ArrayList<String>> processedLexicals) {
        this.processedTokens = processedTokens;
        this.processedLexicals = processedLexicals;
        this.errors = new HashMap<>();
        this.content = new StringBuilder();
    }

    public void analyzeSyntax() {
        errors.clear();
        System.out.println("\n--- SYNTAX DEBUGGING ---");

        HashSet<String> declaredVariables = new HashSet<>();

        for (Map.Entry<Integer, ArrayList<String>> entry : processedTokens.entrySet()) {
            int lineNumber = entry.getKey();
            ArrayList<String> tokens = entry.getValue();
            ArrayList<String> lexemes = processedLexicals.get(lineNumber);
            try {
                String firstToken = tokens.get(0);
                if (firstToken.equals("<data_type>")) {
                    checkDeclaration(tokens, lexemes, declaredVariables);
                } else if (firstToken.equals("<identifier>")) {
                    checkAssignment(tokens, lexemes, declaredVariables);
                } else if (firstToken.equals("<error>")) {
                    throw new Exception("Lexical Error detected: Invalid symbol '" + lexemes.get(0) + "'");
                } else {
                    throw new Exception("Invalid Statement Start. Expected Data Type or Identifier, found '" + lexemes.get(0) + "'");
                }

                System.out.println("Line " + lineNumber + ": Syntax Correct");
                content.append("Line " + lineNumber + ": Syntax Correct\n");

            } catch (Exception e) {
                String errorMsg = e.getMessage();
                errors.put(lineNumber, errorMsg);
                System.err.println("Line " + lineNumber + " Error: " + errorMsg);
            }
        }
    }

    private void checkDeclaration(ArrayList<String> tokens, ArrayList<String> lexemes, HashSet<String> declaredVariables) throws Exception {
        if (tokens.size() < 3) throw new Exception("Statement incomplete. Minimum declaration is 'type variable;'");

        if (!tokens.get(1).equals("<identifier>"))
            throw new Exception("Expected variable name, found: " + lexemes.get(1));

        String varName = lexemes.get(1);
        declaredVariables.add(varName);
        String thirdToken = tokens.get(2);

        if (thirdToken.equals("<delimiter>")) {
            return;
        }
        else if (thirdToken.equals("<assignment_operator>")) {
            if (tokens.size() < 5) throw new Exception("Initialization statement incomplete.");

            if (!tokens.get(3).equals("<value>")) {
                if (tokens.get(3).equals("<delimiter>")) throw new Exception("Unexpected ';'. Expected a value.");
                throw new Exception("Expected a value, found: " + lexemes.get(3));
            }
            if (!tokens.get(4).equals("<delimiter>"))
                throw new Exception("Expected ';', found: " + lexemes.get(4));
        }
        else {
            throw new Exception("Expected ';' or '=', found: " + lexemes.get(2));
        }
    }

    private void checkAssignment(ArrayList<String> tokens, ArrayList<String> lexemes, HashSet<String> declaredVariables) throws Exception {
        if (tokens.size() < 4) throw new Exception("Statement incomplete.");

        String varName = lexemes.get(0);
        if (!declaredVariables.contains(varName)) {
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

    public String getContent() { return content.toString(); }
}