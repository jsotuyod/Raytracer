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
   	eye 0 3 3
   	target 5 0 0
   	up 0 1 0
   	fov 45 
   	aspect 1.333
}

shader {
	name default
   	type diffuse
   	diff 1 0 0
}

object {
  	shader default
	transform {
      	translate 5 0 0
      	rotatex 30
      	scaleu 2
   	}
  	type box
  	name boxName1
}

include "lights.sc"