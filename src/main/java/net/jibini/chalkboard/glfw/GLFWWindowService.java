package net.jibini.chalkboard.glfw;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.jibini.chalkboard.GraphicsContext;
import net.jibini.chalkboard.WindowService;

public class GLFWWindowService<CONTEXT extends GraphicsContext<?, ?, ?>>
		implements WindowService<CONTEXT, GLFWWindow<CONTEXT>, GLFWWindowService<CONTEXT>>
{
	private GLFWErrorCallback error = GLFWErrorCallback.createThrow();
	private CONTEXT context;
	
	public GLFWWindowService<CONTEXT> attachContext(CONTEXT context)
	{
		this.context = context;
		return this;
	}
	
	@Override
	public GLFWWindowService<CONTEXT> destroy()
	{
		GLFW.glfwTerminate();
		return this;
	}

	@Override
	public GLFWWindowService<CONTEXT> initialize()
	{
		GLFW.glfwSetErrorCallback(error);
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Failed to initialize GLFW");
		return this;
	}
	
	public GLFWWindowService<CONTEXT> withContextVersion(int version)
	{
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, version / 10);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, version % 10);
		return this;
	}

	@Override
	public String name()
	{
		return "GLFW";
	}

	@Override
	public String version()
	{
		return GLFW.glfwGetVersionString() + " [LWJGL " + Version.getVersion() + "]";
	}

	@Override
	public GLFWWindow<CONTEXT> createWindow()
	{
		return new GLFWWindow<CONTEXT>()
				.attachContext(context);
	}
}
