package net.jibini.chalkboard.object;

@FunctionalInterface
public interface Generatable<THIS extends Generatable<THIS>>
{
	THIS generate();
}
