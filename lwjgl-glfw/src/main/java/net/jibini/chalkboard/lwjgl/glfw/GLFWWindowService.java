package net.jibini.chalkboard.lwjgl.glfw;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.jibini.chalkboard.WindowService;

public class GLFWWindowService
		<
			CONTEXT extends GLFWGraphicsContext<CONTEXT>
		>
		implements WindowService
		<
			CONTEXT,
			GLFWWindowService<CONTEXT>,
			GLFWWindow<CONTEXT>
		>
{
	private GLFWErrorCallback error = GLFWErrorCallback.createThrow();
	private CONTEXT context;
	
	public GLFWWindowService<CONTEXT> attachContext(CONTEXT context)
	{ this.context = context; return self(); }
	
	@Override
	public GLFWWindowService<CONTEXT> destroy() { GLFW.glfwTerminate(); return self(); }
	

	@Override
	public GLFWWindowService<CONTEXT> initialize()
	{
		GLFW.glfwSetErrorCallback(error);
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Failed to initialize GLFW");
		return self();
	}
	
//	@Override 
//	public GLFWWindowService<CONTEXT, PIPELINE> withContextVersion(int version)
//	{
//		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, version / 10);
//		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, version % 10);
//		return self();
//	}

	@Override
	public String name() { return "GLFW"; }

	@Override
	public String version() { return GLFW.glfwGetVersionString() + " [LWJGL " + Version.getVersion() + "]"; }
	

	@SuppressWarnings("resource")
	@Override
	public GLFWWindow<CONTEXT> createWindow()
	{
		return new GLFWWindow<CONTEXT>()
				.attachContext(context);
	}
	
	public GLFWWindowService<CONTEXT> hint(int hint, int value)
	{ GLFW.glfwWindowHint(hint, value); return self(); }

//	@Override
//	public GLFWWindowService<CONTEXT, PIPELINE> enableGLCore()
//	{ GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); return self(); }
//
//	@Override
//	public GLFWWindowService<CONTEXT, PIPELINE> enableGLForwardCompat()
//	{ GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE); return self(); }
//	
//	public GLFWWindowService<CONTEXT, PIPELINE> withNoAPI()
//	{ GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API); return self(); }
}
