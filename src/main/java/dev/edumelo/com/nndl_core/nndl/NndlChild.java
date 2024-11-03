package dev.edumelo.com.nndl_core.nndl;

public interface NndlChild {
	public abstract NndlChildType getType();
	public abstract void merge(NndlChild otherChild);
}
