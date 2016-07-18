package com.droitfintech.workflow.internal.groovy;

/**
 * Created by barry on 11/25/15.
 */

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GroovyParser {
    private static class CodeTreeImpl implements SourceTree {
        private final GroovyRecognizer parser;

        public CodeTreeImpl(GroovyRecognizer parser) {
            this.parser = parser;
        }

        @Override
        public String[] getTokenNames() {
            return parser.getTokenNames();
        }

        @Override
        public AST getAST() {
            return parser.getAST();
        }
    }

    private GroovyParser() {
    }

    /**
     * Parses the input and returns the tree representation of the recognized source code.
     *
     * <p>
     * TODO : should this be static, or should the parser be instantiated?
     * </p>
     *
     * @param input
     * @return
     * @throws RecognitionException
     * @throws TokenStreamException
     */
    public static SourceTree parse(InputStream input) throws RecognitionException, TokenStreamException {
        SourceBuffer sourceBuffer = new SourceBuffer();
        InputStreamReader reader = new InputStreamReader(input);

        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(reader, sourceBuffer);

        GroovyLexer lexer = new GroovyLexer(unicodeReader);
        unicodeReader.setLexer(lexer);

        GroovyRecognizer parser = GroovyRecognizer.make(lexer);
        parser.setSourceBuffer(sourceBuffer);
        parser.setFilename("N/A");

        // This consumes the input
        parser.compilationUnit();

        return new CodeTreeImpl(parser);
    }

    /**
     * Parses the input string and returns the tree representation of the recognized source code.
     *
     * @see #parse(InputStream)
     */
    public static SourceTree parse(String input) throws RecognitionException, TokenStreamException {
        return parse(new ByteArrayInputStream(input.getBytes()));
    }

}
