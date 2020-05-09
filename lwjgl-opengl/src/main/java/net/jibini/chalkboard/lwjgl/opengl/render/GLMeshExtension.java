package net.jibini.chalkboard.lwjgl.opengl.render;

import net.jibini.chalkboard.object.Attachable;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;

public interface GLMeshExtension<THIS extends GLMeshExtension<THIS>>
		extends Attachable<GLStaticMesh, THIS>, Generatable<THIS>, Destroyable<THIS>
{
	THIS render();
}
