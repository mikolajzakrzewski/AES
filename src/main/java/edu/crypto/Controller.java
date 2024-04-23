package edu.crypto;

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

    private final AES aes = new AES();

    private final Converter converter = new Converter();

    private File encodedInputFile;

    private File decodedInputFile;

    private File encodedOutputFile;

    private File decodedOutputFile;

    public void initialize() {
        keyTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 16) {
                keyTextField.setText(newValue.substring(0, 32));
            }
        });
    }
    public void generateButtonClick() {
        String key = generateHexKey();
        keyTextField.setText(key);
    }

    private String generateHexKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            byte[] bytes = new byte[1];
            random.nextBytes(bytes);
            sb.append(String.format("%02X", bytes[0]));
        }
        return sb.toString();
    }

    public void encryptButtonClick() {
        byte[] key = keyTextField.getText().getBytes();
        byte[] byteDecryptedMessage = decryptedTextArea.getText().getBytes();
        ArrayList<byte[][]> byteEncryptedMessage = aes.cypherText(byteDecryptedMessage, key);
        String stringEncryptedMessage = converter.byteTextToStringText(byteEncryptedMessage);
        encryptedTextArea.setText(stringEncryptedMessage);
    }

    public void decryptButtonClick() {
        byte[] key = keyTextField.getText().getBytes();
        String stringEncryptedMessage = encryptedTextArea.getText();
        ArrayList<byte[][]> byteEncryptedMessage = converter.stringTextToByteText(stringEncryptedMessage);
        byte[] byteDecryptedMessage = aes.decipherText(byteEncryptedMessage,key);
        String decryptedText = new String(byteDecryptedMessage, StandardCharsets.UTF_8);
        decryptedTextArea.setText(decryptedText);
    }

    private void noFileSelectedWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("Plik nie został wybrany");
        alert.showAndWait();
    }

    public void encryptFileButtonClick() throws IOException {
        if (decodedInputFile == null || encodedOutputFile == null) {
            noFileSelectedWarning();
            return;
        }
        byte[] key = keyTextField.getText().getBytes();
        byte[] message = Files.readAllBytes(Paths.get(decodedInputFile.toURI()));
        ArrayList<byte[][]> cypheredMessage = aes.cypherText(message, key);
        String stringEncryptedMessage = converter.byteTextToStringText(cypheredMessage);
        Files.write(encodedOutputFile.toPath(), stringEncryptedMessage.getBytes());
    }

    public void decryptFileButtonClick() throws IOException {
        if (encodedInputFile == null || decodedOutputFile == null) {
            noFileSelectedWarning();
            return;
        }
        byte[] key = keyTextField.getText().getBytes();
        String file = new String(Files.readAllBytes(Paths.get(encodedInputFile.toURI())));
        ArrayList<byte[][]> byteEncryptedFile = converter.stringTextToByteText(file);
        byte[] byteDecryptedFile = aes.decipherText(byteEncryptedFile,key);
        Files.write(decodedOutputFile.toPath(), byteDecryptedFile);
    }

    public void encodedInputFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Wybierz zaszyfrowany plik");
        Stage stage = new Stage();
        encodedInputFile = fileChooser.showOpenDialog(stage);
        if(encodedInputFile != null) {
            chosenInputEncodedFile.setText(encodedInputFile.toString());
        }
    }

    public void decodedInputFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Wybierz jawny plik");
        Stage stage = new Stage();
        decodedInputFile = fileChooser.showOpenDialog(stage);
        if(decodedInputFile != null) {
            chosenInputDecodedFile.setText(decodedInputFile.toString());
        }
    }

    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Wybierz plik do zapisu");
        Stage stage = new Stage();
        return fileChooser.showSaveDialog(stage);
    }

    public void decodedOutputFileButtonClick() {
        decodedOutputFile = chooseFile();
        if(decodedOutputFile != null) {
            chosenOutputDecodedFile.setText(decodedOutputFile.toString());
        }
    }

    public void encodedOutputFileButtonClick() {
        encodedOutputFile = chooseFile();
        if(encodedOutputFile != null) {
            chosenOutputEncodedFile.setText(encodedOutputFile.toString());
        }
    }
}
