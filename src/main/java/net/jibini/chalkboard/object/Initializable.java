package net.jibini.chalkboard.object;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface Initializable<THIS extends Initializable<THIS>>
{
	static List<Class<?>> initialized = new CopyOnWriteArrayList<>();
	
	THIS initialize();
	
	
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
}
