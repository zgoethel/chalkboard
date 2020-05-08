package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Initializable;
import net.jibini.chalkboard.object.NameVersioned;

public interface WindowService
		<
			CONTEXT		extends GraphicsContext	<CONTEXT, WINDOWSERV, WINDOW>,
			WINDOWSERV	extends WindowService	<CONTEXT, WINDOWSERV, WINDOW>,
			WINDOW		extends Window			<CONTEXT, WINDOWSERV, WINDOW>
		>
		extends Destroyable<WINDOWSERV>, Initializable<WINDOWSERV>,
			NameVersioned, Conversational<WINDOWSERV>
{
	WINDOW createWindow();
	
	WINDOWSERV attachContext(CONTEXT context);
}
