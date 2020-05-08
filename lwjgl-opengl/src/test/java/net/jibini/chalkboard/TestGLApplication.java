package net.jibini.chalkboard;

import org.junit.Test;
import org.lwjgl.opengl.GL11;

import net.jibini.chalkboard.decorated.DecoratedGraphicsContext;
import net.jibini.chalkboard.decorated.DecoratedStaticMesh;
import net.jibini.chalkboard.lwjgl.opengl.GLContext;

public class TestGLApplication
{
	@Test
	public void openGLContext() throws InterruptedException
	{
		try (
				DecoratedGraphicsContext context = new DecoratedGraphicsContext(new GLContext());
				DecoratedStaticMesh mesh = context.createStaticMesh();
			)
		{
			context.createWindowService()
					.createWindow()
					.withHeight(420)
					.withWidth(768)
					.withTitle("Test Window (GL)")
					.createLifecycle(() ->
					{
						System.out.println(context.name() + " (" + context.version() + ")");
						System.out.println(context.createWindowService().name()
								+ " (" + context.createWindowService().version() + ")");
						
						mesh.appendVertex(-1.0f, -1.0f, 0.0f)
								.appendVertex(1.0f, -1.0f, 0.0f)
								.appendVertex(1.0f, 1.0f, 0.0f)
								
								.appendVertex(1.0f, 1.0f, 0.0f)
								.appendVertex(-1.0f, 1.0f, 0.0f)
								.appendVertex(-1.0f, -1.0f, 0.0f)
								
								.generate();
					}, () ->
					{
						GL11.glColor4f((float)Math.sin((float)System.nanoTime() / 1000000000L),
								(float)Math.sin((float)System.nanoTime() / 1000000000L + 2 * 3.14f * 0.333f),
								(float)Math.sin((float)System.nanoTime() / 1000000000L + 2 * 3.14f * 0.667f),
								1.0f);
						
					}, () ->
					{
						
					})
					.spawnThread()
					.join();
		}
	}
}
