package net.jibini.chalkboard.glfw;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.jibini.chalkboard.WindowService;
import net.jibini.chalkboard.object.ContextVersioned;

public class GLFWWindowService
		<
			CONTEXT extends GLFWGraphicsContext<?, ? extends GLFWWindowService<?>, ?>
		>
		implements WindowService<CONTEXT, GLFWWindow<CONTEXT>, GLFWWindowService<CONTEXT>>,
				ContextVersioned<GLFWWindowService<CONTEXT>>
{
	private GLFWErrorCallback error = GLFWErrorCallback.createThrow();
	private CONTEXT context;
	
	public GLFWWindowService<CONTEXT> attachContext(CONTEXT context)
	{ this.context = context; return this; }
	
	@Override
	public GLFWWindowService<CONTEXT> destroy() { GLFW.glfwTerminate(); return this; }
	

	@Override
	public GLFWWindowService<CONTEXT> initialize()
	{
		GLFW.glfwSetErrorCallback(error);
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Failed to initialize GLFW");
		return this;
	}
	
	@Override 
	public GLFWWindowService<CONTEXT> withContextVersion(int version)
	{
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, version / 10);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, version % 10);
		return this;
	}

	@Override
	public String name() { return "GLFW"; }

	@Override
	public String version() { return GLFW.glfwGetVersionString() + " [LWJGL " + Version.getVersion() + "]"; }
	

	@Override
	public GLFWWindow<CONTEXT> createWindow()
	{
		return new GLFWWindow<CONTEXT>()
				.attachContext(context);
	}

	@Override
	public GLFWWindowService<CONTEXT> enableGLCore()
	{ GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); return this; }

	@Override
	public GLFWWindowService<CONTEXT> enableGLForwardCompat()
	{ GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE); return this; }
	
	public GLFWWindowService<CONTEXT> withNoAPI()
	{ GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API); return this; }
}
