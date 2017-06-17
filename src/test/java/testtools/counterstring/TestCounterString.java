package testtools.counterstring;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class TestCounterString {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void zeroLengthGenerationIsError() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(">0");

        new CounterString('^', '*').generate(0);
    }

    @Test
    public void oneLengthGenerationIsError() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(">0");

        new CounterString('^', '*').generate(-1);
    }

    @Test
    public void convenienceMethodHasCorrectDefaults() {
        String generated = CounterString.ofLength(3);

        assertThat(generated, is("^3*"));
    }

    @Test
    public void singleParamConstructorSetsTerminatorAndSeparator() {
        CounterString cs = new CounterString('&');

        assertThat(cs.generate(3), is("£3£"));
    }

    @Test
    public void stringsAreCorrectLength() {
        CounterString generator = new CounterString('&', '$');
        Random r = new Random();
        for(int i = 0; i < 100; i++) {
            int wantedLength = r.nextInt(32000);
            if (wantedLength <= 0) {
                continue;
            }
            String result = generator.generate(wantedLength);

            assertThat(result.length(), is(wantedLength));
            if (wantedLength > 1) {
                assertThat(result, endsWith(Integer.toString(wantedLength) + "$"));
            }
        }
    }
}

