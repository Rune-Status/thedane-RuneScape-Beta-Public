package rs.world.scene.loc;

import rs.util.Node;

public final class SceneTemporaryLoc extends Node {

	public int level;
	public int classtype;
	public int tileX;
	public int tileZ;
	public int locIndex;
	public int rotation;
	public int type;
	public int lastCycle;

	public SceneTemporaryLoc(int locIndex, int tileX, int tileY, int level, int type, int rotation, int classtype, int lastCycle) {
		this.level = level;
		this.classtype = classtype;
		this.tileX = tileX;
		this.tileZ = tileY;
		this.locIndex = locIndex;
		this.rotation = rotation;
		this.type = type;
		this.lastCycle = lastCycle;
	}
}
