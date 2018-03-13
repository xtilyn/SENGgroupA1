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

	private static int declarationsCount;
	private static int referencesCount;
	private static String javaType;

	/**
	 * Parse the string (file content) and count declarations and references
	 * 
	 * @param str
	 *            string to parse
	 */
	private static void parse(String str, String[] srcPaths, String unitName) {
		ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setEnvironment(null, srcPaths, null, false);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		parser.setUnitName(unitName);

		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {

			Set<String> names = new HashSet<>();

			// checking declarations
			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				String typeSimpleName;

				//System.out.println(
				//		name + "   " + node.getParent().getClass() + "    " + node.getParent().getParent().getClass());
				//System.out.println(node.resolveBinding().getVariableDeclaration());

				if (node.getParent() instanceof FieldDeclaration) {
					FieldDeclaration declaration = ((FieldDeclaration) node.getParent());

					// System.out.println(declaration.getType());

					typeSimpleName = declaration.getType().toString();

					if (typeSimpleName.equals(javaType)) {
						referencesCount++;
						// System.out.println(declaration.getType());
						this.names.add(name.getIdentifier());
					}

				} else if (node.getParent() instanceof VariableDeclarationStatement) {
					VariableDeclarationStatement declaration = (VariableDeclarationStatement) node.getParent();
					typeSimpleName = declaration.getType().toString();

					if (typeSimpleName.equals(javaType)) {
						referencesCount++;
						// System.out.println(declaration.getType());
						this.names.add(name.getIdentifier());
					}

				}

				return false;
			}

			@Override
			public boolean visit(TypeDeclaration node) {
				System.out.println(node.getName());
				if (node.resolveBinding().getQualifiedName().endsWith(javaType)) {
					declarationsCount++;
				}
				else if(node.getName().toString().equals("Go")) {
					System.out.println("yep  " + node.resolveBinding().getQualifiedName());
				}
				return super.visit(node);
			}

			// checking references
			public boolean visit(SimpleName node) {
				if (this.names.contains(node.getIdentifier())) {
					referencesCount++;
				}
				return true;
			}
		});

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
		declarationsCount = 0;
		referencesCount = 0;
		if (args.length <= 1) {
			System.out.println("Not enough arguments");
			return;
		}
		String pathname = args[0];
		javaType = args[1];
		// String[] ting = javaType.split("\\.");
		// System.out.println(Arrays.toString(ting));
		// javaType = ting[ting.length-1];
		readFilesInDir(pathname);
		System.out.print(javaType + ". Declarations found: " + declarationsCount + "; ");
		System.out.println("references found: " + referencesCount);
	}
}