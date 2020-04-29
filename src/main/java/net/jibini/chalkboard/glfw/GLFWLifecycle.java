package net.jibini.chalkboard.glfw;

import net.jibini.chalkboard.GraphicsContext;
import net.jibini.chalkboard.life.Lifecycle;

public abstract class GLFWLifecycle<CONTEXT extends GraphicsContext<?, ?, ?>>
		implements Lifecycle<GLFWLifecycle<CONTEXT>>
{
	private GLFWWindow<CONTEXT> window;
	
	public GLFWLifecycle<CONTEXT> withWindow(GLFWWindow<CONTEXT> window)
	{
		this.window = window;
		return this;
	}
	
	@Override
	public GLFWLifecycle<CONTEXT> initContext()
	{
		window.generate();
		window.makeCurrent();
		return this;
	}

	@Override
	public GLFWLifecycle<CONTEXT> preUpdate()
	{
		window.prepareRender();
		return this;
	}

	@Override
	public GLFWLifecycle<CONTEXT> postUpdate()
	{
		window.swapBuffers();
		return this;
	}

	@Override
	public GLFWLifecycle<CONTEXT> postDestroy()
	{
		window.destroy();
		return this;
	}

	@Override
	public boolean isAlive()
	{
		return !window.shouldClose();
	}
}
