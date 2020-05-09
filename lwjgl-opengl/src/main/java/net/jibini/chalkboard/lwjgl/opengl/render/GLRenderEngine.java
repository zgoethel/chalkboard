package net.jibini.chalkboard.lwjgl.opengl.render;

import net.jibini.chalkboard.render.AbstractRenderEngine;

public class GLRenderEngine extends AbstractRenderEngine
		<
			GLStaticMesh,
			GLRenderEngine
		>
{
	@Override
	public GLStaticMesh createStaticMesh() { return new GLStaticMesh(self()); }
	

	@Override
	public GLRenderEngine render()
	{
		for (GLStaticMesh mesh : queue)
			mesh.render();
		queue.clear();
		return self();
	}
}
