package rs.world.scene.loc;

import rs.data.Seq;
import rs.util.Node;

public final class SceneSeqLoc extends Node {

	public int level;
	public int classtype;
	public int tileX;
	public int tileZ;
	public int locIndex;
	public Seq seq;
	public int seqFrame;
	public int seqCycle;

	public SceneSeqLoc(Seq seq, int locIndex, int type, int tileX, int tileY, int level) {
		this.level = level;
		this.classtype = type;
		this.tileX = tileX;
		this.tileZ = tileY;
		this.locIndex = locIndex;
		this.seq = seq;
		this.seqFrame = -1;
		this.seqCycle = 0;
	}
}
