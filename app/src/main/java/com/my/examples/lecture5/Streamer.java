/**
 * Copyrigh Mail.ru Games (c) 2015
 * Created by y.bereza.
 */
package com.my.examples.lecture5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Streamer {
	private byte[] buffer;

	public Streamer(int buffersize) {
		buffer = new byte[buffersize];
	}

	public int write(InputStream in, OutputStream out) throws IOException {
		Arrays.fill(buffer, (byte)0);
		int readed = in.read(buffer);
		if (readed > 0) {
			out.write(buffer, 0, readed);
		}
		return readed;
	}

	public static void copy(InputStream in, OutputStream out) throws IOException
	{
		copy(in, out, 1024);
	}

	public static void copy(InputStream in, OutputStream out, final int bufsize) throws IOException
	{
		Streamer writer = new Streamer(bufsize);
		while(writer.write(in, out) >= 0) {}
		out.flush();
	}
}
