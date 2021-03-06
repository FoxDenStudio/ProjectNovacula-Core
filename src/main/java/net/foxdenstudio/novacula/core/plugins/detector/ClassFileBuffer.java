/**************************************************************************************************
 * The MIT License (MIT)                                                                          *
 * *
 * Copyright (c) 2015. FoxDenStudio                                                               *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining a copy                   *
 * of this software and associated documentation files (the "Software"), to deal                  *
 * in the Software without restriction, including without limitation the rights                   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell                      *
 * copies of the Software, and to permit persons to whom the Software is                          *
 * furnished to do so, subject to the following conditions:                                       *
 * *
 * The above copyright notice and this permission notice shall be included in all                 *
 * copies or substantial portions of the Software.                                                *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR                     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,                       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE                    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER                         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,                  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                  *
 * SOFTWARE.                                                                                      *
 **************************************************************************************************/

package net.foxdenstudio.novacula.core.plugins.detector;

import java.io.*;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/24/15.
 */
final class ClassFileBuffer implements DataInput {

    private byte[] buffer;
    private int size; // the number of significant bytes read
    private int pointer; // the "read pointer"

    /**
     * Create a new, empty {@code ClassFileBuffer} with the default initial capacity (8 kb).
     */
    ClassFileBuffer() {
        this(8 * 1024);
    }

    /**
     * Create a new, empty {@code ClassFileBuffer} with the specified initial capacity.
     * The initial capacity must be greater than zero. The internal buffer will grow
     * automatically when a higher capacity is required. However, buffer resizing occurs
     * extra overhead. So in good initial capacity is important in performance critical
     * situations.
     */
    private ClassFileBuffer(final int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity < 1: " + initialCapacity);
        }
        this.buffer = new byte[initialCapacity];
    }

    /**
     * Clear and fill the buffer of this {@code ClassFileBuffer} with the
     * supplied byte stream.
     * The read pointer is reset to the start of the byte array.
     */
    public void readFrom(final InputStream in) throws IOException {
        pointer = 0;
        size = 0;
        int n;
        do {
            n = in.read(buffer, size, buffer.length - size);
            if (n > 0) {
                size += n;
            }
            resizeIfNeeded();
        } while (n >= 0);
    }

    /**
     * Sets the file-pointer offset, measured from the beginning of this file,
     * at which the next read or write occurs.
     */
    private void seek(final int position) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException("position < 0: " + position);
        }
        if (position > size) {
            throw new EOFException();
        }
        this.pointer = position;
    }

    /**
     * Return the size (in bytes) of this Java ClassFile file.
     */
    public int size() {
        return size;
    }

    // DataInput

    @Override
    public void readFully(final byte[] bytes) throws IOException {
        readFully(bytes, 0, bytes.length);
    }

    @Override
    public void readFully(final byte[] bytes, final int offset, final int length)
            throws IOException {

        if (length < 0 || offset < 0 || offset + length > bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        if (pointer + length > size) {
            throw new EOFException();
        }
        System.arraycopy(buffer, pointer, bytes, offset, length);
        pointer += length;
    }

    @Override
    public int skipBytes(final int n) throws IOException {
        seek(pointer + n);
        return n;
    }

    @Override
    public byte readByte() throws IOException {
        if (pointer >= size) {
            throw new EOFException();
        }
        return buffer[pointer++];
    }

    @Override
    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        if (pointer >= size) {
            throw new EOFException();
        }
        return read();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        if (pointer + 2 > size) {
            throw new EOFException();
        }
        return (read() << 8) + read();
    }

    @Override
    public short readShort() throws IOException {
        return (short) readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return (char) readUnsignedShort();
    }

    @Override
    public int readInt() throws IOException {
        if (pointer + 4 > size) {
            throw new EOFException();
        }
        return (read() << 24) +
                (read() << 16) +
                (read() << 8) +
                read();
    }

    @Override
    public long readLong() throws IOException {
        if (pointer + 8 > size) {
            throw new EOFException();
        }
        return ((long) read() << 56) +
                ((long) read() << 48) +
                ((long) read() << 40) +
                ((long) read() << 32) +
                (read() << 24) +
                (read() << 16) +
                (read() << 8) +
                read();
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    /**
     * This methods throws an {@link UnsupportedOperationException} because the method
     * is deprecated and not used in the context of this implementation.
     *
     * @deprecated Does not support UTF-8, use readUTF() instead
     */
    @Override
    @Deprecated
    public String readLine() throws IOException {
        throw new UnsupportedOperationException("readLine() is deprecated and not supported");
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    // private

    private int read() {
        return buffer[pointer++] & 0xff;
    }

    private void resizeIfNeeded() {
        if (size >= buffer.length) {
            final byte[] newBuffer = new byte[buffer.length * 2];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
    }

}