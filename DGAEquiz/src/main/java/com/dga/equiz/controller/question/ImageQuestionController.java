package com.dga.equiz.controller.question;

import com.dga.equiz.model.question.ImageQuestion;
import com.dga.equiz.utils.EquizUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class ImageQuestionController implements QuestionController {
    //region FXML Reference
    @FXML
    public Label labelQuestion;

    @FXML
    public Rectangle imageFrame;

    @FXML
    public Button buttonOption1;

    @FXML
    public Button buttonOption2;

    @FXML
    public Button buttonOption3;

    @FXML
    public Button buttonOption4;
    //endregion

    private ImageQuestion imageQuestionModel;
    public Button currentButton;
    public Button buttonSubmit;

    public void setImageQuestionModel(ImageQuestion imageQuestionModel) {
        this.imageQuestionModel = imageQuestionModel;
    }

    public void setupImageQuestion(ImageQuestion imageQuestionModel) {
        this.labelQuestion.setText(imageQuestionModel.getQuestion());
        Image questionImage = new Image(imageQuestionModel.getImageSrc());
        this.imageFrame.setFill(new ImagePattern(questionImage));
        String[] options = imageQuestionModel.getOptions();
        this.buttonOption1.setText(options[1]);
        this.buttonOption2.setText(options[2]);
        this.buttonOption3.setText(options[3]);
        this.buttonOption4.setText(options[4]);
        setupButtonFunction();
    }

    private void setupButtonFunction() {
        this.buttonOption1.setOnAction((ActionEvent event) -> {
            this.imageQuestionModel.setChosenAnswer((byte) 1);
            this.buttonSubmit.setDisable(false);
            changeChosenButtonStyle(this.buttonOption1);
        });

        this.buttonOption2.setOnAction((ActionEvent event) -> {
            this.imageQuestionModel.setChosenAnswer((byte) 2);
            this.buttonSubmit.setDisable(false);
            changeChosenButtonStyle(this.buttonOption2);
        });

        this.buttonOption3.setOnAction((ActionEvent event) -> {
            this.imageQuestionModel.setChosenAnswer((byte) 3);
            this.buttonSubmit.setDisable(false);
            changeChosenButtonStyle(this.buttonOption3);
        });

        this.buttonOption4.setOnAction((ActionEvent event) -> {
            this.imageQuestionModel.setChosenAnswer((byte) 4);
            this.buttonSubmit.setDisable(false);
            changeChosenButtonStyle(this.buttonOption4);
        });
    }

    private void changeChosenButtonStyle(Button button) {
        if (this.currentButton != null) {
            EquizUtils.setStyle(this.currentButton, "button");
        }
        this.currentButton = button;
        EquizUtils.setStyle(this.currentButton, "button-correct-answer");
    }

    @Override
    public boolean isCorrect() {
        return imageQuestionModel.isCorrect();
    }

    @Override
    public void handleWrongAnswer() {
        EquizUtils.setStyle(this.currentButton, "button-wrong-answer");
    }

    @Override
    public void handleCorrectAnswer() {

    }

    @Override
    public void resetChosenAnswer() {
        this.imageQuestionModel.setChosenAnswer((byte) -1);
        EquizUtils.setStyle(this.currentButton, "button");
    }
}
