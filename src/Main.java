import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {

            Set<String> names = new HashSet<>();

            // checking declarations
            public boolean visit(VariableDeclarationFragment node) {
                SimpleName name = node.getName();
                node.resolveBinding();
                String typeSimpleName;

                if(node.getParent() instanceof FieldDeclaration){
                    FieldDeclaration declaration = ((FieldDeclaration) node.getParent());
                    if(declaration.getType().isSimpleType()){
                        typeSimpleName = declaration.getType().toString().toLowerCase();
                        if (typeSimpleName.contains(javaType.toLowerCase())) {
                            declarationsCount++;
                            this.names.add(name.getIdentifier());
                        }
                    }

                }

                return false;
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
    private static void readFilesInDir() throws IOException{
//        Scanner scan = new Scanner(System.in);
//        String pathname = scan.nextLine();
//        String javaType = scan.nextLine();
        String pathname = "C:\\Users\\stealth 2017\\eclipse-workspace\\SENG3";
        javaType = "String";
        File dirs = new File(pathname);
        String dirPath = dirs.getCanonicalPath() + File.separator+"src"+File.separator;
        

        File root = new File(dirPath);
        File[] files = root.listFiles ( );
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
        readFilesInDir();
        System.out.print(javaType + ". Declarations found: " + declarationsCount + "; ");
        System.out.println("references found: " + referencesCount);
    }
}