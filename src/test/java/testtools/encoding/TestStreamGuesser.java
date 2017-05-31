package testtools.encoding;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestStreamGuesser {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void cantUseANullStream() {
        thrown.expect(NullPointerException.class);
        new StreamGuesser(null);
    }

    @Test
    public void utft16StreamDefinatelyRecognised()  {
        StreamGuesser guesser = new StreamGuesser(makeStream("feff04806506c06c06f0250a3d83ddca9"));
        guesser.guess();

        assertThat(guesser.getCharset(), is("UTF-16BE"));
        assertThat(guesser.confident(), is(true));
    }

    @Test
    public void uft8FileDefinatelyRecognised() throws FileNotFoundException {
        InputStream is = new FileInputStream(new File("src/test/resources/utf-8.bin"));
        StreamGuesser guesser = new StreamGuesser(is);
        guesser.guess();

        assertThat(guesser.getCharset(), is("UTF-8"));
        assertThat(guesser.confident(), is(true));
    }

    @Test
    public void uft16beFileDefinatelyRecognised() throws FileNotFoundException {
        InputStream is = new FileInputStream(new File("src/test/resources/utf-16be.bin"));
        StreamGuesser guesser = new StreamGuesser(is);
        guesser.guess();

        assertThat(guesser.getCharset(), is("UTF-16BE"));
        assertThat(guesser.confident(), is(true));
    }

    @Test
    public void uft16leFileDefinatelyRecognised() throws FileNotFoundException {
        InputStream is = new FileInputStream(new File("src/test/resources/utf-16le.bin"));
        StreamGuesser guesser = new StreamGuesser(is);
        guesser.guess();

        assertThat(guesser.getCharset(), is("UTF-16LE"));
        assertThat(guesser.confident(), is(true));
    }


    @Test
    public void iso_2022_jpFileDefinatelyRecognised() throws FileNotFoundException {
        InputStream is = new FileInputStream(new File("src/test/resources/iso-2022-jp.bin"));
        StreamGuesser guesser = new StreamGuesser(is);
        guesser.guess();

        assertThat(guesser.getCharset(), is("ISO-2022-JP"));
        assertThat(guesser.confident(), is(true));
    }

    @Test
    public void shortAsciiIsGuessed() {
        InputStream is = makeStream("0020217f");
        StreamGuesser guesser = new StreamGuesser(is);
        guesser.guess();

        assertThat(guesser.getCharset(), is("ASCII"));
        assertThat(guesser.confident(), is(false));
    }

    @Test
    public void shortNonAsciiIsUnknown() {
        InputStream is = makeStream("00202181");
        StreamGuesser guesser = new StreamGuesser(is);
        guesser.guess();

        assertThat(guesser.getCharset(), is(nullValue()));
        assertThat(guesser.confident(), is(false));
    }

    @Test
    public void makeStreamSlicesCorrectly() throws IOException{
        InputStream istream = makeStream("0001FEFF");
        assertThat(istream.read(), is(0));
        assertThat(istream.read(), is(1));
        assertThat(istream.read(), is(254));
        assertThat(istream.read(), is(255));
        assertThat(istream.read(), is(-1));
    }

    private ByteArrayInputStream makeStream(String hexed) {
        byte [] byteArray = new byte[hexed.length() / 2];
        for(int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) (Integer.parseInt(hexed.substring(i*2, i*2+2),16) & 0xff);
        }
        return new ByteArrayInputStream(byteArray);
    }

}
