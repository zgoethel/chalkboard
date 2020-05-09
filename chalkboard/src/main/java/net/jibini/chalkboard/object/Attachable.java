package net.jibini.chalkboard.object;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Attachable<T, THIS extends Attachable<T, THIS>>
	extends Conversational<THIS>
{
	static Map<Object, Object> attachments = new ConcurrentHashMap<>();
	
	default THIS attach(T attachment)
	{
		attachments.put(this, attachment);
		return self();
	}
	
	@SuppressWarnings("unchecked")
	default T attachment() { return (T)attachments.get(this); }
}
