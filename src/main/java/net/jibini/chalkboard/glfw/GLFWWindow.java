package net.jibini.chalkboard.glfw;

import org.lwjgl.glfw.GLFW;

import net.jibini.chalkboard.Window;
import net.jibini.chalkboard.object.Pointer;

public class GLFWWindow
		<
			CONTEXT		extends GLFWGraphicsContext	<CONTEXT, PIPELINE>,
			PIPELINE	extends GLFWGraphicsPipeline<CONTEXT, PIPELINE>
		>
		implements Window
		<
			CONTEXT,
			PIPELINE,
			GLFWWindowService	<CONTEXT, PIPELINE>,
			GLFWWindow			<CONTEXT, PIPELINE>
		>,
			Pointer<Long>
{
	private long pointer = 0L;
	private int width = 1280, height = 720;
	private String title = "GLFW Window";
	
	private CONTEXT context;
	
	@Override
	public GLFWWindow<CONTEXT, PIPELINE> generate()
	{
		this.pointer = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);
		return self();
	}

	@Override
	public GLFWWindow<CONTEXT, PIPELINE> destroy() { GLFW.glfwDestroyWindow(this.pointer()); return self(); }

	@Override
	public GLFWWindow<CONTEXT, PIPELINE> attachContext(CONTEXT context)
	{
		this.context = context;
		return self();
	}

	@Override
	public Long pointer() { return pointer; }

	
	
	@Override
	public GLFWWindow<CONTEXT, PIPELINE> withWidth(int width) { this.width = width; return self(); }

	@Override
	public GLFWWindow<CONTEXT, PIPELINE> withHeight(int height) { this.height = height; return self(); }

	@Override
	public GLFWWindow<CONTEXT, PIPELINE> withTitle(String title) { this.title = title; return self(); }
	
	
	
	public boolean shouldClose() { return GLFW.glfwWindowShouldClose(pointer()); }

	@Override
	public GLFWWindow<CONTEXT, PIPELINE> update() { GLFW.glfwPollEvents(); return self(); }
	
	
	public GLFWLifecycle<CONTEXT, PIPELINE> createLifecycle(Runnable init, Runnable update, Runnable destroy)
	{
		return new GLFWLifecycle<CONTEXT, PIPELINE>()
				{
					@Override
					public GLFWLifecycle<CONTEXT, PIPELINE> init()
					{
						init.run();
						return self();
					}

					@Override
					public GLFWLifecycle<CONTEXT, PIPELINE> update()
					{
						update.run();
						return self();
					}

					@Override
					public GLFWLifecycle<CONTEXT, PIPELINE> destroy()
					{
						destroy.run();
						return self();
					}
				}
				.withWindow(self())
				.withContext(context);
	}
}
