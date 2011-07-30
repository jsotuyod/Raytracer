include "shaders.sc"
include "lights.sc"

image {
   resolution 320 240
    aa 0 0
    samples 1
    contrast 0
    filter box
    jitter false
}

camera {
   	type pinhole
   	eye 0.0 0.0 4.0
   	target 3.0 0.0 0.0
   	up 0.0 1.0 0.0
   	fov 60
   	aspect 1.333
}


object {
   	shader shader1
   	transform {
   		translate 4 0 0
   	}
   	type box
   	name sphere1
}

object {
   	shader shader1
   	transform {
   		scaleu 0.5
   		translate 3 0 0
   	}
   	type box
   	name sphere2
}

object {
   	shader shader1
   	transform {
   		scaleu 0.5
   		translate 3 -2 -1
   	}
   	type box
   	name sphere3
}

object {
   	shader shader1
   	transform {
   		scaleu 2
   		translate 7 0 0
   	}
   	type box
   	name sphere4
}

