package dane.rs.io;

import dane.rs.util.*;
import dane.rs.util.LinkedList;
import net.burtleburtle.bob.rand.*;

import java.math.*;
import java.util.*;
import java.util.logging.*;

public final class Buffer extends CacheableNode {

	private static final Logger logger = Logger.getLogger(Buffer.class.getName());

	private static final int[] BITMASK;

	private static int poolSize1;
	private static int poolSize2;
	private static int poolSize3;
	private static final LinkedList pool1 = new LinkedList();
	private static final LinkedList pool2 = new LinkedList();
	private static final LinkedList pool3 = new LinkedList();

	public byte[] data;
	public int position;
	public int bitPosition;
	public IsaacRandom isaac;
	private int varStart, varSize;

	public static Buffer get(int type) {
		synchronized (pool2) {
			Buffer b = null;

			if (type == 0 && poolSize1 > 0) {
				poolSize1--;
				b = (Buffer) pool1.poll();
			} else if (type == 1 && poolSize2 > 0) {
				poolSize2--;
				b = (Buffer) pool2.poll();
			} else if (type == 2 && poolSize3 > 0) {
				poolSize3--;
				b = (Buffer) pool3.poll();
			}

			if (b != null) {
				b.position = 0;
				return b;
			}
		}

		Buffer b = new Buffer();
		b.position = 0;

		if (type == 0) {
			b.data = new byte[100];
		} else if (type == 1) {
			b.data = new byte[5000];
		} else {
			b.data = new byte[30000];
		}

		return b;
	}

	private Buffer() {
	}

	public Buffer(int size) {
		this(new byte[size]);
	}

	public Buffer(byte[] src) {
		data = src;
		position = 0;
	}

	// untested
	public void startVarSize(int opcode, int bytes) {
		putOpcode(opcode);
		position += bytes;
		varStart = position;
		varSize = bytes;
	}

	// untested
	public int endVarSize() {
		final int length = position - varStart;
		final int bytes = varSize + 1;

		for (int i = 1; i < bytes; i++) {
			data[position - length - i] = (byte) (length >> ((i - 1) * 8));
		}
		return length;
	}

	public void putOpcode(int opcode) {
		data[position++] = (byte) (opcode + isaac.nextInt());
	}

	public void putByte(int i) {
		data[position++] = (byte) i;
	}

	public void putShort(int i) {
		data[position++] = (byte) (i >> 8);
		data[position++] = (byte) i;
	}

	public void putInt(int i) {
		data[position++] = (byte) (i >> 24);
		data[position++] = (byte) (i >> 16);
		data[position++] = (byte) (i >> 8);
		data[position++] = (byte) i;
	}

	public void putLong(long l) {
		data[position++] = (byte) (int) (l >> 56);
		data[position++] = (byte) (int) (l >> 48);
		data[position++] = (byte) (int) (l >> 40);
		data[position++] = (byte) (int) (l >> 32);
		data[position++] = (byte) (int) (l >> 24);
		data[position++] = (byte) (int) (l >> 16);
		data[position++] = (byte) (int) (l >> 8);
		data[position++] = (byte) (int) l;
	}

	public void putString(String s) {
		System.arraycopy(s.getBytes(), 0, data, position, s.length());
		position += s.length();
		data[position++] = (byte) 10;
	}

	public void putBytes(byte[] src, int off, int len) {
		for (int i = off; i < off + len; i++) {
			data[position++] = src[i];
		}
	}

	public void putByteLength(int length) {
		data[position - length - 1] = (byte) length;
	}

	public int getUByte() {
		return data[position++] & 0xff;
	}

	public byte getByte() {
		return data[position++];
	}

	public int getUShort() {
		position += 2;
		return (((data[position - 2] & 0xff) << 8) + (data[position - 1] & 0xff));
	}

	public int getShort() {
		position += 2;
		int i = (((data[position - 2] & 0xff) << 8) + (data[position - 1] & 0xff));
		if (i > 0x7fff) {
			i -= 0x10000;
		}
		return i;
	}

	public int getInt24() {
		position += 3;
		return (((data[position - 3] & 0xff) << 16) + ((data[position - 2] & 0xff) << 8) + (data[position - 1] & 0xff));
	}

	public int getInt() {
		position += 4;
		return (((data[position - 4] & 0xff) << 24) + ((data[position - 3] & 0xff) << 16) + ((data[position - 2] & 0xff) << 8) + (data[position - 1] & 0xff));
	}

	public long getLong() {
		long a = (long) getInt() & 0xffffffffL;
		long b = (long) getInt() & 0xffffffffL;
		return (a << 32) + b;
	}

	public String getString() {
		int startPosition = position;
		while (data[position++] != 10) {
			/* empty */
		}
		return new String(data, startPosition, position - startPosition - 1);
	}

	public byte[] getStringBytes() {
		int startPosition = position;
		while (data[position++] != 10) {
			/* empty */
		}
		byte[] bytes = new byte[position - startPosition - 1];
		for (int i = startPosition; i < position - 1; i++) {
			bytes[i - startPosition] = data[i];
		}
		return bytes;
	}

	public void getBytes(byte[] dst, int off, int len) {
		for (int i = off; i < off + len; i++) {
			dst[i] = data[position++];
		}
	}

	public void startBitAccess() {
		bitPosition = position * 8;
	}

	public int getBits(int bits) {
		int bytePos = bitPosition >> 3;
		int msb = 8 - (bitPosition & 0x7);
		int i = 0;

		bitPosition += bits;

		for (/**/; bits > msb; msb = 8) {
			i += ((data[bytePos++] & BITMASK[msb]) << (bits - msb));
			bits -= msb;
		}

		if (bits == msb) {
			i += data[bytePos] & BITMASK[msb];
		} else {
			i += ((data[bytePos] >> (msb - bits)) & BITMASK[bits]);
		}

		return i;
	}

	public void startByteAccess() {
		position = (bitPosition + 7) / 8;
	}

	public int getSmart() {
		int i = data[position] & 0xff;
		if (i < 128) {
			return getUByte() - 64;
		}
		return getUShort() - 49152;
	}

	public int getUSmart() {
		int i = data[position] & 0xff;
		if (i < 128) {
			return getUByte();
		}
		return getUShort() - 32768;
	}

	public void encode(BigInteger exponent, BigInteger modulus) {
		byte[] encoded = new BigInteger(Arrays.copyOfRange(data, 0, position)).modPow(exponent, modulus).toByteArray();
		position = 0;
		putByte(encoded.length);
		putBytes(encoded, 0, encoded.length);
	}

	static {
		BITMASK = new int[33];
		for (int i = 0; i < 32; i++) {
			BITMASK[i] = (1 << i) - 1;
		}
		BITMASK[32] = -1;
	}
}
