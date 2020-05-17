package net.jibini.chalkboard.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ClasspathResource implements BinaryResource
{
	private final String location;
	
	public ClasspathResource(String location) { this.location = location; }
	

	@Override
	public byte[] load()
	{
		try(
				InputStream stream = ClasspathResource.class.getClassLoader().getResourceAsStream(location);
			)
		{
			if (stream == null)
				throw new RuntimeException("Could not find classpath resource", new FileNotFoundException(location));
			byte[] result = new InputStreamResource(stream).load();
			stream.close();
			return result;
		} catch (IOException ex)
		{
			throw new RuntimeException("Could not load classpath resource", ex);
		}
	}	
}
