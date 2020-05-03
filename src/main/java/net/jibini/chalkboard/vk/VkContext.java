package net.jibini.chalkboard.vk;

import java.nio.IntBuffer;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import net.jibini.chalkboard.glfw.GLFWGraphicsContext;
import net.jibini.chalkboard.glfw.GLFWWindow;
import net.jibini.chalkboard.glfw.GLFWWindowService;
import net.jibini.chalkboard.vk.system.VkSys;
import net.jibini.chalkboard.vk.system.VkSysPhysicalDevice;
import net.jibini.chalkboard.vk.system.VkSysExtensions;
import net.jibini.chalkboard.vk.system.VkSysInstance;
import net.jibini.chalkboard.vk.system.VkSysLayers;

public class VkContext  implements GLFWGraphicsContext
		<
			VkContext,
			VkPipeline
		>
{
	private static MemoryStack					contextStack			= MemoryStack.create();
	
	
	private static VkSysLayers					sys_layers				= new VkSysLayers(contextStack);
	private static VkSysExtensions				sys_extensions			= new VkSysExtensions(contextStack);
	private static VkSysInstance				sys_instance			= new VkSysInstance(contextStack);

	private GLFWWindow<VkContext, VkPipeline>	window;
	private VkInstance							instance;
	
	
	private static VkSysPhysicalDevice			sys_physicalDevice		= new VkSysPhysicalDevice(contextStack);
	
	private VkPhysicalDevice					physicalDevice;
	private VkDevice							device;
	private VkPhysicalDeviceProperties			deviceProperties;
	private VkPhysicalDeviceFeatures			deviceFeatures;
	
	private VkQueueFamilyProperties.Buffer		queueProperties;
	
	private VkPhysicalDeviceMemoryProperties	memoryProperties 		= VkPhysicalDeviceMemoryProperties.malloc();
	private long								surface;
	private int									graphicsQueueNodeIndex;
	private int									format, colorSpace;
	private VkQueue								queue;
	
	private void initVk()
	{
		try (MemoryStack stack = contextStack.push())
		{
			sys_layers.checkLayers(VkSysLayers.VALIDATION_FALLBACK_SETS) // Validation only
					.flipBuffer();
			sys_extensions.checkRequiredExtensions()
					.checkExtension(VkSysExtensions.EXT_DEBUG_REPORT) // Validation only
					.flipBuffer();
			sys_instance.enableLayers(sys_layers)
					.enableExtensions(sys_extensions)
					.enableDebug(); // Validation only
		}
	}
	
	private VkContext spawn()
	{
		VkContext spawned = new VkContext();
		spawned.attachWindow(window);
		return spawned;
	}
	
	private VkContext generateVk()
	{
		try (MemoryStack stack = contextStack.push())
		{
			instance = sys_instance.generateInstance();
			physicalDevice = sys_physicalDevice.defaultPhysicalDevice(instance);
			sys_physicalDevice.checkExtension(physicalDevice, VkSysPhysicalDevice.KHR_SWAPCHAIN)
					.flipBuffer();
			
			deviceProperties = VkPhysicalDeviceProperties.mallocStack(stack);
			deviceFeatures = VkPhysicalDeviceFeatures.mallocStack(stack);
			sys_physicalDevice.deviceData(physicalDevice, deviceProperties, deviceFeatures);
			
			queueProperties = sys_physicalDevice.deviceQueueFamilyProperties(physicalDevice);
			
			sys_instance.createDebugCallback(instance); // Validation only
		}
		
		return self();
	}
	
	private void initDevice()
	{
		try (MemoryStack stack = contextStack.push())
		{
			VkSys sys = new VkSys(stack) { };
			
			VkDeviceQueueCreateInfo.Buffer queue = VkDeviceQueueCreateInfo.mallocStack(1, stack)
					.sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
					.pNext(MemoryUtil.NULL)
					.flags(0)
					.queueFamilyIndex(graphicsQueueNodeIndex)
					.pQueuePriorities(stack.floats(0.0f));
			
			VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.callocStack(stack);
			if (deviceFeatures.shaderClipDistance())
				features.shaderClipDistance(true);
			
			sys_extensions.extensions.flip();
			VkDeviceCreateInfo device = VkDeviceCreateInfo.mallocStack(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
					.pNext(MemoryUtil.NULL)
					.flags(0)
					.pQueueCreateInfos(queue)
					.ppEnabledLayerNames(null)
					.ppEnabledExtensionNames(sys_extensions.extensions)
					.pEnabledFeatures(features);
			
			sys.check(VK10.vkCreateDevice(physicalDevice, device, null, sys.pointerParam));
			this.device = new VkDevice(sys.pointerParam.get(0), physicalDevice, device);
		}
	}
	
	private VkContext initVkSwapchain()
	{
		
		try (MemoryStack stack = contextStack.push())
		{
			VkSys sys = new VkSys(stack) { };
			
			GLFWVulkan.glfwCreateWindowSurface(instance, window.pointer(), null, sys.longParam);
			surface = sys.longParam.get(0);
			
			IntBuffer supportsPresent = stack.mallocInt(queueProperties.capacity());
			int graphicsQueueNodeIndex;
			int presentQueueNodeIndex;
			
			for (int i = 0; i < supportsPresent.capacity(); i++)
			{
				supportsPresent.position(i);
				KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, surface, supportsPresent);
			}
			
			graphicsQueueNodeIndex = Integer.MAX_VALUE;
			presentQueueNodeIndex = Integer.MAX_VALUE;
			
			for (int i = 0; i < supportsPresent.capacity(); i++)
				if ((queueProperties.get(i).queueFlags() & VK10.VK_QUEUE_GRAPHICS_BIT) != 0)
				{
					if (graphicsQueueNodeIndex == Integer.MAX_VALUE)
						graphicsQueueNodeIndex = i;
					
					if (supportsPresent.get(i) == VK10.VK_TRUE)
					{
						graphicsQueueNodeIndex = i;
						presentQueueNodeIndex = i;
						break;
					}
				}
			
			if (presentQueueNodeIndex == Integer.MAX_VALUE)
				for (int i = 0; i < supportsPresent.capacity(); i++)
					if (supportsPresent.get(i) == VK10.VK_TRUE)
					{
						presentQueueNodeIndex = i;
						break;
					}
			if (graphicsQueueNodeIndex == Integer.MAX_VALUE || presentQueueNodeIndex == Integer.MAX_VALUE)
				throw new IllegalStateException("Could not find a graphics and a present queue.");
			if (graphicsQueueNodeIndex != presentQueueNodeIndex)
				throw new IllegalStateException("Could not find a common graphics and a present queue.");
			
			this.graphicsQueueNodeIndex = graphicsQueueNodeIndex;
			this.initDevice();
			
			VK10.vkGetDeviceQueue(device, graphicsQueueNodeIndex, 0, sys.pointerParam);
			queue = new VkQueue(sys.pointerParam.get(0), device);
			
			sys.check(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, sys.intParam, null));
			VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.mallocStack(sys.intParam.get(0), stack);
			sys.check(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, sys.intParam, surfaceFormats));
			
			if (sys.intParam.get(0) == 1 && surfaceFormats.get(0).format() == VK10.VK_FORMAT_UNDEFINED)
				format = VK10.VK_FORMAT_B8G8R8A8_UNORM;
			else
			{
				assert sys.intParam.get(0) >= 1;
				format = surfaceFormats.get(0).format();
			}
			
			colorSpace = surfaceFormats.get(0).colorSpace();
			VK10.vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);
		}
		
		return self();
	}

	@Override
	public VkContext initialize()
	{
		if (!GLFWVulkan.glfwVulkanSupported())
			throw new IllegalStateException("Cannot find a compatible Vulkan installable client driver (ICD)");
		initVk();
		return self();
	}

	@Override
	public VkContext attachWindow(GLFWWindow<VkContext, VkPipeline> window)
	{
		this.window = window;
		return self();
	}
	
	@Override
	public VkContext generate()
	{
		return spawn()
				.generateVk()
				.initVkSwapchain();
	}

	@Override
	public VkContext destroy()
	{
		
		return self();
	}

	@Override
	public String name() { return "Vulkan"; }

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

	@SuppressWarnings("resource")
	@Override
	public GLFWWindowService<VkContext, VkPipeline> createWindowService()
	{
		return new GLFWWindowService<VkContext, VkPipeline>()
				.attachContext(self())
				.initializeOnce()
				
				.hint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
	}
	
	@Override
	public VkContext makeCurrent()
	{
		
		return self();
	}

	@Override
	public VkContext prepareRender()
	{
		
		return self();
	}

	@Override
	public VkContext swapBuffers()
	{
		
		return self();
	}
}
