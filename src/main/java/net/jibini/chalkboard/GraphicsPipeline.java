package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Conversational;

public interface GraphicsPipeline
		<
			CONTEXT extends GraphicsContext<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			PIPELINE extends GraphicsPipeline<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			WINDOWSERV extends WindowService<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			WINDOW extends Window<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>
		>
		extends Conversational<PIPELINE>
{  }
