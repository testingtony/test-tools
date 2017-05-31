package testtools.encoding;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by Tony on 19/11/2016.
 */
public class TestConverter {
    private final static Charset UTF_8 = Charset.forName("utf-8");
    private final static Charset UTF_16LE = Charset.forName("utf-16le");
    private final static Charset UTF_16BE = Charset.forName("utf-16be");
    private final static Charset ISO = Charset.forName("ISO-8859-1");
    private final static File UTF_16BE_FILE = new File("src/test/resources/utf-16be.bin");

    @Test
    public void validConversionThrowsNoErrors() throws Exception {
        InputStream is = new FileInputStream(UTF_16BE_FILE);
        OutputStream os = new NullOutputStream();

        Converter converter = new Converter(is, UTF_16BE, os, UTF_16LE);

        converter.convert();

        assertThat(converter.getCharsRead(), is(12044));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void invalidInputStreamEncodingThrowsError() throws Exception {
        thrown.expect(MalformedInputException.class);

        InputStream is = new FileInputStream(UTF_16BE_FILE);
        OutputStream os = new NullOutputStream();
        Converter converter = new Converter(is, UTF_16LE, os, UTF_16LE);

        converter.convert();
    }

    @Test
    public void invalidOutputStreamEncodingThrowsError() throws Exception {
        thrown.expect(UnmappableCharacterException.class);

        InputStream is = new FileInputStream(UTF_16BE_FILE);
        OutputStream os = new NullOutputStream();
        Converter converter = new Converter(is, UTF_16BE, os, ISO);

        converter.convert();
    }

    @Test
    public void validateReturns0OnSuccess() throws FileNotFoundException {
        InputStream is = new FileInputStream(UTF_16BE_FILE);
        int result = Converter.validate(is, UTF_16BE);

        assertThat(result, is(0));
    }

    @Test
    public void validateReturnsPositiveOnFailure() throws FileNotFoundException {
        InputStream is = new FileInputStream(UTF_16BE_FILE);
        int result = Converter.validate(is, UTF_8);

        assertThat(result, is(greaterThan(0)));
    }

    // defect closing the output file without flushing first.
    @Test
    public void validConversionWritesTheWholeFile() throws Exception{
        InputStream is = new FileInputStream(UTF_16BE_FILE);

        File tempFile = File.createTempFile("converted-file", "bin");
        tempFile.deleteOnExit();
        OutputStream os = new FileOutputStream(tempFile);

        Converter converter = new Converter(is, UTF_16BE, os, UTF_8);
        converter.convert();

        assertThat(tempFile.length(), is(12046L));
    }
}
