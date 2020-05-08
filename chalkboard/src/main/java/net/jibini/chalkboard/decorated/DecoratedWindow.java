package net.jibini.chalkboard.decorated;

import net.jibini.chalkboard.Window;
import net.jibini.chalkboard.life.Lifecycle;

public class DecoratedWindow implements Window
		<
			DecoratedGraphicsContext,
			DecoratedWindowService,
			DecoratedWindow
		>
{
	private final Window<?, ?, ?> origin;
	
	public DecoratedWindow(Window<?, ?, ?> origin) { this.origin = origin; }

	
	@Override
	public DecoratedWindow generate()
	{
		origin.generate();
		return self();
	}

	@Override
	public DecoratedWindow destroy()
	{
		origin.destroy();
		return self();
	}

	@Override
	public DecoratedWindow withWidth(int width)
	{
		origin.withWidth(width);
		return self();
	}

	@Override
	public DecoratedWindow withHeight(int height)
	{
		origin.withHeight(height);
		return self();
	}

	@Override
	public DecoratedWindow withTitle(String title)
	{
		origin.withTitle(title);
		return self();
	}

	@Override
	public DecoratedWindow update()
	{
		origin.update();
		return self();
	}

	@Override
	public Lifecycle<?> createLifecycle(Runnable init, Runnable update, Runnable destroy)
	{ return origin.createLifecycle(init, update, destroy); }
}
