package net.jibini.chalkboard.lwjgl.vulkan;

import net.jibini.chalkboard.resource.BinaryResource;
import net.jibini.chalkboard.shading.Pipeline;
import net.jibini.chalkboard.shading.ShaderType;

public class VkPipeline implements Pipeline<VkPipeline>
{
	@Override
	public VkPipeline destroy()
	{
		
		return self();
	}

	@Override
	public VkPipeline generate()
	{
		
		return self();
	}

	@Override
	public VkPipeline attachShader(ShaderType type, BinaryResource glsl, BinaryResource spirv)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VkPipeline use()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
