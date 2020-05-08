package net.jibini.chalkboard.lwjgl.glfw;

import net.jibini.chalkboard.GraphicsContext;

public interface GLFWGraphicsContext
		<
			CONTEXT extends GLFWGraphicsContext<CONTEXT>
		>
		extends GraphicsContext
		<
			CONTEXT,
			GLFWWindowService<CONTEXT>,
			GLFWWindow<CONTEXT>
		>
{
	CONTEXT attachWindow(GLFWWindow<CONTEXT> window);
	
	
//	CONTEXT makeCurrent();
	
	CONTEXT prepareRender();
	
	CONTEXT swapBuffers();
}
