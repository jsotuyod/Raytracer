image {
    resolution 800 600
    aa 0 2
    samples 1
    filter box
}
trace-depths {
    diff 1
    refl 4
    refr 4
}
background {
    color  { "sRGB nonlinear" 0.057 0.221 0.400 }
}

bucket 64 hilbert


shader {
    name def
    type diffuse
    diff 1 1 1
}

shader {
    name "Material.shader"
    type diffuse
    diff { "sRGB nonlinear" 0.800 0.800 0.800 }
}

camera {
    type   pinhole
    eye    7.481132 -6.507640 5.343665
    target 6.826270 -5.896974 4.898420
    up     -0.317370 0.312469 0.895343
    fov    49.1343426412
    aspect 1.33333333333
}

light {
    type point
    color { "sRGB nonlinear" 1.000 1.000 1.000 }
    power 100.0
    p 4.076245 1.005454 5.903862
}



object {
    shader "Material.shader"
    type generic-mesh
    name "Cube"
    points 8
        0.232447 0.389893 -1.000000
        0.232447 -1.610106 -1.000000
        -1.767553 -1.610106 -1.000000
        -1.767553 0.389894 -1.000000
        0.232447 0.389893 1.000000
        0.232446 -1.610107 1.000000
        -1.767554 -1.610106 1.000000
        -1.767553 0.389894 1.000000
    triangles 12
        0 1 2
        0 2 3
        4 7 6
        4 6 5
        0 4 5
        0 5 1
        1 5 6
        1 6 2
        2 6 7
        2 7 3
        4 0 3
        4 3 7
    normals none
    uvs none
}