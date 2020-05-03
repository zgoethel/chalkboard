package net.jibini.chalkboard;

import org.junit.Test;

import net.jibini.chalkboard.lwjgl.vulkan.VkContext;

public class TestVkApplication
{
	@Test
	public void openVkContext() throws InterruptedException
	{
		try (VkContext context = new VkContext())
		{
			context.createWindowService()
					.createWindow()
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
