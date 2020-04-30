package net.jibini.chalkboard.object;

public interface ContextVersioned<THIS extends ContextVersioned<?>>
{
	THIS withContextVersion(int version);
	
	THIS enableGLCore();
	
	THIS enableGLForwardCompat();
}
