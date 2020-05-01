package net.jibini.chalkboard.life;

import net.jibini.chalkboard.object.Conversational;

public interface InitTasks<THIS extends InitTasks<THIS>>
		extends Conversational<THIS>
{
	THIS preInit();
	
	THIS init();
	
	THIS postInit();
}
