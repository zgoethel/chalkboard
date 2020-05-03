package net.jibini.chalkboard.lwjgl.vulkan;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import net.jibini.chalkboard.lwjgl.glfw.GLFWGraphicsContext;
import net.jibini.chalkboard.lwjgl.glfw.GLFWWindow;
import net.jibini.chalkboard.lwjgl.glfw.GLFWWindowService;
import net.jibini.chalkboard.lwjgl.vulkan.system.VkSysDevice;
import net.jibini.chalkboard.lwjgl.vulkan.system.VkSysExtensions;
import net.jibini.chalkboard.lwjgl.vulkan.system.VkSysInstance;
import net.jibini.chalkboard.lwjgl.vulkan.system.VkSysLayers;
import net.jibini.chalkboard.lwjgl.vulkan.system.VkSysPhysicalDevice;
import net.jibini.chalkboard.lwjgl.vulkan.system.VkSysQueue;
import net.jibini.chalkboard.lwjgl.vulkan.system.VkSysSurface;

public class VkContext implements GLFWGraphicsContext
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
	private static VkSysDevice					sys_device				= new VkSysDevice(contextStack);
	
	private VkPhysicalDevice					physicalDevice;
	private VkDevice							device;
	private VkPhysicalDeviceProperties			deviceProperties;
	private VkPhysicalDeviceFeatures			deviceFeatures;
	
	
	private static VkSysSurface					sys_surface				= new VkSysSurface(contextStack);
	private static VkSysQueue					sys_queue				= new VkSysQueue(contextStack);
	
	private long								surface;
	private int									graphicsQueueNodeIndex;
	private VkQueueFamilyProperties.Buffer		queueProperties;
	
	
	private VkPhysicalDeviceMemoryProperties	memoryProperties;
	private int									format, colorSpace;
	private VkQueue								queue;
	
	@SuppressWarnings("resource")
	private VkContext spawn()
	{
		VkContext spawned = new VkContext()
				.attachWindow(window);
		return spawned;
	}

	@Override
	public VkContext initialize()
	{
		if (!GLFWVulkan.glfwVulkanSupported())
			throw new IllegalStateException("Cannot find a compatible Vulkan installable client driver (ICD)");
		
		try (MemoryStack stack = contextStack.push())
		{
			sys_layers.checkLayers(VkSysLayers.VALIDATION_FALLBACK_SETS) // Validation only
					.flipBuffer();
			sys_extensions.checkRequiredExtensions()
					.checkExtension(VkSysExtensions.EXT_DEBUG_REPORT) // Validation only
					.flipBuffer();
			sys_instance.enableLayers(sys_layers) // Validation only
					.enableExtensions(sys_extensions)
					.enableDebug(); // Validation only
		}
		
		return self();
	}
	
	private VkContext generateVk()
	{
		try (MemoryStack stack = contextStack.push())
		{
			instance = sys_instance.generateInstance();
			physicalDevice = sys_physicalDevice.defaultPhysicalDevice(instance);
			sys_physicalDevice
					.checkExtension(physicalDevice, VkSysPhysicalDevice.KHR_SWAPCHAIN)
					.flipBuffer();
			
			deviceProperties = VkPhysicalDeviceProperties.mallocStack(stack);
			deviceFeatures = VkPhysicalDeviceFeatures.mallocStack(stack);
			sys_physicalDevice.deviceData(physicalDevice, deviceProperties, deviceFeatures);
			
			queueProperties = sys_physicalDevice.deviceQueueFamilyProperties(physicalDevice);
			
			sys_instance.createDebugCallback(instance); // Validation only
		}
		
		return self();
	}
	
	private void generateDevice()
	{
		try (MemoryStack stack = contextStack.push())
		{
			VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.callocStack(stack);
			if (deviceFeatures.shaderClipDistance())
				features.shaderClipDistance(true);
			
			device = sys_device
					.enableExtensions(sys_physicalDevice)
					.enableFeatures(features)
					.withQueueFamilyIndex(graphicsQueueNodeIndex)
					.generateDevice(physicalDevice);
		}
	}
	
	private VkContext generateSwapchain()
	{
		try (MemoryStack stack = contextStack.push())
		{
			surface = sys_surface.generateSurface(instance, window);
			graphicsQueueNodeIndex = sys_surface.findQueueNodeIndex(queueProperties, physicalDevice, surface);
			generateDevice();
			
			queue = sys_queue.generateQueue(device, graphicsQueueNodeIndex);
			
			format = sys_surface.surfaceFormat(physicalDevice, surface);
			colorSpace = sys_surface.surfaceColorSpace(physicalDevice, surface);
			
			memoryProperties = VkPhysicalDeviceMemoryProperties.mallocStack(stack);
			sys_physicalDevice.memoryProperties(physicalDevice, memoryProperties);
		}
		
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
				.generateSwapchain();
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
