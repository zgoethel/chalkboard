package net.jibini.chalkboard.object;

public interface Conversational<THIS extends Conversational<THIS>>
{
	@SuppressWarnings("unchecked")
	default THIS self() { return (THIS)this; }
}
