package net.jibini.chalkboard.render;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;

public interface StaticMesh<THIS extends StaticMesh<THIS>>
		extends Conversational<THIS>, Generatable<THIS>, Destroyable<THIS>
{
	THIS appendVertex(float x, float y, float z);
	
	THIS appendVertices(float ... vertices);
	
	THIS assignUniforms(int uniform, float[] uniforms);
	
	THIS breakSection();
	
	
	THIS queue();
}
