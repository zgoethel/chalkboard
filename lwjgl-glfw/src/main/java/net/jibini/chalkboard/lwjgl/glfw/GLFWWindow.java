package net.jibini.chalkboard.lwjgl.glfw;

import org.lwjgl.glfw.GLFW;

import net.jibini.chalkboard.Window;
import net.jibini.chalkboard.object.Pointer;

public class GLFWWindow
		<
			CONTEXT extends GLFWGraphicsContext<CONTEXT>
		>
		implements Window
		<
			CONTEXT,
			GLFWWindowService<CONTEXT>,
			GLFWWindow<CONTEXT>
		>,
			Pointer<Long>
{
	private long pointer = 0L;
	private int width = 1280, height = 720;
	private String title = "GLFW Window";
	
	private CONTEXT context;
	
	@Override
	public GLFWWindow<CONTEXT> generate()
	{
		this.pointer = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);
		return self();
	}

	@Override
	public GLFWWindow<CONTEXT> destroy() { GLFW.glfwDestroyWindow(this.pointer()); return self(); }

	@Override
	public GLFWWindow<CONTEXT> attachContext(CONTEXT context)
	{
		this.context = context;
		return self();
	}

	@Override
	public Long pointer() { return pointer; }

	
	
	@Override
	public GLFWWindow<CONTEXT> withWidth(int width) { this.width = width; return self(); }

	@Override
	public GLFWWindow<CONTEXT> withHeight(int height) { this.height = height; return self(); }

	@Override
	public GLFWWindow<CONTEXT> withTitle(String title) { this.title = title; return self(); }
	
	
	
	public boolean shouldClose() { return GLFW.glfwWindowShouldClose(pointer()); }

	@Override
	public GLFWWindow<CONTEXT> update() { GLFW.glfwPollEvents(); return self(); }
	
	
	public GLFWLifecycle<CONTEXT> createLifecycle(Runnable init, Runnable update, Runnable destroy)
	{
		return new GLFWLifecycle<CONTEXT>()
				{
					@Override
					public GLFWLifecycle<CONTEXT> init()
					{
						init.run();
						return self();
					}

					@Override
					public GLFWLifecycle<CONTEXT> update()
					{
						update.run();
						return self();
					}

					@Override
					public GLFWLifecycle<CONTEXT> destroy()
					{
						destroy.run();
						return self();
					}
				}
				.withWindow(self())
				.withContext(context);
	}
}
