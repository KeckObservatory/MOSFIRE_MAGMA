package edu.ucla.astro.irlab.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TopPriorityList <E>{
	private LinkedList<E> list;
	private int maxSize;
	private Comparator <E> comparator;
	
	public TopPriorityList(int maxSize, Comparator<E> comparator){
		list = new LinkedList<E>();
		this.maxSize = maxSize;
		this.comparator = comparator;
	}
	
	public void add(E entry) {
		// It shouldn't be, but trim the list to make sure the list size does not exceed maxSize.
		trim();
		// Case 0. If list is empty, add the entry.
		if (list.size()==0){
			list.add(entry);
			return;
		// Case 1. Entry worse than last element
		}else if (comparator.compare(entry, list.peekLast())<=0) {
			// Case 1-1 If list is max-ed, do nothing and return current list.
			if (list.size()==maxSize){
				return;
			// Case 1-2 If list is not max-ed out, add the entry to the last
			// *There MUST not be chance of the list size being bigger than maxSize.
			}else {
				list.addLast(entry);
			}
		// Case 2. Entry is better than last element. Insertion guaranteed.
		} else {
			// Loop through the list and compare the entry with element.
			for (int ii = 0; ii < list.size(); ii++) {
				// If entry greater/better/prior than or equal to element,
				if(comparator.compare(entry, list.get(ii))>0) {
					// squeeze in the entry before. then trim, then return the list.
					list.add(ii, entry);
					trim();
					return;
				}
			}	
		}
		
		// This line must not be reached.
		return;
	}
	
	private void trim() {
		// If list size is bigger than maxSize, removeLast until trimmed.
		if (list.size()>maxSize) {
			while (list.size()!=maxSize){
				list.removeLast();
			}
		}
	}
	
	public void remove (int index) {
		list.remove(index);
	}
	public void remove (E object) {
		list.remove(object);
	}
	public boolean contains (E object) {
		return list.contains(object);
	}
	public void clear () {
		list.clear();
	}
	public E peek () {
		return list.peek();
	}
	public E peekLast () {
		return list.peekLast();
	}
	public E peekFirst () {
		return list.peekFirst();
	}
	public E get(int index) {
		return list.get(index);
	}
	public E pop () {
		return list.pop();
	}
	public E getFirst() {
		return list.getFirst();
	}
	public E getLast() {
		return list.getLast();
	}
	public List<E> asList() {
		return list.subList(0, list.size());
	}	
}
