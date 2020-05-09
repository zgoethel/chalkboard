package net.jibini.chalkboard.lwjgl.vulkan;

import net.jibini.chalkboard.GraphicsPipeline;

public class VkPipeline implements GraphicsPipeline<VkPipeline>
{
	@Override
	public VkPipeline destroy()
	{
		
		return self();
	}

	@Override
	public VkPipeline generate()
	{
		
		return self();
	}
}
