package net.jibini.chalkboard.object;

@FunctionalInterface
public interface Pointer<T extends Number>
{
	T pointer();
}
