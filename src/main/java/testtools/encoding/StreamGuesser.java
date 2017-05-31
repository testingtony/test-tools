package testtools.encoding;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Attempt to work out the encoding of a binary stream
 */
public class StreamGuesser {
    private InputStream source;
    private boolean done;
    private String charset;

    public StreamGuesser(InputStream is) {
        if (is == null) {
            throw new NullPointerException("StreamGuesser needs a valid input stream");
        }
        source = new BufferedInputStream(is);
    }

    public boolean guess() {
        byte[] buf = new byte[1024] ;
        int len;
        boolean ascii = true;

        UniversalDetector det = new UniversalDetector(null);

        try {
            while (!det.isDone() && (len = source.read(buf, 0, buf.length)) > 0) {
                det.handleData(buf, 0, len);
                for (int i = 0; i < len; i++) {
                    if (buf[i] < 0) {
                        ascii = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        det.dataEnd();
        done = det.isDone();
        charset = det.getDetectedCharset();
        if (charset == null && ascii) {
            charset = "ASCII";
        }

        return done;
    }

    public String getCharset() {
        return charset;
    }

    public boolean confident() {
        return done;
    }
}
