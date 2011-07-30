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
   	eye 0.0 -1.0 5.0
   	target 3.0 0.0 0.0
   	up 0.0 1.0 0.0
   	fov 60
   	aspect 1.333
}

include "shaders.sc"

shader {
	name default
   	type diffuse
   	diff 1 0.5 0.5
}

object {
   	shader default
   	type sphere
   	name sphere10
   	c 3 -2 -2
   	r 0.5
}

object {
   	shader default
   	type sphere
   	name sphere11
   	c 3 -2 0
   	r 0.5
}

object {
   	shader shader7
   	type sphere
   	name sphere12
   	c 3 -2 2
   	r 0.5
}

object {
   	shader default
   	type sphere
   	name sphere2
   	c 3 0 0
   	r 0.5
}

object {
   	shader default
   	type sphere
   	name sphere3
   	c 3 0 -2
   	r 0.5
}

object {
   	shader shader3
   	transform {
   		scaleu 0.5
   		rotatex 75
   		translate 3 0 2
   	}
   	type sphere
   	name sphere4
}

include "lights.sc"
