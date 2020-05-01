package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;
import net.jibini.chalkboard.object.Initializable;
import net.jibini.chalkboard.object.NameVersioned;

public interface GraphicsContext
		<
			CONTEXT		extends	GraphicsContext	<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			PIPELINE	extends GraphicsPipeline<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			WINDOWSERV	extends WindowService	<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			WINDOW		extends Window			<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>
		>
		extends Generatable<CONTEXT>, Destroyable<CONTEXT>, Initializable<CONTEXT>,
			NameVersioned, Conversational<CONTEXT>
{
	WINDOWSERV createWindowService();
	
	PIPELINE createPipeline();
}
