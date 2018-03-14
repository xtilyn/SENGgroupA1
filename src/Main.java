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
	 * Read all files in specified directory
	 * 
	 * @throws IOException
	 *             e
	 */
	public static File[] readFilesInDir(String pathname) throws IOException {
		File dirs = new File(pathname);
		String dirPath = dirs.getCanonicalPath() + File.separator;

		File root = new File(dirPath);
		File[] files = root.listFiles();
		System.out.println("Files in directory: " + Arrays.toString(files));
		
		return files;
	}
	
	public static ASTVisitor parseAll(File[] files) throws IOException {
		String filePath;
		ASTVisitor astVisitor = new ASTVisitor(0,0,javaType);
		for (File f : files) {
			filePath = f.getAbsolutePath();
			if (f.isFile() && filePath.endsWith(".java")) {
				parse(readFileToString(filePath), new String[] { f.getParent() }, f.getName(), astVisitor);
			}
		}
		return astVisitor;
	}
	
	public static void setTarget(String type) {
		javaType = type;
	}

	public static void main(String[] args) {
		String pathname = args[0];
		javaType = args[1];
		String[] ting = javaType.split("\\.");			//comment out if trying to do the fully
		javaType = ting[ting.length-1];					//qualified java name thing
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