package net.jibini.chalkboard.lwjgl.opengl;

import net.jibini.chalkboard.lwjgl.glfw.GLFWGraphicsPipeline;

public class GLPipeline implements GLFWGraphicsPipeline<GLPipeline>
{
	@Override
	public GLPipeline destroy()
	{
		
		return self();
	}

	@Override
	public GLPipeline generate()
	{
		
		return self();
	}
}
