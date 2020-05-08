package net.jibini.chalkboard.decorated;

import net.jibini.chalkboard.WindowService;

public class DecoratedWindowService implements WindowService
		<
			DecoratedGraphicsContext,
			DecoratedWindowService,
			DecoratedWindow
		>
{
	private final WindowService<?, ?, ?> origin;
	
	public DecoratedWindowService(WindowService<?, ?, ?> origin) { this.origin = origin; }
	

	@Override
	public DecoratedWindowService destroy()
	{
		origin.destroy();
		return self();
	}

	@Override
	public DecoratedWindowService initialize()
	{
		origin.initialize();
		return self();
	}

	@Override
	public String name() { return origin.name(); }

	@Override
	public String version() { return origin.version(); }

	
	@Override
	public DecoratedWindow createWindow() { return new DecoratedWindow(origin.createWindow()); }
}
