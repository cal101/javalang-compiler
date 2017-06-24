package org.walkmod.javalang.compiler.test.assertj;

import org.assertj.core.api.Assertions;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;

public class ClassOrInterfaceTypeAssert extends AbstractNodeAssert<ClassOrInterfaceTypeAssert, ClassOrInterfaceType> {

    public ClassOrInterfaceTypeAssert(ClassOrInterfaceType actual) {
        super(actual, ClassOrInterfaceTypeAssert.class);
    }

    public AbstractSymbolDataAssert<?, ?> symbolData() {
        return symbolData(actual);
    }

    public ClassOrInterfaceTypeAssert hasName(String name) {
        Assertions.assertThat(actual.getName()).as(navigationDescription("name")).isEqualTo(name);
        return this;
    }
}
