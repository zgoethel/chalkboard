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
	public DecoratedStaticMesh appendVertex(float x, float y, float z)
	{
		origin.appendVertex(x, y, z);
		return self();
	}

	@Override
	public DecoratedStaticMesh appendVertices(float ... vertices)
	{
		origin.appendVertices(vertices);
		return self();
	}

	@Override
	public DecoratedStaticMesh assignUniforms(int uniform, float[] uniforms)
	{
		origin.assignUniforms(uniform, uniforms);
		return self();
	}

	@Override
	public DecoratedStaticMesh breakSection()
	{
		origin.breakSection();
		return self();
	}

	@Override
	public DecoratedStaticMesh queue()
	{
		origin.queue();
		return self();
	}
}
