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
   	eye 0.0 5.0 0.0
   	target 15.0 2.5 0.0
   	up 0.0 1.0 0.0
   	fov 60
   	aspect 1.333
}

shader {
	name green
   	type diffuse
   	diff 0 1 0
}

shader {
	name red
   	type diffuse
   	diff 1 0 0
}

object {
	shader green
	type sphere
	name pelota
	c 15.0 5.0 0.0
	r 1.0
}

object {
	shader red
	type plane
	name plano
	p 0 0 0
	n 0 1.0 0
}

light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 300.0
   p 15.0 10.0 0.0
}
