package net.jibini.chalkboard.resource;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamResource implements BinaryResource
{
	private final InputStream stream;
	
	public InputStreamResource(InputStream stream) { this.stream = stream; }
	
	
	@Override
	public byte[] load()
	{
		try
		{
			byte[] buffer = new byte[stream.available()];
			stream.read(buffer);
			return buffer;
		} catch (IOException ex)
		{
			throw new RuntimeException("Failed to read resource stream.", ex);
		}
	}
}
