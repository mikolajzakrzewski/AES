package edu.crypto;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;


public class Controller {

    @FXML
    private TextField keyTextField;

    @FXML
    private TextArea decryptedTextArea;

    @FXML
    private TextArea encryptedTextArea;

    @FXML
    private Label chosenInputDecodedFile;

    @FXML
    private Label chosenInputEncodedFile;

    @FXML
    private Label chosenOutputDecodedFile;

    @FXML
    private Label chosenOutputEncodedFile;

    private AES aes = new AES();
    private Converter converter = new Converter();

    private File encodedInputFile;
    private File decodedInputFile;

    private File encodedOutputFile;
    private File decodedOutputFile;

    public void close() {
        Platform.exit();
    }

    public void initialize() {
        keyTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Sprawdzenie czy długość tekstu przekracza 16 znaków
                if (newValue.length() > 16) {
                    // Jeśli tak, przycięcie tekstu do 16 znaków
                    keyTextField.setText(newValue.substring(0, 32));
                }
            }
        });
    }
    public void generateButtonClick(ActionEvent actionEvent) {
        String key = generateHexKey(32);
        keyTextField.setText(key);
    }

    private String generateHexKey(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            byte[] bytes = new byte[1];
            random.nextBytes(bytes);
            sb.append(String.format("%02X", bytes[0]));
        }
        return sb.toString();
    }

    public void encryptButtonClick(ActionEvent actionEvent) {

        byte[] key = keyTextField.getText().getBytes();
        byte[] byteDecryptedMessage = decryptedTextArea.getText().getBytes();

        ArrayList<byte[][]> byteEncryptedMessage = aes.cypherText(byteDecryptedMessage, key);
        String stringEncryptedMessage = converter.byteTextToStringText(byteEncryptedMessage);
        encryptedTextArea.setText(stringEncryptedMessage);
    }

    public void decryptButtonClick(ActionEvent actionEvent) {
        byte[] key = keyTextField.getText().getBytes();
        String stringEncryptedMessage = encryptedTextArea.getText();
        ArrayList<byte[][]> byteEncryptedMessage = converter.stringTextToByteText(stringEncryptedMessage);

        byte[] byteDecryptedMessage = aes.decipherText(byteEncryptedMessage,key);
        String decryptedText = new String(byteDecryptedMessage, StandardCharsets.UTF_8);

        decryptedTextArea.setText(decryptedText);
    }

    public void encryptFileButtonClick(ActionEvent actionEvent) throws IOException {
        if(decodedInputFile == null || encodedOutputFile == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Plik nie został wybrany");
            alert.showAndWait();
            return;

        }


        byte[] key = keyTextField.getText().getBytes();
        byte[] message = Files.readAllBytes(Paths.get(decodedInputFile.toURI()));
        ArrayList<byte[][]> cypheredMessage = aes.cypherText(message, key);
        String stringEncryptedMessage = converter.byteTextToStringText(cypheredMessage);

        Files.write(encodedOutputFile.toPath(), stringEncryptedMessage.getBytes());


    }

    public void decryptFileButoonClick(ActionEvent actionEvent) throws IOException {
        if(encodedInputFile == null || decodedOutputFile == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Plik nie został wybrany");
            alert.showAndWait();
            return;

        }

        byte[] key = keyTextField.getText().getBytes();
        String file = new String(Files.readAllBytes(Paths.get(encodedInputFile.toURI())));

        ArrayList<byte[][]> byteEncryptedFile = converter.stringTextToByteText(file);

        byte[] byteDecryptedFile = aes.decipherText(byteEncryptedFile,key);
        Files.write(decodedOutputFile.toPath(), byteDecryptedFile);
    }

    public void encodedInputFileButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Wybierz zaszyfrowany plik");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = new Stage();
        encodedInputFile = fileChooser.showOpenDialog(stage);
        if(encodedInputFile != null) {
            chosenInputEncodedFile.setText(encodedInputFile.toString());
        }
    }

    public void decodedInputFileButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Wybierz jawny plik");
        Stage stage = new Stage();
        decodedInputFile = fileChooser.showOpenDialog(stage);
        if(decodedInputFile != null) {
            chosenInputDecodedFile.setText(decodedInputFile.toString());
        }
    }

    public void decodedOutputFileButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Wybierz plik do zapisu");
        Stage stage = new Stage();
        decodedOutputFile = fileChooser.showSaveDialog(stage);
        if(decodedOutputFile != null) {
            chosenOutputDecodedFile.setText(decodedOutputFile.toString());
        }
    }

    public void encodedOutputFileButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Wybierz plik do zapisu");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(".txt File",".txt");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = new Stage();
        encodedOutputFile = fileChooser.showSaveDialog(stage);
        if(encodedOutputFile != null) {
            chosenOutputEncodedFile.setText(encodedOutputFile.toString());
        }
    }
}
