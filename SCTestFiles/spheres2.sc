image {
   resolution 800 600
   aa 0 0
   samples 32
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
   name test1
   type mirror
   refl { "sRGB nonlinear" 1 1 1 }
}

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
   	shader test1
   	type sphere
   	name sphere11
   	c 3 -2 0
   	r 0.5
}

object {
   	shader default
   	type sphere
   	name sphere12
   	c 3 -2 2
   	r 0.5
}

object {
   	shader test1
   	type sphere
   	name sphere2
   	c 3 0 0
   	r 0.5
}

shader {
   name test2
   type glass
   eta 1.0
   color { "sRGB nonlinear" 0.800 0.800 0.800 }
   absorbtion.distance 5.0
   absorbtion.color { "sRGB nonlinear" 1.0 1.0 1.0 }
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
   	type sphere
   	name sphere4
   	c 3 0 2
   	r 0.5
}

include "lights2.sc"
