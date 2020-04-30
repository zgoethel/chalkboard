package net.jibini.chalkboard.glfw;

import net.jibini.chalkboard.GraphicsContext;
import net.jibini.chalkboard.GraphicsPipeline;

public interface GLFWGraphicsContext
		<
			PIPELINE extends GraphicsPipeline<?>,
			WINDOW extends GLFWWindowService<?>,
			THIS extends GLFWGraphicsContext<?, ? extends GLFWWindowService<?>, THIS>
		>
		extends GraphicsContext<PIPELINE, WINDOW, THIS>
{
	THIS makeCurrent(GLFWWindow<?> window);
	
	THIS prepareRender(GLFWWindow<?> window);
	
	THIS swapBuffers(GLFWWindow<?> window);
}
