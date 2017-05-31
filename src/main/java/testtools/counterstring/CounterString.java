package testtools.counterstring;


/**
 * Ripped off from James Bach's Perlclip tool.
 */
public class CounterString {
    private final char separator;
    private final char terminator;

    public CounterString(final char separator, final char terminator) {
        this.separator = separator;
        this.terminator = terminator;
    }

    public CounterString(final char separator) {
        this(separator, separator);
    }

    public CounterString() {
        this('^', '*');
    }

    /**
     * Generates the counter string.
     * @param length how long the counter string should be
     * @return the counter string.
     */
    public String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be >0");
        }
        StringBuilder wip = new StringBuilder();

        while(length > 0) {
            String numberString = Integer.toString(length);
            int textLength = numberString.length() + 1;
            if (length >= textLength) {
                wip.insert(0, separator);
                wip.insert(0, numberString);
                length -= textLength;
            } else {
                wip.insert(0, separator);
                length--;
            }
        }

        wip.setCharAt(wip.length() - 1, terminator);
        return wip.toString();
    }

    /**
     * Convenience method to call with default separator & parameter.
     * @param length how long the counter string should be
     * @return the counter string.
     */
    public static String ofLength(final int length) {
        return new CounterString().generate(length);
    }
}
