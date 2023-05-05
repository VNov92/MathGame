package com.vunt.mathgame;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {

  private Clip clip;

  public SoundPlayer(String soundFilePath) {
    try {
      File soundFile = new File(soundFilePath);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
    } catch (Exception e) {
      System.err.println("Error loading sound file: " + e.getMessage());
    }
  }

  public void play() {
    clip.setFramePosition(0);
    clip.start();
  }
}
