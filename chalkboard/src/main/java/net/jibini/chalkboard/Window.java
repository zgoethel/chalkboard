package net.jibini.chalkboard;

import net.jibini.chalkboard.life.Lifecycle;
import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;

public interface Window
		<
			CONTEXT		extends GraphicsContext	<CONTEXT, WINDOWSERV, WINDOW>,
			WINDOWSERV	extends WindowService	<CONTEXT, WINDOWSERV, WINDOW>,
			WINDOW		extends Window			<CONTEXT, WINDOWSERV, WINDOW>
		>
		extends Generatable<WINDOW>, Destroyable<WINDOW>, Conversational<WINDOW>
{
	WINDOW withWidth(int width);
	
	WINDOW withHeight(int height);
	
	WINDOW withTitle(String title);
	
	
	WINDOW update();
	
	
	Lifecycle<?> createLifecycle(Runnable init, Runnable update, Runnable destroy);
}
