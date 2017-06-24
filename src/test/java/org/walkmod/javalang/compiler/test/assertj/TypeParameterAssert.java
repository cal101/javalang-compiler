package org.walkmod.javalang.compiler.test.assertj;

import org.walkmod.javalang.ast.TypeParameter;
import org.walkmod.javalang.ast.expr.AnnotationExpr;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;

public class TypeParameterAssert extends AbstractNodeAssert<TypeParameterAssert, TypeParameter> {

    public TypeParameterAssert(TypeParameter actual) {
        super(actual, TypeParameterAssert.class);
    }

    public TypeParameterAssert hasName(String name) {
        assertEqualsName(actual.getName(), name);
        return this;
    }

    public TypeParameterAssert hasSymbolName(String name) {
        assertEqualsSymbolName(actual.getSymbolName(), name);
        return this;
    }

    public ExtListAssert<AnnotationExprAssert, AnnotationExpr> annotations() {
        return AssertUtil.assertThat(actual.getAnnotations(), AnnotationExprAssert.class,
                navigationDescription("annotations"));
    }

    public ExtListAssert<ClassOrInterfaceTypeAssert, ClassOrInterfaceType> typeBound() {
        return AssertUtil.assertThat(actual.getTypeBound(), ClassOrInterfaceTypeAssert.class,
                navigationDescription("annotations"));
    }
}
