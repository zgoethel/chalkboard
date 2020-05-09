package net.jibini.chalkboard.lwjgl.opengl;

import net.jibini.chalkboard.GraphicsPipeline;

public class GLPipeline implements GraphicsPipeline<GLPipeline>
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
