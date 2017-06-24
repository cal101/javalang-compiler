package org.walkmod.javalang.compiler.test.assertj;

import org.walkmod.javalang.ast.TypeParameter;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;

public class ClassOrInterfaceDeclarationAssert
        extends AbstractTypeDeclarationAssert<ClassOrInterfaceDeclarationAssert, ClassOrInterfaceDeclaration> {

    ClassOrInterfaceDeclarationAssert(ClassOrInterfaceDeclaration actual) {
        super(actual, ClassOrInterfaceDeclarationAssert.class);
    }

    public ExtListAssert<ClassOrInterfaceTypeAssert, ClassOrInterfaceType> extends_() {
        return AssertUtil.assertThat(actual.getExtends(), ClassOrInterfaceTypeAssert.class,
                navigationDescription("extends"));
    }

    public ExtListAssert<ClassOrInterfaceTypeAssert, ClassOrInterfaceType> implements_() {
        return AssertUtil.assertThat(actual.getImplements(), ClassOrInterfaceTypeAssert.class,
                navigationDescription("implements"));
    }

    public ExtListAssert<TypeParameterAssert, TypeParameter> typeParameters() {
        return AssertUtil.assertThat(actual.getTypeParameters(), TypeParameterAssert.class,
                navigationDescription("implements"));
    }

    public SymbolDataAssert symbolData() {
        return symbolData(actual);
    }
}
