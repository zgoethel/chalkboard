package net.jibini.chalkboard.life;

import net.jibini.chalkboard.object.Conversational;

public interface Lifecycle<THIS extends Lifecycle<THIS>>
		extends Conversational<THIS>,
			InitTasks<THIS>, UpdateTasks<THIS>, DestroyTasks<THIS>
{
	THIS initContext();
	
	boolean isAlive();

	default THIS start()
	{
		this.preInit()
				.initContext()
				.init()
				.postInit();
		
		while (this.isAlive())
		{
			this.preUpdate()
					.update()
					.postUpdate();
		}
		
		this.preDestroy()
				.destroy()
				.postDestroy();
		
		return self();
	}

	default Thread spawnThread()
	{
		Thread t = new Thread(this::start);
		t.start();
		return t;
	}
}
