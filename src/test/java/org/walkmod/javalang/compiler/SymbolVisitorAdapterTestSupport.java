package org.walkmod.javalang.compiler;

import java.util.HashMap;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.compiler.symbols.SymbolVisitorAdapter;
import org.walkmod.javalang.test.SemanticTest;

public class SymbolVisitorAdapterTestSupport extends SemanticTest {

    protected CompilationUnit run(String... code) throws Exception {
        CompilationUnit cu = compile(code);
        SymbolVisitorAdapter<HashMap<String, Object>> visitor = new SymbolVisitorAdapter<HashMap<String, Object>>();
        visitor.setClassLoader(getClassLoader());
        visitor.visit(cu, new HashMap<String, Object>());
        return cu;
    }
}
