package net.jibini.chalkboard.object;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface Initializable<THIS extends Initializable<?>>
{
	static List<Class<?>> initialized = new CopyOnWriteArrayList<>();
	
	@SuppressWarnings("unchecked")
	default THIS initializeOnce()
	{
		if (!initialized.contains(this.getClass()))
		{
			initialized.add(this.getClass());
			return initialize();
		} else
			return (THIS)this;
	}
	
	THIS initialize();
}
