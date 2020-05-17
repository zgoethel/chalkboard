package net.jibini.chalkboard.shading;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;
import net.jibini.chalkboard.resource.BinaryResource;

public interface Pipeline<THIS extends Pipeline<THIS>>
		extends Conversational<THIS>, Destroyable<THIS>, Generatable<THIS>
{
	THIS attachShader(ShaderType type, BinaryResource glsl, BinaryResource spirv);
	
	THIS use();
}
