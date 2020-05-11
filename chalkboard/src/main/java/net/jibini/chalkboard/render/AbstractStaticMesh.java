package net.jibini.chalkboard.render;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jibini.chalkboard.object.Attachable;

public abstract class AbstractStaticMesh
		<
			ENGINE extends RenderEngine<THIS, ENGINE>,
			THIS extends AbstractStaticMesh<ENGINE, THIS>
		>
		implements StaticMesh<THIS>, Attachable<ENGINE, THIS>
{
	public final List<Float> vertexData = new CopyOnWriteArrayList<>();

	public final Map<Integer, List<Float>> interleavedData = new ConcurrentHashMap<>();
	public final List<Long> interleavedMeta = new CopyOnWriteArrayList<>();
	
	@Override
	public THIS appendVertices(float ... vertices)
	{
		if (vertices.length % 3 != 0)
			throw new IllegalStateException("Vertex array length must be divisible by 3.");
		
		for (float f : vertices)
			this.vertexData.add(f);
		return self();
	}
	
	public static long encodeMeta(int uniform, int length) { return ((long)uniform << 32) + length; }
	
	public static int decodeAttrib(long encoded) { return (int)(encoded >> 32); }
	
	public static int decodeLength(long encoded) { return (int)(encoded & Integer.MAX_VALUE); }
	
	
	public int sumInterleavedOffset(int attrib)
	{
		int start = 3;
		
		for (Long meta : interleavedMeta)
		{
			int a = decodeAttrib(meta);
			int l = decodeLength(meta);
			if (a == attrib)
				return start;
			else
				start += l;
		}
		
		throw new IllegalStateException("Failed to find interleaved attrib start; attrib meta not found for ID.");
	}
	
	public int sumInterleavedStride()
	{
		int length = 3;
		for (Long meta : interleavedMeta)
			length += decodeLength(meta);
		return length;
	}
	
	public int sumInterleavedTotalSize()
	{
		int length = vertexData.size();
		for (Long meta : interleavedMeta)
			length += decodeLength(meta) * (vertexData.size() / 3);
		return length;
	}
	
	public THIS populateInterleavedBuffer(FloatBuffer buffer)
	{
		for (int i = 0; i < vertexData.size() / 3; i++)
		{
			buffer.put(vertexData.get(i * 3));
			buffer.put(vertexData.get(i * 3 + 1));
			buffer.put(vertexData.get(i * 3 + 2));
			
			for (long attribMeta : interleavedMeta)
			{
				int attrib = AbstractStaticMesh.decodeAttrib(attribMeta);
				int length = AbstractStaticMesh.decodeLength(attribMeta);
				
				List<Float> attribData = interleavedData.get(attrib);
				for (int j = i * length; j < i * length + length; j++)
					buffer.put(attribData.get(j % attribData.size()));
			}
		}
		
		return self();
	}
	
	@Override
	public THIS appendAttribute(int attrib, int length, float ... values)
	{
		if (values.length % length != 0)
			throw new IllegalStateException("Uniform value array length must be divisible by uniform length.");
		
		if (!interleavedData.containsKey(attrib))
			interleavedData.put(attrib, new CopyOnWriteArrayList<Float>());
		long encoded = encodeMeta(attrib, length);
		if (!interleavedMeta.contains(encoded))
			interleavedMeta.add(encoded);
		
		List<Float> attr = interleavedData.get(attrib);
		for (float f : values)
			attr.add(f);
		
		return self();
	}

	@Override
	public THIS queue()
	{
		attachment().queue(self());
		return self();
	}
}
