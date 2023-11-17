package com.dga.game.GameMode;

import com.dga.game.ClientHandler;
import com.dga.game.DBHelper;
import com.dga.game.EquizPacket.Message.MessageResponse;
import com.dga.game.EquizPacket.PacketResponse;
import com.dga.game.Room;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

/**
 * Red tea is a game which player have to compete to guess the word faster.
 */
class Timer extends Thread {
    long timeMillisecond;

    public Timer(long timeMillisecond) {
        this.timeMillisecond = timeMillisecond;
    }

    public void abort() {
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeMillisecond);
        } catch (InterruptedException e) {
        }
    }
}

public class RedTea extends TeaGame {
    private final Room hostRoom;
    private volatile Timer currentRoundTimer;
    private String currentRoundWord;

    private volatile boolean isRunning = true;
    private Map<String, Integer> playerPoint;

    public RedTea(Room hostRoom) {
        this.hostRoom = hostRoom;
        this.playerPoint = hostRoom.playerPoint;
    }

    private void nextRound() {
        if (hostRoom.currentWinner != null
                && playerPoint.containsKey(hostRoom.currentWinner.username)
                && playerPoint.get(hostRoom.currentWinner.username) >= 3) {
            isRunning = false;
        }
        currentRoundTimer.abort();
    }

    @Override
    public void play() throws IOException, InterruptedException {
        while (isRunning) {
            //Send word to the client
            currentRoundWord = GameHelper.getRandomKeyword();
            MessageResponse messageResponse = new MessageResponse(PacketResponse.OK, "Server", "Guess word: " + currentRoundWord);
            hostRoom.broadcast(messageResponse, null);

            // Wait for player word
            currentRoundTimer = new Timer(10000);
            currentRoundTimer.start();
            currentRoundTimer.join();
        }
    }

    @Override
    public void checkAnswer(String word, ClientHandler client) throws IOException {
        // Check if word is valid
        if (!isValidWord(word)) {
            return;
        }

        // Add point to player.
        int currentPoint = 0;
        int highestPoint = 0;

        if (playerPoint.containsKey(client.username)) {
            currentPoint = playerPoint.get(client.username);
        }

        if (hostRoom.currentWinner != null && playerPoint.containsKey(hostRoom.currentWinner.username)) {
            highestPoint = playerPoint.get(hostRoom.currentWinner.username);
        }

        hostRoom.playerPoint.put(client.username, currentPoint + 1);

        // Check if this player is the top 1
        if (currentPoint + 1 > highestPoint) {
            hostRoom.currentWinner = client;
        }

        // Send response to client
        MessageResponse response = new MessageResponse
                (
                        PacketResponse.OK,
                        "Server",
                        "The winner of this round is " + client.username
                );
        hostRoom.broadcast(response, null);
        nextRound();
    }

    private boolean isValidWord(String word) {
        if (!currentRoundWord.toLowerCase().contains(word.toLowerCase())) {
            return false;
        }

        boolean ret = false;
        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        try {
            String sql = "SELECT word FROM av WHERE word = '" + word + "';";
            resultSet = DBHelper.executeQuerySqlite(sql);
            resultSet.next();
            ret = resultSet.getString(1) != null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                DBHelper.closeQuery(resultSet, statement, connection);
            }catch (Exception ignore){}
        }
        return ret;
    }
}
