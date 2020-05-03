package net.jibini.chalkboard.vk.system;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

public class VkSysPhysicalDevice extends VkSys<VkSysPhysicalDevice>
{
	public static final String KHR_SWAPCHAIN = KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
	
	public final PointerBuffer extensions;
	
	public VkSysPhysicalDevice(MemoryStack stack)
	{
		super(stack);
		
		extensions = stack.mallocPointer(64);
	}
	
	public VkPhysicalDevice defaultPhysicalDevice(VkInstance instance)
	{
		check(VK10.vkEnumeratePhysicalDevices(instance, intParam, null));
		
		if (intParam.get(0) > 0)
		{
			PointerBuffer physicalDevices = stack.mallocPointer(intParam.get(0));
			check(VK10.vkEnumeratePhysicalDevices(instance, intParam, physicalDevices));
			return new VkPhysicalDevice(physicalDevices.get(0), instance);
		} else
			throw new IllegalStateException("vkEnumeratePhysicalDevices reported zero accessible devices.");
	}
	
	public VkSysPhysicalDevice checkExtension(VkPhysicalDevice physicalDevice, String extension)
	{
		check(VK10.vkEnumerateDeviceExtensionProperties(physicalDevice, (String)null, intParam, null));
		
		if (intParam.get(0) != 0)
		{
			VkExtensionProperties.Buffer deviceExtensions = VkExtensionProperties.mallocStack(intParam.get(0), stack);
			check(VK10.vkEnumerateDeviceExtensionProperties(physicalDevice, (String)null, intParam, deviceExtensions));
			
			for (int i = 0; i < intParam.get(0); i++)
			{
				deviceExtensions.position(i);
				
				if (extension.equals(deviceExtensions.extensionNameString()))
				{
					extensions.put(stack.ASCII(extension));
					return self();
				}
			}

			throw new IllegalStateException("vkEnumerateDeviceExtensionProperties failed to find extension '" + extension + "'.");
		} else
			System.err.println("vkEnumerateDeviceExtensionProperties found no extensions.");
		return self();
	}
	
	public VkSysPhysicalDevice flipBuffer()
	{
		extensions.flip();
		return self();
	}
	
	public VkSysPhysicalDevice deviceData(VkPhysicalDevice physicalDevice, VkPhysicalDeviceProperties properties,
			VkPhysicalDeviceFeatures features)
	{
		VK10.vkGetPhysicalDeviceProperties(physicalDevice, properties);
		VK10.vkGetPhysicalDeviceFeatures(physicalDevice, features);
		return self();
	}
	
	public VkQueueFamilyProperties.Buffer deviceQueueFamilyProperties(VkPhysicalDevice physicalDevice)
	{
		VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, intParam, null);
		VkQueueFamilyProperties.Buffer queueProperties = VkQueueFamilyProperties.mallocStack(intParam.get(0), stack);
		VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, intParam, queueProperties);
		return queueProperties;
	}
	
	public VkSysPhysicalDevice memoryProperties(VkPhysicalDevice physicalDevice, VkPhysicalDeviceMemoryProperties properties)
	{
		VK10.vkGetPhysicalDeviceMemoryProperties(physicalDevice, properties);
		return self();
	}
}
