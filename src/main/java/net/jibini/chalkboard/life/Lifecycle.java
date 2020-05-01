package net.jibini.chalkboard.life;

import net.jibini.chalkboard.object.Conversational;

// Replace with GLFW callbacks
@Deprecated
public interface Lifecycle<THIS extends Lifecycle<THIS>>
		extends Conversational<THIS>
{
	THIS initContext();
	
	THIS setup();
	
	
	THIS preUpdate();
	
	THIS update();
	
	THIS postUpdate();
	
	
	THIS destroy();
	
	THIS postDestroy();
	
	
	boolean isAlive();
	
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
		
		return self();
	}
	
	default Thread spawnThread()
	{
		Thread t = new Thread(this::start);
		t.start();
		return t;
	}
}
