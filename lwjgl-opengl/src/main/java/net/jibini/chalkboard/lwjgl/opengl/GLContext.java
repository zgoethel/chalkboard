package net.jibini.chalkboard.lwjgl.opengl;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;

import net.jibini.chalkboard.lwjgl.glfw.GLFWGraphicsContext;
import net.jibini.chalkboard.lwjgl.glfw.GLFWWindow;
import net.jibini.chalkboard.lwjgl.glfw.GLFWWindowService;

public class GLContext implements GLFWGraphicsContext
		<
			GLContext,
			GLPipeline
		>
{
	private int contextVersion = 30;
	private boolean core = false;
	private boolean forwardCompat = false;
	
	private GLFWWindow<GLContext, GLPipeline>	window;
	
	public GLContext attachWindow(GLFWWindow<GLContext, GLPipeline> window)
	{
		this.window = window;
		return self();
	}
	
	@Override
	public GLContext generate()
	{
		GLFW.glfwMakeContextCurrent(window.pointer());
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback();
		return self();
	}

	@Override
	public GLContext initialize() { return self(); }

	@Override
	public GLContext destroy() { GL.destroy(); return self(); }

	
	@Override
	public String name() { return "OpenGL"; }

	@Override
	public String version()
	{
		return GL11.glGetString(GL11.GL_VERSION) + " [Context " + contextVersion / 10 + "." + contextVersion % 10
				+ ", LWJGL " + Version.getVersion() + "]";
	}

	@Override
	public GLPipeline createPipeline() { return new GLPipeline(); }
	
	
	public GLContext withContextVersion(int version) { this.contextVersion = version; return self(); }

	public GLContext enableGLCore() { this.core = true; return self(); }

	public GLContext enableGLForwardCompat() { this.forwardCompat = true; return self(); }
	

	@SuppressWarnings("resource")
	@Override
	public GLFWWindowService<GLContext, GLPipeline> createWindowService()
	{
		GLFWWindowService<GLContext, GLPipeline> w = new GLFWWindowService<GLContext, GLPipeline>()
				.attachContext(this)
				.initializeOnce()
				
				.hint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, contextVersion / 10)
				.hint(GLFW.GLFW_CONTEXT_VERSION_MINOR, contextVersion % 10);
		if (core) w.hint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		if (forwardCompat) w.hint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		return w;
	}

	@Override
	public GLContext prepareRender()
	{
		GLFW.glfwMakeContextCurrent(window.pointer());
		GLFW.glfwSwapInterval(0);
		return self();
	}

	@Override
	public GLContext swapBuffers()
	{ GLFW.glfwSwapBuffers(window.pointer()); return self(); }

	
	@SuppressWarnings("resource")
	@Override
	public GLContext spawn()
	{
		GLContext spawned = new GLContext()
				.withContextVersion(contextVersion);
		if (core) spawned.enableGLCore();
		if (forwardCompat) spawned.enableGLForwardCompat();
		return spawned;
	}
}
