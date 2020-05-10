package net.jibini.chalkboard.render;

import net.jibini.chalkboard.object.Conversational;
import net.jibini.chalkboard.object.Destroyable;
import net.jibini.chalkboard.object.Generatable;

public interface StaticMesh<THIS extends StaticMesh<THIS>>
		extends Conversational<THIS>, Generatable<THIS>, Destroyable<THIS>
{
	static final int DEFAULT_COLOR_ATTRIB = -1;
	static final int DEFAULT_TEX_COORD_ATTRIB = -2;
	static final int DEFAULT_NORMAL_ATTRIB = -3;
	
	THIS appendVertices(float ... vertices);
	
	THIS interleave(int uniform, int length, float ... values);
	
	
	THIS queue();
}
