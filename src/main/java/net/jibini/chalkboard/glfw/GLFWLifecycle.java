package net.jibini.chalkboard.glfw;

import net.jibini.chalkboard.life.Lifecycle;

public abstract class GLFWLifecycle
		<
			CONTEXT extends GLFWGraphicsContext<?, ? extends GLFWWindowService<?>, ?>
		>
		implements Lifecycle<GLFWLifecycle<CONTEXT>>
{
	private GLFWWindow<CONTEXT> window;
	private long t = 0, c = -1;
	
	public GLFWLifecycle<CONTEXT> withWindow(GLFWWindow<CONTEXT> window)
	{ this.window = window; return this; }
	
	
	@Override
	public GLFWLifecycle<CONTEXT> initContext()
	{
		window.generate();
		return this;
	}

	@Override
	public GLFWLifecycle<CONTEXT> preUpdate() { window.prepareRender(); return this; }
	

	@Override
	public GLFWLifecycle<CONTEXT> postUpdate()
	{
		window.swapBuffers();
		if (c == -1)
			t = System.nanoTime();
		c ++;
		
		if (System.nanoTime() - t > 2000000000L)
		{
			System.out.println(c / 2);
			t = System.nanoTime();
			c = 0;
		}
		
		return this;
	}

	@Override
	public GLFWLifecycle<CONTEXT> postDestroy() { window.destroy(); return this; }

	@Override
	public boolean isAlive() { return !window.shouldClose(); }
}
