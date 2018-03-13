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

    private static ASTVisitor astVisitor;

	/**
	 * Parse the string (file content) and count declarations and references
	 * 
	 * @param str
	 *            string to parse
	 */
	private static void parse(String str, String[] srcPaths, String unitName) {
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

		cu.accept(astVisitor);

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
	private static String readFileToString(String filePath) throws IOException {
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
	private static void readFilesInDir(String pathname) throws IOException {
		File dirs = new File(pathname);
		String dirPath = dirs.getCanonicalPath() + File.separator;

		File root = new File(dirPath);
		File[] files = root.listFiles();
		System.out.println("Files in directory: " + Arrays.toString(files));
		String filePath;

		assert files != null;
		for (File f : files) {
			filePath = f.getAbsolutePath();
			if (f.isFile() && filePath.endsWith(".java")) {
				parse(readFileToString(filePath), new String[] { pathname }, f.getName());
			}
		}
	}

	public static void main(String[] args) throws IOException {
		String pathname = args[0];
		String javaType = args[1];
		astVisitor = new ASTVisitor(0,0,javaType);
		String[] ting = javaType.split("\\.");
		javaType = ting[ting.length-1];
		readFilesInDir(pathname);
		System.out.print(javaType + ". Declarations found: " + astVisitor.getDeclarationsCount() + "; ");
		System.out.println("references found: " + astVisitor.getReferencesCount());
	}
}