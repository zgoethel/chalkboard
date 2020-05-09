package net.jibini.chalkboard.decorated;

import net.jibini.chalkboard.GraphicsContext;

public class DecoratedGraphicsContext implements GraphicsContext
		<
			DecoratedGraphicsContext,
			DecoratedWindowService,
			DecoratedWindow
		>
{
	private final GraphicsContext<?, ?, ?> origin;
	
	public DecoratedGraphicsContext(GraphicsContext<?, ?, ?> origin) { this.origin = origin; }
	
	
	@Override
	public DecoratedGraphicsContext generate()
	{
		origin.generate();
		return self();
	}

	@Override
	public DecoratedGraphicsContext destroy()
	{
		origin.destroy();
		return self();
	}

	@Override
	public DecoratedGraphicsContext initialize()
	{
		origin.initialize();
		return self();
	}

	@Override
	public String name() { return origin.name(); }

	@Override
	public String version() { return origin.version(); }
	

	@Override
	public DecoratedGraphicsContext spawn() { return new DecoratedGraphicsContext(origin.spawn()); }

	@Override
	public DecoratedWindowService createWindowService() { return new DecoratedWindowService(origin.createWindowService()); }

	@Override
	public DecoratedGraphicsPipeline createPipeline() { return new DecoratedGraphicsPipeline(origin.createPipeline()); }

	
	@Override
	public DecoratedRenderEngine createRenderEngine() { return new DecoratedRenderEngine(origin.createRenderEngine()); }
}
