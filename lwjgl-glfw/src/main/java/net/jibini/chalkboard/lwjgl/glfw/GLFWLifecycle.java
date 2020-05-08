package net.jibini.chalkboard.lwjgl.glfw;

import net.jibini.chalkboard.life.Lifecycle;

public abstract class GLFWLifecycle<CONTEXT extends GLFWGraphicsContext<CONTEXT>>
		implements Lifecycle<GLFWLifecycle<CONTEXT>>
{
	private final CONTEXT contextGiven;
	private final GLFWWindow<CONTEXT> window;

	private CONTEXT context;
	
	private long t = 0, c = -1;
	
	public GLFWLifecycle(CONTEXT context, GLFWWindow<CONTEXT> window)
	{
		this.contextGiven = context;
		this.context = context;
		this.window = window;
	}
	
	@Override
	public GLFWLifecycle<CONTEXT> initContext()
	{
		window.generate();
		context = contextGiven
			.initializeOnce()
			.spawn()
			.attachWindow(window)
			.generate();
		return self();
	}

	@Override
	public GLFWLifecycle<CONTEXT> preInit() { return self(); };

	@Override
	public GLFWLifecycle<CONTEXT> init() { return self(); };

	@Override
	public GLFWLifecycle<CONTEXT> postInit() { return self(); };
	

	@Override
	public GLFWLifecycle<CONTEXT> preUpdate()
	{
		window.update();
		context.prepareRender();
		return self();
	}

	@Override
	public GLFWLifecycle<CONTEXT> update() { return self(); };
	

	@Override
	public GLFWLifecycle<CONTEXT> postUpdate()
	{
		context.swapBuffers();
		if (c == -1)
			t = System.nanoTime();
		c++;
		
		if (System.nanoTime() - t > 2000000000L)
		{
			System.out.println(c / 2);
			t = System.nanoTime();
			c = 0;
		}
		
		return self();
	}

	@Override
	public GLFWLifecycle<CONTEXT> preDestroy() { return self(); };

	@Override
	public GLFWLifecycle<CONTEXT> destroy() { return self(); };
	
	@Override
	public GLFWLifecycle<CONTEXT> postDestroy()
	{
		window.destroy();
		context.destroy();
		return self();
	}

	@Override
	public boolean isAlive() { return !window.shouldClose(); }
}
