package org.walkmod.javalang.compiler.test.assertj;

import org.walkmod.javalang.ast.expr.AnnotationExpr;

public class AnnotationExprAssert extends AbstractExpressionAssert<AnnotationExprAssert, AnnotationExpr> {

    public AnnotationExprAssert(AnnotationExpr actual) {
        super(actual, AnnotationExprAssert.class);
    }

    public AnnotationExprAssert hasName(String name) {
        name().hasName(name);
        return this;
    }

    public NameExprAssert name() {
        return AstAssertions.assertThat(actual.getName()).as(navigationDescription("name"));
    }

    public AbstractSymbolDataAssert symbolData() {
        return symbolData(actual);
    }
}
