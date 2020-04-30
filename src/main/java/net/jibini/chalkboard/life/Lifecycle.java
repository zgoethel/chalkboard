package net.jibini.chalkboard.life;

public interface Lifecycle<THIS extends Lifecycle<?>>
{
	THIS initContext();
	
	THIS setup();
	
	
	THIS preUpdate();
	
	THIS update();
	
	THIS postUpdate();
	
	
	THIS destroy();
	
	THIS postDestroy();
	
	
	boolean isAlive();
	
	@SuppressWarnings("unchecked")
	default THIS start()
	{
		this.initContext();
		this.setup();
		
		while (isAlive())
		{
			this.preUpdate();
			this.update();
			this.postUpdate();
		}
		
		this.destroy();
		this.postDestroy();
		
		return (THIS)this;
	}
	
	default Thread spawnThread()
	{
		Thread t = new Thread(this::start);
		t.start();
		return t;
	}
}
