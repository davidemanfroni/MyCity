/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Utiliy;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DoneHandlerInputStream extends FilterInputStream
{
	private boolean done;

	public DoneHandlerInputStream(InputStream stream)
	{
		super(stream);
	}

	@Override
	public int read(byte[] bytes, int offset, int count) throws IOException
	{
		if (!done)
		{
			int result = super.read(bytes, offset, count);
			if (result != -1)
			{
				return result;
			}
		}
		done = true;
		return -1;
	}
}