package net.jibini.chalkboard;

import org.junit.Test;

import net.jibini.chalkboard.lwjgl.GLContext;

public class TestGLApplication
{
	@Test
	public void openContext() throws InterruptedException
	{
		new GLContext()
			.createWindowService()
			.createWindow()
			.withHeight(420)
			.withWidth(768)
			.withTitle("Test Window")
			.createLifecycle(() ->
			{
				
			}, () ->
			{
				
			}, () ->
			{
				
			})
			.spawnThread()
			.join();
	}
}
