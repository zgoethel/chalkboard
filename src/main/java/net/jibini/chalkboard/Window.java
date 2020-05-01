package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;

public interface Window
		<
			CONTEXT		extends GraphicsContext	<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			PIPELINE	extends GraphicsPipeline<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			WINDOWSERV	extends WindowService	<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>,
			WINDOW		extends Window			<CONTEXT, PIPELINE, WINDOWSERV, WINDOW>
		>
		extends Generatable<WINDOW>, Destroyable<WINDOW>, Conversational<WINDOW>
{
	WINDOW withWidth(int width);
	
	WINDOW withHeight(int height);
	
	WINDOW withTitle(String title);
	
	
	WINDOW attachContext(CONTEXT context);
	
	WINDOW update();
}
