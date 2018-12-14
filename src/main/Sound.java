package main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class Sound {
	private File mp3File;
	
	public Sound(File file) {
		this.mp3File = file;
	}

	public void playSound() {
		try {
			FileInputStream fileInputStream = new FileInputStream(mp3File);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			Player player = new Player(bufferedInputStream);
			
			new Thread() {
				@Override
				public void run() {
					try {
						player.play();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
