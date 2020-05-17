package net.jibini.chalkboard.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileResource implements BinaryResource
{
	private final File file;
	
	public FileResource(File file) { this.file = file; }
	

	@Override
	public byte[] load()
	{
		try(
				InputStream stream = new FileInputStream(file);
			)
		{
			byte[] result = new InputStreamResource(stream).load();
			stream.close();
			return result;
		} catch (IOException ex)
		{
			throw new RuntimeException("Could not load file resource", ex);
		}
	}	
}
