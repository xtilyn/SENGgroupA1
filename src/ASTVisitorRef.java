import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ASTVisitorRef extends org.eclipse.jdt.core.dom.ASTVisitor {

    private int declarationsCount = 0;
    private int referencesCount = 0;
    private String javaType;
    Set<String> names = new HashSet<>();
    private CompilationUnit cu;

    private List<Integer> startPositions;

    public ASTVisitorRef(int declarationsCount, int referencesCount, String javaType) {
        this.declarationsCount = declarationsCount;
        this.referencesCount = referencesCount;
        this.javaType = javaType;
        startPositions = new ArrayList<>();
    }

    public void setCU(CompilationUnit cu) {
        this.cu = cu;
    }

    // checking declarations
    public boolean visit(VariableDeclarationFragment node) {
        SimpleName name = node.getName();
        String typeSimpleName;

        if (node.getParent() instanceof VariableDeclarationStatement && !(node.getParent().getParent() instanceof Block)
                ) {
            VariableDeclarationStatement declaration = (VariableDeclarationStatement) node.getParent();
            typeSimpleName = declaration.getType().resolveBinding().getQualifiedName();

            if (typeSimpleName.equals(javaType)) {
                referencesCount++;
                System.out.println("VARIABLE_DECLARATION_FRAG_STMENT reference found: typeSimpleName is " + typeSimpleName + " identifier: " + name);
                System.out.println("at line: " + cu.getLineNumber(node.getStartPosition()));
                System.out.println("USAGE: " + (node.getParent().getParent()));
                System.out.println("Parent class: " + (node.getParent().getParent().getClass()) + "\n\n");
                startPositions.add(node.getStartPosition());
            }

        }

        return true;
    }


    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.resolveBinding().getQualifiedName().equals(javaType)) {
            declarationsCount++;
        }
        return super.visit(node);
    }


    // checking references
    public boolean visit(SimpleName node) {
        if (node.getFullyQualifiedName().equals(javaType) && !(node.getParent() instanceof TypeDeclaration)
                ) {
            if ((node.getParent().getParent() instanceof CompilationUnit)) {

                if (node.getParent() instanceof ImportDeclaration) { // check if it's an 'import' statement
                    referencesCount++;
                    System.out.println("SIMPLENAME IMPORT reference found: " + node.getFullyQualifiedName() + " identifier: " + node.getIdentifier());
                    System.out.println("at line: " + cu.getLineNumber(node.getStartPosition()));
                    System.out.println("USAGE: " + (node.getParent().getParent()));
                    System.out.println("Parent class: " + (node.getParent().getParent().getClass()) + "\n\n");
                }
            } else {
                referencesCount++;
                System.out.println("SIMPLENAME reference found: " + node.getFullyQualifiedName() + " identifier: " + node.getIdentifier());
                System.out.println("at line: " + cu.getLineNumber(node.getStartPosition()));
                System.out.println("USAGE: " + (node.getParent().getParent()));
                System.out.println("Parent class: " + (node.getParent().getParent().getClass()) + "\n\n");
            }
        }

        if (node.getParent() instanceof TypeDeclaration && node.getFullyQualifiedName().equals(javaType)) {
            if (node.getParent().getParent() instanceof TypeDeclaration) { // if this node is an inner-class
                System.out.println("Found inner class: "+ node.getFullyQualifiedName());
                declarationsCount++;
            }
            System.out.println("SIMPLENAME & TYPEDECLARATION: " + ((TypeDeclaration)node.getParent()).getName());
//            ASTNode something = node.getParent().getParent();
//            if (something != null)
//                if (something.getClass() != CompilationUnit.class)
//                    declarationsCount++;
        }

        return true;
    }



    @Override
    public boolean visit(PrimitiveType node) {
        if (node.getPrimitiveTypeCode().toString().equals(javaType)) {
            referencesCount++;
            System.out.println("PRIMITIVE_TYPE reference found: " + node.getPrimitiveTypeCode());
            System.out.println("at line: " + cu.getLineNumber(node.getStartPosition()));
            System.out.println("USAGE: " + (node.getParent().getParent()));
            System.out.println("Parent class: " + (node.getParent().getParent().getClass()) + "\n\n");

        }
        return true;
    }

    public int getDeclarationsCount() {
        return declarationsCount;
    }

    public int getReferencesCount() {
        return referencesCount;
    }

    public String getJavaType() {
        return javaType;
    }
}
