package com.droitfintech.workflow.internal.groovy;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal;
import org.codehaus.groovy.antlr.treewalker.Visitor;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

import java.util.Collection;
import java.util.HashSet;

/**
 * Get a list of d.attribute references from a groovy AST
 */
public class ReferenceCollector {

    /**
     * Reference to some variable.property.optionalProperty parsed from groovy AST.
     * variable in typically "d". lhs and rhs refer to the second "." left and right side.
     * the right side may be "".
     * The row and column location of the reference are returned for error reporting purposes.
     */
    public static class Reference {
        private String referenced;
        private String lhs;
        private String rhs;
        private int    line;
        private int    column;

        Reference(String referenced, String lhsValue, String rhsValue, int line, int column) {
            this.referenced = referenced;
            lhs = lhsValue != null ? lhsValue : "";
            rhs = rhsValue != null ? rhsValue : "";
            this.line = line;
            this.column = column;
        }

        @Override
        public int hashCode() {
            return (31 + lhs.hashCode()) * (31 + rhs.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Reference) {
                return lhs.equals(((Reference) obj).lhs) && rhs.equals(((Reference) obj).rhs);
            }
            return false;
        }

        @Override
        public String toString() {
            return "(" + line + "," + column + ") " + referenced + "." + lhs + "." + rhs;
        }

        public String getLhs() {
            return lhs;
        }

        public String getRhs() {
            return rhs;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }
    }

    private class SimpleVisitor extends VisitorAdapter {
        private int level = 0;
        private boolean foundD = false;
        private String module;
        private String variable;
        private String variableName = "d";
        private int foundLine = 0;
        private int foundColumn = 0;
        private HashSet<Reference> references = new HashSet<Reference>();

        public Collection<Reference> getReferences() {
            return references;
        }

        @Override
        public void push(GroovySourceAST t) {
            super.push(t);
            level++;
        }

        @Override
        public GroovySourceAST pop() {
            level--;
            return super.pop();
        }

        @Override
        public void visitDefault(GroovySourceAST t, int visit) {
            super.visitDefault(t, visit);

            if (visit == Visitor.CLOSING_VISIT) {
                switch(t.getType()) {
                    case GroovyTokenTypes.DOT: // (89) "."
                        if(foundD && variable != null) {
                            //references.add(variableName + "." + module + "." + variable);
                            references.add(new Reference(variableName, module, variable, foundLine, foundColumn));
                            foundD = false;
                        }
                        break;
                    case GroovyTokenTypes.IDENT: // (86) name
                        if(variableName.equals(t.getText())) {
                            foundColumn = t.getColumn();
                            foundLine = t.getLine();
                            foundD = true;
                            module = null;
                            variable = null;
                        } else if(foundD) {
                            if(module == null) {
                                module = t.getText();
                            } else if(variable == null) {
                                variable = t.getText();
                            }
                        }
                        break;
                    case GroovyTokenTypes.ELIST: // opening paren of function parameters
                        foundD = false;
                        break;
                    default:
                        if(foundD && module != null) {
                            //references.add(variableName + "." + module);
                            references.add(new Reference(variableName, module, "", foundLine, foundColumn));
                        }
                        foundD = false;
                        break;
                }
            }
        }
    }

    /**
     * Get a list of d.attribute references from a groovy AST. These will include d.x.y and only d.x but not functions like d.foo()
     * @param tree
     * @return
     */
    public Collection<Reference> getReferences(SourceTree tree) {
        SimpleVisitor visitor = new SimpleVisitor();
        PreOrderTraversal walker = new PreOrderTraversal(visitor);
        walker.process(tree.getAST());
        return visitor.getReferences();
    }
}
