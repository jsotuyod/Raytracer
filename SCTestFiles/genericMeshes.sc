image {
   resolution 800 600
   aa 0 0
   samples 1
   contrast 0
   filter box
   jitter false
}

camera {
   type pinhole
   eye 0.0 0.0 5.0
   target 0.0 0.0 0.0
   up 0.0 1.0 0.0
   fov 60
   aspect 1.333
}

include "shaders.sc"

shader {
   name shaderName0
   type diffuse
   diff 1 0 0
}
/*
object { 
	shader shaderName0 
	transform {
		rotatex 15
	}
	type generic-mesh 
	name meshName
	points 4 
	   4 0 -1
	   4 0 1
	   6 0 0
	   5 1 0
	triangles 3 
	   0 1 3
	   1 2 3
	   0 2 3
	normals none 
	uvs facevarying
	   11 21 12 22 13 23
	   14 24 15 25 16 26
	   14 24 15 25 16 26
	face_shaders
	   0
	   1
	   2
}
*/

object { 
	shader shader3 
	type generic-mesh 
	name meshName
	points 4 
	   	0.5 -0.5 0.0
		0.5 0.5 0.0
		-0.5 0.5 0.0
		-0.5 -0.5 0.0
	triangles 2
	   1 2 3 
	   0 1 3
	normals none 
	uvs vertex
	  	1.0 0.0
		1.0 1.0
		0.0 1.0
		0.0 0.0
}

include "lights.sc"