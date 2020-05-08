package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;

public interface GraphicsPipeline<THIS extends GraphicsPipeline<THIS>>
		extends Conversational<THIS>, Destroyable<THIS>, Generatable<THIS>
{  }
