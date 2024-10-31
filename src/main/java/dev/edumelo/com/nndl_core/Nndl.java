package dev.edumelo.com.nndl_core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Nndl {
	private String name;
	private String value;
	private List<Nndl> imports;
	
	public Nndl() {
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<Nndl> getImports() {
		return imports;
	}
	public void setImports(List<Nndl> imports) {
		this.imports = imports;
	}
	public void addImport(Nndl importNndl) {
		imports = imports == null ? new ArrayList<>() : imports;
		imports.add(importNndl);		
	}
	
	public Optional<List<String>> extractImportsNames() {
		return new NndlParser().getImports(value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(imports, name, value);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nndl other = (Nndl) obj;
		return Objects.equals(imports, other.imports) && Objects.equals(name, other.name)
				&& Objects.equals(value, other.value);
	}
	@Override
	public String toString() {
		return "Nndl [name=" + name + ", value=" + value + ", imports=" + imports + "]";
	}
	
}
