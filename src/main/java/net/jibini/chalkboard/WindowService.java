package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Initializable;
import net.jibini.chalkboard.object.NameVersioned;

public interface WindowService
		<
			CONTEXT extends GraphicsContext<?, ?, ?>,
			WINDOW extends Window<?, ?>,
			THIS extends WindowService<?, ?, ?>
		>
		extends Destroyable<THIS>, Initializable<THIS>, NameVersioned
{
	WINDOW createWindow();
}
