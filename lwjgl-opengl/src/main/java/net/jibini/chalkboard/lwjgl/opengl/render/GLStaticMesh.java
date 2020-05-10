package net.jibini.chalkboard.lwjgl.opengl.render;

import net.jibini.chalkboard.lwjgl.opengl.GLContext;
import net.jibini.chalkboard.render.AbstractStaticMesh;

public class GLStaticMesh extends AbstractStaticMesh<GLRenderEngine, GLStaticMesh>
{
	private GLMeshExtension<?> origin;
	
	public GLStaticMesh(GLRenderEngine engine) { attach(engine); }
	
	
	@Override
	public GLStaticMesh generate()
	{
		/*
		 * GL30 is marked as an arbitrary cutoff point for "modern" VBO rendering,
		 * where more modern hardware may handle VBOs better than display lists;
		 * older hardware, even if it supports VBOs, may favor older implementations
		 */
		if (GLContext.contextVersion() >= 30)
			origin = new GL15StaticMesh(self());
		else
			origin = new GL11StaticMesh(self());
		
		origin.generate();
		return self();
	}

	@Override
	public GLStaticMesh destroy()
	{
		origin.destroy();
		return self();
	}
	
	public GLStaticMesh render()
	{
		origin.render();
		return self();
	}
}
