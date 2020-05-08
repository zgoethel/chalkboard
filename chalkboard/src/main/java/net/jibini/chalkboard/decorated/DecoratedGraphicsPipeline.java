package net.jibini.chalkboard.decorated;

import net.jibini.chalkboard.GraphicsPipeline;

public class DecoratedGraphicsPipeline implements GraphicsPipeline<DecoratedGraphicsPipeline>
{
	private final GraphicsPipeline<?> origin;
	
	public DecoratedGraphicsPipeline(GraphicsPipeline<?> origin) { this.origin = origin; }
	
	
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
}
