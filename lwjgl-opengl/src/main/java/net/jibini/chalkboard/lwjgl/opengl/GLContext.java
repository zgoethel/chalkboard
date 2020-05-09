package net.jibini.chalkboard.lwjgl.opengl;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;

import net.jibini.chalkboard.lwjgl.glfw.GLFWGraphicsContext;
import net.jibini.chalkboard.lwjgl.glfw.GLFWWindowService;
import net.jibini.chalkboard.lwjgl.opengl.render.GLRenderEngine;

public class GLContext implements GLFWGraphicsContext<GLContext>
{
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
	

	@SuppressWarnings("resource")
	@Override
	public GLFWWindowService<GLContext> createWindowService()
	{
		return new GLFWWindowService<GLContext>(self())
				.initializeOnce();
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
	public GLContext spawn() { return new GLContext(); }

	@Override
	public GLRenderEngine createRenderEngine() { return new GLRenderEngine(); }
}
