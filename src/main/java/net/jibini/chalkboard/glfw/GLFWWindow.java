package net.jibini.chalkboard.glfw;

import org.lwjgl.glfw.GLFW;

import net.jibini.chalkboard.Window;
import net.jibini.chalkboard.object.Pointer;

public class GLFWWindow
		<
			CONTEXT extends GLFWGraphicsContext<?, ? extends GLFWWindowService<?>, ?>
		>
		implements Window<CONTEXT, GLFWWindow<CONTEXT>>, Pointer<Long>
{
	private long pointer = 0L;
	private int width = 1280, height = 720;
	private String title = "GLFW Window";
	
	private CONTEXT context = null;
	
	@Override
	public GLFWWindow<CONTEXT> generate()
	{
		this.pointer = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);
		this.makeCurrent();
		context.generate();
		
		return this;
	}

	@Override
	public GLFWWindow<CONTEXT> destroy() { GLFW.glfwDestroyWindow(this.pointer()); return this; }

	@Override
	public GLFWWindow<CONTEXT> attachContext(CONTEXT context) { this.context = context; return this; }

	@Override
	public Long pointer() { return pointer; }

	
	
	@Override
	public GLFWWindow<CONTEXT> withWidth(int width) { this.width = width; return this; }

	@Override
	public GLFWWindow<CONTEXT> withHeight(int height) { this.height = height; return this; }

	@Override
	public GLFWWindow<CONTEXT> withTitle(String title) { this.title = title; return this; }
	
	
	
	public boolean shouldClose() { return GLFW.glfwWindowShouldClose(pointer()); }

	@Override
	public GLFWWindow<CONTEXT> prepareRender() { context.prepareRender(this); return this; }

	
	@Override
	public GLFWWindow<CONTEXT> swapBuffers()
	{
		GLFW.glfwPollEvents();
		context.swapBuffers(this);
		return this;
	}
	
	public GLFWWindow<CONTEXT> makeCurrent() { context.makeCurrent(this); return this; }
	
	public GLFWWindow<CONTEXT> initContext() { context.initializeOnce(); return this; }
	
	
	public GLFWLifecycle<CONTEXT> createLifecycle(Runnable setup, Runnable update, Runnable destroy)
	{
		return new GLFWLifecycle<CONTEXT>()
				{
					@Override
					public GLFWLifecycle<CONTEXT> setup()
					{
						setup.run();
						return this;
					}

					@Override
					public GLFWLifecycle<CONTEXT> update()
					{
						update.run();
						return this;
					}

					@Override
					public GLFWLifecycle<CONTEXT> destroy()
					{
						destroy.run();
						return this;
					}
				}
				.withWindow(this);
	}
}
