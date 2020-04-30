package net.jibini.chalkboard.lwjgl;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;

import net.jibini.chalkboard.GraphicsContext;
import net.jibini.chalkboard.glfw.GLFWWindowService;
import net.jibini.chalkboard.object.ContextVersioned;

public class GLContext implements GraphicsContext<GLPipeline, GLFWWindowService<GLContext>, GLContext>,
		ContextVersioned<GLContext>
{
	private int contextVersion = 33;
	private boolean core = false;
	private boolean forwardCompat = false;
	
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
	
	@Override
	public GLContext withContextVersion(int version)
	{
		this.contextVersion = version;
		return this;
	}

	@Override
	public GLContext enableGLCore()
	{
		this.core = true;
		return this;
	}

	@Override
	public GLContext enableGLForwardCompat()
	{
		this.forwardCompat = true;
		return this;
	}

	@Override
	public GLFWWindowService<GLContext> createWindowService()
	{
		GLFWWindowService<GLContext> w = new GLFWWindowService<GLContext>()
				.attachContext(this)
				.initializeOnce()
				
				.withContextVersion(contextVersion);
		if (core) w.enableGLCore();
		if (forwardCompat) w.enableGLForwardCompat();
		return w;
	}
}
