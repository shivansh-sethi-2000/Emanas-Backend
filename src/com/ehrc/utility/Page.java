package com.ehrc.utility;

import java.util.List;

public interface Page<T> {
	List<T> getContent();
	
	/**
	 * Returns the total amount of elements.
	 * @return
	 */
	Long getTotalElements();
	
	/**
	 * Returns the size of the Slice.
	 * @return
	 */
	Integer getSize();
	
	/**
	 * Returns the number of elements currently on this Slice.
	 * @return
	 */
	Integer getNumberOfElements();
	
	/**
	 * Returns the number of total pages.
	 * @return
	 */
	Integer getTotalPages();

}
