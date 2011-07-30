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
   	eye 0.0 0.0 0.0
   	target 10.0 0.0 0.0
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
	name glass
   	type glass
   	eta 1.3
    color { "sRGB nonlinear" 1 1 1 }
    absorbtion.distance 0
    absorbtion.color { "sRGB nonlinear" 0.5 0.5 0.5 } 
}

shader {
   name red
   type diffuse
   diff 1 0 0
}

shader {
   name shi.shader
   type shiny
   diff { "sRGB nonlinear" 0.800 0.800 0.800 }
   refl 0.5
}

object {
   	shader glass
   	type sphere
   	name sphere10
   	c 5 0 0
   	r 1
}

object {
   	shader shader1
   	type sphere
   	name sphere11
   	c 10 0 1
   	r 1
}

object {
   	shader shi.shader
   	type sphere
   	name sphere14
   	c 10 2 2
   	r 2
}

object {
   	shader red
   	type plane
   	name plane1
   	p 15 0 0
   	n -1 0 0
}

include "lights.sc"