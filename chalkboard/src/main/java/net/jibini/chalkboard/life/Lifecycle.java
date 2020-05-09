package net.jibini.chalkboard.life;

public interface Lifecycle<THIS extends Lifecycle<THIS>>
		extends InitTasks<THIS>, UpdateTasks<THIS>, DestroyTasks<THIS>
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
