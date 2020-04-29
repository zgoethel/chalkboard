package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;
import net.jibini.chalkboard.object.NameVersioned;

public interface GraphicsContext
		<
			PIPELINE extends GraphicsPipeline<?>,
			WINDOW extends WindowService<?, ?, ?>,
			THIS extends GraphicsContext<?, ?, ?>
		>
		extends Generatable<THIS>, Destroyable<THIS>, NameVersioned
{
	 PIPELINE createPipeline();
	 
	 WINDOW createWindowService();
}
