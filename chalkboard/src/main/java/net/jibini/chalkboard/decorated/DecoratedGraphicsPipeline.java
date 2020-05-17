package net.jibini.chalkboard.decorated;

import net.jibini.chalkboard.resource.BinaryResource;
import net.jibini.chalkboard.shading.Pipeline;
import net.jibini.chalkboard.shading.ShaderType;

public class DecoratedGraphicsPipeline implements Pipeline<DecoratedGraphicsPipeline>
{
	private final Pipeline<?> origin;
	
	public DecoratedGraphicsPipeline(Pipeline<?> origin) { this.origin = origin; }
	
	
	@Override
	public DecoratedGraphicsPipeline destroy()
	{
		origin.destroy();
		return self();
	}

	@Override
	public DecoratedGraphicsPipeline generate()
	{
		origin.generate();
		return self();
	}

	@Override
	public DecoratedGraphicsPipeline attachShader(ShaderType type, BinaryResource glsl, BinaryResource spirv)
	{
		origin.attachShader(type, glsl, spirv);
		return self();
	}

	@Override
	public DecoratedGraphicsPipeline use()
	{
		origin.use();
		return self();
	}
}
