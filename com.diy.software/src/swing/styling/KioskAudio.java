package swing.styling;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;


public class KioskAudio {

    Clip clip;

    public void playBarcodeScannedSound() {

        try {

            InputStream audioSrc = getClass().getResourceAsStream("/barcodeScannedSound.wav");
            //add buffer for mark/reset support
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }


    public void usePinPadSound() {

        try {

            InputStream audioSrc = getClass().getResourceAsStream("/usePinPadSound.wav");
            //add buffer for mark/reset support
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }




}
