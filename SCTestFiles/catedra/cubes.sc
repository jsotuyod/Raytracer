image {
  resolution 640 480
  samples 5
  filter mitchell
}

camera {
  type pinhole
  eye    0 -5 0
  target 0 1 0
  up     0 0 1
  fov    60
  aspect 1.333333
}

include "../lights.sc"

include "../lights2.sc"

shader {
  name blue
  type diffuse
  diff { "sRGB nonlinear" 0.25 0.25 1.0 }
}

shader {
  name red
  type diffuse
  diff { "sRGB nonlinear" 1.0 0.25 0.25 }
}

object {
  shader red
  transform {
	rotatex 30.0
	rotatey 30.0
	translate 2.0 0 0
  }
  type box
}

object {
  shader blue
  transform {
	translate -2.0 0 0
	scaleu 2.0
  }
  type box
}


