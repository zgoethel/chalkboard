package net.jibini.chalkboard;

import org.junit.Test;
import org.lwjgl.opengl.GL11;

import net.jibini.chalkboard.lwjgl.opengl.GLContext;

public class TestGLApplication
{
	@Test
	public void openGLContext() throws InterruptedException
	{
		try (GLContext context = new GLContext())
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
					}, () ->
					{
						GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
						GL11.glLoadIdentity();
						
						GL11.glOrtho(-768.0 / 2, 768.0 / 2, -420.0f/ 2, 420.0 / 2, -1.0, 1.0);
						GL11.glColor4f((float)Math.sin((float)System.nanoTime() / 1000000000L),
								(float)Math.sin((float)System.nanoTime() / 1000000000L + 2 * 3.14f * 0.333f),
								(float)Math.sin((float)System.nanoTime() / 1000000000L + 2 * 3.14f * 0.667f),
								1.0f);
						
						GL11.glBegin(GL11.GL_QUADS);
						GL11.glVertex2f(-200, -200);
						GL11.glVertex2f(200, -200);
						GL11.glVertex2f(200, 200);
						GL11.glVertex2f(-200, 200);
						GL11.glEnd();
					}, () ->
					{
						
					})
					.spawnThread()
					.join();
		}
	}
}
