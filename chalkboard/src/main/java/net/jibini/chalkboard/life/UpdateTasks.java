package net.jibini.chalkboard.life;

import net.jibini.chalkboard.object.Conversational;

public interface UpdateTasks<THIS extends UpdateTasks<THIS>>
		extends Conversational<THIS>
{
	THIS preUpdate();
	
	THIS update();
	
	THIS postUpdate();
}
