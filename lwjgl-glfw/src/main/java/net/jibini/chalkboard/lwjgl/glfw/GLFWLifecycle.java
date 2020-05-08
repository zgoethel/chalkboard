package net.jibini.chalkboard.lwjgl.glfw;

import net.jibini.chalkboard.life.Lifecycle;

public abstract class GLFWLifecycle<CONTEXT extends GLFWGraphicsContext<CONTEXT>>
		implements Lifecycle<GLFWLifecycle<CONTEXT>>
{
	private CONTEXT contextGiven, context;
	private GLFWWindow<CONTEXT> window;
	
	private long t = 0, c = -1;
	
	public GLFWLifecycle<CONTEXT> withWindow(GLFWWindow<CONTEXT> window)
	{ this.window = window; return self(); }
	
	public GLFWLifecycle<CONTEXT> withContext(CONTEXT context)
	{ this.contextGiven = context; return self(); }
	
	
	@Override
	public GLFWLifecycle<CONTEXT> initContext()
	{
		window.generate();
		context = contextGiven
			.initializeOnce()
			.spawn()
			
			.attachWindow(window)
//			.makeCurrent()
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
		context//.makeCurrent()
				.prepareRender();
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
