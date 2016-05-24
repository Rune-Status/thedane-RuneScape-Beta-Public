package dane.rs.util;

public final class ColorTools {

	public static int compositeAlpha(int src, int dst, int alpha) {
		int opacity = 256 - alpha;
		return (((((dst & 0xff00ff) * opacity) + ((src & 0xff00ff) * alpha)) & 0xff00ff00) + ((((dst & 0xff00) * opacity) + ((src & 0xff00) * alpha)) & 0xff0000)) >> 8;
	}

	private ColorTools() {

	}
}
