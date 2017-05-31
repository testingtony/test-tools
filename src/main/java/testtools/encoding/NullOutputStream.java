package testtools.encoding;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A bitbucket OutputStream
 */
public class NullOutputStream extends OutputStream {
    @Override
    public void write(int b) throws IOException {

    }
}
