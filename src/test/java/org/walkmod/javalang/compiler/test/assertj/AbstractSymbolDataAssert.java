package org.walkmod.javalang.compiler.test.assertj;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractClassAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.Objects;

import org.walkmod.javalang.ast.FieldSymbolData;
import org.walkmod.javalang.ast.MethodSymbolData;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.compiler.symbols.SymbolType;

public abstract class AbstractSymbolDataAssert<S extends AbstractSymbolDataAssert<S, A>, A extends SymbolData>
        extends AbstractObjectAssert<S, A> {

    AbstractSymbolDataAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public A asActual() {
        return actual;
    }

    protected <T> T asInstanceOf(final Class<T> clazz) {
        Objects.instance().assertIsInstanceOf(this.info, this.actual, clazz);
        return clazz.cast(actual);
    }

    protected String navigationDescription(final String description) {
        return AssertUtil.navigationDescription(this, description);
    }

    public MethodSymbolDataAssert asMethodSymbolData() {
        return AstAssertions.assertThat(asInstanceOf(MethodSymbolData.class))
                .as(navigationDescription("(MethodSymbolData)"));
    }

    public FieldSymbolDataAssert asFieldSymbolData() {
        return AstAssertions.assertThat(asInstanceOf(FieldSymbolData.class))
                .as(navigationDescription("(FieldSymbolData)"));
    }

    public SymbolTypeAssert asSymbolType() {
        return AstAssertions.assertThat(asInstanceOf(SymbolType.class)).as(navigationDescription("(SymbolType)"));
    }

    public ExtListAssert<AbstractClassAssert, Class<?>> boundClasses() {
        return AssertUtil.assertThat(actual.getBoundClasses(), AbstractClassAssert.class, "boundClasses");
    }

    public ExtListAssert<SymbolDataAssert, SymbolData> parameterizedTypes() {
        return AssertUtil.assertThat(actual.getParameterizedTypes(), SymbolDataAssert.class, "parameterizedTypes");
    }

    public AbstractSymbolDataAssert<S, A> hasName(String name) {
        name().isEqualTo(name);
        return this;
    }

    public AbstractCharSequenceAssert<?, String> name() {
        return Assertions.assertThat(actual.getName()).as(navigationDescription("name"));
    }

    public AbstractClassAssert<?> clazz() {
        return Assertions.assertThat(actual.getClazz()).as(navigationDescription("clazz"));
    }

    public AbstractSymbolDataAssert<?, ?> hasArrayCount(int value) {
        Assertions.assertThat(actual.getArrayCount()).as(navigationDescription("arrayCount")).isEqualTo(value);
        return this;
    }

    public AbstractSymbolDataAssert<S, A> isTemplateVariable(boolean v) {
        Assertions.assertThat(actual.isTemplateVariable()).as(navigationDescription("templateVariable")).isEqualTo(v);
        return this;
    }
}
