package org.walkmod.javalang.compiler.test.assertj;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;
import org.walkmod.javalang.ast.body.Parameter;

public class ParameterAssert extends BaseParameterAssert<ParameterAssert, Parameter> {

    public ParameterAssert(Parameter actual) {
        super(actual, ParameterAssert.class);
    }

    public ParameterAssert hasSymbolName(String name) {
        symbolName().isEqualTo(name);
        return this;
    }

    public AbstractCharSequenceAssert<?, String> symbolName() {
        return Assertions.assertThat(actual.getSymbolName()).as(navigationDescription("symbolName"));
    }

    public AbstractSymbolDataAssert symbolData() {
        return symbolData(actual);
    }
}
