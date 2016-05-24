package dane.rs.audio;

import dane.rs.*;
import dane.rs.util.*;

import java.io.*;
import java.net.*;

public final class Audio implements Runnable {

	private MidiPlayer player = new MidiPlayer();
	private String current = "none";
	private String playing = "none";
	private String last = "none";
	private int volume = 96;

	@Override
	public void run() {
		if (player == null) {
			return;
		}

		while (true) {
			try {
				Thread.sleep(20);
			} catch (Exception ignored) {
			}

			// no music means no sound.
			if (Signlink.midi == null) {
				if (fadeOut()) {
					playing = "none";
				}
				continue;
			}

			if (!current.equals(Signlink.midi)) {
				current = Signlink.midi;
				if (current.equals("replay")) {
					current = last;
				}
			}

			last = current;

			if (!playing.equals(current)) {
				if (fadeOut()) {
					play(current);
				}
			} else {
				int volume = player.getVolume();

				if (volume < this.volume) {
					player.adjustVolume(1);
				} else if (volume > this.volume) {
					player.adjustVolume(-1);
				}
			}
		}
	}

	private void play(String song) {
		if (song == null) {
			return;
		}

		playing = song;

		try (InputStream in = new URL("http://" + Game.ADDRESS + "/" + playing).openStream()) {
			player.play(in, true);
		} catch (Exception e) {
			System.out.println("Unable to play midi: " + e.getMessage());
		}
	}

	private boolean fadeOut() {
		if (!player.isMuted()) {
			player.adjustVolume(-1);
		}
		return player.isMuted();
	}
}
