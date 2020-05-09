package net.jibini.chalkboard.decorated;

import net.jibini.chalkboard.render.RenderEngine;

public class DecoratedRenderEngine implements RenderEngine
		<
			DecoratedStaticMesh,
			DecoratedRenderEngine
		>
{
	private final RenderEngine<?, ?> origin;
	
	public DecoratedRenderEngine(RenderEngine<?, ?> origin) { this.origin = origin; }
	
	
	@Override
	public DecoratedRenderEngine destroy()
	{
		origin.destroy();
		return self();
	}

	@Override
	public DecoratedRenderEngine queue(DecoratedStaticMesh mesh)
	{
		mesh.queue();
		return self();
	}

	@Override
	public DecoratedRenderEngine render()
	{
		origin.render();
		return self();
	}

	@Override
	public DecoratedStaticMesh createStaticMesh() { return new DecoratedStaticMesh(origin.createStaticMesh()); }
}
