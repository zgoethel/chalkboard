package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;
import net.jibini.chalkboard.object.Initializable;
import net.jibini.chalkboard.object.NameVersioned;
import net.jibini.chalkboard.object.Spawnable;
import net.jibini.chalkboard.render.RenderEngine;
import net.jibini.chalkboard.shading.Pipeline;

public interface GraphicsContext
		<
			CONTEXT		extends	GraphicsContext	<CONTEXT, WINDOWSERV, WINDOW>,
			WINDOWSERV	extends WindowService	<CONTEXT, WINDOWSERV, WINDOW>,
			WINDOW		extends Window			<CONTEXT, WINDOWSERV, WINDOW>
		>
		extends Generatable<CONTEXT>, Destroyable<CONTEXT>, Initializable<CONTEXT>,
			NameVersioned, Conversational<CONTEXT>, Spawnable<CONTEXT>
{
	WINDOWSERV createWindowService();
	
	
	Pipeline<?> createPipeline();
	
	RenderEngine<?, ?> createRenderEngine();
}
