package rs.world.scene.tile;

import rs.util.Node;
import rs.world.scene.loc.SceneGroundDecoration;
import rs.world.scene.loc.SceneLoc;
import rs.world.scene.loc.SceneWallDecoration;
import rs.world.scene.loc.SceneWallLoc;
import rs.world.scene.renderable.SceneObj;

public final class Tile extends Node {

	public int level;
	public int x;
	public int z;
	public int renderLevel;
	public TileUnderlay underlay;
	public TileOverlay overlay;
	public SceneWallLoc wall;
	public SceneWallDecoration sceneWallDecoration;
	public SceneGroundDecoration sceneGroundDecoration;
	public SceneObj object;
	public int locationCount;
	public SceneLoc[] locs = new SceneLoc[5];
	public int[] locFlags = new int[5];
	public int flags;
	public int physicalLevel; // who knows??
	public boolean draw;
	public boolean isVisible;
	public boolean drawLocations;
	public int wallCullDirection;
	public int wallUncullDirection;
	public int wallCullOppositeDirection;
	public int wallDrawFlags;
	public Tile bridge;

	public Tile(int level, int x, int y) {
		this.renderLevel = this.level = level;
		this.x = x;
		this.z = y;
	}
}
