import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

public class Main {

    private static String javaType;

	/**
	 * Parse the string (file content) and count declarations and references
	 * 
	 * @param str
	 *            string to parse
	 */
	private static void parse(String str, String[] srcPaths, String unitName, ASTVisitor visitor) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setEnvironment(null, srcPaths, null, false);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		parser.setUnitName(unitName);

		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		parser.setCompilerOptions(options);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(visitor);

	}

	/**
	 * Reads content of a file into a string
	 * 
	 * @param filePath
	 *            absolute path of file
	 * @return contents of file
	 * @throws IOException
	 *             e
	 */
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

	/**
	 * get list of files in directory
	 * 
	 * @param pathname
	 * 				path to directory
	 * 
	 * @return a File[] containing all files in directory
	 * 
	 * @throws IOException
	 *             e
	 */
	public static File[] readFilesInDir(String pathname) throws IOException {
		File dirs = new File(pathname);
		String dirPath = dirs.getCanonicalPath() + File.separator;

		File root = new File(dirPath);
		File[] files = root.listFiles();
		
		return files;
	}
	
	/**
	 * parse all files and count the number of declarations and references of javaType
	 * @param files array containing all files to be parsed
	 * @return an ASTVisitor from which the number of declarations and references can be counted
	 * @throws IOException if something goes wrong in readFileToString
	 */
	public static ASTVisitor parseAll(File[] files) throws IOException {
		String filePath;
		ASTVisitor astVisitor = new ASTVisitor(0,0,javaType);
		for (File f : files) {
			filePath = f.getAbsolutePath();
			if (f.isFile() && filePath.endsWith(".java")) {
				String name = f.getName();
				name = name.substring(0, name.length() - 5);
				parse(readFileToString(filePath), new String[] { f.getParent() }, name, astVisitor);
			}
		}
		return astVisitor;
	}
	
	public static void setTarget(String type) {
		javaType = type;
	}

	public static void main(String[] args) {
		if(args.length <= 1)
			return;
					
		String pathname = args[0];
		javaType = args[1];
		//String[] ting = javaType.split("\\.");			//comment out if trying to do the fully
		//javaType = ting[ting.length-1];					//qualified java name thing
		ASTVisitor pointer;
		
		try {
			File[] files = readFilesInDir(pathname);
			if(files == null)
				return;
			pointer = parseAll(files);
			System.out.print(javaType + ". Declarations found: " + pointer.getDeclarationsCount() + "; ");
			System.out.println("references found: " + pointer.getReferencesCount());
		}
		catch(IOException e){
			System.err.println("Something went horribaly wrong");
			System.err.println(e.getMessage());
		}
	}
}