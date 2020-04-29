package net.jibini.chalkboard;

import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;

public interface WindowContext<THIS extends WindowContext<?>>
	extends Generatable<THIS>, Destroyable<THIS>
{

}
