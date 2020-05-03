package net.jibini.chalkboard.life;

import net.jibini.chalkboard.object.Conversational;

public interface DestroyTasks<THIS extends DestroyTasks<THIS>>
		extends Conversational<THIS>
{
	THIS preDestroy();
	
	THIS destroy();
	
	THIS postDestroy();
}
