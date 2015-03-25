package cg.main;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cg.parser.SCParser;
import cg.raycasting.Raycast;
import cg.scene.Scene;
import cg.utils.ImageBuffer;
import cg.utils.ImageBufferIO;
import cg.utils.ImageBufferIO.ImageFormat;

public class Run {

	static int penumbraCount = 0;

	public static int getPenumbraCount() {
		return penumbraCount;
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		String inputFile = null;
		String outputFile = null;
		boolean showProgress = false;
		boolean gui = false;
		boolean showImage = false;
		boolean showTime = false;
		boolean showAvg = false;
		int renderCount = 1;
		int nCores = 1;
		boolean debug = true;
		int resX = 320, resY = 240;
		ImageFormat outputExtension = ImageFormat.PNG;


		Options options = new Options();

		Option inputFileOption = OptionBuilder.withArgName( "filename" )
		.hasArg()
		.withDescription( "Scene file to use in Sunflow format")
		.isRequired()
		.create( "i" );

		Option outputFileOption = OptionBuilder.withArgName( "filename" )
		.hasArg()
		.withDescription( "File in which to save the render")
		.create( "o" );

		Option progressOption = OptionBuilder.withDescription( "Show rendering progress")
		.create( "progress" );

		Option guiOption = OptionBuilder.withDescription( "Use GUI for entering parameters (not implemented)")
		.create( "gui" );

		Option showImageOption = OptionBuilder.withDescription( "Show image after program finishes")
		.create( "show" );

		Option penumbraOption = OptionBuilder.withArgName( "N" )
		.hasArg()
		.withDescription( "Use penumbra. N specifies processing limit amount")
		.create( "p" );

		Option dofOption = OptionBuilder.withArgName( "T" )
		.hasArg()
		.withDescription( "Use Depth of Field. T specifies lens size (in scene measures) (not implemented)")
		.create( "dof" );

		Option benchmarkOption = OptionBuilder.withArgName( "N" )
		.hasArg()
		.withDescription( "Will render N times and show total and average times")
		.create( "benchmark" );

		Option timeOption = OptionBuilder.withDescription( "Shows total rendering time")
		.create( "time" );

		Option coresOption = OptionBuilder.withArgName( "N" )
		.hasArg()
		.withDescription( "Parallelize in N cores (not implemented yet)")
		.create( "cores" );

		options.addOption(inputFileOption);
		options.addOption(outputFileOption);
		options.addOption(progressOption);
		options.addOption(guiOption);
		options.addOption(showImageOption);
		options.addOption(penumbraOption);
		options.addOption(dofOption);
		options.addOption(benchmarkOption);
		options.addOption(timeOption);
		options.addOption(coresOption);

		CommandLineParser cliParser = new GnuParser();
		CommandLine cmd = null;

		try {
			cmd = cliParser.parse( options, args);
			inputFile = cmd.getOptionValue("i");
			if (!getExtension(inputFile).equalsIgnoreCase("SC")) {
				System.err.println ("Input file must have a .sc extension");
				System.exit(-1);
			}
			if (cmd.hasOption("o")) {
				outputFile = cmd.getOptionValue("o");
				String extension = getExtension (outputFile);

				if (extension.equalsIgnoreCase("PNG")) {
					outputExtension = ImageFormat.PNG;
				}
				else if (extension.equalsIgnoreCase("BMP")) {
					outputExtension = ImageFormat.BMP;
				}
				else {
					System.err.println ("Valid output formats are png and bmp only");
					System.exit(-1);
				}
			}
			else {
				outputFile = inputFile.replace(".sc", ".png");
			}
			if (cmd.hasOption("progress")) {
				showProgress = true;
			}
			if (cmd.hasOption("gui")) {
				gui = true;
			}
			if (cmd.hasOption("show")) {
				showImage = true;
			}
			if (cmd.hasOption("benchmark")) {
				renderCount = Integer.parseInt(cmd.getOptionValue("benchmark"));
				if (renderCount <= 0) {
					System.err.println ("Benchmark value must be larger than 0");
					System.exit(-1);
				}
				else {
					showAvg = true;
				}
			}
			if (cmd.hasOption("time")) {
				showTime = true;
			}
			if (cmd.hasOption("p")) {
				penumbraCount = Integer.parseInt(cmd.getOptionValue("p"));
				if (penumbraCount < 0) {
					System.err.println ("Penumbra count must be a positive integer");
					System.exit(-1);
				}
			}
			if (cmd.hasOption("dof")) {
				System.err.println ("Depth of Field is not implemented");
			}
			if(cmd.hasOption("cores")){
				nCores = Integer.parseInt(cmd.getOptionValue("cores"));
				if (nCores <= 0) {
					System.err.println ("Cores value must be larger than 0. (recommended: "
							+ Runtime.getRuntime().availableProcessors() + " cores)");
					System.exit(-1);
				}
			}
		} catch (ParseException e) {
			showHelp(options);
		}
		catch (NumberFormatException e) {
			System.err.println("Invalid argument");
			showHelp(options);
		}

		if (gui) {
			System.err.print("GUI not implemented");
			System.exit(-1);
		}

		try {
			SCParser parser = new SCParser();
			Scene scene = parser.parseAllFiles(cmd.getOptionValue("i"));
			if(scene == null){
				System.exit(-1);
			}

			// Set up the scene's tree
			scene.initialize();

//			if(debug)
//				System.out.println(scene);

			if(scene.getImageData() != null){
				resX = scene.getImageData().getResX();
				resY = scene.getImageData().getResY();
			}

			ImageBuffer buffer = new ImageBuffer( resX, resY );

			Raycast rc = new Raycast(showProgress, nCores, scene, buffer);

			long total = 0;
			for (int i = 0; i < renderCount; i++) {
				if (debug) {
					System.out.println("Rendering[" + (i + 1) + " / " + renderCount + "] ...");
				}
				long start = System.currentTimeMillis();
				if (!rc.render()) {
					System.err.println("Error rendering, check the camera existance on your input file");
					System.exit(-1);
				}
				long stop = System.currentTimeMillis();

				if (showTime) {
					long duration = stop - start;

					System.out.print("\n[Cores: " + nCores + "] Rendering took " + (duration) + " ms. -> ");
					printConvertedMs(duration);
				}

				total += stop - start;
			}

			if (showAvg) {
				double avgDuration = total / (double) renderCount;
				System.out.print("\n[Cores: " + nCores + "] Total rendering time for " + renderCount + " renders : " + total + " ms. (" + avgDuration + " ms. avg) => ");
				printConvertedMs((int) avgDuration);
			}


			ImageBufferIO.saveToFile(buffer, new File(outputFile), outputExtension);


			if (showImage) {
				cg.ui.showImage.show(outputFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getExtension(String outputFile) {
		return outputFile.substring(outputFile.lastIndexOf('.')+1);
	}

	private static void showHelp (Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "raytracing", options, true );
		System.exit(-1);
	}

	private static void printConvertedMs (long duration) {
		long ms = duration % 1000;
		duration = duration / 1000;
		long s = duration % 60;
		duration = duration / 60;
		long m = duration % 60;
		duration = duration / 60;
		long h = duration % 60;
		duration = duration / 60;

		System.out.printf ("%02d:%02d:%02d:%d (HH:MM:SS:ms)\n", h, m, s, ms);
	}
}
