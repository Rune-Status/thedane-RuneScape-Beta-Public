package dane.rs.data;

import dane.rs.io.*;
import dane.rs.util.*;
import dane.rs.world.scene.*;

public class Npc {

	public static int count;
	public static int[] pointers;
	public static Buffer data;
	public static Npc[] cache;
	public static int cachePosition;
	public static Cache models = new Cache(30);

	public int index;
	public String name;
	public byte[] description;
	public byte size = 1;
	public int[] modelIndices;
	public int[] headModelIndices;
	public int standSeq = -1;
	public int walkSeq = -1;
	public int turnAroundSeq = -1;
	public int turnRightSeq = -1;
	public int turnLeftSeq = -1;
	public boolean disposeAlpha = false;
	public int[] oldColors;
	public int[] newColors;
	public String[] options;
	public boolean showOnMinimap = true;
	public int level = -1;
	public int scaleX = 128;
	public int scaleY = 128;

	public static int getCount() {
		return count;
	}

	public static void load(Archive a) {
		data = new Buffer(a.get("npc.dat", null));
		Buffer idx = new Buffer(a.get("npc.idx", null));
		count = idx.getUShort();
		pointers = new int[count];

		int off = 2;
		for (int n = 0; n < count; n++) {
			pointers[n] = off;
			off += idx.getUShort();
		}

		cache = new Npc[20];
		for (int n = 0; n < 20; n++) {
			cache[n] = new Npc();
		}
	}

	public static void unload() {
		models = null;
		pointers = null;
		cache = null;
		data = null;
	}

	public static Npc get(int index) {
		for (int n = 0; n < 20; n++) {
			if (cache[n].index == index) {
				return cache[n];
			}
		}
		cachePosition = (cachePosition + 1) % 20;
		Npc info = cache[cachePosition] = new Npc();
		data.position = pointers[index];
		info.index = index;
		info.read(data);
		return info;
	}

	public Npc() {
		this.index = -1;
	}

	public void read(Buffer b) {
		for (; ; ) {
			int opcode = b.getUByte();

			if (opcode == 0) {
				break;
			}

			if (opcode == 1) {
				int count = b.getUByte();
				modelIndices = new int[count];
				for (int n = 0; n < count; n++) {
					modelIndices[n] = b.getUShort();
				}
			} else if (opcode == 2) {
				name = b.getString();
			} else if (opcode == 3) {
				description = b.getStringBytes();
			} else if (opcode == 12) {
				size = b.getByte();
			} else if (opcode == 13) {
				standSeq = b.getUShort();
			} else if (opcode == 14) {
				walkSeq = b.getUShort();
			} else if (opcode == 16) {
				disposeAlpha = true;
			} else if (opcode == 17) {
				walkSeq = b.getUShort();
				turnAroundSeq = b.getUShort();
				turnRightSeq = b.getUShort();
				turnLeftSeq = b.getUShort();
			} else if (opcode >= 30 && opcode < 40) {
				if (options == null) {
					options = new String[5];
				}
				options[opcode - 30] = b.getString();
			} else if (opcode == 40) {
				int count = b.getUByte();
				oldColors = new int[count];
				newColors = new int[count];
				for (int n = 0; n < count; n++) {
					oldColors[n] = b.getUShort();
					newColors[n] = b.getUShort();
				}
			} else if (opcode == 60) {
				int n = b.getUByte();
				headModelIndices = new int[n];
				for (int m = 0; m < n; m++) {
					headModelIndices[m] = b.getUShort();
				}
			} else if (opcode == 90) {
				b.getUShort();
			} else if (opcode == 91) {
				b.getUShort();
			} else if (opcode == 92) {
				b.getUShort();
			} else if (opcode == 93) {
				showOnMinimap = false;
			} else if (opcode == 95) {
				level = b.getUShort();
			} else if (opcode == 97) {
				scaleX = b.getUShort();
			} else if (opcode == 98) {
				scaleY = b.getUShort();
			}
		}
	}

	public final Model getModel(int primaryFrame, int secondaryFrame, int[] labelGroups) {
		Model m = (Model) models.get(index);

		if (m == null) {
			Model[] models = new Model[modelIndices.length];

			for (int n = 0; n < modelIndices.length; n++) {
				models[n] = new Model(modelIndices[n]);
			}

			if (models.length == 1) {
				m = models[0];
			} else {
				m = new Model(models, models.length);
			}

			if (oldColors != null) {
				for (int n = 0; n < oldColors.length; n++) {
					m.recolor(oldColors[n], newColors[n]);
				}
			}

			m.applyGroups();
			m.applyLighting(64, 850, -30, -50, -30, true);
			Npc.models.put(index, m);
		}

		m = new Model(m, !disposeAlpha);

		if (primaryFrame != -1 && secondaryFrame != -1) {
			m.applyFrames(primaryFrame, secondaryFrame, labelGroups);
		} else if (primaryFrame != -1) {
			m.applyFrame(primaryFrame);
		}

		if (scaleX != 128 || scaleY != 128) {
			m.scale(scaleX, scaleY, scaleX);
		}

		m.calculateYBoundaries();
		m.skinTriangle = null;
		m.labelVertices = null;
		return m;
	}

	public final Model getHeadModel() {
		if (headModelIndices == null) {
			return null;
		}

		Model[] models = new Model[headModelIndices.length];

		for (int n = 0; n < headModelIndices.length; n++) {
			models[n] = new Model(headModelIndices[n]);
		}

		Model m;

		if (models.length == 1) {
			m = models[0];
		} else {
			m = new Model(models, models.length);
		}

		if (oldColors != null) {
			for (int n = 0; n < oldColors.length; n++) {
				m.recolor(oldColors[n], newColors[n]);
			}
		}
		return m;
	}

}
