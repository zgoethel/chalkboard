package net.jibini.chalkboard.glfw;

import net.jibini.chalkboard.life.Lifecycle;

@Deprecated
public abstract class GLFWLifecycle
		<
			CONTEXT extends GLFWGraphicsContext<CONTEXT, PIPELINE>,
			PIPELINE extends GLFWGraphicsPipeline<CONTEXT, PIPELINE>
		>
		implements Lifecycle<GLFWLifecycle<CONTEXT, PIPELINE>>
{
	private GLFWWindow<CONTEXT, PIPELINE> window;
	private long t = 0, c = -1;
	
	public GLFWLifecycle<CONTEXT, PIPELINE> withWindow(GLFWWindow<CONTEXT, PIPELINE> window)
	{ this.window = window; return self(); }
	
	
	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> initContext()
	{
		window.generate();
		return self();
	}

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> preUpdate() { window.prepareRender(); return self(); }
	

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> postUpdate()
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
		
		return self();
	}

	@Override
	public GLFWLifecycle<CONTEXT, PIPELINE> postDestroy() { window.destroy(); return self(); }

	@Override
	public boolean isAlive() { return !window.shouldClose(); }
}
