package net.jibini.chalkboard.glfw;

import net.jibini.chalkboard.GraphicsContext;

public interface GLFWGraphicsContext
		<
			CONTEXT extends GLFWGraphicsContext<CONTEXT, PIPELINE>,
			PIPELINE extends GLFWGraphicsPipeline<CONTEXT, PIPELINE>
		>
		extends GraphicsContext
		<
			CONTEXT,
			PIPELINE,
			GLFWWindowService<CONTEXT, PIPELINE>,
			GLFWWindow<CONTEXT, PIPELINE>
		>
{
	CONTEXT makeCurrent(GLFWWindow<CONTEXT, PIPELINE> window);
	
	CONTEXT prepareRender(GLFWWindow<CONTEXT, PIPELINE> window);
	
	CONTEXT swapBuffers(GLFWWindow<CONTEXT, PIPELINE> window);
}
