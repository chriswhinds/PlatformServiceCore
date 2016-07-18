package com.droitfintech.workflow.internal.groovy;

/**
 * Created by barry on 11/25/15.
 */

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal;
import org.codehaus.groovy.antlr.treewalker.Visitor;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

import java.io.PrintStream;


public class TreePrinter implements Printer {

    public class SimpleVisitor extends VisitorAdapter {
        private int level = 0;

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
                for (int i = 0; i < level - 1; i++) {
                    stream.print("  ");
                }
                //t.getType() = JavaTokenTypes.ABSTRACT
                stream.println(t + " (" + Integer.toString(t.getType()) + ")");
            }
        }
    }

    private final PrintStream stream;

    public TreePrinter() {
        this(System.out);
    }

    public TreePrinter(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void write(SourceTree tree) {
        Visitor visitor = new SimpleVisitor();
        PreOrderTraversal walker = new PreOrderTraversal(visitor);
        walker.process(tree.getAST());
    }
}
