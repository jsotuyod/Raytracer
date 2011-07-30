image {
   resolution 640 480
   aa 0 2
   samples 4
   contrast 0.1
   filter gaussian
   jitter false
}

camera {
	type pinhole
   	eye 0 5 5
   	target 10 0 0
   	up 0 1 0
   	fov 45 
   	aspect 1.333
}

shader {
	name shader1
   	type diffuse
   	diff 1 0 0
}

shader {
	name shader2
   	type diffuse
   	diff 0 1 0
}

shader {
	name shader3
   	type diffuse
   	diff 0 0 1
}

shader {
	name shader_texture
   	type diffuse
   	texture "textures/texture2.jpg"
}
object {
   shader shader_texture
   type plane
   p  10 5 0
   n  0 0 1
}


object {
   shader shader_texture
   type plane
   p  10 0 0
   n  -1 0 0
}

object {
   shader shader_texture
   type plane
   p 0 0 0
   n 0 1 0
}

include "lights.sc"