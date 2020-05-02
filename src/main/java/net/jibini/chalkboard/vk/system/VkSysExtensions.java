package net.jibini.chalkboard.vk.system;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTDebugReport;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkExtensionProperties;

public class VkSysExtensions extends VkSys<VkSysExtensions>
{
	public static final String EXT_DEBUG_REPORT = EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
	public static final String KHR_SWAPCHAIN = KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
	
	public final PointerBuffer extensions;
	
	public VkSysExtensions(MemoryStack stack)
	{
		super(stack);
		extensions = stack.mallocPointer(64);
	}
	
	public VkSysExtensions addRequiredExtensions()
	{
		PointerBuffer requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
		if (requiredExtensions == null)
			throw new IllegalStateException("glfwGetRequiredInstanceExtensions failed to find the platform surface extensions.");
		for (int i = 0; i < requiredExtensions.capacity(); i++)
			extensions.put(requiredExtensions.get(i));
		return self();
	}
	
	public VkSysExtensions addIfFound(String extension)
	{
		try (MemoryStack stack = this.stack.push())
		{
			check(VK10.vkEnumerateInstanceExtensionProperties((String)null, intParam, null));
			
			if (intParam.get(0) != 0)
			{
				VkExtensionProperties.Buffer instanceExtensions = VkExtensionProperties.mallocStack(intParam.get(0), stack);
				check(VK10.vkEnumerateInstanceExtensionProperties((String)null, intParam, instanceExtensions));
				
				for (int i = 0; i < intParam.get(0); i++)
				{
					instanceExtensions.position(i);
					if (extension.equals(instanceExtensions.extensionNameString()))
						extensions.put(stack.ASCII(extension));
				}
			}
		}
		
		return self();
	}
}
