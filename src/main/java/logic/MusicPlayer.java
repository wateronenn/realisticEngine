package logic;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer {
    private static MediaPlayer bgmPlayer;

    public static void playMusic(GameState gameState) {
        try {

            String gameStatus = "" ;
            if(gameState == GameState.VICTORY) {
                gameStatus = "victory";
            }
            else if(gameState == GameState.BATTLE) {
                gameStatus = "battle";
            }
            else if (gameState == GameState.DEFEAT) {
                gameStatus = "defeat";
            }
            else {
                gameStatus = "menu";
            }
            String path = MusicPlayer.class.getResource("/Music/"+gameStatus+"Music.mp3").toExternalForm();
            Media media = new Media(path);


            if (bgmPlayer != null) {
                bgmPlayer.stop();
            }

            bgmPlayer = new MediaPlayer(media);


            if(gameStatus == "menu" || gameStatus == "battle"){
                bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            else{
                bgmPlayer.setCycleCount(1);
            }


            bgmPlayer.setVolume(0.05);

            bgmPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void stopMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }
    }

    public static void pauseMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }
    }

    public static void resumeMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.play();
        }
    }
}