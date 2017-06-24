package org.walkmod.javalang.compiler.test.assertj;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;

import org.walkmod.javalang.ast.TypeParameter;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.Parameter;

public class MethodDeclarationAssert extends AbstractNodeAssert<MethodDeclarationAssert, MethodDeclaration> {

    MethodDeclarationAssert(MethodDeclaration actual) {
        super(actual, MethodDeclarationAssert.class);
    }

    public BlockStmtAssert body() {
        return AstAssertions.assertThat(actual.getBody()).as(navigationDescription("body"));
    }

    public MethodDeclarationAssert hasName(String name) {
        name().isEqualTo(name);
        return this;
    }

    public AbstractCharSequenceAssert<?, String> name() {
        return Assertions.assertThat(actual.getName()).as(navigationDescription("name"));
    }

    public ExtListAssert<ParameterAssert, Parameter> parameters() {
        return AssertUtil.assertThat(actual.getParameters(), ParameterAssert.class,
                navigationDescription("parameters"));
    }

    public ExtListAssert<TypeParameterAssert, TypeParameter> typeParameters() {
        return AssertUtil.assertThat(actual.getTypeParameters(), TypeParameterAssert.class,
                navigationDescription("typeParameters"));
    }

    public SymbolDataAssert symbolData() {
        return symbolData(actual);
    }
}
