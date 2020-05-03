package net.jibini.chalkboard.vk.system;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import net.jibini.chalkboard.glfw.GLFWWindow;
import net.jibini.chalkboard.vk.VkContext;
import net.jibini.chalkboard.vk.VkPipeline;

public class VkSysSurface extends VkSys<VkSysSurface>
{
	public VkSysSurface(MemoryStack stack)
	{
		super(stack);
	}
	
	public long generateSurface(VkInstance instance, GLFWWindow<VkContext, VkPipeline> window)
	{
		GLFWVulkan.glfwCreateWindowSurface(instance, window.pointer(), null, longParam);
		return longParam.get(0);
	}
	
	public int findQueueNodeIndex(VkQueueFamilyProperties.Buffer queueProperties,
			VkPhysicalDevice physicalDevice, long surface)
	{
		int graphicsQueueNodeIndex = Integer.MAX_VALUE;
		
		for (int i = 0; i < queueProperties.capacity(); i++)
		{
			KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, surface, intParam);
			
			if ((queueProperties.get(i).queueFlags() & VK10.VK_QUEUE_GRAPHICS_BIT) != 0)
				if (intParam.get(0) == VK10.VK_TRUE)
				{
					graphicsQueueNodeIndex = i;
					break;
				}
		}
		
		if (graphicsQueueNodeIndex == Integer.MAX_VALUE)
			throw new IllegalStateException("Could not find a graphics and a present queue.");
		return graphicsQueueNodeIndex;
	}
	
	public VkSurfaceFormatKHR.Buffer surfaceFormats(VkPhysicalDevice physicalDevice, long surface)
	{
		check(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, intParam, null));
		VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.mallocStack(intParam.get(0), stack);
		check(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, intParam, surfaceFormats));
		return surfaceFormats;
	}
	
	public int surfaceColorSpace(VkPhysicalDevice physicalDevice, long surface)
	{
		VkSurfaceFormatKHR.Buffer surfaceFormats = surfaceFormats(physicalDevice, surface);
		return surfaceFormats.get(0).colorSpace();
	}
	
	public int surfaceFormat(VkPhysicalDevice physicalDevice, long surface)
	{
		VkSurfaceFormatKHR.Buffer surfaceFormats = surfaceFormats(physicalDevice, surface);
		if (intParam.get(0) == 1 && surfaceFormats.get(0).format() == VK10.VK_FORMAT_UNDEFINED)
			return VK10.VK_FORMAT_B8G8R8A8_UNORM;
		else
		{
			assert intParam.get(0) >= 1;
			return surfaceFormats.get(0).format();
		}
	}
}
