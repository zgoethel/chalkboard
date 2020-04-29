package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;

public interface Window<CONTEXT extends GraphicsContext<?, ?, ?>, THIS extends Window<?, ?>>
		extends Generatable<THIS>, Destroyable<THIS>
{
	THIS withWidth(int width);
	
	THIS withHeight(int height);
	
	THIS withTitle(String title);
	
	
	THIS attachContext(CONTEXT context);
	
	THIS prepareRender();
	
	THIS swapBuffers();
}
