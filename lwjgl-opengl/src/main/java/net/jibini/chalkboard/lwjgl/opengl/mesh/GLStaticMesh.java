package net.jibini.chalkboard.lwjgl.opengl.mesh;

import net.jibini.chalkboard.signature.StaticMesh;

public abstract class GLStaticMesh<THIS extends GLStaticMesh<THIS>>
		implements StaticMesh<THIS>
{
	public abstract THIS renderCall();
}
