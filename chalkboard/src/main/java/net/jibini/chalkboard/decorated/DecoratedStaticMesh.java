package net.jibini.chalkboard.decorated;

import net.jibini.chalkboard.render.StaticMesh;

public class DecoratedStaticMesh implements StaticMesh<DecoratedStaticMesh>
{
	private final StaticMesh<?> origin;
	
	public DecoratedStaticMesh(StaticMesh<?> origin) { this.origin = origin; }
	

	@Override
	public DecoratedStaticMesh generate()
	{
		origin.generate();
		return self();
	}

	@Override
	public DecoratedStaticMesh destroy()
	{
		origin.destroy();
		return self();
	}

	@Override
	public DecoratedStaticMesh appendVertices(float ... vertices)
	{
		origin.appendVertices(vertices);
		return self();
	}

	@Override
	public DecoratedStaticMesh queue()
	{
		origin.queue();
		return self();
	}


	@Override
	public DecoratedStaticMesh appendAttribute(int uniform, int length, float... values)
	{
		origin.appendAttribute(uniform, length, values);
		return self();
	}
}
