/**
 * Copyright (C) 2017 Massachusetts Inpstitute of Technology (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * @author: Vincent Mirian
 * 
 * @date: Oct 27, 2017
 *
 */

public class CObjectCollection<T extends CObject> extends CObject implements List<T>, RandomAccess, Cloneable, Serializable{
	
	private void init(){
		collection = new ArrayList<T>();
	}

	public CObjectCollection() {
		super();
		init();
	}

	public T findCObjectByName(final String name){
		T rtn = null, cobjTemp = null;
		Iterator<T> cobjIt = collection.iterator();
		while (
				(rtn == null)
				&& (cobjIt.hasNext())
				){
			cobjTemp = cobjIt.next();
			if (cobjTemp.getName().equals(name)){
				rtn = cobjTemp;
			}
		}
		return rtn;
	}
	
	public T findCObjectByIdx(final int index){
		T rtn = null, cobjTemp = null;
		Iterator<T> cobjIt = collection.iterator();
		while (
				(rtn == null)
				&& (cobjIt.hasNext())
				){
			cobjTemp = cobjIt.next();
			if (cobjTemp.getIdx() == index){
				rtn = cobjTemp;
			}
		}
		return rtn;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<T> collection;


	@Override
	public boolean add(T e) {
		return collection.add(e);
	}

	@Override
	public void add(int index, T element) {
		collection.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return collection.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return collection.addAll(index, c);
	}

	@Override
	public void clear() {
		collection.clear();
	}

	@Override
	public boolean contains(Object o) {
		return collection.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return collection.containsAll(c);
	}

	@Override
	public T get(int index) {
		return collection.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return collection.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return collection.iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return collection.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return collection.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return collection.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return collection.remove(o);
	}

	@Override
	public T remove(int index) {
		return collection.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return collection.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return collection.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		return collection.set(index, element);
	}

	@Override
	public int size() {
		return collection.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return collection.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return collection.toArray();
	}

	@Override
	public <Type> Type[] toArray(Type[] a) {
		return collection.toArray(a);
	}

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((collection == null) ? 0 : collection.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!(obj instanceof CObjectCollection<?>)) {
			return false;
		}
		CObjectCollection<?> other = (CObjectCollection<?>) obj;
		if (collection == null) {
			if (other.collection != null)
				return false;
		} else if (!collection.equals(other.collection))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	protected String getCollectionElementToString() {
		String rtn = "";
		for (int i = 0; i < this.size(); i++) {
			rtn = rtn + Utils.getTabCharacter();	
			T element = this.get(i);
			rtn = rtn + element.toString();
		}
		return rtn;
	}
	
	@Override
	public String toString() {
		String rtn = "";
		rtn = rtn + "collection = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getCollectionElementToString();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();	
		return rtn;
	}
	
	
}
