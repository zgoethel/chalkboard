package net.jibini.chalkboard;

import org.junit.Test;
import org.lwjgl.opengl.GL11;

import net.jibini.chalkboard.lwjgl.opengl.GLContext;
import net.jibini.chalkboard.signature.StaticMesh;

public class TestGLApplication
{
	@SuppressWarnings("deprecation")
	@Test
	public void openGLContext() throws InterruptedException
	{
		try (
				GLContext context = new GLContext();
				StaticMesh<?> mesh = context.createStaticMesh();
			)
		{
			context.withContextVersion(20)
					.createWindowService()
					.createWindow()
					.withHeight(420)
					.withWidth(768)
					.withTitle("Test Window (GL)")
					.createLifecycle(() ->
					{
						System.out.println(context.name() + " (" + context.version() + ")");
						System.out.println(context.createWindowService().name()
								+ " (" + context.createWindowService().version() + ")");
						
						mesh
								.appendVertex(-1.0f, -1.0f, 0.0f)
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
						context.renderMesh(mesh);
					}, () ->
					{
						
					})
					.spawnThread()
					.join();
		}
	}
}
