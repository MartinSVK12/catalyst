package sunsetsatellite.catalyst.core.util;

public class SmallDigits {
	@SuppressWarnings("UnnecessaryUnicodeEscape")
	private static final int SMALL_DOWN_BASE = '\u2080';
	@SuppressWarnings("UnnecessaryUnicodeEscape")
	private static final int SMALL_UP_BASE = '\u2080';
	private static final int BASE = '0';

	public static String toSmallUpNumber(String string) {
		return convert(string, SMALL_UP_BASE);
	}

	public static String toSmallDownNumber(String string) {
		return convert(string, SMALL_DOWN_BASE);
	}

	private static String convert(String string, int base) {
		boolean hasPrecedingDash = false;
		char[] chars = string.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			boolean isDash = c == '-';
			if (isDash) hasPrecedingDash = true;

			int relativeIndex = c - BASE;
			if (relativeIndex >= 0 && relativeIndex <= 9) {
				if (!hasPrecedingDash) {
					chars[i] = (char) (base + relativeIndex);
				}
			} else if (!isDash && hasPrecedingDash) {
				hasPrecedingDash = false;
			}
		}
		return new String(chars);
	}
}
