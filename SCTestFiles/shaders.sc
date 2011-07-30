texturepath "textures"

shader {
   name shader1
   type diffuse
   diff 1 .5 .5
}

shader {
   name shader2
   type diffuse
   diff { "sRGB nonlinear" 0.800 0.800 0.800 }
}


shader {
	name shader3
  	type diffuse
	texture "textures/earth.png"
}

shader {
   name shader4
   type phong
   diff { "sRGB nonlinear" 0.800 0.800 0.800 }
   spec { "sRGB nonlinear" 1.0 1.0 1.0 } 50
   samples 4
}

/*
shader {
   name shader5
   type phong
   texture "C:\mypath\image.png"
   spec { "sRGB nonlinear" 1.0 1.0 1.0 } 50
   samples 4
}
*/

shader {
   name shader6
   type glass
   eta 1.0
   color { "sRGB nonlinear" 0.800 0.800 0.800 }
   absorbtion.distance 5.0
   absorbtion.color { "sRGB nonlinear" 1.0 1.0 1.0 }
}

shader {
   name shader7
   type mirror
   refl { "sRGB nonlinear" 0.800 0.800 0.800 }
}


