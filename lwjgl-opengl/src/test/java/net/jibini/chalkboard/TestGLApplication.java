package net.jibini.chalkboard;

import org.junit.Test;

import net.jibini.chalkboard.decorated.DecoratedGraphicsContext;
import net.jibini.chalkboard.decorated.DecoratedRenderEngine;
import net.jibini.chalkboard.decorated.DecoratedStaticMesh;
import net.jibini.chalkboard.decorated.DecoratedWindowService;
import net.jibini.chalkboard.lwjgl.opengl.GLContext;
import net.jibini.chalkboard.render.DefaultAttrib;

public class TestGLApplication
{
	DecoratedStaticMesh mesh;
	
	@Test
	public void openGLContext() throws InterruptedException
	{
		try (
				DecoratedGraphicsContext context = new DecoratedGraphicsContext(new GLContext());
				DecoratedWindowService windowService = context.createWindowService();
				
				DecoratedRenderEngine renderEngine = context.createRenderEngine();
			)
		{
			windowService.createWindow()
					.withHeight(420)
					.withWidth(768)
					.withTitle("Test Window (GL)")
					.createLifecycle(() ->
					{
						System.out.println(context.name() + " (" + context.version() + ")");
						System.out.println(context.createWindowService().name()
								+ " (" + context.createWindowService().version() + ")");
						
						mesh = renderEngine.createStaticMesh()
								.appendVertices(
										-1.0f, -1.0f, 0.0f,
										 1.0f, -1.0f, 0.0f,
										 1.0f,  1.0f, 0.0f,
										
										 1.0f,  1.0f, 0.0f,
										-1.0f,  1.0f, 0.0f,
										-1.0f, -1.0f, 0.0f
								)
								.interleave(DefaultAttrib.DEFAULT_COLOR_ATTRIB, 4,
										1.0f, 0.0f, 0.0f, 1.0f,
										0.0f, 1.0f, 0.0f, 1.0f,
										0.0f, 0.0f, 1.0f, 1.0f,

										0.0f, 0.0f, 1.0f, 1.0f,
										1.0f, 0.0f, 1.0f, 1.0f,
										1.0f, 0.0f, 0.0f, 1.0f)
								.generate();
					}, () ->
					{
						renderEngine.queue(mesh)
								.render();
					}, () ->
					{
						mesh.destroy();
					})
					.spawnThread()
					.join();
		}
	}
}
