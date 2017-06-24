package org.walkmod.javalang.compiler.test.assertj;

import org.walkmod.javalang.ast.body.BaseParameter;
import org.walkmod.javalang.ast.expr.AssignExpr;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.IntegerLiteralExpr;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;

public class BaseParameterAssert<S extends BaseParameterAssert<S, A>, A extends BaseParameter>
        extends AbstractNodeAssert<S, A> {

    BaseParameterAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    /** public for reflection */
    public BaseParameterAssert(A actual) {
        this(actual, BaseParameterAssert.class);
    }

    public AbstractSymbolDataAssert symbolData() {
        return symbolData(actual);
    }
}
