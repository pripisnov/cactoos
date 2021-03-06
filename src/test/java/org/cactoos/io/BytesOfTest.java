/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2020 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cactoos.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import org.cactoos.Text;
import org.cactoos.iterable.Endless;
import org.cactoos.iterable.HeadOf;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.EndsWith;
import org.llorllale.cactoos.matchers.IsTrue;
import org.llorllale.cactoos.matchers.StartsWith;
import org.llorllale.cactoos.matchers.TextHasString;
import org.llorllale.cactoos.matchers.TextIs;

/**
 * Test case for {@link BytesOf}.
 *
 * @since 0.12
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class BytesOfTest {

    @Test
    public void readsLargeInMemoryContent() throws Exception {
        final int multiplier = 5_000;
        final String body = "1234567890";
        new Assertion<>(
            "must read large content from in-memory Input",
            new BytesOf(
                new InputOf(
                    new Joined(
                        "",
                        new HeadOf<>(
                            multiplier, new Endless<>(body)
                        )
                    )
                )
            ).asBytes().length,
            new IsEqual<>(body.length() * multiplier)
        ).affirm();
    }

    @Test
    public void readsInputIntoBytes() throws Exception {
        new Assertion<>(
            "must read bytes from Input",
            new TextOf(
                new BytesOf(
                    new InputOf("Hello, друг!")
                )
            ),
            Matchers.allOf(
                new StartsWith("Hello, "),
                new EndsWith("друг!")
            )
        ).affirm();
    }

    @Test
    public void readsFromReader() throws Exception {
        final String source = "hello, друг!";
        new Assertion<>(
            "must read string through a reader",
            new TextOf(
                new Sticky(
                    new InputOf(
                        new BytesOf(
                            new StringReader(source),
                            StandardCharsets.UTF_8,
                            // @checkstyle MagicNumberCheck (1 line)
                            16 << 10
                        )
                    )
                )
            ),
            new TextIs(source)
        ).affirm();
    }

    @Test
    public void readsInputIntoBytesWithSmallBuffer() throws Exception {
        new Assertion<>(
            "must read bytes from Input with a small reading buffer",
            new TextOf(
                new BytesOf(
                    new InputOf("Hello, товарищ!"),
                    2
                )
            ),
            Matchers.allOf(
                new StartsWith("Hello,"),
                new EndsWith("товарищ!")
            )
        ).affirm();
    }

    @Test
    public void closesInputStream() throws Exception {
        final AtomicBoolean closed = new AtomicBoolean();
        final InputStream input = new ByteArrayInputStream(
            "how are you?".getBytes()
        );
        new TextOf(
            new InputOf(
                new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return input.read();
                    }

                    @Override
                    public void close() throws IOException {
                        input.close();
                        closed.set(true);
                    }
                }
            ),
            StandardCharsets.UTF_8
        ).asString();
        new Assertion<>(
            "must close InputStream correctly",
            closed.get(),
            new IsTrue()
        ).affirm();
    }

    @Test
    public void asBytes() throws Exception {
        final Text text = new TextOf("Hello!");
        new Assertion<>(
            "Can't convert text into bytes",
            new BytesOf(
                new InputOf(text)
            ).asBytes(),
            new IsEqual<>(
                new BytesOf(text.asString()).asBytes()
            )
        ).affirm();
    }

    @Test
    public void printsStackTrace() {
        new Assertion<>(
            "Can't print exception stacktrace",
            new TextOf(
                new BytesOf(
                    new IOException(
                        "It doesn't work at all"
                    )
                )
            ),
            new TextHasString(
                new Joined(
                    System.lineSeparator(),
                    "java.io.IOException: It doesn't work at all",
                    "\tat org.cactoos.io.BytesOfTest"
                )
            )
        ).affirm();
    }

    @Test
    public void printsStackTraceFromArray() {
        new Assertion<>(
            "Can't print exception stacktrace from array",
            new TextOf(
                new BytesOf(
                    new IOException("").getStackTrace()
                )
            ),
            new TextHasString("org.cactoos.io.BytesOfTest")
        ).affirm();
    }

}
