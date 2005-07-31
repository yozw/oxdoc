import java.util.*;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.security.*;
import java.net.*;

public class Config {

	public static String  Latex       = "latex.exe";
	public static String  LatexArg    = "";
	public static String  Dvipng      = "dvipng.exe";
	public static String  DvipngArg   = "-Q 10 -D 110";
	public static String  TempDir     = ".";
	public static String  OutputDir   = ".";
	public static boolean EnableLatex = true;
	public static String  ConfigFile  = "oxdoc.xml";
	public static ArrayList LatexPackages = new ArrayList();

	public static boolean setSimpleOption(String name) {
		if (name.equals("nolatex"))
			setOption("enablelatex", "0");
		else
			return false;

		return true;
	}

	public static boolean setOption(String name, String value) {
		if (name.equals("latex"))            Latex = value;
		else if (name.equals("dvipng"))      Dvipng = value;
		else if (name.equals("tempdir"))     TempDir = value;
		else if (name.equals("outputdir"))   OutputDir = value;
		else if (name.equals("latexpackages")) {
			String[] packages = value.split("[,;]");
			for (int i = 0; i < packages.length; i++)
				LatexPackages.add(packages[i]);
		}
		else if (name.equals("enablelatex")) EnableLatex = toBoolean(value);
		else return false;
		
		return true;
	}

	public static void listOptions() {
		System.out.println("    -outputdir <dir>       Specifies the output directory");
		System.out.println("    -tempdir <dir>         Provides the directory in which temporary files");
		System.out.println("                           will be written");
		System.out.println("    -latex <executable>    Provides the path to the LaTeX executable");
		System.out.println("    -latexpackages <...>   Provides a list of packages to load in LaTeX files");
		System.out.println("    -dvipng <executable>   Provides the path to the dvipng executable");
		System.out.println("    -nolatex               Disables LaTeX support. Formulas will be ");
		System.out.println("                           inserted literally");
	}

	public static void validate() {
		// check whether LaTeX executable exists
		if ( EnableLatex && !(new File(Latex)).exists() ) {
			oxdoc.warning("LaTeX executable not found. LaTeX support disabled");
			EnableLatex = false;
		}
		// check whether dvipng executable exists
		if ( EnableLatex && !(new File(Dvipng)).exists() ) {
			oxdoc.warning("Dvipng executable not found. LaTeX support disabled");
			EnableLatex = false;
		}

		if ( (OutputDir.length() != 0) && (!OutputDir.endsWith(File.separator) ) )
			OutputDir += File.separator;
		if ( (TempDir.length() != 0) && (!TempDir.endsWith(File.separator) ) )
			TempDir += File.separator;
	}

	private static boolean toBoolean(String value) {
		return (value.equals("1") || value.equals("yes"));
	}
	
	public static void load() {
		File appDir = getApplicationDirectory(oxdoc.class);
		if (appDir != null)
			load(appDir.toString() + File.separator + ConfigFile);
		load(ConfigFile);
	}

	public static File getApplicationDirectory( Class clas ) {
      	ProtectionDomain pd = clas.getProtectionDomain();
      	if ( pd == null ) return null;

      	CodeSource cs = pd.getCodeSource();
      	if ( cs == null ) return null;

      	URL url = cs.getLocation();
      	if ( url == null ) return null;

      	return new File( url.getFile() ).getParentFile();
   } 
	
	public static void load(String Filename) {
     	File file = new File(Filename);
		if (!file.exists()) 
			return;
		oxdoc.message("Loading configuration file " + Filename);

    	try {
         	// Parse the file
         	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         	Document doc = builder.parse(file);

         	// Find the tags of interest
         	NodeList nodes = doc.getElementsByTagName("option");
			for (int i = 0; i < nodes.getLength(); i++) {
            	Element element = (Element) nodes.item(i);

            	String name  = element.getAttribute("name");
				String value = element.getAttribute("value");
				setOption(name, value);
			}
		}
		catch (Exception E)
		{
		}		
	}
}