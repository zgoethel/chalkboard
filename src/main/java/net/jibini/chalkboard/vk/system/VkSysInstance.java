package net.jibini.chalkboard.vk.system;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.EXTDebugReport;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

public class VkSysInstance extends VkSys<VkSysInstance>
{
	private static final VkDebugReportCallbackEXT debugFunc = VkDebugReportCallbackEXT
			.create((flags, objectType, object, location, messageCode, pLayerPrefix, pMessage, pUserData) ->
			{
				String type;
				if ((flags & EXTDebugReport.VK_DEBUG_REPORT_INFORMATION_BIT_EXT) != 0)
					type = "INFORMATION";
				else if ((flags & EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT) != 0)
					type = "WARNING";
				else if ((flags & EXTDebugReport.VK_DEBUG_REPORT_PERFORMANCE_WARNING_BIT_EXT) != 0)
					type = "PERFORMANCE WARNING";
				else if ((flags & EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT) != 0)
					type = "ERROR";
				else if ((flags & EXTDebugReport.VK_DEBUG_REPORT_DEBUG_BIT_EXT) != 0)
					type = "DEBUG";
				else
					type = "UNKNOWN";

				System.err.format("%s: [%s] Code %d : %s\n", type, MemoryUtil.memASCII(pLayerPrefix), messageCode,
						VkDebugReportCallbackEXT.getString(pMessage));
				return VK10.VK_FALSE;
			});
	
	public final VkApplicationInfo appCreateInfo;
	public final VkInstanceCreateInfo instanceCreateInfo;
	
	public final VkDebugReportCallbackCreateInfoEXT debugCallbackCreateInfo;
	
	public VkSysInstance(MemoryStack stack)
	{
		super(stack);
		
		appCreateInfo = VkApplicationInfo.mallocStack(stack)
				.sType(VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO)
				.pNext(MemoryUtil.NULL)
				.pApplicationName(stack.UTF8("chb"))
				.applicationVersion(0)
				.pEngineName(stack.UTF8("chb"))
				.engineVersion(0)
				.apiVersion(VK.getInstanceVersionSupported());;
		instanceCreateInfo = VkInstanceCreateInfo.mallocStack(stack)
				.sType(VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
				.pNext(MemoryUtil.NULL)
				.flags(0)
				.pApplicationInfo(appCreateInfo);
		
		debugCallbackCreateInfo = VkDebugReportCallbackCreateInfoEXT.mallocStack(stack);
	}
	
	public VkSysInstance enableLayers(VkSysLayers layers)
	{
		instanceCreateInfo.ppEnabledLayerNames(layers.layers);
		return self();
	}
	
	public VkSysInstance enableExtensions(VkSysExtensions extensions)
	{
		instanceCreateInfo.ppEnabledExtensionNames(extensions.extensions);
		return self();
	}
	
	public VkSysInstance enableDebug()
	{
		debugCallbackCreateInfo.sType(EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
				.pNext(MemoryUtil.NULL)
				.flags(EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT | EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT)
				.pfnCallback(debugFunc)
				.pUserData(MemoryUtil.NULL);
		instanceCreateInfo.pNext(debugCallbackCreateInfo.address());
		return self();
	}

	public VkInstance generateInstance()
	{
		int err = VK10.vkCreateInstance(instanceCreateInfo, null, pointerParam);
		
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
		
		return new VkInstance(pointerParam.get(0), instanceCreateInfo);
	}
	
	public long createDebugCallback(VkInstance instance)
	{
		int err = EXTDebugReport.vkCreateDebugReportCallbackEXT(instance, debugCallbackCreateInfo, null, longParam);
		
		switch (err)
		{
		case VK10.VK_SUCCESS:
			return longParam.get(0);
		case VK10.VK_ERROR_OUT_OF_HOST_MEMORY:
			throw new IllegalStateException("CreateDebugReportCallback: out of host memory");
		default:
			throw new IllegalStateException("CreateDebugReportCallback: unknown failure");
        }
	}
}
