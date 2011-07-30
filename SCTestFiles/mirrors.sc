image {
   resolution 800 600
   aa 0 0
   samples 5
   contrast 0
   filter box
   jitter false
}

camera {
   type pinhole
   eye -4.0 4.0 0.0
   target 10.0 0.0 0.0
   up 0.0 1.0 0.0
   fov 60
   aspect 1.333
}

include "shaders.sc"

light {
	type point
	color { "sRGB nonlinear" 1.000 1.000 1.000 }
	power 100.0
	p 10.0 3.0 0
}

light {
	type point
	color { "sRGB nonlinear" 1.000 1.000 1.000 }
	power 100.0
	p 0.0 3.0 0
}

light {
	type point
	color { "sRGB nonlinear" 1.000 1.000 1.000 }
	power 100.0
	p 6.0 3.0 0
}

light {
	type point
	color { "sRGB nonlinear" 1.000 1.000 1.000 }
	power 100.0
	p 12.0 3.0 0
}

light {
	type point
	color { "sRGB nonlinear" 1.000 1.000 1.000 }
	power 100.0
	p 18.0 3.0 0
}

shader {
   name red
   type diffuse
   diff 1 0 0
}

shader {
   name green
   type diffuse
   diff 0 1 0
}

shader {
   name blue
   type diffuse
   diff 0 0 1
}

object {
	shader shader7
	type plane
	p 0 0 -5
	n 0 0 1
}

object {
	shader shader7
	type plane
	p 0 0 5
	n 0 0 -1
}

object {
	shader shader3
	type plane
	p 0 -1 0
	n 0 1 0
}

object {
	shader red
	type sphere
	c 10 0 0
	r 1
}

object {
	shader red
	type sphere
	c 7 0 2
	r 0.4
}

object {
	shader red
	type sphere
	c 14 0 -4
	r 1.3
}

object {
	shader green
	type sphere
	c 16 0 -2
	r 1
}

object {
	shader green
	type sphere
	c 5 0 -1
	r 0.4
}

object {
	shader green
	type sphere
	c 12 0 -2
	r 1.3
}

object {
	shader blue
	type sphere
	c 4 0 -3
	r 1
}

object {
	shader blue
	type sphere
	c 9 0 4
	r 0.4
}

object {
	shader blue
	type sphere
	c 8 0 1
	r 1.3
}