package net.jibini.chalkboard;

import org.junit.Test;

import net.jibini.chalkboard.vk.VkContext;

public class TestVkApplication
{
	@Test
	public void openContext() throws InterruptedException
	{
		VkContext context = new VkContext();
		context.createWindowService();
		context.generate()
				.destroy();
	}
}
