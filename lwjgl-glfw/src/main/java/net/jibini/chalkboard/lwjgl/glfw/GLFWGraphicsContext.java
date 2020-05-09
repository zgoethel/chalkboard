package net.jibini.chalkboard.lwjgl.glfw;

import net.jibini.chalkboard.GraphicsContext;
import net.jibini.chalkboard.object.Attachable;

public interface GLFWGraphicsContext
		<
			CONTEXT extends GLFWGraphicsContext<CONTEXT>
		>
		extends GraphicsContext
		<
			CONTEXT,
			GLFWWindowService<CONTEXT>,
			GLFWWindow<CONTEXT>
		>,
			Attachable<GLFWWindow<CONTEXT>, CONTEXT>
{
	CONTEXT prepareRender();
	
	CONTEXT swapBuffers();
}
