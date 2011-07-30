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
   eye 0.0 0.0 0.0
   target 3.0 0.0 0.0
   up 0.0 1.0 0.0
   fov 60
   aspect 1.333
}

shader {
   name default
   type diffuse
   diff 1 .5 .5
}

object {
	shader default
	transform {
      	translate 3.0 0.0 0.0
      	scaleu 2
   	}
   	type sphere
   	name mirror
}

object {
	shader default
   	type sphere
   	name mirror
   	c 3 0 0
   	r 0.5
}
