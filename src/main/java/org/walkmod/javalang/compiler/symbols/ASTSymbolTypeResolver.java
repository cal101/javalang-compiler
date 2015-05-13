package org.walkmod.javalang.compiler.symbols;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.TypeParameter;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.ast.type.PrimitiveType;
import org.walkmod.javalang.ast.type.PrimitiveType.Primitive;
import org.walkmod.javalang.ast.type.ReferenceType;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.javalang.ast.type.VoidType;
import org.walkmod.javalang.ast.type.WildcardType;
import org.walkmod.javalang.compiler.reflection.ClassInspector;
import org.walkmod.javalang.compiler.types.TypesLoaderVisitor;
import org.walkmod.javalang.visitors.GenericVisitorAdapter;

public class ASTSymbolTypeResolver extends
		GenericVisitorAdapter<SymbolType, List<TypeParameter>> implements
		SymbolTypeResolver<Type> {

	private SymbolTable symbolTable = null;

	private static ASTSymbolTypeResolver instance = null;

	private ASTSymbolTypeResolver() {

	}

	public static ASTSymbolTypeResolver getInstance() {
		if (instance == null) {
			instance = new ASTSymbolTypeResolver();
		}
		return instance;
	}

	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	@Override
	public SymbolType visit(PrimitiveType n, List<TypeParameter> arg) {
		SymbolType result = new SymbolType();
		Primitive pt = n.getType();
		if (pt.equals(Primitive.Boolean)) {
			result.setName(boolean.class.getName());

		} else if (pt.equals(Primitive.Char)) {
			result.setName(char.class.getName());
		} else if (pt.equals(Primitive.Double)) {
			result.setName(double.class.getName());
		} else if (pt.equals(Primitive.Float)) {
			result.setName(float.class.getName());
		} else if (pt.equals(Primitive.Int)) {
			result.setName(int.class.getName());
		} else if (pt.equals(Primitive.Long)) {
			result.setName(long.class.getName());
		} else if (pt.equals(Primitive.Short)) {
			result.setName(short.class.getName());
		} else if (pt.equals(Primitive.Byte)) {
			result.setName(byte.class.getName());
		}
		return result;
	}

	@Override
	public SymbolType visit(ClassOrInterfaceType type, List<TypeParameter> arg) {
		SymbolType result = null;

		String name = type.getName();
		ClassOrInterfaceType scope = type.getScope();
		Node parent = type.getParentNode();
		boolean isObjectCreationCtxt = (parent != null && parent instanceof ObjectCreationExpr);
		isObjectCreationCtxt = isObjectCreationCtxt
				&& ((ObjectCreationExpr) parent).getScope() != null;
		if (scope == null && !isObjectCreationCtxt) {

			if (arg != null) {
				Iterator<TypeParameter> it = arg.iterator();
				while (it.hasNext() && result == null) {
					TypeParameter next = it.next();
					if (next.getName().equals(name)) {
						List<ClassOrInterfaceType> bounds = next.getTypeBound();
						if (bounds == null || bounds.isEmpty()) {
							result = new SymbolType(Object.class);
						} else {
							List<SymbolType> params = new LinkedList<SymbolType>();
							for (ClassOrInterfaceType bound : bounds) {
								params.add(bound.accept(this, arg));
							}
							result = new SymbolType(params);
						}
					}
				}
			}
			if (result == null) {

				// it can be resolved through the symbol table (imports,
				// generics, sibling/children inner classes, package
				// classes)
				result = symbolTable
						.getType(
								name,
								org.walkmod.javalang.compiler.symbols.ReferenceType.TYPE);
				if (result != null) {
					result = result.clone();
				} else {
					SymbolType thisType = symbolTable
							.getType(
									"this",
									org.walkmod.javalang.compiler.symbols.ReferenceType.VARIABLE);
					if (thisType != null) {
						Class<?> clazz = thisType.getClazz();
						// we look for a declared class in one of our super
						// classes
						Class<?> superClass = clazz.getSuperclass();
						Class<?> nestedClass = ClassInspector.findClassMember(
								thisType.getClazz().getPackage(), name,
								superClass);

						// this is an inner class? If so, we look for a nested
						// class
						// in one of our parent classes
						while (clazz.isMemberClass() && nestedClass == null) {
							clazz = clazz.getDeclaringClass();
							nestedClass = ClassInspector.findClassMember(
									clazz.getPackage(), name, clazz);
						}
						// this is an anonymous class? If so, we look for a
						// nested
						// class in the enclosing class
						while (clazz.isAnonymousClass() && nestedClass == null) {
							clazz = clazz.getEnclosingClass();
							nestedClass = ClassInspector.findClassMember(
									clazz.getPackage(), name, clazz);
							while (clazz.isMemberClass() && nestedClass == null) {
								clazz = clazz.getDeclaringClass();
								nestedClass = ClassInspector.findClassMember(
										clazz.getPackage(), name, clazz);
							}
						}
						if (nestedClass != null) {
							result = new SymbolType(nestedClass);
						}

					}
				}
			}

		} else {
			// it is a fully qualified name or a inner class (>1 hop)

			String scopeName = "";
			if (isObjectCreationCtxt) {
				scopeName = ((ObjectCreationExpr) parent).getScope()
						.getSymbolData().getName()
						+ ".";
			}
			ClassOrInterfaceType ctxt = type;
			while (ctxt.getScope() != null) {
				ctxt = (ClassOrInterfaceType) ctxt.getScope();
				if (ctxt.getSymbolData() != null) {
					scopeName = ctxt.getName() + "$" + scopeName;
				} else {
					scopeName = ctxt.getName() + "." + scopeName;
				}
			}

			String innerClassName = name;
			if (scopeName.length() > 1) {
				innerClassName = scopeName.substring(0, scopeName.length() - 1)
						+ "$" + name;
			}
			String fullName = scopeName + name;

			result = symbolTable.getType(innerClassName,
					org.walkmod.javalang.compiler.symbols.ReferenceType.TYPE);
			if (result == null) {
				result = symbolTable
						.getType(
								fullName,
								org.walkmod.javalang.compiler.symbols.ReferenceType.TYPE);
				if (result == null) {
					// in the code appears B.C
					SymbolType scopeType = type.getScope().accept(this, arg);
					if (scopeType != null) {
						result = new SymbolType();
						result.setName(scopeType.getName() + "$" + name);
					} else {

						try {
							TypesLoaderVisitor.getClassLoader().loadClass(
									fullName);
						} catch (ClassNotFoundException e) {
							return null;
						}
						result = new SymbolType();
						// it is a type that has not previously imported
						result.setName(fullName);

					}
				}
			}

		}

		if (type.getTypeArgs() != null) {
			if (result == null) {
				result = new SymbolType();
			}
			List<SymbolType> typeArgs = new LinkedList<SymbolType>();

			for (Type typeArg : type.getTypeArgs()) {
				SymbolType aux = valueOf(typeArg);
				if (aux == null) {
					aux = new SymbolType(Object.class);
				}
				typeArgs.add(aux);
			}
			if (!typeArgs.isEmpty()) {
				result.setParameterizedTypes(typeArgs);
			}
		}
		return result;
	}

	@Override
	public SymbolType visit(VoidType n, List<TypeParameter> arg) {
		return new SymbolType(Void.class.getName());
	}

	@Override
	public SymbolType visit(WildcardType n, List<TypeParameter> arg) {
		SymbolType result = new SymbolType();
		if (n.toString().equals("?")) {
			result.setName("java.lang.Object");
		} else {
			ReferenceType extendsRef = n.getExtends();
			ReferenceType superRef = n.getSuper();
			if (extendsRef != null) {
				result = extendsRef.accept(this, arg);
			} else {
				result = superRef.accept(this, arg);
			}
		}
		return result;
	}

	public SymbolType visit(ReferenceType n, List<TypeParameter> arg) {
		Type containerType = n.getType();
		SymbolType result = null;
		if (containerType instanceof PrimitiveType) {
			result = new SymbolType(containerType.accept(this, arg).getName());

		} else if (containerType instanceof ClassOrInterfaceType) {

			result = containerType.accept(this, arg);

		}
		if (result != null) {
			result.setArrayCount(n.getArrayCount());
		}
		return result;
	}

	@Override
	public SymbolType valueOf(Type parserType) {
		return valueOf(parserType, null);
	}

	public SymbolType valueOf(Type parserType, List<TypeParameter> tps) {
		return parserType.accept(this, tps);
	}

}