package net.jibini.chalkboard.lwjgl;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;

import net.jibini.chalkboard.GraphicsContext;
import net.jibini.chalkboard.glfw.GLFWWindowService;

public class GLContext implements GraphicsContext<GLPipeline, GLFWWindowService<GLContext>, GLContext>
{
	private int contextVersion = 33;
	
	@Override
	public GLContext generate()
	{
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback();
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
		return GL11.glGetString(GL11.GL_VERSION) + " [Context " + contextVersion / 10 + "." + contextVersion % 10
				+ ", LWJGL " + Version.getVersion() + "]";
	}

	@Override
	public GLPipeline createPipeline()
	{
		return new GLPipeline();
	}
	
	public GLContext withContextVersion(int version)
	{
		this.contextVersion = version;
		return this;
	}

	@Override
	public GLFWWindowService<GLContext> createWindowService()
	{
		return new GLFWWindowService<GLContext>()
				.attachContext(this)
				.initializeOnce()
				.withContextVersion(contextVersion);
	}
}
