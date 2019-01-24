/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
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
package org.cactoos.text;

import org.cactoos.Text;
import org.cactoos.scalar.Equals;
import org.cactoos.scalar.HashCode;
import org.cactoos.scalar.UncheckedScalar;

/**
 * Text implementing Comparable.<br>
 * Below the example how you can sort words in a string:
 * <pre>{@code
 * Iterable<Text> sorted = new Sorted<>(
 *     new Mapped<>(
 *         ComparableText::new,
 *         new SplitText("The quick brown fox jumps over the lazy dog", " ")
 *     )
 * )
 * }</pre>
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.27
 */
public final class ComparableText implements Text, Comparable<Text> {

    /**
     * The origin.
     */
    private final Text text;

    /**
     * Ctor.
     * @param text The text
     */
    public ComparableText(final Text text) {
        this.text = text;
    }

    @Override
    public int compareTo(final Text other) {
        return new UncheckedScalar<>(
            () -> this.text.asString().compareTo(other.asString())
        ).value();
    }

    @Override
    public String asString() throws Exception {
        return this.text.asString();
    }

    @Override
    public boolean equals(final Object other) {
        return new UncheckedScalar<>(
            new Equals<>(
                this.text::asString,
                () -> ComparableText.class.cast(other).asString()
            )
        ).value();
    }

    @Override
    public int hashCode() {
        return new HashCode(this.text).value();
    }
}
