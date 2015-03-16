package org.walkmod.javalang.compiler.actions;

import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.ConstructorDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.body.TypeDeclaration;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.javalang.compiler.providers.SymbolActionProvider;
import org.walkmod.javalang.compiler.symbols.MethodSymbol;
import org.walkmod.javalang.compiler.symbols.Symbol;
import org.walkmod.javalang.compiler.symbols.SymbolAction;
import org.walkmod.javalang.compiler.symbols.SymbolEvent;
import org.walkmod.javalang.compiler.symbols.SymbolTable;
import org.walkmod.javalang.compiler.symbols.SymbolType;
import org.walkmod.javalang.compiler.types.TypeTable;

public class LoadMethodDeclarationsAction implements SymbolAction {

	private TypeTable<?> typeTable;
	private SymbolActionProvider actionProvider;

	public LoadMethodDeclarationsAction(TypeTable<?> typeTable,
			SymbolActionProvider actionProvider) {
		this.typeTable = typeTable;
		this.actionProvider = actionProvider;
	}

	private void pushMethod(Symbol symbol, SymbolTable table,
			MethodDeclaration md) throws Exception {

		Type type = md.getType();
		SymbolType resolvedType = typeTable.valueOf(type);
		resolvedType.setClazz(typeTable.loadClass(resolvedType));
		List<Parameter> params = md.getParameters();
		SymbolType[] args = null;
		if (params != null) {
			args = new SymbolType[params.size()];
			for (int i = 0; i < args.length; i++) {
				args[i] = typeTable.valueOf(params.get(i).getType());
			}
		}
		List<SymbolAction> actions = null;
		if (actionProvider != null) {
			actions = actionProvider.getActions(md);
		}
		MethodSymbol method = new MethodSymbol(md.getName(), resolvedType, md,
				symbol.getType(), args, actions);
		table.pushSymbol(method);
	}

	private void pushConstructor(Symbol symbol, SymbolTable table,
			ConstructorDeclaration md) throws Exception {
		Type type = new ClassOrInterfaceType(md.getName());
		SymbolType resolvedType = typeTable.valueOf(type);
		resolvedType.setClazz(typeTable.loadClass(resolvedType));
		List<Parameter> params = md.getParameters();
		SymbolType[] args = null;
		if (params != null) {
			args = new SymbolType[params.size()];
			for (int i = 0; i < args.length; i++) {
				args[i] = typeTable.valueOf(params.get(i).getType());
			}
		}
		List<SymbolAction> actions = null;
		if (actionProvider != null) {
			actions = actionProvider.getActions(md);
		}
		MethodSymbol method = new MethodSymbol(md.getName(), resolvedType, md,
				symbol.getType(), args, actions);
		table.pushSymbol(method);
	}

	@Override
	public void execute(Symbol symbol, SymbolTable table, SymbolEvent event)
			throws Exception {
		if (event.equals(SymbolEvent.PUSH)) {
			Node node = symbol.getLocation();
			if (node instanceof TypeDeclaration) {
				TypeDeclaration n = (TypeDeclaration) node;

				if (n.getMembers() != null) {

					for (BodyDeclaration member : n.getMembers()) {
						if (member instanceof MethodDeclaration) {
							MethodDeclaration md = (MethodDeclaration) member;
							pushMethod(symbol, table, md);

						} else if (member instanceof ConstructorDeclaration) {
							ConstructorDeclaration cd = (ConstructorDeclaration) member;
							pushConstructor(symbol, table, cd);
						}
					}
				}

			}
		}
	}
}
