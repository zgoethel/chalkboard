package net.jibini.chalkboard.vk;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.EXTDebugReport;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkLayerProperties;
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
import net.jibini.chalkboard.object.Initializable;

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
public class VkContext implements GLFWGraphicsContext<VkPipeline, GLFWWindowService<VkContext>, VkContext>,
		Initializable<VkContext>
{
	private static final ByteBuffer KHR_swapchain    = MemoryUtil.memASCII(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME);
	private static final ByteBuffer EXT_debug_report = MemoryUtil.memASCII(EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
	
	
	private static class SwapchainBuffer
	{
		long								image;
		VkCommandBuffer						command;
		long								view;
	}
	
	private static class Depth
	{
		int									format;
		long								image;
		long								memory;
		long								view;
	}
	
	private VkInstance						instance;
	private VkPhysicalDevice				physicalDevice;
	
	private VkPhysicalDeviceProperties		deviceProperties		= VkPhysicalDeviceProperties.malloc();
	private VkPhysicalDeviceFeatures		deviceFeatures			= VkPhysicalDeviceFeatures.malloc();

	private final PointerBuffer				pointerParam			= MemoryUtil.memAllocPointer(1);
	private final IntBuffer					intParam				= MemoryUtil.memAllocInt(1);
	private final LongBuffer				longParam				= MemoryUtil.memAllocLong(1);
	
	private PointerBuffer					extensionNames			= MemoryUtil.memAllocPointer(64);
	
	private long							messageCallback;
	
	private VkQueueFamilyProperties.Buffer	queueProps;
	
	private long							surface;
	private int								graphicsQueueNodeIndex;
	
	private VkDevice						device;
	private VkQueue							queue;
	
	private int								format;
	private int								colorSpace;
	
	// Messing up my formatting. :'(
	private VkPhysicalDeviceMemoryProperties memoryProperties 		= VkPhysicalDeviceMemoryProperties.malloc();
	
	private long							commandPool;
	private VkCommandBuffer					drawCommand;
	
	private long							swapchain;
	private	int								swapchainImageCount;
	private SwapchainBuffer[]				buffers;
	private int								currentBuffer;
	
	private VkCommandBuffer					setupCommand;
	
	private Depth							depth;
	
	private long							descLayout;
	private long							pipelineLayout;
	
	private long							renderPass;
	
	private	long							pipeline;
	
	private long							descPool;
	private long							descSet;
	
	private LongBuffer						framebuffers;
	
	private GLFWWindow<?>					window;
	
	
	private final VkDebugReportCallbackEXT dbgFunc = VkDebugReportCallbackEXT
			.create((flags, objectType, object, location, messageCode, pLayerPrefix, pMessage, pUserData) ->
			{
				String type;
				if ((flags & EXTDebugReport.VK_DEBUG_REPORT_INFORMATION_BIT_EXT) != 0)
				{
					type = "INFORMATION";
				} else if ((flags & EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT) != 0)
				{
					type = "WARNING";
				} else if ((flags & EXTDebugReport.VK_DEBUG_REPORT_PERFORMANCE_WARNING_BIT_EXT) != 0)
				{
					type = "PERFORMANCE WARNING";
				} else if ((flags & EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT) != 0)
				{
					type = "ERROR";
				} else if ((flags & EXTDebugReport.VK_DEBUG_REPORT_DEBUG_BIT_EXT) != 0)
				{
					type = "DEBUG";
				} else
				{
					type = "UNKNOWN";
				}

				System.err.format("%s: [%s] Code %d : %s\n", type, MemoryUtil.memASCII(pLayerPrefix), messageCode,
						VkDebugReportCallbackEXT.getString(pMessage));
				return VK10.VK_FALSE;
			});
	
	
	private void check(int errcode)
	{
		if (errcode != 0)
			throw new IllegalStateException(String.format("Vulkan error [0x%X]", errcode));
	}
	
	private static PointerBuffer checkLayers(MemoryStack stack, VkLayerProperties.Buffer available, String ... layers)
	{
		PointerBuffer required = stack.mallocPointer(layers.length);
		
		for (int i = 0; i < layers.length; i++)
		{
			boolean found = false;

			for (int j = 0; j < available.capacity(); j++)
			{
				available.position(j);
				
				if (layers[i].equals(available.layerNameString()))
				{
					found = true;
					break;
				}
			}

			if (!found)
			{
				System.err.format("Cannot find layer: %s\n", layers[i]);
				return null;
			}

			required.put(i, stack.ASCII(layers[i]));
		}

		return required;
	}
	
	private void initVk()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			/*--------------------------------------------------------------------------------------------------------------
			 												REQUIRED LAYERS
			--------------------------------------------------------------------------------------------------------------*/
			PointerBuffer requiredLayers = null;
			check(VK10.vkEnumerateInstanceLayerProperties(intParam, null));
			
			if (intParam.get(0) > 0)
			{
				VkLayerProperties.Buffer availableLayers = VkLayerProperties.mallocStack(intParam.get(0), stack);
				check(VK10.vkEnumerateInstanceLayerProperties(intParam, availableLayers));
				
				requiredLayers = checkLayers(
							stack, availableLayers,
							"VK_LAYER_KHRONOS_validation"
						);
				if (requiredLayers == null)
					requiredLayers = checkLayers(
							stack, availableLayers,
							"VK_LAYER_LUNARG_standard_validation"
						);
				if (requiredLayers == null)
					requiredLayers = checkLayers(stack, availableLayers,
							"VK_LAYER_GOOGLE_threading",
							"VK_LAYER_LUNARG_parameter_validation",
							"VK_LAYER_LUNARG_object_tracker",
							"VK_LAYER_LUNARG_core_validation",
							"VK_LAYER_GOOGLE_unique_objects"
						);
				if (requiredLayers == null)
					throw new IllegalStateException("vkEnumerateInstanceLayerProperties failed to find required validation layer.");
			}
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 											  REQUIRED EXTENSIONS
			--------------------------------------------------------------------------------------------------------------*/
			PointerBuffer requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
			if (requiredExtensions == null)
				throw new IllegalStateException("glfwGetRequiredInstanceExtensions failed to find the platform surface extensions.");
			for (int i = 0; i < requiredExtensions.capacity(); i ++)
				extensionNames.put(requiredExtensions.get(i));
			check(VK10.vkEnumerateInstanceExtensionProperties((String)null, intParam, null));
			
			if (intParam.get(0) != 0)
			{
				VkExtensionProperties.Buffer instanceExtensions = VkExtensionProperties.mallocStack(intParam.get(0), stack);
				check(VK10.vkEnumerateInstanceExtensionProperties((String)null, intParam, instanceExtensions));
				
				for (int i = 0; i < intParam.get(0); i ++)
				{
					instanceExtensions.position(i);
					if (EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME.equals(instanceExtensions.extensionNameString()))
						extensionNames.put(EXT_debug_report);
				}
			}
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 											   APPLICATION/INSTANCE
			--------------------------------------------------------------------------------------------------------------*/
			ByteBuffer APP_SHORT_NAME = stack.UTF8("chb");
			
			VkApplicationInfo app = VkApplicationInfo.mallocStack(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO)
					.pNext(MemoryUtil.NULL)
					.pApplicationName(APP_SHORT_NAME)
					.applicationVersion(0)
					.pEngineName(APP_SHORT_NAME)
					.engineVersion(0)
					.apiVersion(VK.getInstanceVersionSupported());
			
			extensionNames.flip();
			VkInstanceCreateInfo instanceInfo = VkInstanceCreateInfo.mallocStack(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
					.pNext(MemoryUtil.NULL)
					.flags(0)
					.pApplicationInfo(app)
					.ppEnabledLayerNames(requiredLayers)
					.ppEnabledExtensionNames(extensionNames);
			extensionNames.clear();
			
			VkDebugReportCallbackCreateInfoEXT debugCreateInfo = VkDebugReportCallbackCreateInfoEXT.mallocStack(stack)
					.sType(EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
					.pNext(MemoryUtil.NULL)
					.flags(EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT | EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT)
					.pfnCallback(dbgFunc)
					.pUserData(MemoryUtil.NULL);
			instanceInfo.pNext(debugCreateInfo.address());
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 											  INSTANCE CREATION
			--------------------------------------------------------------------------------------------------------------*/
			int err = VK10.vkCreateInstance(instanceInfo, null, pointerParam);
			
			switch (err)
			{
			case VK10.VK_ERROR_INCOMPATIBLE_DRIVER:
				throw new IllegalStateException("Cannot find a compatible Vulkan installable client driver (ICD).");
			case VK10.VK_ERROR_EXTENSION_NOT_PRESENT:
				throw new IllegalStateException("Cannot find a specified extension library. Make sure your layers path "
						+ "is set appropriately.");
			default:
				if (err != 0)
					throw new IllegalStateException("vkCreateInstance failed. Do you have a compatible Vulkan installable "
							+ "client driver (ICD) installed?");
			}
			
			instance = new VkInstance(pointerParam.get(0), instanceInfo);
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 											  PHYSICAL DEVICES
			--------------------------------------------------------------------------------------------------------------*/
			check(VK10.vkEnumeratePhysicalDevices(instance, intParam, null));
			
			if (intParam.get(0) > 0)
			{
				PointerBuffer physicalDevices = stack.mallocPointer(intParam.get(0));
				check(VK10.vkEnumeratePhysicalDevices(instance, intParam, physicalDevices));
				physicalDevice = new VkPhysicalDevice(physicalDevices.get(0), instance);
			} else
				throw new IllegalStateException("vkEnumeratePhysicalDevices reported zero accessible devices.");
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 										     SWAPCHAIN EXTENSION
			--------------------------------------------------------------------------------------------------------------*/
			boolean swapchainExtFound = false;
			check(VK10.vkEnumerateDeviceExtensionProperties(physicalDevice, (String)null, intParam, null));
			
			if (intParam.get(0) > 0)
			{
				VkExtensionProperties.Buffer deviceExtensions = VkExtensionProperties.mallocStack(intParam.get(0), stack);
				check(VK10.vkEnumerateDeviceExtensionProperties(physicalDevice, (String)null, intParam, deviceExtensions));
				
				for (int i = 0; i < intParam.get(0); i ++)
				{
					deviceExtensions.position(i);

					if (KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(deviceExtensions.extensionNameString()))
					{
						swapchainExtFound = true;
						extensionNames.put(KHR_swapchain);
					}
				}
			}

			if (!swapchainExtFound)
				throw new IllegalStateException("vkEnumerateDeviceExtensionProperties failed to find the "
						+ KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME + " extension.");
			err = EXTDebugReport.vkCreateDebugReportCallbackEXT(instance, debugCreateInfo, null, longParam);
			
			switch (err)
			{
			case VK10.VK_SUCCESS:
				messageCallback = longParam.get(0);
				break;
			case VK10.VK_ERROR_OUT_OF_HOST_MEMORY:
				throw new IllegalStateException("CreateDebugReportCallback: out of host memory");
			default:
				throw new IllegalStateException("CreateDebugReportCallback: unknown failure");
            }
			
			
			/*--------------------------------------------------------------------------------------------------------------
			 										  DEVICE PROPERTIES/FEATURES
			--------------------------------------------------------------------------------------------------------------*/
			VK10.vkGetPhysicalDeviceProperties(physicalDevice, deviceProperties);
			VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, intParam, null);
			
			queueProps = VkQueueFamilyProperties.malloc(intParam.get(0));
			VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, intParam, queueProps);
			if (intParam.get(0) == 0)
				throw new IllegalStateException("Physical device queue family properties is empty.");
			VK10.vkGetPhysicalDeviceFeatures(physicalDevice, deviceFeatures);
		}
	}
	
	private void initDevice()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			VkDeviceQueueCreateInfo.Buffer queue = VkDeviceQueueCreateInfo.mallocStack(1, stack)
					.sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
					.pNext(MemoryUtil.NULL)
					.flags(0)
					.queueFamilyIndex(graphicsQueueNodeIndex)
					.pQueuePriorities(stack.floats(0.0f));
			
			VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.callocStack(stack);
			if (deviceFeatures.shaderClipDistance())
				features.shaderClipDistance(true);
			
			extensionNames.flip();
			VkDeviceCreateInfo device = VkDeviceCreateInfo.mallocStack(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
					.pNext(MemoryUtil.NULL)
					.flags(0)
					.pQueueCreateInfos(queue)
					.ppEnabledLayerNames(null)
					.ppEnabledExtensionNames(extensionNames)
					.pEnabledFeatures(features);
			
			check(VK10.vkCreateDevice(physicalDevice, device, null, pointerParam));
			this.device = new VkDevice(pointerParam.get(0), physicalDevice, device);
		}
	}
	
	private void initVkSwapchain()
	{
		GLFWVulkan.glfwCreateWindowSurface(instance, window.pointer(), null, longParam);
		surface = longParam.get(0);
		
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer supportsPresent = stack.mallocInt(queueProps.capacity());
			int graphicsQueueNodeIndex;
			int presentQueueNodeIndex;
			
			for (int i = 0; i < supportsPresent.capacity(); i ++)
			{
				supportsPresent.position(i);
				KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, surface, supportsPresent);
			}
			
			graphicsQueueNodeIndex = Integer.MAX_VALUE;
			presentQueueNodeIndex = Integer.MAX_VALUE;
			
			for (int i = 0; i < supportsPresent.capacity(); i ++)
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
				for (int i = 0; i < supportsPresent.capacity(); i ++)
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
			
			VK10.vkGetDeviceQueue(device, graphicsQueueNodeIndex, 0, pointerParam);
			queue = new VkQueue(pointerParam.get(0), device);
			
			check(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, intParam, null));
			VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.mallocStack(intParam.get(0), stack);
			check(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, intParam, surfaceFormats));
			
			if (intParam.get(0) == 1 && surfaceFormats.get(0).format() == VK10.VK_FORMAT_UNDEFINED)
				format = VK10.VK_FORMAT_B8G8R8A8_UNORM;
			else
			{
				assert intParam.get(0) >= 1;
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
		return this;
	}
	
	@Override
	public VkContext generate()
	{
		initVkSwapchain();
		return this;
	}

	@Override
	public VkContext destroy()
	{
		
		return this;
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
	public GLFWWindowService<VkContext> createWindowService()
	{
		return new GLFWWindowService<VkContext>()
				.attachContext(this)
				.initializeOnce()
				
				.withNoAPI();
	}
	
	@Override
	public VkContext makeCurrent(GLFWWindow<?> window)
	{
		this.window = window;
		return this;
	}

	@Override
	public VkContext prepareRender(GLFWWindow<?> window)
	{
		
		return this;
	}

	@Override
	public VkContext swapBuffers(GLFWWindow<?> window)
	{
		
		return this;
	}
}
