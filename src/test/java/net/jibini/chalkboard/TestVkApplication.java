package net.jibini.chalkboard;

import org.junit.Test;

import net.jibini.chalkboard.vk.VkContext;

public class TestVkApplication
{
	@Test
	public void openVkContext() throws InterruptedException
	{
		VkContext context = new VkContext();
		context.createWindowService()
				.createWindow()
				.withHeight(420)
				.withWidth(768)
				.withTitle("Test Window (Vk)")
				.createLifecycle(() ->
				{
					
				}, () ->
				{
					
				}, () ->
				{
					
				})
				.spawnThread()
				.join();
		context.destroy();
	}
}
