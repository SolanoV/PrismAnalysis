package com.prismx.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class semanticsAction {
    private HashMap<Integer, ArrayList<String>> processedTokens;
    private HashMap<Integer, ArrayList<String>> processedLexicals;
    private HashMap<Integer, String> errors;

    // Symbol Table: Stores Variable Name -> Data Type (e.g., "count" -> "int")
    private HashMap<String, String> symbolTable;

    public semanticsAction(HashMap<Integer, ArrayList<String>> processedTokens, HashMap<Integer, ArrayList<String>> processedLexicals) {
        this.processedTokens = processedTokens;
        this.processedLexicals = processedLexicals;
        this.errors = new HashMap<>();
        this.symbolTable = new HashMap<>();
    }

    public void analyzeSemantics() {
        errors.clear();
        symbolTable.clear();

        System.out.println("\n--- SEMANTICS DEBUG ---");

        for (Map.Entry<Integer, ArrayList<String>> entry : processedTokens.entrySet()) {
            int lineNumber = entry.getKey();
            ArrayList<String> tokens = entry.getValue();
            ArrayList<String> lexemes = processedLexicals.get(lineNumber);

            if (tokens.isEmpty()) continue;

            try {
                String firstToken = tokens.get(0);

                if (firstToken.equals("<data_type>")) {
                    // Handle Declaration: int x = 5;
                    analyzeDeclaration(tokens, lexemes);
                }
                else if (firstToken.equals("<identifier>")) {
                    // Handle Assignment: x = 10;
                    analyzeAssignment(tokens, lexemes);
                }

                // If we get here without exception, the line is semantically valid
                System.out.println("Line " + lineNumber + ": Semantics Correct");

            } catch (Exception e) {
                errors.put(lineNumber, e.getMessage());
                System.err.println("Line " + lineNumber + " Error: " + e.getMessage());
            }
        }
    }

    // Logic: int x = 5;
    private void analyzeDeclaration(ArrayList<String> tokens, ArrayList<String> lexemes) throws Exception {
        String dataType = lexemes.get(0); // e.g., "int", "byte", "long"
        String varName = lexemes.get(1);  // e.g., "x"

        // 1. Check for Re-declaration
        if (symbolTable.containsKey(varName)) {
            throw new Exception("Variable '" + varName + "' is already declared.");
        }

        // 2. Register Variable
        symbolTable.put(varName, dataType);

        // 3. Check Initialization (if exists)
        if (tokens.size() > 2 && tokens.get(2).equals("<assignment_operator>")) {
            String valueLexeme = lexemes.get(3); // The actual value (e.g., "5", "100L", "3.14f")
            checkTypeCompatibility(dataType, valueLexeme);
        }
    }

    // Logic: x = 10;
    private void analyzeAssignment(ArrayList<String> tokens, ArrayList<String> lexemes) throws Exception {
        String varName = lexemes.get(0);
        String valueLexeme = lexemes.get(2); // The value being assigned

        // 1. Check if declared
        if (!symbolTable.containsKey(varName)) {
            throw new Exception("Variable '" + varName + "' is not declared.");
        }

        // 2. Retrieve the variable's expected type
        String expectedType = symbolTable.get(varName);

        // 3. Check compatibility
        checkTypeCompatibility(expectedType, valueLexeme);
    }

    // Logic: Does "100L" fit into "long"? Does "128" fit into "byte"?
    private void checkTypeCompatibility(String declaredType, String valueLexeme) throws Exception {
        String inferredType = inferType(valueLexeme);

        // 1. Exact Match (e.g., int -> int)
        if (declaredType.equals(inferredType)) return;

        // 2. Widening / Special Handling / Suffix checks

        // byte -> int literal (Check Range)
        if (declaredType.equals("byte") && inferredType.equals("int")) {
            try {
                int val = Integer.parseInt(valueLexeme);
                if (val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE) return;
                throw new Exception("Value " + val + " is out of range for byte (-128 to 127).");
            } catch (NumberFormatException e) { /* Ignore */ }
        }

        // short -> int literal (Check Range)
        if (declaredType.equals("short") && inferredType.equals("int")) {
            try {
                int val = Integer.parseInt(valueLexeme);
                if (val >= Short.MIN_VALUE && val <= Short.MAX_VALUE) return;
                throw new Exception("Value " + val + " is out of range for short (-32768 to 32767).");
            } catch (NumberFormatException e) { /* Ignore */ }
        }

        // long -> int (Widening)
        if (declaredType.equals("long") && inferredType.equals("int")) return;

        // float -> int or long (Widening)
        if (declaredType.equals("float") && (inferredType.equals("int") || inferredType.equals("long"))) return;

        // double -> int, long, or float (Widening)
        if (declaredType.equals("double") && (inferredType.equals("int") || inferredType.equals("long") || inferredType.equals("float"))) return;

        // If none of the above passed, it's a mismatch
        throw new Exception("Type Mismatch: Cannot assign " + inferredType + " (" + valueLexeme + ") to " + declaredType + " variable.");
    }

    // Helper: Guess the type based on the string format
    private String inferType(String value) {
        if (value.matches("\".*\"")) return "String";
        if (value.matches("'[^']'")) return "char";
        if (value.equals("true") || value.equals("false")) return "boolean";

        // Numeric Logic
        if (value.matches("-?\\d+[lL]")) return "long"; // Ends in L or l
        if (value.matches("[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?[fF]")) return "float"; // Ends in f or F
        if (value.matches("[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?[dD]")) return "double"; // Ends in d or D
        if (value.contains(".")) return "double"; // Default decimal is double (e.g. 3.14)
        if (value.matches("-?\\d+")) return "int"; // Standard integer (also used for byte/short candidates)

        return "unknown";
    }

    public HashMap<Integer, String> getErrors() {
        return errors;
    }
}