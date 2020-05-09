package net.jibini.chalkboard.render;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;

public interface RenderEngine
		<
			MESH extends StaticMesh<?>,
			THIS extends RenderEngine<?, THIS>
		>
		extends Conversational<THIS>, Destroyable<THIS>
{
	MESH createStaticMesh();
	
	
	THIS queue(MESH mesh);
	
	THIS render();
}
