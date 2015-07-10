/**
 * The class that runs the CryptoChat window.
 * 
 * Happy cow says: "Muuuuuuu.."
 * 
 * @author Ben Sabah.
 */

public class Main {
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		String mode = "";
		if (args.length != 0) {
			mode = args[0];
		}
		CryptoChat gui = new CryptoChat(mode);
	}
}
