package net.jibini.chalkboard.lwjgl.vulkan.system;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;

public class VkSysDevice extends VkSys<VkSysDevice>
{
	public final VkDeviceQueueCreateInfo.Buffer queue;
	public final VkDeviceCreateInfo device;
	
	public VkSysDevice(MemoryStack stack)
	{
		super(stack);
		
		queue = VkDeviceQueueCreateInfo.mallocStack(1, stack)
				.sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
				.pNext(MemoryUtil.NULL)
				.flags(0)
				.pQueuePriorities(stack.floats(0.0f));
		device = VkDeviceCreateInfo.mallocStack(stack)
				.sType(VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
				.pNext(MemoryUtil.NULL)
				.flags(0)
				.pQueueCreateInfos(queue);
	}
	
	public VkSysDevice withQueueFamilyIndex(int index)
	{
		queue.queueFamilyIndex(index);
		return self();
	}
	
	public VkSysDevice enableLayers(VkSysLayers layers)
	{
		device.ppEnabledLayerNames(layers.layers);
		return self();
	}
	
	public VkSysDevice enableFeatures(VkPhysicalDeviceFeatures features)
	{
		device.pEnabledFeatures(features);
		return self();
	}
	
	public VkSysDevice enableExtensions(VkSysPhysicalDevice sys_physicalDevice)
	{
		device.ppEnabledExtensionNames(sys_physicalDevice.extensions);
		return self();
	}
	
	public VkDevice generateDevice(VkPhysicalDevice physicalDevice)
	{
		check(VK10.vkCreateDevice(physicalDevice, device, null, pointerParam));
		return new VkDevice(pointerParam.get(0), physicalDevice, device);
	}
}
