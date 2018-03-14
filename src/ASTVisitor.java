import org.eclipse.jdt.core.dom.*;

import java.util.HashSet;
import java.util.Set;

public class ASTVisitor extends org.eclipse.jdt.core.dom.ASTVisitor {

    private int declarationsCount = 0;
    private int referencesCount = 0;
    private String javaType;
    Set<String> names = new HashSet<>();

    public ASTVisitor(int declarationsCount, int referencesCount, String javaType) {
        this.declarationsCount = declarationsCount;
        this.referencesCount = referencesCount;
        this.javaType = javaType;
    }
    
    // checking declarations
    public boolean visit(VariableDeclarationFragment node) {
        SimpleName name = node.getName();
        String typeSimpleName;

        if (node.getParent() instanceof FieldDeclaration) {
            FieldDeclaration declaration = ((FieldDeclaration) node.getParent());

            typeSimpleName = declaration.getType().resolveBinding().getQualifiedName();			//not working for classes that are not inner classes

            if (typeSimpleName.equals(javaType)) {
                referencesCount++;
                this.names.add(name.getIdentifier());
            }

        } else if (node.getParent() instanceof VariableDeclarationStatement) {
            VariableDeclarationStatement declaration = (VariableDeclarationStatement) node.getParent();
            typeSimpleName = declaration.getType().resolveBinding().getQualifiedName();

            if (typeSimpleName.equals(javaType)) {
                referencesCount++;
                this.names.add(name.getIdentifier());
            }

        }

        return false;
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
        if (this.names.contains(node.getIdentifier())) {
            referencesCount++;
        }

        if (node.getParent() instanceof TypeDeclaration && node.getIdentifier().equals(javaType)) {
        	ASTNode something = node.getParent().getParent();
        	if(something != null)
        		if(something.getClass() != CompilationUnit.class)
        			declarationsCount++;
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
