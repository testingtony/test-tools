package testtools.encoding;

import java.io.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * This converter is both slow and pedantic, it will ensure that the input really can be read as the charset and that
 * the characters can be converted to the output charset.
 * If there is a failure, it will throw and exception and the index of the last successful character (not necessarily byte) read can be
 * recovered with the getCharsRead method.
 */
public class Converter {
    private InputStream inputStream;
    private OutputStream outputStream;
    private CharsetDecoder inputDecoder;
    private CharsetEncoder outputEncoder;
    private int charsRead = 0;

    public Converter(InputStream is, Charset ic, OutputStream os, Charset oc) {
        inputStream = is;
        inputDecoder = ic.newDecoder();
        outputStream = os;
        outputEncoder = oc.newEncoder();
    }

    public void convert() throws IOException {
        Reader reader = new InputStreamReader(inputStream, inputDecoder);
        Writer writer = new OutputStreamWriter(outputStream, outputEncoder);

        int ch;
        do {
            ch = reader.read();
            if (ch <= 0) {
                break;
            }
            writer.write(ch);
            charsRead++;
        } while (true);
        
        writer.close();
    }

    public int getCharsRead() {
        return charsRead;
    }

    /**
     * Convenience method to validate a stream has the expected encoding.
     *
     * @param inputStream the stream to be checked
     * @param charset     the expected encoding
     * @return 0 if the stream could be encoded correctly, a +ve number is the character where the encoding failed or
     * -1 if there was a general IO error.
     */
    public static int validate(InputStream inputStream, Charset charset) {
        Converter c = new Converter(inputStream, charset, new NullOutputStream(), charset);
        try {
            c.convert();
        } catch (CharacterCodingException e) {
            return c.getCharsRead() + 1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }
}
