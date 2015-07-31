import java.util.Random;
import java.util.Calendar;

import java.nio.ByteBuffer;

import java.text.SimpleDateFormat;

/**
 * Utilities class, holds all the needed static functions.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */
class CoreUtils {
	private final static Random rnd = new Random();
	private final static SimpleDateFormat dateFormatHM = new SimpleDateFormat("HH:mm:ss");
	private final static SimpleDateFormat dateFormatHMS = new SimpleDateFormat("HH:mm:ss");
	private final static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+";

	/**
	 * This method returns a string of random characters in the requested
	 * length.
	 * 
	 * @param length
	 *            The length of the string of random characters.
	 * @return The string of random characters.
	 */
	static String getRandomString(int length) {
		if (length < 0) {
			throw new IllegalArgumentException("The requested string length should be > 0");
		}
		StringBuilder result = new StringBuilder();
		while (length > 0) {
			result.append(chars.charAt(rnd.nextInt(chars.length())));
			length--;
		}
		return result.toString();
	}

	/**
	 * 
	 * This method returns the current time as a String.
	 *
	 * @param withSeconds
	 *            Should the method return the time with seconds
	 * @return The current time as a String (like HH:MM:SS or HH:MM).
	 * 
	 */
	static String getTime(boolean withSeconds) {
		if (withSeconds) {
			return dateFormatHMS.format(Calendar.getInstance().getTime());
		}
		return dateFormatHM.format(Calendar.getInstance().getTime());
	}

	/**
	 * This method concatenate all the bytes of any number of given byte arrays.
	 * 
	 * @param arrays
	 *            The byte arrays.
	 * @return The concatenated byte array of all the given byte arrays.
	 */
	static byte[] combineArrays(byte[]... arrays) {
		// Calculating the number of byte we have in total.
		int numOfBytes = 0;
		for (int i = 0; i < arrays.length; i++) {
			numOfBytes += arrays[i].length;
		}

		// Copy the bytes from each byte array into the result byte array.
		byte[] result = new byte[numOfBytes];
		int curPos = 0;
		for (int i = 0; i < arrays.length; i++) {
			System.arraycopy(arrays[i], 0, result, curPos, arrays[i].length);
			curPos += arrays[i].length;
		}
		return result;
	}

	/**
	 * This method converts an int number into a 4 byte array of that number.
	 * 
	 * @param x
	 *            The int we want to convert to byte array.
	 * @return The byte array of the given int.
	 */
	static byte[] intToBytes(int x) {
		return ByteBuffer.allocate(4).putInt(x).array();
	}

	/**
	 * This method converts an array of 8 bytes into a long.
	 * 
	 * @param bytes
	 *            The array of bytes we want to convert to a long.
	 * @return the long we converted from the given byte array.
	 */
	static int bytesToInt(byte[] bytes) {
		if (bytes.length != 4) {
			throw new IllegalArgumentException("The number of bytes != 4.");
		}
		return ByteBuffer.wrap(bytes).getInt();
	}

	/**
	 * This method converts a long number into a 8 byte array of that number.
	 * 
	 * @param x
	 *            The long we want to convert to byte array.
	 * @return The byte array of the given long.
	 */
	static byte[] longToBytes(long x) {
		return ByteBuffer.allocate(8).putLong(x).array();
	}

	/**
	 * This method converts an array of 8 bytes into a long.
	 * 
	 * @param bytes
	 *            The array of bytes we want to convert to a long.
	 * @return the long we converted from the given byte array.
	 */
	static long bytesToLong(byte[] bytes) {
		if (bytes.length != 8) {
			throw new IllegalArgumentException("The number of bytes != 8.");
		}
		return ByteBuffer.wrap(bytes).getLong();
	}

	/**
	 * This method checks if the given two byte arrays and return if they
	 * contain the same bytes.
	 *
	 * @param arrayA
	 *            The array we want to compare with arrayB.
	 * @param arrayB
	 *            The array we want to compare with arrayA.
	 * @return True if the given phrase is the same as ours, False otherwise.
	 */
	static boolean cmprByteArray(byte[] arrayA, byte[] arrayB) {
		// Check for different sizes first.
		if (arrayA.length != arrayB.length) {
			return false;
		}

		// Check each byte. return false if encounter a difference.
		for (int i = 0; i < arrayB.length; i++) {
			if (arrayA[i] != arrayB[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method surround any given text with the needed tags to make it bold
	 * in HTML.
	 * 
	 * @param str
	 *            The given string we want to add bold.
	 * @return The given string with the HTML styling.
	 */
	static String boldTextHTML(String str) {
		return String.format("<html><b>%s</b></html>", str);
	}

	/**
	 * This method remove any HTML tags from a give string.
	 * 
	 * @param strToClear
	 *            The string we want to clear from HTML tags.
	 * @return The cleared string.
	 */
	static String clearHtml(String strToClear) {
		return strToClear.replaceAll("<[^>]*>", "");
	}

	/**
	 * This function applies a bitwise-XOR over two byte vectors.
	 * 
	 * @param arrayA
	 *            The array of bytes to be xor'ed with <code>byteArrB</code>
	 * @param arrayB
	 *            The array of bytes to be xor'ed with <code>byteArrA</code>
	 * @return The result Xor'ed byte array
	 */
	static byte[] xor(byte[] arrayA, byte[] arrayB) {
		if (arrayA.length != arrayB.length) {
			throw new IllegalArgumentException("The length of the 2 arrays is diffrent.");
		}
		byte[] result = new byte[arrayA.length];

		for (int i = 0; i < arrayA.length; i++) {
			result[i] = (byte) (arrayA[i] ^ arrayB[i]);
		}

		return result;
	}

	/**
	 * This function get a array of bytes, an index of a bit and a value (1 or
	 * 0, the method will change the bit of the given index (as if the bytes
	 * were concatenated) to the given vale.
	 * 
	 * @param array
	 *            The byte array that needed a bit change.
	 * @param index
	 *            The index of the bit to change.
	 * @param val
	 *            The value (0 or 1) of the bit that we want to change to.
	 */
	static void setBit(byte[] array, int index, int val) {
		if (index > array.length * 8) {
			throw new IndexOutOfBoundsException("The given index is > array.length * 8");
		}
		int whichBit = index % 8;
		int whichByte = index / 8;
		byte byteToModify = array[whichByte];

		byteToModify = (byte) (((0xFF7F >> whichBit) & byteToModify) & 0x00FF);
		array[whichByte] = (byte) ((val << (8 - (whichBit + 1))) | byteToModify);
	}

	/**
	 * This function get array of bytes and an index, and returns the value of
	 * that bit (as if the bytes were concatenated).
	 * 
	 * @param array
	 *            The array of bytes.
	 * @param index
	 *            The index of the bit we want to get.
	 * @return The value of the bit in the given index.
	 */
	static int getBit(byte[] array, int index) {
		if (index > array.length * 8) {
			throw new IndexOutOfBoundsException("The given index is > array.length * 8");
		}

		int whichByte = index / 8;
		int whichBit = index % 8;

		return array[whichByte] >> (8 - (whichBit + 1)) & 0x0001;
	}

	/**
	 * This function get array of bytes, a starting index and the number of bits
	 * to copy and returns array of bytes with the request bits.
	 * 
	 * @param array
	 *            The array of bytes.
	 * @param index
	 *            The index from where to start copying the bits.
	 * @param length
	 *            how many bits to return from the starting <code>index</code>.
	 * @return The requested bits in an array of bytes.
	 */
	static byte[] getBits(byte[] array, int index, int length) {
		if (index > array.length * 8) {
			throw new IndexOutOfBoundsException("The given index is > array.length * 8");
		}
		if (length + index > array.length * 8) {
			throw new IndexOutOfBoundsException("The given index + length > array.length * 8");
		}
		int numOfBytes = (length - 1) / 8 + 1;
		byte[] result = new byte[numOfBytes];
		int val;

		for (int i = 0; i < length; i++) {
			val = getBit(array, index + i);
			setBit(result, i, val);
		}
		return result;
	}

	/**
	 * This method return an array of bytes starting at a given index and at the
	 * given length of the given array.
	 * 
	 * @param array
	 *            The array we want to copy bytes off.
	 * @param index
	 *            The starting index we want to copy from.
	 * @param length
	 *            The number of bytes we want to copy.
	 * @return The byte array containing the segment of bytes we asked for.
	 */
	static byte[] getBytes(byte[] array, int index, int length) {
		if (index + length > array.length) {
			throw new IndexOutOfBoundsException("Index + length > array.length");
		}

		byte[] result = new byte[length];
		System.arraycopy(array, index, result, 0, length);
		return result;
	}

	/**
	 * This function concatenates the bits of 2 byte arrays.
	 * 
	 * @param arrayA
	 *            The first byte array to copy from.
	 * @param lengthA
	 *            The number of bits to copy from the <code>byteArrA</code>.
	 * @param arrayB
	 *            The second byte array to copy from.
	 * @param lengthB
	 *            The number of bits to copy from the <code>byteArrA</code>.
	 * @return The result byte array of the concatenation.
	 */
	static byte[] concatenateBits(byte[] arrayA, int lengthA, byte[] arrayB, int lengthB) {
		if (lengthA > arrayA.length * 8 || lengthB > arrayB.length * 8) {
			throw new IndexOutOfBoundsException("The given length is > arrayA\\B.length * 8");
		}

		int numOfBytes = (lengthA + lengthB - 1) / 8 + 1;
		byte[] result = new byte[numOfBytes];
		int j = 0;
		int val;

		for (int i = 0; i < lengthA; i++, j++) {
			val = getBit(arrayA, i);
			setBit(result, j, val);
		}
		for (int i = 0; i < lengthB; i++, j++) {
			val = getBit(arrayB, i);
			setBit(result, j, val);
		}

		return result;
	}

	/**
	 * This function get array of bytes and split it into
	 * <code>howManyParts</code> parts.
	 * 
	 * @param byteArr
	 *            The byte array to copy from.
	 * @param howManyParts
	 *            The number of bytes split the bit to.
	 * @return The split byte array.
	 */
	static byte[] splitBytes(byte[] byteArr, int howManyParts) {
		byte[] result = new byte[howManyParts];
		int whichByte;
		int val;

		for (int i = 0; i < byteArr.length * 8; i++) {
			whichByte = i / 3;
			val = getBit(byteArr, i);
			setBit(result, 5 * (whichByte + 1) + i, val);
		}

		return result;
	}

	/**
	 * This function get array of bytes, the number of relevant bits in it, and
	 * rotate its bits to the left by the given number of times.
	 * 
	 * @param byteArr
	 *            The bytes array to rotate.
	 * @param length
	 *            The number of relevant bits in the byte array.
	 * @param timesToRotate
	 *            how many "ticks" to rotate the bits.
	 * @return
	 */
	static byte[] leftRotation(byte[] byteArr, int length, int timesToRotate) {
		int numberOfBytes = (length - 1) / 8 + 1;
		byte[] result = new byte[numberOfBytes];
		int val;

		for (int i = 0; i < length; i++) {
			val = getBit(byteArr, (i + timesToRotate) % length);
			setBit(result, i, val);
		}

		return result;
	}

	/**
	 * This function applies a permutation on a array of bytes. it uses an
	 * indexing-table in-order to create a permutation. the "new" location of
	 * bits is given in the table.
	 * 
	 * @param array
	 *            The byte array to rearrange.
	 * @param table
	 *            The indexing table that we use to rearrange the
	 *            <code>byteArr</code>.
	 * @return The permutation of the array.
	 */
	static byte[] permutation(byte[] array, int[] table) {
		int tableSize = (table.length - 1) / 8 + 1;
		byte[] result = new byte[tableSize];
		int val;

		for (int i = 0; i < table.length; i++) {
			val = getBit(array, table[i] - 1);
			setBit(result, i, val);
		}

		return result;
	}

	/**
	 * The function counts the size of the padding if the vector of bytes and
	 * creates a new vector of bytes like the original just without the padding.
	 * 
	 * @param array
	 *            The array of bytes of which to remove the padding.
	 * @return The array with its padding removed.
	 */
	static byte[] removePadding(byte[] array) {
		int paddingSize = 0;
		int i = array.length - 1;

		while (i >= 0 && array[i] == 0) {
			paddingSize++;
			i--;
		}
		byte[] result = new byte[array.length - paddingSize - 1];
		System.arraycopy(array, 0, result, 0, result.length);

		return result;
	}

	/**
	 * This function generates 16 sub-keys from a given 64-bit master key.
	 * 
	 * @param key
	 *            The 64-bit master key.
	 * @param PC1
	 *            The 1st permutation table, used on the master key to remove
	 *            and mix some of its bits.
	 * @param PC2
	 *            The 2nd permutation table, used on the generated 54 bits keys
	 *            to add more confusion to the process.
	 * @param keyShift
	 *            The table that says how many 'ticks' to rotate the keys in
	 *            each round.
	 * @return
	 */
	static byte[][] subKeysGenerator(byte[] key, int[] PC1, int[] PC2, int[] keyShift) {
		byte[][] subKeys = new byte[16][];
		byte[] tmp = CoreUtils.permutation(key, PC1);
		byte[] C = CoreUtils.getBits(tmp, 0, PC1.length / 2);
		byte[] D = CoreUtils.getBits(tmp, PC1.length / 2, PC1.length / 2);

		for (int i = 0; i < 16; i++) {
			C = CoreUtils.leftRotation(C, 28, keyShift[i]);
			D = CoreUtils.leftRotation(D, 28, keyShift[i]);
			byte[] CD = CoreUtils.concatenateBits(C, 28, D, 28);
			subKeys[i] = CoreUtils.permutation(CD, PC2);
		}
		return subKeys;
	}

	/**
	 * This method gets a byte and return its string representation, for example
	 * the following <code>byteToString((byte) 4)</code> will return the string
	 * <code>00000101</code>.
	 * 
	 * @param b
	 *            The byte to return as a string.
	 * @return The string representing the byte.
	 */
	static String byteToString(byte b) {
		String result = Integer.toBinaryString(Byte.toUnsignedInt(b));

		while (result.length() < 8) {
			result = "0" + result;
		}

		return result;
	}

	/**
	 * This method gets an array of bytes and return their string
	 * representation, for example the following
	 * <code>byteArrToString(new byte[] {(byte) 4, (byte) 7})</code> will return
	 * the string <code>0000010100000111</code>.
	 * 
	 * @param b
	 *            The byte to return as a string.
	 * @return The string representing the byte array.
	 */
	static String byteArrToString(byte[] b, boolean addSeparator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			sb.append(byteToString(b[i]) + '|');
		}
		sb.deleteCharAt(sb.length() - 1);
		return (addSeparator) ? sb.toString() : sb.toString().replace("|", "");
	}
}