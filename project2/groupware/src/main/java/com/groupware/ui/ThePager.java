package com.groupware.ui;

public class ThePager {

	private int pageSize; // 한 페이지당 데이터 개수
	private int pagerSize; // 번호로 보여주는 페이지 Link 개수
	private int dataCount; // 총 데이터 수

	private int pageNo; // 현재 페이지 번호
	private int pageCount; // 총 페이지 수

	private String linkUrl; // 페이저가 포함되는 페이지의 주소

	private String queryString = "";

	public ThePager(int dataCount, int pageNo, int pageSize, int pagerSize, String linkUrl, String queryString) {
		this.linkUrl = linkUrl;
		this.dataCount = dataCount;
		this.pageSize = pageSize;
		this.pagerSize = pagerSize;
		this.pageNo = pageNo;
		this.pageCount = (dataCount / pageSize) + ((dataCount % pageSize) > 0 ? 1 : 0);

		if (queryString == null || queryString.trim().length() == 0) {
			this.queryString = "";
		} else {
			String[] items = queryString.split("&");
			for (String item : items) {
				if (!item.startsWith("pageNo")) {
					this.queryString += "&" + item;
				}
			}
		}
	}

	public String toString() {
		StringBuilder linkString = new StringBuilder();

		linkString.append("<nav aria-label='Page navigation'>");
		linkString.append("<ul class='pagination justify-content-center'>");

		// 처음, 이전 항목 만들기
		if (pageNo > 1) {
			linkString.append(String.format("<li class='page-item'><a class='page-link' href='%s?pageNo=1%s' aria-label='First'><span aria-hidden='true'>&laquo;&laquo;</span></a></li>", linkUrl, queryString));
			linkString.append(String.format("<li class='page-item'><a class='page-link' href='%s?pageNo=%d%s' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></a></li>", linkUrl, pageNo - 1, queryString));
		} else {
			linkString.append("<li class='page-item disabled'><span class='page-link' aria-label='First'><span aria-hidden='true'>&laquo;&laquo;</span></span></li>");
			linkString.append("<li class='page-item disabled'><span class='page-link' aria-label='Previous'><span aria-hidden='true'>&laquo;</span></span></li>");
		}

		// 페이지 번호 Link 만들기
		int pagerBlock = (pageNo - 1) / pagerSize;
		int start = (pagerBlock * pagerSize) + 1;
		int end = Math.min(start + pagerSize - 1, pageCount);

		for (int i = start; i <= end; i++) {
			if (i == pageNo) {
				linkString.append(String.format("<li class='page-item active'><span class='page-link'>%d</span></li>", i));
			} else {
				linkString.append(String.format("<li class='page-item'><a class='page-link' href='%s?pageNo=%d%s'>%d</a></li>", linkUrl, i, queryString, i));
			}
		}

		// 다음, 마지막 항목 만들기
		if (pageNo < pageCount) {
			linkString.append(String.format("<li class='page-item'><a class='page-link' href='%s?pageNo=%d%s' aria-label='Next'><span aria-hidden='true'>&raquo;</span></a></li>", linkUrl, pageNo + 1, queryString));
			linkString.append(String.format("<li class='page-item'><a class='page-link' href='%s?pageNo=%d%s' aria-label='Last'><span aria-hidden='true'>&raquo;&raquo;</span></a></li>", linkUrl, pageCount, queryString));
		} else {
			linkString.append("<li class='page-item disabled'><span class='page-link' aria-label='Next'><span aria-hidden='true'>&raquo;</span></span></li>");
			linkString.append("<li class='page-item disabled'><span class='page-link' aria-label='Last'><span aria-hidden='true'>&raquo;&raquo;</span></span></li>");
		}

		linkString.append("</ul>");
		linkString.append("</nav>");

		return linkString.toString();
	}
}
