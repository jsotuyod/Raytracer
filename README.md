# Raytracer - a raytracer written in Java

This project started out as an assignment for my computer graphics course back when I was at University. It was originally coded along Ignacio Luciani and Alejandro Boldt. The original task was to create a raytracer that would load .sc files (the format used by [Sunflow](http://sunflow.sourceforge.net/)) and have a decent performance. The target was to render a scene with a bunny faster than any other teams, and attempt to reach 20 FPS in a small scene (300x200 px) with 100 standard objects (that is, no triangle meshes). Of course, we could never get even near that target, though we would end up rendering the bunny faster than any other teams in about 2.5s in the test environment using 2 cores, and 2 threads.

Since I enjoyed so much working on it, I kept coming back to it every now and then. In the process the raytracer got several improvements and performance boosts (for instance, I wrote a new version of KDTree making use of SAH). Nowadays, it's ~300% faster than when the course finished, rendering the same bunny in 0.8s on the same (old) machine, a newer one can probably do it twice as fast. Also the raytracer used to be a memory hog (1GB memory was minimum), but those days seems to be behind it now.

I still believe there is a lot of room for improvements. For instance, I've so far never got around to implement ray packets...

Anyway, I'm sharing this with the world hoping someone out there can have the same fun I did, learn as much as I did, and maybe even show me new ways to make this even faster.

# Included scenes

A couple of the scenes included (the bunny and aliens among others) are taken from Sunflow. Some of them may have been altered to add / change lights, but otherwhise were not changed. I claim no ownership of such files, all credit should be go to the Sunflow dev team.

The scenes included under the "catedra" folder where given to us by the professors. I don't know if they were created by them or taken from somewhere else.

A few other scenes where written by us, those I give to you for free usage. Do whatever you may want with them.

# Usage

the project builds with ant, and is used from the command line. It has a nice help, so you should have no issues using it.

Non the less, I'll include it here:

	usage: raytracing [-benchmark <N>] [-cores <N>] [-dof <T>] [-gui] -i
	        <filename> [-o <filename>] [-p <N>] [-progress] [-show] [-time]
	 -benchmark <N>   Will render N times and show total and average times
	 -cores <N>       Parallelize in N cores (not implemented yet)
	 -dof <T>         Use Depth of Field. T specifies lens size (in scene
	                  measures) (not implemented)
	 -gui             Use GUI for entering parameters (not implemented)
	 -i <filename>    Scene file to use in Sunflow format
	 -o <filename>    File in which to save the render
	 -p <N>           Use penumbra. N specifies processing limit amount
	 -progress        Show rendering progress
	 -show            Show image after program finishes
	 -time            Shows total rendering time

A sample usage:

	java -jar raytracing.jar -i SCTestFiles/sunflow/aliens_shiny_sf.sc -time -show -benchmark 10 -cores 2

This will render 10 times the aliens scene from Sunflow, using 2 threads, showing the time of each iteration (and the final average), and once it's done a window will let you see the rendered image.
