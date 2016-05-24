package dane.rs.data;

import dane.rs.io.*;

public class Seq {

	public static int count;
	public static Seq[] instances;

	public int frameCount;
	public int[] primaryFrames;
	public int[] secondaryFrames;
	public int[] frameDelay;
	public int delta = -1;
	public int[] labelGroups;
	public int renderPadding;
	public int priority = 5;
	public int shieldOverride = -1;
	public int weaponOverride = -1;
	public int replays = 99;

	public static void load(Archive a) {
		Buffer b = new Buffer(a.get("seq.dat"));
		count = b.getUShort();

		if (instances == null) {
			instances = new Seq[count];
		}

		for (int n = 0; n < count; n++) {
			if (instances[n] == null) {
				instances[n] = new Seq();
			}
			instances[n].read(b);
		}
	}

	public void read(Buffer b) {
		for (;;) {
			int opcode = b.getUByte();

			if (opcode == 0) {
				break;
			}

			if (opcode == 1) {
				frameCount = b.getUByte();
				primaryFrames = new int[frameCount];
				secondaryFrames = new int[frameCount];
				frameDelay = new int[frameCount];

				for (int n = 0; n < frameCount; n++) {
					primaryFrames[n] = b.getUShort();
					secondaryFrames[n] = b.getUShort();

					if (secondaryFrames[n] == 65535) {
						secondaryFrames[n] = -1;
					}

					frameDelay[n] = b.getUShort();

					if (frameDelay[n] == 0) {
						frameDelay[n] = SeqFrame.instance[primaryFrames[n]].delay;
					}

					if (frameDelay[n] == 0) {
						frameDelay[n] = 1;
					}
				}
			} else if (opcode == 2) {
				delta = b.getUShort();
			} else if (opcode == 3) {
				int n = b.getUByte();
				labelGroups = new int[n + 1];
				for (int m = 0; m < n; m++) {
					labelGroups[m] = b.getUByte();
				}
				labelGroups[n] = 9999999;
			} else if (opcode == 4) {
				renderPadding = b.getUShort();
			} else if (opcode == 5) {
				priority = b.getUByte();
			} else if (opcode == 6) {
				shieldOverride = b.getUShort();
			} else if (opcode == 7) {
				weaponOverride = b.getUShort();
			} else if (opcode == 8) {
				replays = b.getUByte();
			} else if (opcode >= 9 && opcode <= 11) {
				b.getUByte(); // newer revision opcode
			} else if (opcode == 12) {
				b.getInt(); // newer revision opcode
			} else {
				System.out.println("Error unrecognised seq config code: " + opcode);
			}
		}

		if (frameCount == 0) {
			frameCount = 1;
			primaryFrames = new int[1];
			primaryFrames[0] = -1;
			secondaryFrames = new int[1];
			secondaryFrames[0] = -1;
			frameDelay = new int[1];
			frameDelay[0] = -1;
		}
	}
}
