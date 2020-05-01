package net.jibini.chalkboard.vk.system;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkLayerProperties;

public class VkSysLayers
{
	private VkSys sys = new VkSys();
	
	public static final String[][] VALIDATION_FALLBACK_SETS = 
		{
				new String[] { "VK_LAYER_KHRONOS_validation" },
				new String[] { "VK_LAYER_LUNARG_standard_validation" },
				new String[] {
						"VK_LAYER_GOOGLE_threading",
						"VK_LAYER_LUNARG_parameter_validation",
						"VK_LAYER_LUNARG_object_tracker",
						"VK_LAYER_LUNARG_core_validation",
						"VK_LAYER_GOOGLE_unique_objects"
				},
		};
	
	public boolean checkLayer(PointerBuffer required, MemoryStack stack, VkLayerProperties.Buffer available, int i, String layer)
	{
		for (int j = 0; j < available.capacity(); j++)
		{
			available.position(j);
			
			if (layer.equals(available.layerNameString()))
			{
				required.put(i, stack.ASCII(layer));
				return true;
			}
		}

		System.err.format("Cannot find layer: %s\n", layer);
		return false;
	}
	
	public PointerBuffer checkLayers(MemoryStack stack, VkLayerProperties.Buffer available, String ... layers)
	{
		PointerBuffer required = stack.mallocPointer(layers.length);
		for (int i = 0; i < layers.length; i++)
			if (!checkLayer(required, stack, available, i, layers[i]))
				return null;
		return required;
	}
	
	public PointerBuffer checkLayers(MemoryStack stack, String[] ... fallbackSets)
	{
		PointerBuffer requiredLayers = null;
		sys.check(VK10.vkEnumerateInstanceLayerProperties(sys.intParam, null));
		
		if (sys.intParam.get(0) > 0)
		{
			VkLayerProperties.Buffer availableLayers = VkLayerProperties.mallocStack(sys.intParam.get(0), stack);
			sys.check(VK10.vkEnumerateInstanceLayerProperties(sys.intParam, availableLayers));
			
			for (String[] set : fallbackSets)
			{
				requiredLayers = checkLayers(stack, availableLayers, set);
				if (requiredLayers != null)
					break;
			}
			
			if (requiredLayers == null)
				throw new IllegalStateException("vkEnumerateInstanceLayerProperties failed to find required validation layer.");
		} else
			System.err.println("vkEnumerateInstanceLayerProperties found no layers.");
		return requiredLayers;
	}
}
