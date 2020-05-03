package net.jibini.chalkboard.lwjgl.vulkan.system;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTDebugReport;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkExtensionProperties;

public class VkSysExtensions extends VkSys<VkSysExtensions>
{
	public static final String EXT_DEBUG_REPORT = EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
	
	public final PointerBuffer extensions;
	
	public VkSysExtensions(MemoryStack stack)
	{
		super(stack);
		
		extensions = stack.mallocPointer(64);
	}
	
	public VkSysExtensions checkRequiredExtensions()
	{
		PointerBuffer requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
		if (requiredExtensions == null)
			throw new IllegalStateException("glfwGetRequiredInstanceExtensions failed to find the platform surface extensions.");
		for (int i = 0; i < requiredExtensions.capacity(); i++)
			checkExtension(requiredExtensions.getStringASCII(i));
		return self();
	}
	
	public VkSysExtensions checkExtension(String extension)
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
				{
					extensions.put(stack.ASCII(extension));
					return self();
				}
			}

			throw new IllegalStateException("vkEnumerateInstanceExtensionProperties failed to find extension '" + extension + "'.");
		} else
			System.err.println("vkEnumerateInstanceExtensionProperties found no extensions.");
		return self();
	}
	
	public VkSysExtensions flipBuffer()
	{
		extensions.flip();
		return self();
	}
}
