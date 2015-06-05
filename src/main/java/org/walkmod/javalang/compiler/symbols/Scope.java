/*
 Copyright (C) 2015 Raquel Pau and Albert Coroleu.
 
Walkmod is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Walkmod is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Walkmod.  If not, see <http://www.gnu.org/licenses/>.*/
package org.walkmod.javalang.compiler.symbols;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolDefinition;
import org.walkmod.javalang.compiler.reflection.MethodComparator;

public class Scope {

	private Map<String, ArrayList<Symbol<?>>> symbols = new HashMap<String, ArrayList<Symbol<?>>>();

	private List<SymbolAction> actions;

	private Symbol<?> rootSymbol = null;

	private int innerAnonymousClassCounter = 0;

	private boolean hasMethodsLoaded = false;

	private boolean hasFieldsLoaded = false;

	private Map<String, SymbolType> typeParams = null;

	public Scope() {
	}

	public Scope(Symbol<?> rootSymbol) {
		this.rootSymbol = rootSymbol;
	}

	public Scope(List<SymbolAction> actions) {
		this.actions = actions;

	}

	

	public void setRootSymbol(Symbol<?> rootSymbol) {
		this.rootSymbol = rootSymbol;
	}

	public void setHasMethodsLoaded(boolean hasMethodsLoaded) {
		this.hasMethodsLoaded = hasMethodsLoaded;
	}

	public boolean hasFieldsLoaded() {
		return hasFieldsLoaded;
	}

	public void setHasFieldsLoaded(boolean hasFieldsLoaded) {
		this.hasFieldsLoaded = hasFieldsLoaded;
	}

	public Symbol<?> getRootSymbol() {
		return rootSymbol;
	}

	public List<Symbol<?>> getSymbols() {
		List<Symbol<?>> result = new LinkedList<Symbol<?>>();

		Iterator<ArrayList<Symbol<?>>> it = symbols.values().iterator();
		while (it.hasNext()) {
			result.addAll(it.next());
		}
		return result;
	}

	public Symbol<?> findSymbol(String name) {
		return findSymbol(name, null, ReferenceType.VARIABLE);
	}

	public Symbol<?> findSymbol(String name, ReferenceType... referenceType) {
		Symbol<?> result = null;
		List<Symbol<?>> list = symbols.get(name);
		if (list != null) {
			Iterator<Symbol<?>> it = list.iterator();
			while (it.hasNext() && result == null) {
				Symbol<?> s = it.next();
				if (referenceType == null || referenceType.length == 0) {
					result = s;
				} else {
					boolean found = false;
					for (int i = 0; i < referenceType.length && !found; i++) {
						found = s.getReferenceType().equals(referenceType[i]);
					}
					if (found) {
						result = s;
					}
				}
			}
		}
		if (result == null) {
			list = symbols.get("super");
			if (list != null) {
				Scope scope = list.get(0).getInnerScope();
				if (scope != null) {
					result = scope.findSymbol(name, referenceType);
				}
			}
		}
		return result;
	}

	public List<Symbol<?>> getSymbols(String name) {
		return symbols.get(name);
	}

	public List<Symbol<?>> getSymbolsByLocation(Node node) {
		List<Symbol<?>> result = new LinkedList<Symbol<?>>();
		Collection<ArrayList<Symbol<?>>> values = symbols.values();
		Iterator<ArrayList<Symbol<?>>> it = values.iterator();
		while (it.hasNext()) {
			List<Symbol<?>> list = it.next();
			for (Symbol<?> symbol : list) {
				if (symbol.getLocation() == node) {// yes, by reference
					result.add(symbol);
				}
			}
		}

		return result;
	}

	public List<Symbol<?>> getSymbolsByType(String typeName,
			ReferenceType referenceType) {
		List<Symbol<?>> result = new LinkedList<Symbol<?>>();
		Collection<ArrayList<Symbol<?>>> values = symbols.values();
		Iterator<ArrayList<Symbol<?>>> it = values.iterator();
		while (it.hasNext()) {
			List<Symbol<?>> list = it.next();
			for (Symbol<?> symbol : list) {
				if (symbol.getReferenceType() == referenceType) {// yes, by
																	// reference

					if (symbol.getType().getName().startsWith(typeName)) {
						result.add(symbol);
					}
				}
			}
		}

		return result;

	}

	public List<Symbol<?>> getSymbolsByType(ReferenceType... referenceType) {
		List<Symbol<?>> result = new LinkedList<Symbol<?>>();
		Collection<ArrayList<Symbol<?>>> values = symbols.values();
		Iterator<ArrayList<Symbol<?>>> it = values.iterator();
		while (it.hasNext()) {
			List<Symbol<?>> list = it.next();
			for (Symbol<?> symbol : list) {
				boolean found = false;

				if (referenceType == null || referenceType.length == 0) {

					found = true;
				} else {

					for (int i = 0; i < referenceType.length && !found; i++) {
						found = symbol.getReferenceType() == referenceType[i];
					}
				}
				if (found) {
					result.add(symbol);
				}
			}
		}

		return result;

	}

	public Map<String, SymbolType> getTypeParams() {
		Map<String, SymbolType> aux = new HashMap<String, SymbolType>();
		List<Symbol<?>> superSymbol = symbols.get("super");
		if (superSymbol != null) {
			Scope superScope = superSymbol.get(0).getInnerScope();
			if (superScope != null) {

				aux.putAll(superScope.getTypeParams());
			}
		}
		if (typeParams != null) {
			aux.putAll(typeParams);
		}

		return aux;
	}

	public Map<String, SymbolType> getLocalTypeParams() {
		return typeParams;
	}

	public Symbol<?> findSymbol(String name, SymbolType scope,
			SymbolType[] args, ReferenceType... referenceType) {
		Symbol<?> result = null;
		if (args == null) {
			return findSymbol(name, referenceType);
		} else {

			List<Symbol<?>> values = symbols.get(name);
			if (values != null) {
				Iterator<Symbol<?>> it = values.iterator();
				while (it.hasNext() && result == null) {
					Symbol<?> symbol = it.next();
					if (symbol instanceof MethodSymbol) {
						MethodSymbol aux = (MethodSymbol) symbol;
						if (aux.hasCompatibleSignature(scope, args)) {
							result = aux;
						}
					}
				}
			}
			if (result == null) {
				values = symbols.get("super");
				if (values != null) {
					Scope innerScope = values.get(0).getInnerScope();
					if (innerScope != null) {
						result = innerScope.findSymbol(name, scope, args,
								referenceType);
					}
				}
			}
			if (result != null) {
				MethodSymbol sm = (MethodSymbol) result;
				result = sm.buildTypeParameters(typeParams);
			}

		}
		return result;
	}

	public void chageSymbol(Symbol<?> oldSymbol, Symbol<?> newSymbol) {
		List<Symbol<?>> list = symbols.get(oldSymbol.getName());
		if (list.remove(oldSymbol)) {
			ArrayList<Symbol<?>> values = symbols.get(newSymbol.getName());
			if (values == null) {
				values = new ArrayList<Symbol<?>>();
				symbols.put(newSymbol.getName(), values);
			}
			values.add(newSymbol);
		}
	}

	public <T extends Node & SymbolDefinition> Symbol<T> addSymbol(
			String symbolName, SymbolType type, T location) {

		Symbol<T> s = new Symbol<T>(symbolName, type, location);
		if (addSymbol(s)) {
			return s;
		}
		return null;
	}

	public <T extends Node & SymbolDefinition> boolean addSymbol(
			Symbol<T> symbol) {
		return addSymbol(symbol, false);
	}

	public <T extends Node & SymbolDefinition> boolean addSymbol(
			Symbol<T> symbol, boolean override) {
		String name = symbol.getName();
		ArrayList<Symbol<?>> values = symbols.get(name);
		boolean added = false;
		if (values == null) {
			values = new ArrayList<Symbol<?>>();
			symbols.put(symbol.getName(), values);
		} else {
			if (override) {
				Iterator<Symbol<?>> it = values.iterator();
				while (it.hasNext()) {
					Symbol<?> value = it.next();
					if (!value.getReferenceType().equals(ReferenceType.METHOD)
							&& value.getReferenceType().equals(
									symbol.getReferenceType())) {
						it.remove();
					}
				}
			}
		}
		if (symbol.getReferenceType().equals(ReferenceType.TYPE_PARAM)) {
			if (typeParams == null) {
				typeParams = new LinkedHashMap<String, SymbolType>();
			}

			if (!typeParams.containsKey(name)) {
				typeParams.put(name, symbol.getType());
			} else {
				added = true;
			}
		}
		if (values.isEmpty()) {
			values.add(symbol);
		} else {
			int pos = values.size() - 1;

			if (symbol.getReferenceType().equals(ReferenceType.METHOD)) {
				MethodSymbol ms = (MethodSymbol) symbol;
				Method refMethod = ms.getReferencedMethod();

				if (refMethod != null) {
					ListIterator<Symbol<?>> it = values.listIterator(values
							.size());

					MethodComparator cmp = new MethodComparator();
					while (it.hasPrevious() && !added) {
						Symbol<?> aux = it.previous();
						if (aux instanceof MethodSymbol) {
							MethodSymbol auxMethod = (MethodSymbol) aux;
							Method md = auxMethod.getReferencedMethod();
							if (md != null) {
								if (cmp.compare(refMethod, md) == 1) {
									values.add(pos + 1, ms);
									added = true;
								}
							}
						}
						pos--;
					}
					if (!added) {
						pos = 0;
					}
				}
			}
			if (!added) {
				values.add(pos, symbol);
			}
		}
		return true;
	}

	public boolean hasMethodsLoaded() {
		return hasMethodsLoaded;
	}

	public List<SymbolAction> getActions() {
		return actions;
	}

	public void addActions(List<SymbolAction> actions) {
		if (this.actions == null) {
			this.actions = actions;
		} else {
			this.actions.addAll(actions);
		}
	}

	public int getInnerAnonymousClassCounter() {
		return innerAnonymousClassCounter;
	}

	public void incrInnerAnonymousClassCounter() {
		innerAnonymousClassCounter++;
	}

}
