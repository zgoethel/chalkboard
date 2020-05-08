package net.jibini.chalkboard.render;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.signature.StaticMesh;

public interface RenderEngine
		<
			THIS extends RenderEngine<THIS, MESH>,
			MESH extends StaticMesh<MESH>
		>
		extends Conversational<THIS>
{
	MESH createStaticMesh();
	
	
	THIS prepareMesh(MESH mesh);
	
	THIS render();
}
