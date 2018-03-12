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

    private static int declarationsCount = 0;
    private static int referencesCount = 0;
    private static String javaType;

    /**
     * Parse the string (file content) and count declarations and references
     * @param str string to parse
     */
    private static void parse(String str) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(str.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
        parser.setCompilerOptions(options);


        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {

            Set<String> names = new HashSet<>();

            // checking declarations
            public boolean visit(VariableDeclarationFragment node) {
                SimpleName name = node.getName();
                String typeSimpleName;

                if(node.getParent() instanceof FieldDeclaration){
                    FieldDeclaration declaration = ((FieldDeclaration) node.getParent());
                    if(declaration.getType().isSimpleType()){
                        typeSimpleName = declaration.getType().toString().toLowerCase();
                        if (typeSimpleName.equals(javaType.toLowerCase())) {
                            declarationsCount++;
                            System.out.println(declaration.getType());
                            this.names.add(name.getIdentifier());
                        }
                    }

                }

                return false;
            }

            @Override
            public boolean visit(TypeDeclaration node) {
               System.out.println(node.getName().resolveBinding());
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
     * @param filePath absolute path of file
     * @return contents of file
     * @throws IOException e
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

        return  fileData.toString();
    }

    /**
     * Read all files in specified directory
     * @throws IOException e
     */
    private static void readFilesInDir(String pathname) throws IOException{
        File dirs = new File(pathname);
        String dirPath = dirs.getCanonicalPath() + File.separator+"src"+File.separator;

        File root = new File(dirPath);
        File[] files = root.listFiles ( );
        System.out.println("Files in directory: " + Arrays.toString(files));
        String filePath;

        assert files != null;
        for (File f : files ) {
            filePath = f.getAbsolutePath();
            if(f.isFile()){
                parse(readFileToString(filePath));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String pathname = args[0];
        javaType = args[1].toLowerCase();
        String[] ting = javaType.split("\\.");
        System.out.println(Arrays.toString(ting));
        javaType = ting[ting.length-1];
        readFilesInDir(pathname);
        System.out.print(javaType + ". Declarations found: " + declarationsCount + "; ");
        System.out.println("references found: " + referencesCount);
    }
}