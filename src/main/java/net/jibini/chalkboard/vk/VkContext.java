package net.jibini.chalkboard.vk;

import org.lwjgl.Version;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import net.jibini.chalkboard.GraphicsContext;
import net.jibini.chalkboard.glfw.GLFWWindowService;

public class VkContext implements GraphicsContext<VkPipeline, GLFWWindowService<VkContext>, VkContext>
{
	private VkInstance instance;
	private VkPhysicalDevice device;
	
	private VkPhysicalDeviceProperties deviceProperties = VkPhysicalDeviceProperties.malloc();
	private VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.malloc();
	
	@Override
	public VkContext generate()
	{
		
		return this;
	}

	@Override
	public VkContext destroy()
	{
		return this;
	}

	@Override
	public String name()
	{
		return "Vulkan";
	}

	@Override
	public String version()
	{
		// https://www.khronos.org/registry/vulkan/specs/1.2/html/chap29.html#extendingvulkan-coreversions-versionnumbers
		int versionEncoded = VK.getInstanceVersionSupported();
		int versionMajor = versionEncoded >> 22;
		int versionMinor = (versionEncoded >> 12) & 0x3ff;
		int versionPatch = versionEncoded & 0xfff;
		return versionMajor + "." + versionMinor + "." + versionPatch + " [LWJGL " + Version.getVersion() + "]";
	}

	@Override
	public VkPipeline createPipeline()
	{
		return new VkPipeline();
	}

	@Override
	public GLFWWindowService<VkContext> createWindowService()
	{
		return new GLFWWindowService<VkContext>()
				.initializeOnce();
	}
}
