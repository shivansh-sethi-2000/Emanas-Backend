package com.ehrc.utility;

import java.util.List;

public class PageImpl<T> implements Page<T> {
	private List<T> content;
	private Long totalElements;
	private Integer size;
	private Integer numberOfElements;
	private Integer totalPages;
	
	
	public PageImpl(List<T> content, Long totalElements, Integer size, Integer numberOfElements,
			Integer totalPages) {
		super();
		this.content = content;
		this.totalElements = totalElements;
		this.size = size;
		this.numberOfElements = numberOfElements;
		this.totalPages = totalPages;
	}

	@Override
	public List<T> getContent() {
		return content;
	}

	@Override
	public Long getTotalElements() {
		return totalElements;
	}

	@Override
	public Integer getSize() {
		return size;
	}

	@Override
	public Integer getNumberOfElements() {
		return numberOfElements;
	}

	@Override
	public Integer getTotalPages() {
		return totalPages;
	}

}
