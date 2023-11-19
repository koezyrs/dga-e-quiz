package com.dga.equiz.controller;

import com.dga.equiz.utils.EquizUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class TranslateController implements Initializable {

    @FXML
    private Button buttonTranslate;

    @FXML
    private Button buttonExit;

    @FXML
    private Button buttonFullScreen;

    @FXML
    private Button buttonZoomIn;

    @FXML
    private Label labelLang1;

    @FXML
    private Label labelLang2;

    @FXML
    private TextArea taInput;

    @FXML
    private TextField taOutput;
    @FXML
    private MenuButton menuButton;
    @FXML
    private MenuItem action1;
    @FXML
    private MenuItem action2;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onClickTranslate() throws IOException {
        String inpWord = taInput.getText().trim();
        taOutput.clear();
        CompletableFuture<Void> translateToViFuture = CompletableFuture.runAsync(() -> {
            try {
                taOutput.setText(translateToVi(inpWord));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<Void> translateToEnFuture = CompletableFuture.runAsync(() -> {
            try {
                taOutput.setText(translateToEn(inpWord));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public String translateToVi(String word) throws IOException {
        return EquizUtils.translateTextToVi(word);
    }

    public String translateToEn(String word) throws IOException {
        return EquizUtils.translateTextToEn(word);
    }
}
