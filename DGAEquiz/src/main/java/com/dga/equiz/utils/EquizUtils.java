package com.dga.equiz.utils;

import com.dga.equiz.Main;
import com.dga.equiz.model.event.IEvent;
import com.dga.equiz.model.Profile;
import com.dga.equiz.model.nodeObject.NodeObject;
import com.dga.equiz.model.word.Word;
import com.dga.equiz.utils.ApplicationEnum.AnchorType;
import com.dga.game.ClientHelperRequest;
import com.dga.game.ClientListener;
import com.dga.game.EquizPacket.Client.ConnectClientRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class EquizUtils {
    /**
     * Instantiates a JavaFX NodeObject by loading an FXML file from the specified path.
     *
     * @param path the path to the FXML file to be loaded.
     * @return a NodeObject containing the loaded JavaFX Node and its associated FXMLLoader.
     * @throws IOException if an I/O error occurs while loading the FXML file.
     */
    public static NodeObject Instantiate(String path) throws IOException {
        // Create an FXMLLoader for loading the FXML file from the specified path
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(path));

        // Load the FXML file and get the JavaFX Node
        Node node = fxmlLoader.<Node>load();

        // Create and return a NodeObject containing the Node and FXMLLoader
        return new NodeObject(node, fxmlLoader);
    }

    public static NodeObject Instantiate(String path, Pane parent) throws IOException {
        NodeObject nodeObject = Instantiate(path);
        parent.getChildren().add(nodeObject.getNode());
        return nodeObject;
    }

    public static NodeObject Instantiate(String path, Pane parent, AnchorType anchorType) throws IOException {
        NodeObject nodeObject = Instantiate(path);
        Node node = nodeObject.getNode();

        switch (anchorType) {
            case FitToParent:
                fitNodeToParent(node);
                break;
            case Center:
                centerNodeToParent(node);
                break;
            default:
                break;
        }

        parent.getChildren().add(node);

        return nodeObject;
    }

    private static void fitNodeToParent(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
    }

    private static void centerNodeToParent(Node node) {
        StackPane.setAlignment(node, Pos.CENTER);
    }

    /**
     * Makes an HTTP API call to the specified URL using the GET method and returns the response.
     *
     * @param apiCall The URL to make the API call to.
     * @return A Response object representing the HTTP response from the API.
     * @throws IOException If an I/O error occurs while making the API call or processing the response.
     */
    public static Response APICall(String apiCall) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiCall)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code: " + response.code());
        }

        return response;
    }

    /**
     * Fetches information about a target word from an online dictionary API.
     * <p>
     * This method sends a GET request to an online dictionary API using the provided
     * target word and retrieves information about the word. The response from the API
     * is deserialized into a list of Word objects.
     *
     * @param targetWord The word for which information is to be fetched from the API.
     * @return A List of Word objects representing the information about the target word.
     * @throws IOException If an I/O error occurs during the HTTP request or response handling.
     */
    public static List<Word> FetchWordFromDictionary(String targetWord) throws IOException {
        // Construct the API URL for fetching word information
        String apiCall = "https://api.dictionaryapi.dev/api/v2/entries/en/" + targetWord;

        // Create an ObjectMapper to deserialize JSON responses
        ObjectMapper mapper = new ObjectMapper();

        // Make an API call to retrieve word information
        Response response = APICall(apiCall);

        // Deserialize the JSON response into a List of Word objects
        List<Word> wordList = mapper.readValue(response.body().byteStream(), new TypeReference<List<Word>>() {
        });

        return wordList;
    }


    /**
     * Fetches suggested words or related words for a given input word using an online
     * dictionary API.
     *
     * @param word The input word for which to fetch suggested or related words.
     * @return A List of Word objects containing suggested words or related words for
     * the input word.
     * @throws IOException If there is an issue with the network communication or data reading.
     */
    public static List<Word> FetchSuggestWordFromDictionary(String word) throws IOException {
        // Construct the API URL for fetching word information
        String apiCall = "https://api.datamuse.com/sug?s=" + word;

        // Create an ObjectMapper to deserialize JSON responses
        ObjectMapper mapper = new ObjectMapper();

        // Make an API call to retrieve word information
        Response response = APICall(apiCall);

        // Deserialize the JSON response into a List of Word objects
        List<Word> wordList = mapper.readValue(response.body().byteStream(), new TypeReference<List<Word>>() {
        });

        return wordList;
    }

    public static void setStyle(Node node, String... styles) {
        node.getStyleClass().clear();
        for (String style : styles) {
            node.getStyleClass().add(style);
        }
    }

    // Print the Alert to the screen when you receive an ERROR!
    public static void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Username Alert");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showAlert(String title, String headerText, String message, AlertType alertType){
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void callFuncDelay(IEvent func, long milliseconds) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace(); // Log or handle the exception
                Thread.currentThread().interrupt(); // Restore the interrupted status
                return; // Exit the thread
            }

            Platform.runLater(func::handle);
        });

        thread.start();
    }
    // Convert binary String to Image by using ID.
    public static Image toImage(int id) throws SQLException {
        String sqlQuery = "SELECT * FROM `information` WHERE id = '" + id + "';";
        ResultSet resultSet = DBHelper.executeQuery(sqlQuery);
        byte[] imageData = new byte[0];
        if (resultSet.next()) {
            imageData =  resultSet.getBytes("link_ava_test");
        }
        return new Image(new ByteArrayInputStream(imageData));
    }

    // Convert binary String to Image by using username.
    public static Image toImage(String username) throws SQLException {
        String sqlQuery = "SELECT * FROM `information` WHERE username = '" + username + "';";
        ResultSet resultSet = DBHelper.executeQuery(sqlQuery);
        byte[] imageData = new byte[0];
        if (resultSet.next()) {
            imageData =  resultSet.getBytes("link_ava_test");
        }
        return new Image(new ByteArrayInputStream(imageData));
    }

    // Connect socket
    public static boolean connectServer() {
        boolean success;
        try {
            Socket socket = new Socket(SecretKey.HOST, SecretKey.GAMEPORT);
            ApplicationData.getInstance().socket = socket;
            Profile profile = ApplicationData.getInstance().profile;
            int id = profile.getID();
            String username = profile.getUsername();
            String name = profile.getName();

            ConnectClientRequest request = new ConnectClientRequest(id, username, name);
            ClientHelperRequest.sendRequest(request);
            new ClientListener(socket).start();
            System.out.println("Success connect to equiz server at port 54321...");
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success =  false;
        }
        return success;
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
