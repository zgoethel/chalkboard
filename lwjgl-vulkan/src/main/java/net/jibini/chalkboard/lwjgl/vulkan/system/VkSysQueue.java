package net.jibini.chalkboard.lwjgl.vulkan.system;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;

public class VkSysQueue extends VkSys<VkSysQueue>
{
	public VkSysQueue(MemoryStack stack)
	{
		super(stack);
	}
	
	public VkQueue generateQueue(VkDevice device, int graphicsQueueNodeIndex)
	{
		VK10.vkGetDeviceQueue(device, graphicsQueueNodeIndex, 0, pointerParam);
		return new VkQueue(pointerParam.get(0), device);
	}
}
