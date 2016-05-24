package dane.rs.data;

import dane.rs.io.*;

public class SeqTransform {

	public static SeqTransform[] instance;
	public int length;
	public int id;
	public int[] types;
	public int[][] groupLabels;

	public static void load(Archive a) {
		Buffer head = new Buffer(a.get("base_head.dat", null));
		Buffer type = new Buffer(a.get("base_type.dat", null));
		Buffer label = new Buffer(a.get("base_label.dat", null));

		int total = head.getUShort();

		instance = new SeqTransform[head.getUShort() + 1];

		for (int i = 0; i < total; i++) {
			int index = head.getUShort();

			int length = head.getUByte();
			int[] transformTypes = new int[length];
			int[][] groups = new int[length][];

			for (int n = 0; n < length; n++) {
				transformTypes[n] = type.getUByte();

				int groupCount = label.getUByte();
				groups[n] = new int[groupCount];

				for (int g = 0; g < groupCount; g++) {
					groups[n][g] = label.getUByte();
				}
			}

			instance[index] = new SeqTransform();
			instance[index].id = index;
			instance[index].length = length;
			instance[index].types = transformTypes;
			instance[index].groupLabels = groups;
		}
	}
}
