package net.jibini.chalkboard.lwjgl.vulkan.system;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import net.jibini.chalkboard.object.Conversational;

public abstract class VkSys<THIS extends VkSys<THIS>>
		implements Conversational<THIS>
{
	public final MemoryStack stack;
	
	public final PointerBuffer pointerParam;
	public final IntBuffer intParam;
	public final LongBuffer longParam;
	
	public VkSys(MemoryStack stack)
	{
		this.stack = stack;
		
		this.pointerParam = stack.mallocPointer(1);
		this.intParam = stack.mallocInt(1);
		this.longParam = stack.mallocLong(1);
	}
	
	public VkSys()
	{
		this(MemoryStack.create());
	}
	
	public THIS check(int errcode)
	{
		if (errcode != 0)
			throw new IllegalStateException(String.format("Vulkan error [0x%X]", errcode));
		return self();
	}
}
