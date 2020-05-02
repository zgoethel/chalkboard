package net.jibini.chalkboard.vk;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.EXTDebugReport;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkExtensionProperties;
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
import net.jibini.chalkboard.vk.system.VkSysExtensions;
import net.jibini.chalkboard.vk.system.VkSysInstance;
import net.jibini.chalkboard.vk.system.VkSysLayers;

/*
 * Copyright (c) 2015-2016 The Khronos Group Inc.
 * Copyright (c) 2015-2016 Valve Corporation
 * Copyright (c) 2015-2016 LunarG, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Chia-I Wu <olvaffe@gmail.com>
 * Author: Cody Northrop <cody@lunarg.com>
 * Author: Courtney Goeltzenleuchter <courtney@LunarG.com>
 * Author: Ian Elliott <ian@LunarG.com>
 * Author: Jon Ashburn <jon@lunarg.com>
 * Author: Piers Daniell <pdaniell@nvidia.com>
 * Author: Gwan-gyeong Mun <elongbug@gmail.com>
 * Porter: Camilla Berglund <elmindreda@glfw.org>
 */

/*
 * Borrows from the implementation of the LWJGL HelloVulkan:
 * https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/vulkan/HelloVulkan.java
 * 
 * which is ported from the GLFW Vulkan tests:
 * https://github.com/glfw/glfw/blob/master/tests/triangle-vulkan.c
 * 
 * which I assume is from somewhere else, as well.
 * Thanks, Vulkan.
 */
public class VkContext  implements GLFWGraphicsContext
		<
			VkContext,
			VkPipeline
		>
{
	
	
	private static class SwapchainBuffer
	{
		long									image;
		VkCommandBuffer							command;
		long									view;
	}
	
	private static class Depth
	{
		int										format;
		long									image;
		long									memory;
		long									view;
	}
	
	private VkInstance							instance;
	private VkPhysicalDevice					physicalDevice;
	
	private VkPhysicalDeviceProperties			deviceProperties		= VkPhysicalDeviceProperties.malloc();
	private VkPhysicalDeviceFeatures			deviceFeatures			= VkPhysicalDeviceFeatures.malloc();
	
	private long								messageCallback;
	
	private VkQueueFamilyProperties.Buffer		queueProps;
	
	private long								surface;
	private int									graphicsQueueNodeIndex;
	
	private VkDevice							device;
	private VkQueue								queue;
	
	private int									format;
	private int									colorSpace;
	
	// Messing up my formatting. :'(
	private VkPhysicalDeviceMemoryProperties	memoryProperties 		= VkPhysicalDeviceMemoryProperties.malloc();
	
	private long								commandPool;
	private VkCommandBuffer						drawCommand;
	
	private long								swapchain;
	private	int									swapchainImageCount;
	private SwapchainBuffer[]					buffers;
	private int									currentBuffer;
	
	private VkCommandBuffer						setupCommand;
	
	private Depth								depth;
	
	private long								descLayout;
	private long								pipelineLayout;
	
	private long								renderPass;
	
	private	long								pipeline;
	
	private long								descPool;
	private long								descSet;
	
	private LongBuffer							framebuffers;
	
	
	
	private GLFWWindow<VkContext, VkPipeline>	window;
	private final MemoryStack					contextStack			= MemoryStack.create();
	
	private final VkSysLayers					sys_layers				= new VkSysLayers(contextStack);
	private final VkSysExtensions				sys_extensions			= new VkSysExtensions(contextStack);
	private final VkSysInstance					sys_instance			= new VkSysInstance(contextStack);
	
	
	
	
	private void initVk()
	{
		try (MemoryStack stack = contextStack.push())
		{
			VkSys sys = new VkSys(stack) { };
			sys_layers.checkLayers(VkSysLayers.VALIDATION_FALLBACK_SETS) // Validation only
					.flipBuffer();
			sys_extensions.checkRequiredExtensions()
					.checkExtension(VkSysExtensions.EXT_DEBUG_REPORT) // Validation only
					.flipBuffer();
			sys_instance.enableLayers(sys_layers)
					.enableExtensions(sys_extensions)
					.enableDebug(); // Validation only

			instance = sys_instance.generateInstance();
			sys_extensions.extensions.clear();
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 											  PHYSICAL DEVICES
			--------------------------------------------------------------------------------------------------------------*/
			sys.check(VK10.vkEnumeratePhysicalDevices(instance, sys.intParam, null));
			
			if (sys.intParam.get(0) > 0)
			{
				PointerBuffer physicalDevices = stack.mallocPointer(sys.intParam.get(0));
				sys.check(VK10.vkEnumeratePhysicalDevices(instance, sys.intParam, physicalDevices));
				physicalDevice = new VkPhysicalDevice(physicalDevices.get(0), instance);
			} else
				throw new IllegalStateException("vkEnumeratePhysicalDevices reported zero accessible devices.");
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 										     SWAPCHAIN EXTENSION
			--------------------------------------------------------------------------------------------------------------*/
			boolean swapchainExtFound = false;
			sys.check(VK10.vkEnumerateDeviceExtensionProperties(physicalDevice, (String)null, sys.intParam, null));
			
			if (sys.intParam.get(0) > 0)
			{
				VkExtensionProperties.Buffer deviceExtensions = VkExtensionProperties.mallocStack(sys.intParam.get(0), stack);
				sys.check(VK10.vkEnumerateDeviceExtensionProperties(physicalDevice, (String)null, sys.intParam, deviceExtensions));
				
				for (int i = 0; i < sys.intParam.get(0); i++)
				{
					deviceExtensions.position(i);

					if (KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(deviceExtensions.extensionNameString()))
					{
						swapchainExtFound = true;
						sys_extensions.extensions.put(stack.ASCII(VkSysExtensions.KHR_SWAPCHAIN));
					}
				}
			}

			if (!swapchainExtFound)
				throw new IllegalStateException("vkEnumerateDeviceExtensionProperties failed to find the "
						+ KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME + " extension.");
			sys_instance.createDebugCallback(instance);
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 										  DEVICE PROPERTIES/FEATURES
			--------------------------------------------------------------------------------------------------------------*/
			VK10.vkGetPhysicalDeviceProperties(physicalDevice, deviceProperties);
			VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, sys.intParam, null);
			
			queueProps = VkQueueFamilyProperties.malloc(sys.intParam.get(0));
			VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, sys.intParam, queueProps);
			if (sys.intParam.get(0) == 0)
				throw new IllegalStateException("Physical device queue family properties is empty.");
			VK10.vkGetPhysicalDeviceFeatures(physicalDevice, deviceFeatures);
		}
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
	
	private void initVkSwapchain()
	{
		
		try (MemoryStack stack = contextStack.push())
		{
			VkSys sys = new VkSys(stack) { };
			
			GLFWVulkan.glfwCreateWindowSurface(instance, window.pointer(), null, sys.longParam);
			surface = sys.longParam.get(0);
			
			IntBuffer supportsPresent = stack.mallocInt(queueProps.capacity());
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
				if ((queueProps.get(i).queueFlags() & VK10.VK_QUEUE_GRAPHICS_BIT) != 0)
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
	}

	@Override
	public VkContext initialize()
	{
		if (!GLFWVulkan.glfwVulkanSupported())
			throw new IllegalStateException("Cannot find a compatible Vulkan installable client driver (ICD)");
		initVk();
		return self();
	}
	
	public VkContext attachWindow(GLFWWindow<VkContext, VkPipeline> window)
	{
		this.window = window;
		return self();
	}
	
	@Override
	public VkContext generate()
	{
		initVkSwapchain();
		return self();
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
