package com.prismx.controller;

import com.prismx.model.lexicalAction;
import com.prismx.model.semanticsAction;
import com.prismx.model.syntaxAction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private File selectedFile;

    @FXML private TextArea sourceCodeArea;
    @FXML private TextArea resultArea;
    @FXML private Button btnOpenFile;
    @FXML private Button btnLexical;
    @FXML private Button btnSyntax;
    @FXML private Button btnSemantic;
    @FXML private Button btnClear;
    @FXML private Circle statusLoaded;
    @FXML private Circle statusLexical;
    @FXML private Circle statusSyntax;
    @FXML private Circle statusSemantic;
    @FXML private AnchorPane rootPane;
    @FXML private ImageView imgThemeIcon;

    private boolean isLightMode = false;
    private boolean loadIsSuccessful = false;
    private boolean lexicalIsSuccessful = false;
    private boolean syntaxIsSuccessful = false;

    private HashMap<Integer, ArrayList<String>> processedLexical;
    private HashMap<Integer, ArrayList<String>> processedTokens;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetStatuses();
        disableButtons();
        sourceCodeArea.setEditable(false);
        resultArea.setEditable(false);
    }

    @FXML
    protected void toggleMode() {
        isLightMode = !isLightMode;

        if (isLightMode) {
            rootPane.getStyleClass().add("light-mode");
            Image moonIcon = new Image(getClass().getResourceAsStream("/com/images/darkmode_icon.png"));
            imgThemeIcon.setImage(moonIcon);

        } else {
            rootPane.getStyleClass().remove("light-mode");
            Image sunIcon = new Image(getClass().getResourceAsStream("/com/images/lightmode_icon.png"));
            imgThemeIcon.setImage(sunIcon);
        }
    }

    @FXML
    public void btnOpenFileAction(){
        Stage stage = (Stage) sourceCodeArea.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            resetStatuses();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                for(int i=1;(line=br.readLine())!=null;i++){
                    content.append(i+"   "+line).append("\n");
                }
                if(content.toString().isEmpty()||content.toString().isBlank()){
                    sourceCodeArea.setText("");
                    resultArea.appendText("UnexpectedEmptyStringError: The text file does not contain string(s).");
                }
                else {
                    sourceCodeArea.setText(content.toString());
                    resultArea.clear();
                    resetStatuses();
                    statusLoaded.setFill(Color.GREEN);
                    loadIsSuccessful = true;
                    enableButtons();
                    resultArea.appendText("File \"" + selectedFile.getName() + "\" successfully loaded.\n");
                }

            } catch (IOException e) {
                showErrorAlert("File Load Error", "Could not load file: " + e.getMessage());
                statusLoaded.setFill(Color.RED);
            }
        }
    }

    @FXML
    public void btnLexicalAction() throws IOException {
        lexicalAction la=new lexicalAction(selectedFile);
        la.lexicalAnalysis();
        if(!la.lexicalSuccessStatus()){
            statusLoaded.setFill(Color.RED);
            resultArea.appendText("\n>>> ERROR: Lexical Analysis attempt failed.\n");
        }
        else {
            lexicalIsSuccessful = true;
            processedLexical=la.getLexicalDict();
            processedTokens=la.getTokenDict();
            enableButtons();
            statusLexical.setFill(Color.GREEN);
            resultArea.appendText("\n>>> Lexical Analysis attempt successful.\n");
            resultArea.appendText(la.getContent());
        }
    }

    @FXML
    public void btnSyntaxAction(){
        resultArea.appendText("\n>>> Starting Syntax Analysis...\n");
        syntaxAction syn = new syntaxAction(processedTokens, processedLexical);
        syn.analyzeSyntax();
        HashMap<Integer, String> errors = syn.getErrors();

        if(errors.isEmpty()){
            statusSyntax.setFill(Color.GREEN);
            syntaxIsSuccessful = true;
            resultArea.appendText(">>> Syntax Analysis successful. No errors found.\n");
            enableButtons();}
        else{
            statusSyntax.setFill(Color.RED);
            syntaxIsSuccessful = false;
            resultArea.appendText(">>> Syntax Analysis Failed! Found " + errors.size() + " error(s):\n");

            // Print specific errors to the text area
            for (Map.Entry<Integer, String> entry : errors.entrySet()) {
                resultArea.appendText("Line " + entry.getKey() + ": " + entry.getValue() + "\n");
            }
            disableButtons();
        }
    }

    @FXML
    public void btnSemanticAction(){
        semanticsAction sem = new semanticsAction(processedTokens, processedLexical);
        sem.analyzeSemantics();
        HashMap<Integer, String> errors = sem.getErrors();
        boolean hasErrors = false;
        if (hasErrors) {
            statusSemantic.setFill(Color.RED);
            resultArea.appendText(">>> Semantic Analysis Failed.\n");
        } else {
            statusSemantic.setFill(Color.GREEN);
            resultArea.appendText(">>> Semantic Analysis Passed. Code is logically sound.\n");
        }

        for (Map.Entry<Integer, ArrayList<String>> entry : processedTokens.entrySet()) {
            int lineNumber = entry.getKey();
            if (entry.getValue().isEmpty()) continue;

            if (errors.containsKey(lineNumber)) {
                hasErrors = true;
                resultArea.appendText("Line " + lineNumber + " Error: " + errors.get(lineNumber) + " \n");
            } else {
                resultArea.appendText("Line " + lineNumber + ": Semantics Correct \n");
            }
        }
    }

    @FXML
    public void btnClearAction() {
        sourceCodeArea.clear();
        resultArea.clear();
        resetStatuses();
    }

    public void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void enableButtons(){
        if(loadIsSuccessful){
            btnLexical.setDisable(false);
        }
        if(lexicalIsSuccessful){
            btnSyntax.setDisable(false);
        }
        if(syntaxIsSuccessful){
            btnSemantic.setDisable(false);
        }

    }

    public void disableButtons(){
        if(!loadIsSuccessful){
            btnLexical.setDisable(true);
        }
        if(!lexicalIsSuccessful){
            btnSyntax.setDisable(true);
        }
        if(!syntaxIsSuccessful){
            btnSemantic.setDisable(true);
        }


    }
    public void resetStatuses() {
        statusLoaded.setFill(Color.GREY);
        statusLexical.setFill(Color.GREY);
        statusSyntax.setFill(Color.GREY);
        statusSemantic.setFill(Color.GREY);
        loadIsSuccessful=false;
        lexicalIsSuccessful=false;
        syntaxIsSuccessful=false;
        disableButtons();

    }




}