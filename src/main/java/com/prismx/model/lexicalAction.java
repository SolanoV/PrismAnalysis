package com.prismx.model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class lexicalAction {
    private BufferedReader br;
    private HashMap<Integer, String> lineDict;
    private HashMap<Integer, ArrayList<String>> lexemeDict;
    private HashMap<Integer, ArrayList<String>> tokenDict;
    private HashMap<Integer, ArrayList<String>> errors;
    private StringBuilder content;

    public lexicalAction(File file) throws FileNotFoundException {
        this.br = new BufferedReader(new FileReader(file));
        this.lineDict = new HashMap<>();
        this.lexemeDict = new HashMap<>();
        this.tokenDict = new HashMap<>();
        this.content = new StringBuilder();
        this.errors = new HashMap<>();
    }

    public void lexicalAnalysis() throws IOException {
        cleaner();
        for(Map.Entry<Integer, String> entry : lineDict.entrySet()) {
            int key=entry.getKey();
            ArrayList<String> lexemes=lexemeSplit(entry.getValue());
            ArrayList<String> toTokenDict=new ArrayList<>();


            for (String lexeme : lexemes) {
                if (!lexeme.isEmpty()) {
                    toTokenDict.add(tokenizer(lexeme));
                    //System.out.print(tokenizer(lexeme)+" "+lexeme);//debugging
                    //System.out.print(" " + lexeme + "\n");//for debugging
                }
            }
            tokenDict.put(key, toTokenDict);
            lexemeDict.put(key, lexemes);
            //System.out.println();//debuggin
            ArrayList<String> currentLine = tokenDict.get(key);
            if(currentLine.contains("<error>")) {
                errors.put(key, currentLine);
            }

        }

        System.out.println("--- LEXICAL DEBUG MODE ---");
        for (Map.Entry<Integer, ArrayList<String>> entry : tokenDict.entrySet()) {
            System.out.print("[" + entry.getKey() + "] ");
            content.append("[" + entry.getKey() + "] ");

            for (String token : entry.getValue()) {
                System.out.print(token + " ");
                content.append(token+" ");
            }
            System.out.println();
            content.append("\n");
        }
    }

    public String getContent() {
        return content.toString();
    }
    public HashMap<Integer, ArrayList<String>> getTokenDict() {
        return tokenDict;
    }
    public HashMap<Integer, ArrayList<String>> getLexicalDict() {
        return lexemeDict;
    }

    public String getErrors() {
        StringBuilder errorContent = new StringBuilder();
        for (Map.Entry<Integer, ArrayList<String>> entry : errors.entrySet()) {
            errorContent.append("Line "+entry.getKey()+": Lexeme Error Found\n");
        }
        return errorContent.toString();
    }

    public void cleaner() throws IOException {
        String line;
        for(int i=1;(line=br.readLine())!=null;i++){
            if(!line.isEmpty()){
                lineDict.put(i,line);
                System.out.println(lineDict.get(i));//debugging
            }
        }
    }

    public boolean lexicalSuccessStatus(){
        if(!errors.isEmpty()){
            return false;
        }
        return true;
    }

    public static String tokenizer(String lexeme){
        if(lexeme.matches("byte|short|int|long|double|float|char|String|boolean")){
            return "<data_type>";
        }
        else if(lexeme.equals("=")){
            return "<assignment_operator>";
        }
        else if(lexeme.equals(";")){
            return "<delimiter>";
        }
        else if (lexeme.matches("\".*\"|'[^']'|true|false|[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?[fFdDlL]?")) {
            return "<value>";
        }
        else if(lexeme.matches("[a-zA-Z_][a-zA-Z0-9_]*")){
            return "<identifier>";
        }
        return "<error>";
    }

    public static ArrayList<String> lexemeSplit(String input) {
        ArrayList<String> lexemes = new ArrayList<>();
        StringBuilder currentLexeme = new StringBuilder();
        boolean inString = false;
        char stringDelimiter = '"';

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (inString) {
                currentLexeme.append(c);
                if (c == stringDelimiter) {
                    inString = false;
                    lexemes.add(currentLexeme.toString().trim());
                    currentLexeme.setLength(0);
                }
            } else {
                if (c == '"' || c == '\'') {
                    if (currentLexeme.length() > 0) {
                        lexemes.add(currentLexeme.toString().trim());
                        currentLexeme.setLength(0);
                    }
                    inString = true;
                    stringDelimiter = c;
                    currentLexeme.append(c);
                } else if (c == '=' || c == ';') {
                    if (currentLexeme.length() > 0) {
                        lexemes.add(currentLexeme.toString().trim());
                        currentLexeme.setLength(0);
                    }
                    lexemes.add(String.valueOf(c));
                } else if (Character.isWhitespace(c)) {
                    if (currentLexeme.length() > 0) {
                        lexemes.add(currentLexeme.toString().trim());
                        currentLexeme.setLength(0);
                    }
                } else {
                    currentLexeme.append(c);
                }
            }
        }

        if (currentLexeme.length() > 0) {
            lexemes.add(currentLexeme.toString().trim());
        }
        return lexemes;
    }

}
