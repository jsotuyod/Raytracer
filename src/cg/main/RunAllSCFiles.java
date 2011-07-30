package cg.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;

public class RunAllSCFiles {

	public static void main(String[] args) {

		String dir = "SCTestFiles";
		String sfDir = "SCTestFiles/sunflow";
		String catDir = "SCTestFiles/catedra";
		String arg = " -time ";
		Runtime r = Runtime.getRuntime(); 

		String [] dirFiles = new File(dir).list(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sc");
			}
		});
		processFiles(dir, arg, dirFiles, r);

		dirFiles = new File(sfDir).list(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sc");
			}
		});
		processFiles(sfDir, arg, dirFiles, r);
		
		dirFiles = new File(catDir).list(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sc");
			}
		});
		processFiles(catDir, arg, dirFiles, r);
	}

	private static void processFiles(String dir, String arg, String[] dirFiles,	Runtime r) {
		Process p;
		String line;
		for (String fileName: dirFiles) {
			try {
				p = r.exec ("java -jar raytracing.jar -i " + dir + "/" + fileName + arg);
				//STDOUT
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null) {
					System.out.println(line);
				}
				input.close();
				//STDERR
				input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = input.readLine()) != null) {
					System.err.println(line);
				}
				input.close();

			} 
			catch (Exception e){
				e.printStackTrace();
			}
			System.out.println();
		}
	}
}
