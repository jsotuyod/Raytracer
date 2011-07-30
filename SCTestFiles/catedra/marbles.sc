image {
   resolution 600 600
   samples 5
}

accel bih
filter mitchell
bucket 32 row

camera {
   type pinhole
   eye    3.27743673325 -9.07978439331 9.93055152893
   target 0 0 0
   up     0 0 1
   fov    40
   aspect 1
}

include "../lights.sc"

include "../lights2.sc"

shader {
  name "ground"
  type diffuse
  diff { "sRGB nonlinear" 1 1 1 }
}

shader {
  name "shader01"
  type diffuse
  diff { "sRGB nonlinear" 0 1 0 }
}

shader {
  name "shader02"
  type diffuse
  diff { "sRGB nonlinear" 0 1 0 }
}

shader {
  name "shader03"
  type diffuse
  diff { "sRGB nonlinear" 0 1 0 }
}

shader {
  name "shader04"
  type diffuse
  diff { "sRGB nonlinear" 1 0 0 }
}

shader {
  name "shader05"
  type diffuse
  diff { "sRGB nonlinear" 1 0 0 }
}

shader {
  name "shader06"
  type diffuse
  diff { "sRGB nonlinear" 1 0 0 }
}

shader {
  name "shader07"
  type diffuse
  diff { "sRGB nonlinear" 0 0 1 }
}

shader {
  name "shader08"
  type diffuse
  diff { "sRGB nonlinear" 0 0 1 }
}

shader {
  name "shader09"
  type diffuse
  diff { "sRGB nonlinear" 0 0 1 }
}

object {
   shader "ground"
   type generic-mesh
   name "Plane"
   points 8
       3.1  3.1 0
       3.1 -3.1 0
      -3.1 -3.1 0
      -3.1  3.1 0
      -3.1  3.1 -0.61
      -3.1 -3.1 -0.61
       3.1 -3.1 -0.61
       3.1  3.1 -0.61
/*
      2.99000000954 2.98999977112 0
      2.99000000954 -2.99000000954 0
      -2.99000024796 -2.9899995327 0
      -2.98999905586 2.99000096321 0
      -2.98999905586 2.99000096321 -0.611932575703
      -2.99000024796 -2.9899995327 -0.611932575703
      2.99000000954 -2.99000000954 -0.611932575703
      2.99000000954 2.98999977112 -0.611932575703
*/
   triangles 12
      0 3 2
      0 2 1
      2 3 4
      2 4 5
      3 0 7
      3 7 4
      0 1 6
      0 6 7
      1 2 5
      1 5 6
      5 4 7
      5 7 6
   normals none
   uvs none
}

object {
  shader "shader01"
  type sphere
  c -2 2 0.7
  r 0.7
}

object {
  shader "shader02"
  type sphere
  c 0 2 0.7
  r 0.7
}

object {
  shader "shader03"
  type sphere
  c 2 2 0.7
  r 0.7
}

object {
  shader "shader04"
  type sphere
  c -2 0 0.7
  r 0.7
}
object {
  shader "shader05"
  type sphere
  c 0 0 0.7
  r 0.7
}

object {
  shader "shader06"
  type sphere
  c 2 0 0.7
  r 0.7
}

object {
  shader "shader07"
  type sphere
  c -2 -2 0.7
  r 0.7
}

object {
  shader "shader08"
  type sphere
  c 0 -2 0.7
  r 0.7
}

object {
  shader "shader09"
  type sphere
  c 2 -2 0.7
  r 0.7
}