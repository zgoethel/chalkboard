package net.jibini.chalkboard.lwjgl.opengl;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;

import net.jibini.chalkboard.lwjgl.glfw.GLFWGraphicsContext;
import net.jibini.chalkboard.lwjgl.glfw.GLFWWindowService;
import net.jibini.chalkboard.lwjgl.opengl.render.GLRenderEngine;
import net.jibini.chalkboard.render.RenderEngine;

public class GLContext implements GLFWGraphicsContext<GLContext>
{
//	private int contextVersion = 10;
//	private boolean core = false;
//	private boolean forwardCompat = false;
	
	public static int contextVersion()
	{
		int version = 10;
		String versionString = GL11.glGetString(GL11.GL_VERSION);
		int middleIndex = versionString.indexOf('.');
		
		version *= Integer.valueOf(versionString.substring(middleIndex - 1, middleIndex));
		version += Integer.valueOf(versionString.substring(middleIndex + 1, middleIndex + 2));
		return version;
	}
	
	@Override
	public GLContext generate()
	{
		GLFW.glfwMakeContextCurrent(attachment().pointer());
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback(); // Validation only
		
//		if (contextVersion == 10)
//		{
//			String versionString = GL11.glGetString(GL11.GL_VERSION);
//			int middleIndex = versionString.indexOf('.');
//			
//			contextVersion *= Integer.valueOf(versionString.substring(middleIndex - 1, middleIndex));
//			contextVersion += Integer.valueOf(versionString.substring(middleIndex + 1, middleIndex + 2));
//		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CCW);
		
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
		return GL11.glGetString(GL11.GL_VERSION) + " [LWJGL " + Version.getVersion() + "]";
	}

	@Override
	public GLPipeline createPipeline() { return new GLPipeline(); }
	
	
//	public int contextVersion() { return contextVersion; }
//	
//	public GLContext withContextVersion(int version) { this.contextVersion = version; return self(); }
//
//	public GLContext enableGLCore() { this.core = true; return self(); }
//
//	public GLContext enableGLForwardCompat() { this.forwardCompat = true; return self(); }
	

	@SuppressWarnings("resource")
	@Override
	public GLFWWindowService<GLContext> createWindowService()
	{
		GLFWWindowService<GLContext> w = new GLFWWindowService<GLContext>(this)
				.initializeOnce();
				
//				.hint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, contextVersion / 10)
//				.hint(GLFW.GLFW_CONTEXT_VERSION_MINOR, contextVersion % 10);
//		if (core) w.hint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
//		if (forwardCompat) w.hint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		return w;
	}

	@Override
	public GLContext prepareRender()
	{
		GLFW.glfwMakeContextCurrent(attachment().pointer());
		GLFW.glfwSwapInterval(0);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		return self();
	}

	@Override
	public GLContext swapBuffers()
	{ GLFW.glfwSwapBuffers(attachment().pointer()); return self(); }

	
	@Override
	public GLContext spawn()
	{
		GLContext spawned = new GLContext();
//				.withContextVersion(contextVersion);
//		if (core) spawned.enableGLCore();
//		if (forwardCompat) spawned.enableGLForwardCompat();
		return spawned;
	}

	@Override
	public RenderEngine<?, ?> createRenderEngine() { return new GLRenderEngine(); }
}
