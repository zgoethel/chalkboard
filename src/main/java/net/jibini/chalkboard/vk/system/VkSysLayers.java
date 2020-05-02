package net.jibini.chalkboard.vk.system;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkLayerProperties;

public class VkSysLayers extends VkSys<VkSysLayers>
{
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
	
	public PointerBuffer layers = null;
	
	public VkSysLayers(MemoryStack stack)
	{
		super(stack);
	}
	
	public boolean checkLayer(VkLayerProperties.Buffer available, int i, String layer)
	{
		for (int j = 0; j < available.capacity(); j++)
		{
			available.position(j);
			if (layer.equals(available.layerNameString()))
				return true;
		}

		System.err.format("Cannot find layer: %s\n", layer);
		return false;
	}
	
	public boolean checkLayers(VkLayerProperties.Buffer available, String ... layers)
	{
		for (int i = 0; i < layers.length; i++)
			if (!checkLayer(available, i, layers[i]))
				return false;

		this.layers = stack.mallocPointer(layers.length);
		for (int i = 0; i < layers.length; i++)
			this.layers.put(stack.ASCII(layers[i]));
		return true;
	}
	
	public VkSysLayers checkLayers(String[] ... fallbackSets)
	{
		check(VK10.vkEnumerateInstanceLayerProperties(intParam, null));
		
		if (intParam.get(0) > 0)
		{
			VkLayerProperties.Buffer availableLayers = VkLayerProperties.mallocStack(intParam.get(0), stack);
			check(VK10.vkEnumerateInstanceLayerProperties(intParam, availableLayers));
			
			for (String[] set : fallbackSets)
				if(checkLayers(availableLayers, set))
					return self();
			throw new IllegalStateException("vkEnumerateInstanceLayerProperties failed to find primary or fallback layers.");
		} else
			System.err.println("vkEnumerateInstanceLayerProperties found no layers.");
		
		return self();
	}
}
