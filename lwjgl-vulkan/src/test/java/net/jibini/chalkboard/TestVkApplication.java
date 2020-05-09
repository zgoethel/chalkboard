package net.jibini.chalkboard;

import org.junit.Test;

import net.jibini.chalkboard.decorated.DecoratedGraphicsContext;
import net.jibini.chalkboard.decorated.DecoratedRenderEngine;
import net.jibini.chalkboard.decorated.DecoratedWindowService;
import net.jibini.chalkboard.lwjgl.vulkan.VkContext;

public class TestVkApplication
{
	@Test
	public void openVkContext() throws InterruptedException
	{
		try (
				DecoratedGraphicsContext context = new DecoratedGraphicsContext(new VkContext());
				DecoratedWindowService windowService = context.createWindowService();
				
				DecoratedRenderEngine renderEngine = context.createRenderEngine();
				//TODO: Loses link to context when new one spawns
			)
		{
			windowService.createWindow()
					.withHeight(420)
					.withWidth(768)
					.withTitle("Test Window (Vk)")
					.createLifecycle(() ->
					{
						System.out.println(context.name() + " (" + context.version() + ")");
						System.out.println(context.createWindowService().name()
								+ " (" + context.createWindowService().version() + ")");
					}, () ->
					{
						
					}, () ->
					{
						
					})
					.spawnThread()
					.join();
		}
	}
}
