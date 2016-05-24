package dane.rs.world.scene.loc;

import dane.rs.data.*;
import dane.rs.util.*;

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
