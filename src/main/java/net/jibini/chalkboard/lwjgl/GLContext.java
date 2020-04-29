package net.jibini.chalkboard.lwjgl;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import net.jibini.chalkboard.GraphicsContext;
import net.jibini.chalkboard.glfw.GLFWWindowService;

public class GLContext implements GraphicsContext<GLPipeline, GLFWWindowService<GLContext>, GLContext>
{
	private long contextVersion = 33;
	
	@Override
	public GLContext generate()
	{
		GL.createCapabilities();
		return this;
	}

	@Override
	public GLContext destroy()
	{
		GL.destroy();
		return this;
	}

	@Override
	public String name()
	{
		return "OpenGL";
	}

	@Override
	public String version()
	{
		return "[Context " + contextVersion / 10 + "." + contextVersion % 10 + ", LWJGL " + Version.getVersion() + "]";
	}

	@Override
	public GLPipeline createPipeline()
	{
		return new GLPipeline();
	}

	@Override
	public GLFWWindowService<GLContext> createWindowService()
	{
		return new GLFWWindowService<GLContext>()
				.attachContext(this)
				.initializeOnce();
	}
}
