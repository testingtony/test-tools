package testtools.counterstring;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class TestCounterStringResullts {
    private final CounterString generator = new CounterString('^', '*');

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1, "*"},
                {2, "2*"},
                {3, "^3*"},
                {4, "2^4*"},
                {5, "^3^5*"},
                {10, "^3^5^7^10*"}
        });
    }

    @Parameter(value = 0)
    public int length;

    @Parameter(value = 1)
    public String expected;

    @Test
    public void lengthGivesExpected() {
        String generated = generator.generate(length);

        assertThat("Length was not correct", generated.length(), is(length));
        assertThat("String mismatched", generated, is(expected));
    }


}
