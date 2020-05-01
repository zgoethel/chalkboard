package net.jibini.chalkboard.glfw;

import net.jibini.chalkboard.life.Lifecycle;

public abstract class GLFWLifecycle
		<
			CONTEXT		extends GLFWGraphicsContext	<CONTEXT, PIPELINE>,
			PIPELINE	extends GLFWGraphicsPipeline<CONTEXT, PIPELINE>
		>
		implements Lifecycle<GLFWLifecycle<CONTEXT, PIPELINE>>
{
	private CONTEXT contextGiven, context;
	private GLFWWindow<CONTEXT, PIPELINE> window;
	
	private long t = 0, c = -1;
	
	public GLFWLifecycle<CONTEXT, PIPELINE> withWindow(GLFWWindow<CONTEXT, PIPELINE> window)
	{ this.window = window; return self(); }
	
	public GLFWLifecycle<CONTEXT, PIPELINE> withContext(CONTEXT context)
	{ this.contextGiven = context; return self(); }
	
	
	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> initContext()
	{
		window.generate();
		context = contextGiven
			.initializeOnce()
			.attachWindow(window)
			.makeCurrent()
			
			.generate();
		return self();
	}

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> preInit() { return self(); };

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> init() { return self(); };

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> postInit() { return self(); };
	

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> preUpdate()
	{
		window.update();
		context.makeCurrent()
				.prepareRender();
		return self();
	}

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> update() { return self(); };
	

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> postUpdate()
	{
		context.swapBuffers();
		if (c == -1)
			t = System.nanoTime();
		c ++;
		
		if (System.nanoTime() - t > 2000000000L)
		{
			System.out.println(c / 2);
			t = System.nanoTime();
			c = 0;
		}
		
		return self();
	}

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> preDestroy() { return self(); };

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> destroy() { return self(); };
	
	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> postDestroy() { window.destroy(); return self(); }
	

	@Override
	public boolean isAlive() { return !window.shouldClose(); }
}
