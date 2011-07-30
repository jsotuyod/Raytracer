package cg.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cg.main.Run;
import cg.math.Matrix4;
import cg.math.Point3D;
import cg.math.Vector3D;
import cg.parser.Parser.ParserException;
import cg.scene.Camera;
import cg.scene.ImageData;
import cg.scene.LightManager;
import cg.scene.Scene;
import cg.scene.light.DirectionalLight;
import cg.scene.light.PointLight;
import cg.scene.light.SphereLight;
import cg.scene.object3d.Box;
import cg.scene.object3d.Plane;
import cg.scene.object3d.Sphere;
import cg.scene.object3d.TriangleMesh;
import cg.scene.object3d.TriangleMesh.NormalType;
import cg.scene.object3d.TriangleMesh.UVType;
import cg.scene.shaders.ConstantShader;
import cg.scene.shaders.DiffuseShader;
import cg.scene.shaders.DiffuseShaderTexture;
import cg.scene.shaders.GlassShader;
import cg.scene.shaders.MirrorShader;
import cg.scene.shaders.Shader;
import cg.scene.shaders.ShinyShader;
import cg.utils.Color;
import cg.utils.ImageBuffer;

/**
 * This class provides a static method for loading files in the Sunflow scene
 * file format.
 */
public class SCParser {
	
    private Parser p;
//    private int numLightSamples;
    private Map<String, Shader> colorShadersMap = new HashMap<String, Shader>();
    private Map<String, ImageBuffer> texturesShadersMap = new HashMap<String, ImageBuffer>();

    private Set<String> parsedFiles = new HashSet<String>();
    private List<String>includeSearchPath = new LinkedList<String>();
    private List<String>textureSearchPath = new LinkedList<String>();
    
    private boolean debug = true;
    private Scene scene;
    
    public Scene parseAllFiles(String fileName){
    	this.scene = new Scene();
    	LightManager lm = new LightManager();
    	scene.setLightManager(lm);

    	parsedFiles.add (fileName);
		if( !parse(fileName, scene)){
    		System.err.println("Parsing error...");
    		System.exit(-1);
    	}

    	return scene;
    }
    
    private boolean parse(String filename, Scene scene) {
    	String parentDirectory = new File(filename).getParent();
    	String localDir = new File(filename).getAbsolutePath();
//        numLightSamples = 1;
    	if(debug)
        	System.out.println("\nPARSING: " + localDir + "... ");
        try {
            p = new Parser(localDir );
            while (true) {
                String token = p.getNextToken();
                if (token == null)
                    break;
                if (token.equals("image")) {
                    if(debug)
                    	System.out.println("Reading image settings ...");
                    parseImageBlock(scene);
                } 
//                else if (token.equals("background")) {
//                    UI.printInfo(Module.API, "Reading background ...");
//                    parseBackgroundBlock(api);
//                }
                else if (token.equals("accel")) {
//                    UI.printInfo(Module.API, "Reading accelerator type ...");
                    p.getNextToken();
//                    UI.printWarning(Module.API, "Setting accelerator type is not recommended - ignoring");
                } else if (token.equals("filter")) {
//                    UI.printInfo(Module.API, "Reading image filter type ...");
//                    parseFilter(api);
                	//TODO complete the block
                	p.getNextToken();
                } else if (token.equals("bucket")) {
//                    UI.printInfo(Module.API, "Reading bucket settings ...");
//                    api.parameter("bucket.size", p.getNextInt());
                	p.getNextInt();
                	p.getNextToken();
//                    api.parameter("bucket.order", p.getNextToken());
//                    api.options(SunflowAPI.DEFAULT_OPTIONS);
                }
//                	else if (token.equals("photons")) {
//                    UI.printInfo(Module.API, "Reading photon settings ...");
//                    parsePhotonBlock(api);
//                } else if (token.equals("gi")) {
//                    UI.printInfo(Module.API, "Reading global illumination settings ...");
//                    parseGIBlock(api);
//                } else if (token.equals("lightserver")) {
//                    UI.printInfo(Module.API, "Reading light server settings ...");
//                    parseLightserverBlock(api);
//                } else if (token.equals("trace-depths")) {
//                    UI.printInfo(Module.API, "Reading trace depths ...");
//                    parseTraceBlock(api);
//                } else 
                	
                else if (token.equals("camera")) {
                    parseCamera(scene);
                } 
                
                else if (token.equals("shader")) {
                    if (!parseShader(parentDirectory))
                        return false;
                } 
//                else if (token.equals("modifier")) {
//                    if (!parseModifier(api))
//                        return false;
//                } else if (token.equals("override")) {
//                    api.shaderOverride(p.getNextToken(), p.getNextBoolean());
//                } else 
                
                else if (token.equals("object")) {
                    parseObjectBlock(scene);
                } 
                
//                else if (token.equals("instance")) {
//                    parseInstanceBlock(api);
//                } 
                else if (token.equals("light")) {
                    parseLightBlock(scene);
                } 
                
                else if (token.equals("texturepath")) {
                    String path = p.getNextToken();
                    if (!new File(path).isAbsolute())
                        path = new File (parentDirectory + File.separator + path).getCanonicalPath();
                    textureSearchPath.add(path);
                } else if (token.equals("includepath")) {
                    String path = p.getNextToken();
                    if (!new File(path).isAbsolute())
                        path = new File (parentDirectory + File.separator + path).getCanonicalPath();
                    includeSearchPath.add(path);
                } 
                else if (token.equals("include")) {
                    String fileName = p.getNextToken();
                    
                    if (! new File(fileName).isAbsolute()) {
                    	if (new File(parentDirectory + File.separatorChar + fileName).isFile()) {
                    		fileName = new File (parentDirectory + File.separatorChar + fileName).getCanonicalPath();
                    	}
                    	else {
                    		for (String path : includeSearchPath) {
                    			if (new File (path + File.separatorChar + fileName).isFile()) {
                    				fileName = new File (path + File.separatorChar + fileName).getCanonicalPath();
                    				break;
                    			}
                    		}
                    	}
                    }
                    
                    if ( !new File(fileName).isFile()) {
                    	System.err.println (fileName + " doesn't exist. Ignoring.");
                    	continue;
                    }
                    
                    if(debug)
                    	System.out.println("Including: " + fileName +" ...");
                    
                    if (parsedFiles.contains(fileName)) {
                    	System.err.println ("Ignoring duplicate include");
                    }
                    else {
                    	parsedFiles.add(fileName);
                    	Parser pBak = p;
                    	if( !parse(fileName, scene)){
                    		System.err.println("Parsing error...");
                    		System.exit(-1);
                    	}
                    	p = pBak;
                    }
                } 
                else{
                	consumeUnrecognizedBlock(token, 0);
                }
            }
            p.close();
        } catch (ParserException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        if(debug){
        	System.out.println("Color Shaders: " + colorShadersMap);
        	System.out.println("Textures shaders: " + texturesShadersMap);
        }
        if(debug)
        	System.out.println("Done parsing...");
 
        return true;
    }

	private void consumeUnrecognizedBlock(String token, int initParenBalance) throws IOException{
    	int parenBalance = initParenBalance;
    	if(debug)
    		System.out.println("[BegUnrecBlk] " + token);

    	do{
    		token = p.getNextToken();
    		if(token != null){
    			if(debug)
    				System.out.println(token);
    			if(token.equals("{"))
    				parenBalance++;
    			if(token.equals("}"))
    				parenBalance--;
    		}
    	}while(token != null && parenBalance != 0);
    	if(debug)
    		System.out.println("[FinUnrecBlk]");
    }

    private void parseImageBlock(Scene scene) throws IOException, ParserException {
    	float contrast = 0;
    	String filter = "";
    	boolean jitter = false;
    	int maxAA = 0, minAA = 0, resX = 0, resY = 0, samples = 0;

    	p.checkNextToken("{");
        if (p.peekNextToken("resolution")) {
        	resX = p.getNextInt();
        	resY  = p.getNextInt();
        }
        if (p.peekNextToken("aa")) {
        	minAA = p.getNextInt();
        	maxAA = p.getNextInt();
        }
        if (p.peekNextToken("samples"))
        	samples = p.getNextInt();
        if (p.peekNextToken("contrast"))
        	contrast = p.getNextFloat();
        if (p.peekNextToken("filter"))
        	filter = p.getNextToken();
        if (p.peekNextToken("jitter"))
        	jitter = p.getNextBoolean();
        
        if (p.peekNextToken("show-aa")) {
            System.err.println("Deprecated: show-aa ignored");
            p.getNextBoolean();
        }
        if (p.peekNextToken("output")) {
            System.err.println("Deprecated: output statement ignored");
            p.getNextToken();
        }
        p.checkNextToken("}");
        scene.setImageData(new ImageData(contrast, filter, jitter, maxAA, minAA, resX, resY, samples < 1 ? 1 : samples));
    }

//    private void parseBackgroundBlock(SunflowAPI api) throws IOException, ParserException {
//        p.checkNextToken("{");
//        p.checkNextToken("color");
//        api.parameter("color", parseColor());
//        api.shader("background.shader", new ConstantShader());
//        api.geometry("background", new Background());
//        api.parameter("shaders", "background.shader");
//        api.instance("background.instance", "background");
//        p.checkNextToken("}");
//    }

//    private void parseFilter(SunflowAPI api) throws IOException, ParserException {
//        UI.printWarning(Module.API, "Deprecated keyword \"filter\" - set this option in the image block");
//        String name = p.getNextToken();
//        api.parameter("filter", name);
//        api.options(SunflowAPI.DEFAULT_OPTIONS);
//        boolean hasSizeParams = name.equals("box") || name.equals("gaussian") || name.equals("blackman-harris") || name.equals("sinc") || name.equals("triangle");
//        if (hasSizeParams) {
//            p.getNextFloat();
//            p.getNextFloat();
//        }
//    }

//    private void parsePhotonBlock(SunflowAPI api) throws ParserException, IOException {
//        int numEmit = 0;
//        boolean globalEmit = false;
//        p.checkNextToken("{");
//        if (p.peekNextToken("emit")) {
//            UI.printWarning(Module.API, "Shared photon emit values are deprectated - specify number of photons to emit per map");
//            numEmit = p.getNextInt();
//            globalEmit = true;
//        }
//        if (p.peekNextToken("global")) {
//            UI.printWarning(Module.API, "Global photon map setting belonds inside the gi block - ignoring");
//            if (!globalEmit)
//                p.getNextInt();
//            p.getNextToken();
//            p.getNextInt();
//            p.getNextFloat();
//        }
//        p.checkNextToken("caustics");
//        if (!globalEmit)
//            numEmit = p.getNextInt();
//        api.parameter("caustics.emit", numEmit);
//        api.parameter("caustics", p.getNextToken());
//        api.parameter("caustics.gather", p.getNextInt());
//        api.parameter("caustics.radius", p.getNextFloat());
//        api.options(SunflowAPI.DEFAULT_OPTIONS);
//        p.checkNextToken("}");
//    }

//    private void parseGIBlock(SunflowAPI api) throws ParserException, IOException {
//        p.checkNextToken("{");
//        p.checkNextToken("type");
//        if (p.peekNextToken("irr-cache")) {
//            api.parameter("gi.engine", "irr-cache");
//            p.checkNextToken("samples");
//            api.parameter("gi.irr-cache.samples", p.getNextInt());
//            p.checkNextToken("tolerance");
//            api.parameter("gi.irr-cache.tolerance", p.getNextFloat());
//            p.checkNextToken("spacing");
//            api.parameter("gi.irr-cache.min_spacing", p.getNextFloat());
//            api.parameter("gi.irr-cache.max_spacing", p.getNextFloat());
//            // parse global photon map info
//            if (p.peekNextToken("global")) {
//                api.parameter("gi.irr-cache.gmap.emit", p.getNextInt());
//                api.parameter("gi.irr-cache.gmap", p.getNextToken());
//                api.parameter("gi.irr-cache.gmap.gather", p.getNextInt());
//                api.parameter("gi.irr-cache.gmap.radius", p.getNextFloat());
//            }
//        } else if (p.peekNextToken("path")) {
//            api.parameter("gi.engine", "path");
//            p.checkNextToken("samples");
//            api.parameter("gi.path.samples", p.getNextInt());
//            if (p.peekNextToken("bounces")) {
//                UI.printWarning(Module.API, "Deprecated setting: bounces - use diffuse trace depth instead");
//                p.getNextInt();
//            }
//        } else if (p.peekNextToken("fake")) {
//            api.parameter("gi.engine", "fake");
//            p.checkNextToken("up");
//            api.parameter("gi.fake.up", parseVector());
//            p.checkNextToken("sky");
//            api.parameter("gi.fake.sky", parseColor());
//            p.checkNextToken("ground");
//            api.parameter("gi.fake.ground", parseColor());
//        } else if (p.peekNextToken("igi")) {
//            api.parameter("gi.engine", "igi");
//            p.checkNextToken("samples");
//            api.parameter("gi.igi.samples", p.getNextInt());
//            p.checkNextToken("sets");
//            api.parameter("gi.igi.sets", p.getNextInt());
//            if (!p.peekNextToken("b"))
//                p.checkNextToken("c");
//            api.parameter("gi.igi.c", p.getNextFloat());
//            p.checkNextToken("bias-samples");
//            api.parameter("gi.igi.bias_samples", p.getNextInt());
//        } else if (p.peekNextToken("ambocc")) {
//            api.parameter("gi.engine", "ambocc");
//            p.checkNextToken("bright");
//            api.parameter("gi.ambocc.bright", parseColor());
//            p.checkNextToken("dark");
//            api.parameter("gi.ambocc.dark", parseColor());
//            p.checkNextToken("samples");
//            api.parameter("gi.ambocc.samples", p.getNextInt());
//            if (p.peekNextToken("maxdist"))
//                api.parameter("gi.ambocc.maxdist", p.getNextFloat());
//        } else if (p.peekNextToken("none") || p.peekNextToken("null")) {
//            // disable GI
//            api.parameter("gi.engine", "none");
//        } else
//            UI.printWarning(Module.API, "Unrecognized gi engine type \"%s\" - ignoring", p.getNextToken());
//        api.options(SunflowAPI.DEFAULT_OPTIONS);
//        p.checkNextToken("}");
//    }

//    private void parseLightserverBlock(SunflowAPI api) throws ParserException, IOException {
//        p.checkNextToken("{");
//        if (p.peekNextToken("shadows")) {
//            UI.printWarning(Module.API, "Deprecated: shadows setting ignored");
//            p.getNextBoolean();
//        }
//        if (p.peekNextToken("direct-samples")) {
//            UI.printWarning(Module.API, "Deprecated: use samples keyword in area light definitions");
//            numLightSamples = p.getNextInt();
//        }
//        if (p.peekNextToken("glossy-samples")) {
//            UI.printWarning(Module.API, "Deprecated: use samples keyword in glossy shader definitions");
//            p.getNextInt();
//        }
//        if (p.peekNextToken("max-depth")) {
//            UI.printWarning(Module.API, "Deprecated: max-depth setting - use trace-depths block instead");
//            int d = p.getNextInt();
//            api.parameter("depths.diffuse", 1);
//            api.parameter("depths.reflection", d - 1);
//            api.parameter("depths.refraction", 0);
//            api.options(SunflowAPI.DEFAULT_OPTIONS);
//        }
//        if (p.peekNextToken("global")) {
//            UI.printWarning(Module.API, "Deprecated: global settings ignored - use photons block instead");
//            p.getNextBoolean();
//            p.getNextInt();
//            p.getNextInt();
//            p.getNextInt();
//            p.getNextFloat();
//        }
//        if (p.peekNextToken("caustics")) {
//            UI.printWarning(Module.API, "Deprecated: caustics settings ignored - use photons block instead");
//            p.getNextBoolean();
//            p.getNextInt();
//            p.getNextFloat();
//            p.getNextInt();
//            p.getNextFloat();
//        }
//        if (p.peekNextToken("irr-cache")) {
//            UI.printWarning(Module.API, "Deprecated: irradiance cache settings ignored - use gi block instead");
//            p.getNextInt();
//            p.getNextFloat();
//            p.getNextFloat();
//            p.getNextFloat();
//        }
//        p.checkNextToken("}");
//    }

//    private void parseTraceBlock(SunflowAPI api) throws ParserException, IOException {
//        p.checkNextToken("{");
//        if (p.peekNextToken("diff"))
//            api.parameter("depths.diffuse", p.getNextInt());
//        if (p.peekNextToken("refl"))
//            api.parameter("depths.reflection", p.getNextInt());
//        if (p.peekNextToken("refr"))
//            api.parameter("depths.refraction", p.getNextInt());
//        p.checkNextToken("}");
//        api.options(SunflowAPI.DEFAULT_OPTIONS);
//    }

    private void parseCamera(Scene scene) throws ParserException, IOException {
        p.checkNextToken("{");
        //always pinhole
        p.checkNextToken("type");
        String type = p.getNextToken();
        if(debug)
        	System.out.println("Reading " + type + " camera ...");
//        parseCameraTransform(api);
//        String name = api.getUniqueName("camera");
        if (type.equals("pinhole")) {
        	
        	p.checkNextToken("eye");
        	Point3D pos = parsePoint();
            
            p.checkNextToken("target");
            Point3D target = parsePoint();
            
            p.checkNextToken("up");
            Vector3D up = parseVector();
            
            p.checkNextToken("fov");
            float fov =  p.getNextFloat();
            p.checkNextToken("aspect");
            float aspect = p.getNextFloat();
            scene.setCamera(new Camera(pos, target, up, fov, aspect));
        } 
//        else if (type.equals("thinlens")) {
//            p.checkNextToken("fov");
//            api.parameter("fov", p.getNextFloat());
//            p.checkNextToken("aspect");
//            api.parameter("aspect", p.getNextFloat());
//            p.checkNextToken("fdist");
//            api.parameter("focus.distance", p.getNextFloat());
//            p.checkNextToken("lensr");
//            api.parameter("lens.radius", p.getNextFloat());
//            if (p.peekNextToken("sides"))
//                api.parameter("lens.sides", p.getNextInt());
//            if (p.peekNextToken("rotation"))
//                api.parameter("lens.rotation", p.getNextFloat());
//            api.camera(name, new ThinLens());
//        } else if (type.equals("spherical")) {
//            // no extra arguments
//            api.camera(name, new SphericalLens());
//        } else if (type.equals("fisheye")) {
//            // no extra arguments
//            api.camera(name, new FisheyeLens());
//        } 
        else {
            System.err.println("Unrecognized camera type: " + p.getNextToken());
            p.checkNextToken("}");
            return;
        }
        p.checkNextToken("}");
    }

//    private void parseCameraTransform(SunflowAPI api) throws ParserException, IOException {
//        if (p.peekNextToken("steps")) {
//            // motion blur camera
//            int n = p.getNextInt();
//            api.parameter("transform.steps", n);
//            for (int i = 0; i < n; i++)
//                parseCameraMatrix(i, api);
//        } else
//            parseCameraMatrix(-1, api);
//    }

//    private void parseCameraMatrix(int index, SunflowAPI api) throws IOException, ParserException {
//        String offset = index < 0 ? "" : String.format("[%d]", index);
//        if (p.peekNextToken("transform")) {
//            // advanced camera
//            api.parameter(String.format("transform%s", offset), parseMatrix());
//        } else {
//            if (index >= 0)
//                p.checkNextToken("{");
//            // regular camera specification
//            p.checkNextToken("eye");
//            api.parameter(String.format("eye%s", offset), parsePoint());
//            p.checkNextToken("target");
//            api.parameter(String.format("target%s", offset), parsePoint());
//            p.checkNextToken("up");
//            api.parameter(String.format("up%s", offset), parseVector());
//            if (index >= 0)
//                p.checkNextToken("}");
//        }
//    }

    private boolean parseShader(String parentDirectory) throws ParserException, IOException {
    	Shader shader = null;
        p.checkNextToken("{");
        p.checkNextToken("name");
        String name = p.getNextToken();
        if(debug)
        	System.out.println("Reading shader: " + name + " ...");
        p.checkNextToken("type");
        String type;
        
        if (p.peekNextToken("diffuse")) {
        	type = "diffuse";
            if (p.peekNextToken("diff")) {
            	Color colorRGB = parseColor();
            	shader = new DiffuseShader(name, type, colorRGB, this.scene.getLightManager());
                colorShadersMap.put(name, shader);
            }
            else if (p.peekNextToken("texture")) {
            	String textureFilePath = p.getNextToken();
            	
                if (! new File(textureFilePath).isAbsolute()) {
                	if (new File (parentDirectory + File.separatorChar + textureFilePath).isFile()) {
                		textureFilePath = parentDirectory + File.separatorChar + textureFilePath;
                	}
                	else {
                		for (String path : textureSearchPath) {
                			if (new File (path + File.separatorChar + textureFilePath).isFile()) {
                				textureFilePath = path + File.separatorChar + textureFilePath;
                				break;
                			}
                		}
                	}
                }
                
                if ( !new File(textureFilePath).isFile()) {
                	System.err.println (textureFilePath + " doesn't exist. Aborting.");
                	System.exit(-1);
                }
                
            	texturesShadersMap.put(name, new ImageBuffer(new File(textureFilePath).getAbsoluteFile()));
            	shader = new DiffuseShaderTexture(name, type, textureFilePath ,this.scene.getLightManager());
            } 
            else
                System.err.println("Unrecognized option in diffuse shader block: " + p.getNextToken());
        }
        else if (p.peekNextToken("phong")) {
            String textureFilePath = null;
            Color colorRGB = null;
            type = "phong";
            
            if (p.peekNextToken("texture")){
            	textureFilePath = p.getNextToken();
//            	texturesShadersMap.put(name, new ImageBuffer(new File(textureFilePath).getAbsoluteFile()));
            }else {
                p.checkNextToken("diff");
                colorRGB = parseColor();
            }

            p.checkNextToken("spec");
            Color colorRGBSpec = parseColor();
            float specPower = p.getNextFloat();
            
            p.checkNextToken("samples");
            int samples = p.getNextInt();

            if (textureFilePath == null){
//            	shader = new PhongShaderTexture(name, type, textureFilePath, 
//            			colorRGBSpec, specPower, samples,this.scene.getLightManager());
            	shader = new DiffuseShader(name, type, colorRGB, this.scene.getLightManager());
                colorShadersMap.put(name, shader);
            }else{
//            	shader = new PhongShader(name, type, colorRGB, colorRGBSpec, 
//            			specPower, samples, this.scene.getLightManager());
//            	colorShadersMap.put(name, shader);

//            	textureFilePath = p.getNextToken();

            	if (! new File(textureFilePath).isAbsolute()) {
            		if (new File (parentDirectory + File.separatorChar + textureFilePath).isFile()) {
            			textureFilePath = parentDirectory + File.separatorChar + textureFilePath;
            		}
            		else {
            			for (String path : textureSearchPath) {
            				if (new File (path + File.separatorChar + textureFilePath).isFile()) {
            					textureFilePath = path + File.separatorChar + textureFilePath;
            					break;
            				}
            			}
            		}
            	}

            	if ( !new File(textureFilePath).isFile()) {
            		System.err.println (textureFilePath + " doesn't exist. Aborting.");
            		System.exit(-1);
            	}

            	texturesShadersMap.put(name, new ImageBuffer(new File(textureFilePath).getAbsoluteFile()));
            	shader = new DiffuseShaderTexture(name, type, textureFilePath ,this.scene.getLightManager());
            }
        } else if (p.peekNextToken("amb-occ") || p.peekNextToken("amb-occ2")) {
//            String tex = null;
            if (p.peekNextToken("diff") || p.peekNextToken("bright")) {
//                api.parameter("bright", parseColor());
            } else if (p.peekNextToken("texture")) {
//                api.parameter("texture", tex = p.getNextToken());
            } if (p.peekNextToken("dark")) {
//                api.parameter("dark", parseColor());
                p.checkNextToken("samples");
//                api.parameter("samples", p.getNextInt());
                p.checkNextToken("dist");
//                api.parameter("maxdist", p.getNextFloat());
            }
//            if (tex == null) {
//                api.shader(name, new AmbientOcclusionShader());
//            } else {
//                api.shader(name, new TexturedAmbientOcclusionShader());
//            }
        } else if (p.peekNextToken("mirror")) {
            p.checkNextToken("refl");
            type = "mirror";
            Color colorRGB = parseColor();
            shader = new MirrorShader(name, type, colorRGB, this.scene.getLightManager());
            colorShadersMap.put(name, shader);
        } else if (p.peekNextToken("glass")) {
        	type = "glass";
            p.checkNextToken("eta");
            float eta = p.getNextFloat();
            p.checkNextToken("color");
            Color colorRGB = parseColor();
            float absDist = 0.0f;
            if (p.peekNextToken("absorbtion.distance")) {
            	absDist = p.getNextFloat();
            }
            Color absColor = null;
            if (p.peekNextToken("absorbtion.color")) {
            	absColor = parseColor();
            }
            shader = new GlassShader(name, type, eta, colorRGB, absDist, absColor, this.scene.getLightManager());
            colorShadersMap.put(name, shader);
        } else if (p.peekNextToken("shiny")) {
        	type = "shiny";
            String tex = null;
            Color c = null;
            if (p.peekNextToken("texture")) {
            	tex = p.getNextToken();
//                api.parameter("texture", tex = p.getNextToken());
            } else {
                p.checkNextToken("diff");
                c = parseColor();
//                api.parameter("diffuse", parseColor());
            }
            p.checkNextToken("refl");
            float refl = p.getNextFloat();
//            api.parameter("shiny", p.getNextFloat());
            if (tex == null) {
//                api.shader(name, new ShinyDiffuseShader());
            	shader = new ShinyShader(name, type, c, refl, this.scene.getLightManager());
            } else {
//                api.shader(name, new TexturedShinyDiffuseShader());
            }
            colorShadersMap.put(name, shader);
        } else if (p.peekNextToken("ward")) {
            String tex = null;
            if (p.peekNextToken("texture")) {
            	p.getNextToken();
//                api.parameter("texture", tex = p.getNextToken());
            } else {
                p.checkNextToken("diff");
                parseColor();
//                api.parameter("diffuse", parseColor());
            }
            p.checkNextToken("spec");
            parseColor();
//            api.parameter("specular", parseColor());
            p.checkNextToken("rough");
            p.getNextFloat();
            p.getNextFloat();
//            api.parameter("roughnessX", p.getNextFloat());
//            api.parameter("roughnessY", p.getNextFloat());
            if (p.peekNextToken("samples")) {
            	p.getNextInt();
//                api.parameter("samples", p.getNextInt());
            }
            if (tex != null) {
//                api.shader(name, new TexturedWardShader());
            } else {
//                api.shader(name, new AnisotropicWardShader());
            }
        } else if (p.peekNextToken("view-caustics")) {
//            api.shader(name, new ViewCausticsShader());
        } else if (p.peekNextToken("view-irradiance")) {
//            api.shader(name, new ViewIrradianceShader());
        } else if (p.peekNextToken("view-global")) {
//            api.shader(name, new ViewGlobalPhotonsShader());
        } else if (p.peekNextToken("constant")) {
            // backwards compatibility -- peek only
            p.peekNextToken("color");
            type = "constant";
            shader = new ConstantShader(name, type, parseColor(), this.scene.getLightManager());
            colorShadersMap.put(name, shader);
        } else if (p.peekNextToken("janino")) {
            p.getNextCodeBlock();
//            try {
//                Shader shader = (Shader) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(code)), Shader.class, ClassLoader.getSystemClassLoader());
//                api.shader(name, shader);
//            } catch (CompileException e) {
//                UI.printDetailed(Module.API, "Compiling: %s", code);
//                UI.printError(Module.API, "%s", e.getMessage());
//                e.printStackTrace();
//                return false;
//            } catch (ParseException e) {
//                UI.printDetailed(Module.API, "Compiling: %s", code);
//                UI.printError(Module.API, "%s", e.getMessage());
//                e.printStackTrace();
//                return false;
//            } catch (ScanException e) {
//                UI.printDetailed(Module.API, "Compiling: %s", code);
//                UI.printError(Module.API, "%s", e.getMessage());
//                e.printStackTrace();
//                return false;
//            } catch (IOException e) {
//                UI.printDetailed(Module.API, "Compiling: %s", code);
//                UI.printError(Module.API, "%s", e.getMessage());
//                e.printStackTrace();
//                return false;
//            }
        } else if (p.peekNextToken("id")) {
//            api.shader(name, new IDShader());
        } else if (p.peekNextToken("uber")) {
            if (p.peekNextToken("diff")) {
            	parseColor();
//                api.parameter("diffuse", parseColor());
            }
            if (p.peekNextToken("diff.texture")) {
            	p.getNextToken();
//                api.parameter("diffuse.texture", p.getNextToken());
            }
            if (p.peekNextToken("diff.blend")) {
            	p.getNextFloat();
//                api.parameter("diffuse.blend", p.getNextFloat());
            }
            if (p.peekNextToken("refl") || p.peekNextToken("spec")) {
            	parseColor();
//                api.parameter("specular", parseColor());
            }
            if (p.peekNextToken("texture")) {
                // deprecated
//                UI.printWarning(Module.API, "Deprecated uber shader parameter \"texture\" - please use \"diffuse.texture\" and \"diffuse.blend\" instead");
//                api.parameter("diffuse.texture", p.getNextToken());
//                api.parameter("diffuse.blend", p.getNextFloat());
            	p.getNextToken();
            	p.getNextFloat();
            }
            if (p.peekNextToken("spec.texture")) {
            	p.getNextToken();
//                api.parameter("specular.texture", p.getNextToken());
            }
            if (p.peekNextToken("spec.blend")) {
            	p.getNextFloat();
//                api.parameter("specular.blend", p.getNextFloat());
            }
            if (p.peekNextToken("glossy")) {
            	p.getNextFloat();
//                api.parameter("glossyness", p.getNextFloat());
            }
            if (p.peekNextToken("samples")) {
            	p.getNextInt();
//                api.parameter("samples", p.getNextInt());
            }
//            api.shader(name, new UberShader());
        } else
            System.err.println("Unrecognized shader type: " + p.getNextToken());
        p.checkNextToken("}");
        
        if(shader != null){
        	this.colorShadersMap.put(name, shader);
        }
        return true;
    }

//    private boolean parseModifier(SunflowAPI api) throws ParserException, IOException {
//        p.checkNextToken("{");
//        p.checkNextToken("name");
//        String name = p.getNextToken();
//        UI.printInfo(Module.API, "Reading shader: %s ...", name);
//        p.checkNextToken("type");
//        if (p.peekNextToken("bump")) {
//            p.checkNextToken("texture");
//            api.parameter("texture", p.getNextToken());
//            p.checkNextToken("scale");
//            api.parameter("scale", p.getNextFloat());
//            api.modifier(name, new BumpMappingModifier());
//        } else if (p.peekNextToken("normalmap")) {
//            p.checkNextToken("texture");
//            api.parameter("texture", p.getNextToken());
//            api.modifier(name, new NormalMapModifier());
//        } else {
//            UI.printWarning(Module.API, "Unrecognized modifier type: %s", p.getNextToken());
//        }
//        p.checkNextToken("}");
//        return true;
//    }

    private void parseObjectBlock(Scene scene) throws ParserException, IOException {
        p.checkNextToken("{");
        boolean noInstance = false;
        Matrix4 transform = null;
        String name = null;
        String actualShader = null;
        
        if (p.peekNextToken("noinstance")) {
            // this indicates that the geometry is to be created, but not
            // instanced into the scene
            noInstance = true;
        } else {
            // these are the parameters to be passed to the instance
            if (p.peekNextToken("shaders")) {
                int n = p.getNextInt();
                for (int i = 0; i < n; i++)
                    p.getNextToken();
            } else {
                p.checkNextToken("shader");
                actualShader = p.getNextToken();
            }
            if (p.peekNextToken("modifiers")) {
                int n = p.getNextInt();
                for (int i = 0; i < n; i++)
                    p.getNextToken();
            } else if (p.peekNextToken("modifier")){
            	p.getNextToken();
            }
            if (p.peekNextToken("transform"))
                transform = parseMatrix();
        }
        if (p.peekNextToken("accel"))
            p.getNextToken();
        
        p.checkNextToken("type");
        String type = p.getNextToken();
        
        if (p.peekNextToken("name"))
            name = p.getNextToken();
        
//        else
//            name = api.getUniqueName(type);
        if (type.equals("mesh")) {
        	if(debug)
        		System.out.println("Reading deprecated mesh object : " + name + " ...");
        	
            int numVertices = p.getNextInt();
            int numTriangles = p.getNextInt();
            
            Point3D[] points = new Point3D[numVertices];
            float[] pointCoords = new float[3];
            float[] normals = new float[numVertices * 3];
            float[] uvs = new float[numVertices * 2];
            
            for (int i = 0; i < numVertices; i++) {
                p.checkNextToken("v");
                pointCoords[0] = p.getNextFloat();
                pointCoords[1] = p.getNextFloat();
                pointCoords[2] = p.getNextFloat();
                
                points[i] = new Point3D(pointCoords[0], pointCoords[1], pointCoords[2]);
                
                if ( transform != null ) {
                	points[i] = transform.transform(points[i]);
                }
                
                normals[3 * i + 0] = p.getNextFloat();
                normals[3 * i + 1] = p.getNextFloat();
                normals[3 * i + 2] = p.getNextFloat();
                uvs[2 * i + 0] = p.getNextFloat();
                uvs[2 * i + 1] = p.getNextFloat();
            }
            
            int[] triangles = new int[numTriangles * 3];
            for (int i = 0; i < numTriangles; i++) {
                p.checkNextToken("t");
                triangles[i * 3 + 0] = p.getNextInt();
                triangles[i * 3 + 1] = p.getNextInt();
                triangles[i * 3 + 2] = p.getNextInt();
            }
            
            scene.add(new TriangleMesh(points, triangles, colorShadersMap.get(actualShader), UVType.VERTEX, uvs, NormalType.VERTEX, normals, transform));
        }
//        else if (type.equals("flat-mesh")) {
//            UI.printWarning(Module.API, "Deprecated object type: flat-mesh");
//            UI.printInfo(Module.API, "Reading flat mesh: %s ...", name);
//            int numVertices = p.getNextInt();
//            int numTriangles = p.getNextInt();
//            float[] points = new float[numVertices * 3];
//            float[] uvs = new float[numVertices * 2];
//            for (int i = 0; i < numVertices; i++) {
//                p.checkNextToken("v");
//                points[3 * i + 0] = p.getNextFloat();
//                points[3 * i + 1] = p.getNextFloat();
//                points[3 * i + 2] = p.getNextFloat();
//                p.getNextFloat();
//                p.getNextFloat();
//                p.getNextFloat();
//                uvs[2 * i + 0] = p.getNextFloat();
//                uvs[2 * i + 1] = p.getNextFloat();
//            }
//            int[] triangles = new int[numTriangles * 3];
//            for (int i = 0; i < numTriangles; i++) {
//                p.checkNextToken("t");
//                triangles[i * 3 + 0] = p.getNextInt();
//                triangles[i * 3 + 1] = p.getNextInt();
//                triangles[i * 3 + 2] = p.getNextInt();
//            }
//            // create geometry
//            api.parameter("triangles", triangles);
//            api.parameter("points", "point", "vertex", points);
//            api.parameter("uvs", "texcoord", "vertex", uvs);
//            api.geometry(name, new TriangleMesh());
//        }
        else if (type.equals("box")) {
        	if(debug)
        		System.out.println("Reading box ...");

        	if ( transform == null ) {
        		transform = Matrix4.identity();
        	}
        	
        	// This will be only translated and serve as reference for normals
        	Point3D ref = transform.transform(new Point3D(0, 0, 0));
        	
            scene.add(new Box(transform.transform(new Point3D(-1,-1,-1)),
            		transform.transform(new Point3D(1,1,1)),
            		transform.transform(new Point3D(-1,-1,1)),
            		transform.transform(new Point3D(1,1,-1)),
            		transform.transform(new Point3D(1,-1,-1)),
            		transform.transform(new Point3D(-1,1,1)),
            		transform.transform(new Point3D(1,-1,1)),
            		transform.transform(new Point3D(-1,1,-1)),
            		transform.transform(new Vector3D(0,1,0)).substract(new Vector3D(ref)).normalize(),
            		transform.transform(new Vector3D(1,0,0)).substract(new Vector3D(ref)).normalize(),
            		colorShadersMap.get(actualShader)));
            
        }else if (type.equals("sphere")) {
            if(debug)
            	System.out.println("Reading sphere ...");
            
            Point3D c;
            float radius;

            if (transform == null && !noInstance) {
                // legacy method of specifying transformation for spheres
                p.checkNextToken("c");
                c = parsePoint();
                
                p.checkNextToken("r");
                radius = p.getNextFloat();
                
                noInstance = true; // disable future auto-instancing because
                // instance has already been created
            } else {
            	// Create a sphere at 0,0,0 with radius 1 and transform it
            	c = new Point3D(0,0,0);
            	Vector3D r = new Vector3D(1,0,0);
            	
            	c = transform.transform(c);
            	r = transform.transform(r).substract(new Vector3D(c));
            	
            	radius = (float) Math.sqrt(r.dotProduct(r));	// Just get radius, ignoring rotations if any
            }
            
            scene.add(new Sphere(c , radius, colorShadersMap.get(actualShader), transform));
        } 
        
//        else if (type.equals("banchoff")) {
//            UI.printInfo(Module.API, "Reading banchoff ...");
//            api.geometry(name, new BanchoffSurface());
//        } else if (type.equals("torus")) {
//            UI.printInfo(Module.API, "Reading torus ...");
//            p.checkNextToken("r");
//            api.parameter("radiusInner", p.getNextFloat());
//            api.parameter("radiusOuter", p.getNextFloat());
//            api.geometry(name, new Torus());
//        } 
        else if (type.equals("plane")) {
            if(debug)
            	System.out.println("Reading plane ...");

            if ( transform == null ) {
	            p.checkNextToken("p");
	            
	            Point3D p1 = parsePoint();
	            
	            if (p.peekNextToken("n")) {
	                Vector3D n = parseVector();
	                scene.add(new Plane(p1, n, colorShadersMap.get(actualShader)));
	            } else {
	                p.checkNextToken("p");
	                Point3D p2 = parsePoint(); 
	                p.checkNextToken("p");
	                Point3D p3 = parsePoint();
	                scene.add(new Plane(p1, p2, p3, colorShadersMap.get(actualShader)));
	            }
            } else {
            	Point3D p1 = transform.transform(new Point3D(0,0,0));
            	Vector3D n = transform.transform(new Vector3D(0,1,0)).substract(new Vector3D(p1)).normalize();
            	
                scene.add(new Plane(p1, n, colorShadersMap.get(actualShader)));
            }
        } 
//            else if (type.equals("cornellbox")) {
//            UI.printInfo(Module.API, "Reading cornell box ...");
//            if (transform != null)
//                UI.printWarning(Module.API, "Instancing is not supported on cornell box -- ignoring transform");
//            p.checkNextToken("corner0");
//            api.parameter("corner0", parsePoint());
//            p.checkNextToken("corner1");
//            api.parameter("corner1", parsePoint());
//            p.checkNextToken("left");
//            api.parameter("leftColor", parseColor());
//            p.checkNextToken("right");
//            api.parameter("rightColor", parseColor());
//            p.checkNextToken("top");
//            api.parameter("topColor", parseColor());
//            p.checkNextToken("bottom");
//            api.parameter("bottomColor", parseColor());
//            p.checkNextToken("back");
//            api.parameter("backColor", parseColor());
//            p.checkNextToken("emit");
//            api.parameter("radiance", parseColor());
//            if (p.peekNextToken("samples"))
//                api.parameter("samples", p.getNextInt());
//            new CornellBox().init(name, api);
//            noInstance = true; // instancing is handled natively by the init
//            // method
//        }
        
        else if (type.equals("generic-mesh")) {
        	if(debug) {
        		System.out.println("Reading generic mesh... ");
        	}
            // parse vertices
            p.checkNextToken("points");
            int np = p.getNextInt();
            Point3D[] points = parsePointArray(np);
            float[] uvVertex = null, normalsVertex = null;
            UVType uvType;
            NormalType normalType;
            
            // transform all points
            if ( transform != null ) {
            	for ( int i = 0; i < np; i++ ) {
            		points[i] = transform.transform(points[i]);
            	}
            }
            
            // parse triangle indices
            p.checkNextToken("triangles");
            int nt = p.getNextInt();
            int[] triangles = parseIntArray(nt * 3);
            
            // parse normals
            p.checkNextToken("normals");
            if (p.peekNextToken("vertex")){
            	normalsVertex = parseFloatArray(np * 3);
            	normalType = NormalType.VERTEX;
            }
            else if (p.peekNextToken("facevarying")){
            	normalsVertex = parseFloatArray(nt * 9);
            	normalType = NormalType.FACEVARYING;
            }
            else {
                p.checkNextToken("none");
                normalType = NormalType.NONE;
            }
            
            // parse texture coordinates
            p.checkNextToken("uvs");
            if (p.peekNextToken("vertex")){
            	uvVertex = parseFloatArray(np * 2);
            	uvType = UVType.VERTEX;
            }
            else if (p.peekNextToken("facevarying")){
            	uvVertex = parseFloatArray(nt * 6);
            	uvType = UVType.FACEVARYING;
            }
            else {
                p.checkNextToken("none");
                uvType = UVType.NONE;
            }
            
            if (p.peekNextToken("face_shaders")) {
            	parseIntArray(nt);
            }

            scene.add(new TriangleMesh(points, triangles, colorShadersMap.get(actualShader), uvType, uvVertex, normalType, normalsVertex, transform));
        }
        
//        else if (type.equals("hair")) {
//            UI.printInfo(Module.API, "Reading hair curves: %s ... ", name);
//            p.checkNextToken("segments");
//            api.parameter("segments", p.getNextInt());
//            p.checkNextToken("width");
//            api.parameter("widths", p.getNextFloat());
//            p.checkNextToken("points");
//            api.parameter("points", "point", "vertex", parseFloatArray(p.getNextInt()));
//            api.geometry(name, new Hair());
//        } else if (type.equals("janino-tesselatable")) {
//            UI.printInfo(Module.API, "Reading procedural primitive: %s ... ", name);
//            String code = p.getNextCodeBlock();
//            try {
//                Tesselatable tess = (Tesselatable) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(code)), Tesselatable.class, ClassLoader.getSystemClassLoader());
//                api.geometry(name, tess);
//            } catch (CompileException e) {
//                UI.printDetailed(Module.API, "Compiling: %s", code);
//                UI.printError(Module.API, "%s", e.getMessage());
//                e.printStackTrace();
//                noInstance = true;
//            } catch (ParseException e) {
//                UI.printDetailed(Module.API, "Compiling: %s", code);
//                UI.printError(Module.API, "%s", e.getMessage());
//                e.printStackTrace();
//                noInstance = true;
//            } catch (ScanException e) {
//                UI.printDetailed(Module.API, "Compiling: %s", code);
//                UI.printError(Module.API, "%s", e.getMessage());
//                e.printStackTrace();
//                noInstance = true;
//            } catch (IOException e) {
//                UI.printDetailed(Module.API, "Compiling: %s", code);
//                UI.printError(Module.API, "%s", e.getMessage());
//                e.printStackTrace();
//                noInstance = true;
//            }
//        } else if (type.equals("teapot")) {
//            UI.printInfo(Module.API, "Reading teapot: %s ... ", name);
//            boolean hasTesselationArguments = false;
//            if (p.peekNextToken("subdivs")) {
//                api.parameter("subdivs", p.getNextInt());
//                hasTesselationArguments = true;
//            }
//            if (p.peekNextToken("smooth")) {
//                api.parameter("smooth", p.getNextBoolean());
//                hasTesselationArguments = true;
//            }
//            if (hasTesselationArguments)
//                api.geometry(name, (Tesselatable) new Teapot());
//            else
//                api.geometry(name, (PrimitiveList) new Teapot());
//        } else if (type.equals("gumbo")) {
//            UI.printInfo(Module.API, "Reading gumbo: %s ... ", name);
//            boolean hasTesselationArguments = false;
//            if (p.peekNextToken("subdivs")) {
//                api.parameter("subdivs", p.getNextInt());
//                hasTesselationArguments = true;
//            }
//            if (p.peekNextToken("smooth")) {
//                api.parameter("smooth", p.getNextBoolean());
//                hasTesselationArguments = true;
//            }
//            if (hasTesselationArguments)
//                api.geometry(name, (Tesselatable) new Gumbo());
//            else
//                api.geometry(name, (PrimitiveList) new Gumbo());
//        } else if (type.equals("julia")) {
//            UI.printInfo(Module.API, "Reading julia fractal: %s ... ", name);
//            if (p.peekNextToken("q")) {
//                api.parameter("cw", p.getNextFloat());
//                api.parameter("cx", p.getNextFloat());
//                api.parameter("cy", p.getNextFloat());
//                api.parameter("cz", p.getNextFloat());
//            }
//            if (p.peekNextToken("iterations"))
//                api.parameter("iterations", p.getNextInt());
//            if (p.peekNextToken("epsilon"))
//                api.parameter("epsilon", p.getNextFloat());
//            api.geometry(name, new JuliaFractal());
//        } else if (type.equals("particles") || type.equals("dlasurface")) {
//            if (type.equals("dlasurface"))
//                UI.printWarning(Module.API, "Deprecated object type: \"dlasurface\" - please use \"particles\" instead");
//            p.checkNextToken("filename");
//            String filename = p.getNextToken();
//            boolean littleEndian = false;
//            if (p.peekNextToken("little_endian"))
//                littleEndian = true;
//            UI.printInfo(Module.USER, "Loading particle file: %s", filename);
//            File file = new File(filename);
//            FileInputStream stream = new FileInputStream(filename);
//            MappedByteBuffer map = stream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
//            if (littleEndian)
//                map.order(ByteOrder.LITTLE_ENDIAN);
//            FloatBuffer buffer = map.asFloatBuffer();
//            float[] data = new float[buffer.capacity()];
//            for (int i = 0; i < data.length; i++)
//                data[i] = buffer.get(i);
//            stream.close();
//            api.parameter("particles", "point", "vertex", data);
//            if (p.peekNextToken("num"))
//                api.parameter("num", p.getNextInt());
//            else
//                api.parameter("num", data.length / 3);
//            p.checkNextToken("radius");
//            api.parameter("radius", p.getNextFloat());
//            api.geometry(name, new ParticleSurface());
//        } else if (type.equals("file-mesh")) {
//            UI.printInfo(Module.API, "Reading file mesh: %s ... ", name);
//            p.checkNextToken("filename");
//            api.parameter("filename", p.getNextToken());
//            if (p.peekNextToken("smooth_normals"))
//                api.parameter("smooth_normals", p.getNextBoolean());
//            api.geometry(name, new FileMesh());
//        } else if (type.equals("bezier-mesh")) {
//            UI.printInfo(Module.API, "Reading bezier mesh: %s ... ", name);
//            p.checkNextToken("n");
//            int nu, nv;
//            api.parameter("nu", nu = p.getNextInt());
//            api.parameter("nv", nv = p.getNextInt());
//            if (p.peekNextToken("wrap")) {
//                api.parameter("uwrap", p.getNextBoolean());
//                api.parameter("vwrap", p.getNextBoolean());
//            }
//            p.checkNextToken("points");
//            float[] points = new float[3 * nu * nv];
//            for (int i = 0; i < points.length; i++)
//                points[i] = p.getNextFloat();
//            api.parameter("points", "point", "vertex", points);
//            if (p.peekNextToken("subdivs"))
//                api.parameter("subdivs", p.getNextInt());
//            if (p.peekNextToken("smooth"))
//                api.parameter("smooth", p.getNextBoolean());
//            api.geometry(name, (Tesselatable) new BezierMesh());
//        } 
        else {
        	if(debug)
        		System.out.println("Unrecognized object type: " );
            consumeUnrecognizedBlock(p.getNextToken(), 1);
            return;
//            noInstance = true;
        }
        if (!noInstance) {
            // create instance
//            api.parameter("shaders", shaders);
//            if (modifiers != null)
//                api.parameter("modifiers", modifiers);
//            if (transform != null)
//                api.parameter("transform", transform);
//            api.instance(name + ".instance", name);
        }
        p.checkNextToken("}");
    }

//    private void parseInstanceBlock(SunflowAPI api) throws ParserException, IOException {
//        p.checkNextToken("{");
//        p.checkNextToken("name");
//        String name = p.getNextToken();
//        UI.printInfo(Module.API, "Reading instance: %s ...", name);
//        p.checkNextToken("geometry");
//        String geoname = p.getNextToken();
//        p.checkNextToken("transform");
//        api.parameter("transform", parseMatrix());
//        String[] shaders;
//        if (p.peekNextToken("shaders")) {
//            int n = p.getNextInt();
//            shaders = new String[n];
//            for (int i = 0; i < n; i++)
//                shaders[i] = p.getNextToken();
//        } else {
//            p.checkNextToken("shader");
//            shaders = new String[] { p.getNextToken() };
//        }
//        api.parameter("shaders", shaders);
//        String[] modifiers = null;
//        if (p.peekNextToken("modifiers")) {
//            int n = p.getNextInt();
//            modifiers = new String[n];
//            for (int i = 0; i < n; i++)
//                modifiers[i] = p.getNextToken();
//        } else if (p.peekNextToken("modifier"))
//            modifiers = new String[] { p.getNextToken() };
//        if (modifiers != null)
//            api.parameter("modifiers", modifiers);
//        api.instance(name, geoname);
//        p.checkNextToken("}");
//    }

    private void parseLightBlock(Scene scene) throws ParserException, IOException {
        p.checkNextToken("{");
        p.checkNextToken("type");
        if (p.peekNextToken("mesh")) {
            System.err.println( "Deprecated light type: mesh");
            p.checkNextToken("name");
            String name = p.getNextToken();
            
            if(debug)
            	System.out.println( "Reading light mesh: " + name);
            
            p.checkNextToken("emit");
            parseColor();
            
            if (p.peekNextToken("samples"))
            	p.getNextInt();
            else
            	System.err.println("Samples keyword not found");

            int numVertices = p.getNextInt();
            int numTriangles = p.getNextInt();
            float[] points = new float[3 * numVertices];
            int[] triangles = new int[3 * numTriangles];
            for (int i = 0; i < numVertices; i++) {
                p.checkNextToken("v");
                points[3 * i + 0] = p.getNextFloat();
                points[3 * i + 1] = p.getNextFloat();
                points[3 * i + 2] = p.getNextFloat();
                // ignored
                p.getNextFloat();
                p.getNextFloat();
                p.getNextFloat();
                p.getNextFloat();
                p.getNextFloat();
            }
            for (int i = 0; i < numTriangles; i++) {
                p.checkNextToken("t");
                triangles[3 * i + 0] = p.getNextInt();
                triangles[3 * i + 1] = p.getNextInt();
                triangles[3 * i + 2] = p.getNextInt();
            }
//            api.parameter("points", "point", "vertex", points);
//            api.parameter("triangles", triangles);
//            TriangleMeshLight mesh = new TriangleMeshLight();
//            mesh.init(name, api);
        } else if (p.peekNextToken("point")) {
        	if(debug){
        		System.out.println("Reading point light ...");
        	}
            Color pow;
            float po = 1.0f;
            if (p.peekNextToken("color")) {
                pow = parseColor();
                p.checkNextToken("power");
                po = p.getNextFloat();
                pow.mul(po);
            } else {
                System.err.println("Deprecated color specification - please use color and power instead");
                p.checkNextToken("power");
                pow = parseColor();
            }
            p.checkNextToken("p");
            Point3D pos = parsePoint();
            
            if ( Run.getPenumbraCount() > 0 ) {
            	if ( debug ) {
            		System.out.println("Penumbra requested, using a spherical light instead of a point light.");
            	}
            	
            	scene.add(new SphereLight(pow, pos, 1.0f, Run.getPenumbraCount() ));
            } else {
            	scene.add(new PointLight(pow, pos));
            }
        } else if (p.peekNextToken("spherical")) {
        	if(debug) {
        		System.out.println("Reading spherical light ...");
        	}
        	
            p.checkNextToken("color");
            Color pow = parseColor();
            
            p.checkNextToken("radiance");
            pow.mul(p.getNextFloat());
            
            p.checkNextToken("center");
            Point3D pos = parsePoint();
            
            p.checkNextToken("radius");
            float radius = p.getNextFloat();
            
            p.checkNextToken("samples");
            int samples = p.getNextInt();
            
            scene.add(new SphereLight( pow, pos, radius, samples ));
        } 
        else if (p.peekNextToken("directional")) {
        	if(debug)
        		System.out.println("Reading directional light ...");
        	
            p.checkNextToken("source");
            Point3D s = parsePoint();
            
            p.checkNextToken("target");
            Point3D t = parsePoint();
            
            p.checkNextToken("radius");
            float radius = p.getNextFloat();
            
            p.checkNextToken("emit");
            Color e = parseColor();
            
            if (p.peekNextToken("intensity")) {
                float i = p.getNextFloat();
                e.mul(i);
            } else {
                System.err.println("Deprecated color specification - please use emit and intensity instead");
            }
            
            scene.add(new DirectionalLight(e, s, new Vector3D(t.substract(s)), radius));
        } else if (p.peekNextToken("ibl")) {
        	if(debug)
            System.out.println("Reading image based light ...");
            p.checkNextToken("image");
            p.getNextToken();
            p.checkNextToken("center");
            parseVector();
            p.checkNextToken("up");
            parseVector();
            p.checkNextToken("lock");
            p.getNextBoolean();
            if (p.peekNextToken("samples"))
            	p.getNextInt();
            else
                System.err.println("Samples keyword not found");
//            api.parameter("samples", samples);
//            ImageBasedLight ibl = new ImageBasedLight();
//            ibl.init(api.getUniqueName("ibl"), api);
        } else if (p.peekNextToken("meshlight")) {
            p.checkNextToken("name");
            String name = p.getNextToken();
            if(debug)
            	System.out.println("Reading meshlight: " + name);
            p.checkNextToken("emit");
            parseColor();
            p.peekNextToken("radiance");
//                float r = p.getNextFloat();
            p.getNextFloat();
//                e.mul(r);
//            } else
//                UI.printWarning(Module.API, "Deprecated color specification - please use emit and radiance instead");
//            api.parameter("radiance", e);
//            int samples = numLightSamples;
//            if (p.peekNextToken("samples"))
            p.peekNextToken("samples");
//                samples = p.getNextInt();
            p.getNextInt();
//            else
//                UI.printWarning(Module.API, "Samples keyword not found - defaulting to %d", samples);
//            api.parameter("samples", samples);
//            // parse vertices
            p.checkNextToken("points");
            int np = p.getNextInt();
//            api.parameter("points", "point", "vertex", parseFloatArray(np * 3));
            parseFloatArray(np * 3);
//            // parse triangle indices
            p.checkNextToken("triangles");
            int nt = p.getNextInt();
//            api.parameter("triangles", parseIntArray(nt * 3));
            parseIntArray(nt * 3);
//            TriangleMeshLight mesh = new TriangleMeshLight();
//            mesh.init(name, api);
        } else if (p.peekNextToken("sunsky")) {
            p.checkNextToken("up");
//            api.parameter("up", parseVector());
            parseVector();
            p.checkNextToken("east");
//            api.parameter("east", parseVector());
            parseVector();
            p.checkNextToken("sundir");
//            api.parameter("sundir", parseVector());
            parseVector();
            p.checkNextToken("turbidity");
//            api.parameter("turbidity", p.getNextFloat());
            p.getNextFloat();
//            if (p.peekNextToken("samples"))
            p.peekNextToken("samples");
//                api.parameter("samples", p.getNextInt());
            p.getNextInt();
//            SunSkyLight sunsky = new SunSkyLight();
//            sunsky.init(api.getUniqueName("sunsky"), api);
        } 
        else{
        	System.err.println( "Unrecognized object type: "+ p.getNextToken());
        }
        p.checkNextToken("}");
    }

    private Color parseColor() throws IOException, ParserException {
    	float r,g,b;
    	String space;
    	
        if (p.peekNextToken("{")) {
            space = p.getNextToken();
            Color c = null;
            if (space.equals("sRGB nonlinear")) {
                r = p.getNextFloat();
                g = p.getNextFloat();
                b = p.getNextFloat();
                c = new Color(r, g, b);
                
                c.toLinear();
            } else if (space.equals("sRGB linear")) {
                r = p.getNextFloat();
                g = p.getNextFloat();
                b = p.getNextFloat();
                c = new Color(r, g, b);
            } else
            	System.err.println("Unrecognized color space: " + space);

            p.checkNextToken("}");
            return c;
        } else {
            r = p.getNextFloat();
            g = p.getNextFloat();
            b = p.getNextFloat();
            return new Color(r, g, b);
        }
    }

    private Point3D parsePoint() throws IOException {
        float x = p.getNextFloat();
        float y = p.getNextFloat();
        float z = p.getNextFloat();
        return new Point3D(x, y, z);
    }

    private Vector3D parseVector() throws IOException {
        float x = p.getNextFloat();
        float y = p.getNextFloat();
        float z = p.getNextFloat();
        return new Vector3D(x, y, z);
    }

    private int[] parseIntArray(int size) throws IOException {
        int[] data = new int[size];
        for (int i = 0; i < size; i++)
            data[i] = p.getNextInt();
        return data;
    }

    private float[] parseFloatArray(int size) throws IOException {
        float[] data = new float[size];
        for (int i = 0; i < size; i++)
            data[i] = p.getNextFloat();
        return data;
    }
    
    private Point3D[] parsePointArray(int size) throws IOException {
        Point3D[] data = new Point3D[size];
        for (int i = 0; i < size; i++)
            data[i] = parsePoint();
        return data;
    }
    
    private Matrix4 parseMatrix() throws IOException, ParserException {
        if (p.peekNextToken("row")) {
            return new Matrix4(parseFloatArray(16), true);
        } else if (p.peekNextToken("col")) {
            return new Matrix4(parseFloatArray(16), false);
        } else {
    		Matrix4 m = Matrix4.identity();
            p.checkNextToken("{");
            while (!p.peekNextToken("}")) {
                if (p.peekNextToken("translate")) {
                    float x = p.getNextFloat();
                    float y = p.getNextFloat();
                    float z = p.getNextFloat();
                    m = m.translate(x, y, z);
                } else if (p.peekNextToken("scaleu")) {
                    float s = p.getNextFloat();
                    m = m.scale(s);
                } else if (p.peekNextToken("scalex")) {
                    float x = p.getNextFloat();
                    m = m.scale(x, 1, 1);
                } else if (p.peekNextToken("scaley")) {
                    float y = p.getNextFloat();
                    m = m.scale(1, y, 1);
                } else if (p.peekNextToken("scalez")) {
                    float z = p.getNextFloat();
                    m = m.scale(1, 1, z);
                } else if (p.peekNextToken("rotatex")) {
                    float angle = p.getNextFloat();
                    m = m.rotateX((float) Math.toRadians(angle));
                } else if (p.peekNextToken("rotatey")) {
                    float angle = p.getNextFloat();
                    m = m.rotateY((float) Math.toRadians(angle));
                } else if (p.peekNextToken("rotatez")) {
                    float angle = p.getNextFloat();
                    m = m.rotateZ((float) Math.toRadians(angle));
                } else if (p.peekNextToken("rotate")) {
                    float x = p.getNextFloat();
                    float y = p.getNextFloat();
                    float z = p.getNextFloat();
                    float angle = p.getNextFloat();
                    m = m.rotate(x, y, z, (float) Math.toRadians(angle));
                } else
                    System.err.println( "Unrecognized transformation type: " + p.getNextToken());
            }
            return m;
        }
    }
}
