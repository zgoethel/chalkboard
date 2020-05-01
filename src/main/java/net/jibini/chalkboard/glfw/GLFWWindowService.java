package net.jibini.chalkboard.glfw;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.jibini.chalkboard.WindowService;

public class GLFWWindowService
		<
			CONTEXT		extends GLFWGraphicsContext	<CONTEXT, PIPELINE>,
			PIPELINE	extends GLFWGraphicsPipeline<CONTEXT, PIPELINE>
		>
		implements WindowService
		<
			CONTEXT,
			PIPELINE,
			GLFWWindowService	<CONTEXT, PIPELINE>,
			GLFWWindow			<CONTEXT, PIPELINE>
		>
{
	private GLFWErrorCallback error = GLFWErrorCallback.createThrow();
	private CONTEXT context;
	
	public GLFWWindowService<CONTEXT, PIPELINE> attachContext(CONTEXT context)
	{ this.context = context; return self(); }
	
	@Override
	public GLFWWindowService<CONTEXT, PIPELINE> destroy() { GLFW.glfwTerminate(); return self(); }
	

	@Override
	public GLFWWindowService<CONTEXT, PIPELINE> initialize()
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
	

	@Override
	public GLFWWindow<CONTEXT, PIPELINE> createWindow()
	{
		return new GLFWWindow<CONTEXT, PIPELINE>()
				.attachContext(context);
	}
	
	public GLFWWindowService<CONTEXT, PIPELINE> hint(int hint, int value)
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
