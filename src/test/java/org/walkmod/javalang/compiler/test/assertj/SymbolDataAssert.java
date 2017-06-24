package org.walkmod.javalang.compiler.test.assertj;

import org.walkmod.javalang.ast.SymbolData;

public class SymbolDataAssert extends AbstractSymbolDataAssert<SymbolDataAssert, SymbolData> {

    public SymbolDataAssert(SymbolData actual) {
        super(actual, SymbolDataAssert.class);
    }

    public SymbolDataAssert hasName(String name) {
        name().isEqualTo(name);
        return this;
    }
}
