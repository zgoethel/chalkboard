package net.jibini.chalkboard.lwjgl.glfw;

import net.jibini.chalkboard.GraphicsPipeline;

public interface GLFWGraphicsPipeline
		<
			CONTEXT		extends GLFWGraphicsContext	<CONTEXT, PIPELINE>,
			PIPELINE	extends GLFWGraphicsPipeline<CONTEXT, PIPELINE>
		>
		extends GraphicsPipeline
		<
			CONTEXT,
			PIPELINE,
			GLFWWindowService	<CONTEXT, PIPELINE>,
			GLFWWindow			<CONTEXT, PIPELINE>
		>
{  }
