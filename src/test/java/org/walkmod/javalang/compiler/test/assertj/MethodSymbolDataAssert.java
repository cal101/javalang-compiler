package org.walkmod.javalang.compiler.test.assertj;

import java.lang.reflect.Method;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;

import org.walkmod.javalang.ast.MethodSymbolData;

public class MethodSymbolDataAssert extends AbstractSymbolDataAssert<MethodSymbolDataAssert, MethodSymbolData> {

    MethodSymbolDataAssert(MethodSymbolData actual) {
        super(actual, MethodSymbolDataAssert.class);
    }

    public AbstractObjectAssert<?, Method> method() {
        return Assertions.assertThat(actual.getMethod()).as(navigationDescription("method"));
    }
}
