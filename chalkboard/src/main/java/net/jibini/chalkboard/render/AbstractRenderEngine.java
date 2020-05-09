package net.jibini.chalkboard.render;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractRenderEngine
		<
			MESH extends StaticMesh<?>,
			THIS extends AbstractRenderEngine<?, THIS>
		>
		implements RenderEngine<MESH, THIS>
{
	public final List<MESH> queue = new CopyOnWriteArrayList<>();

	@Override
	public THIS destroy()
	{
		queue.clear();
		return self();
	}

	@Override
	public THIS queue(MESH mesh)
	{
		queue.add(mesh);
		return self();
	}
}
