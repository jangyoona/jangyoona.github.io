package com.groupware.ui;

public class MailPager {

	private int pageSize; // 한 페이지당 데이터 개수
	private int pagerSize; // 번호로 보여주는 페이지 Link 개수
	private int dataCount; // 총 데이터 수

	private int pageNo; // 현재 페이지 번호
	private int pageCount; // 총 페이지 수

	private String linkUrl; // 페이저가 포함되는 페이지의 주소
	private String queryString = "";

	public MailPager(int dataCount, int pageNo, int pageSize, int pagerSize, String linkUrl, String queryString) {
		this.linkUrl = linkUrl;
		this.dataCount = dataCount;
		this.pageSize = pageSize;
		this.pagerSize = pagerSize;
		this.pageNo = pageNo;
		pageCount = (dataCount / pageSize) + ((dataCount % pageSize) > 0 ? 1 : 0);

		if (queryString == null || queryString.trim().length() == 0) {
			this.queryString = "";
		} else {
			String[] items = queryString.split("&");
			for (int idx = 0; idx < items.length; idx++) {
				if (!items[idx].startsWith("pageNo")) {
					this.queryString += "&" + items[idx];
				}
			}
		}
	}

	public String toString() {
		StringBuffer linkString = new StringBuffer(2048);

		linkString.append("<ul class='pagination'>");

		// 처음, 이전 항목 만들기
		if (pageNo > 1) {
			linkString.append(String.format("<li class='page-item'><a class='page-link' href='javascript:void(0)' data-page='1' %s>처음</a></li>", queryString));
			linkString.append(String.format("<li class='page-item'><a class='page-link' href='javascript:void(0)' data-page='%d' %s>이전</a></li>", pageNo - 1, queryString));
		} else {
			linkString.append("<li class='page-item disabled'><span class='page-link'>처음</span></li>");
			linkString.append("<li class='page-item disabled'><span class='page-link'>이전</span></li>");
		}

		// 페이지 번호 Link 만들기
		int pagerBlock = (pageNo - 1) / pagerSize;
		int start = (pagerBlock * pagerSize) + 1;
		int end = Math.min(start + pagerSize, pageCount + 1);
		for (int i = start; i < end; i++) {
			if (i > pageCount) break;
			if (i == pageNo) {
				linkString.append(String.format("<li class='page-item active'><span class='page-link'>%d</span></li>", i));
			} else {
				linkString.append(String.format("<li class='page-item'><a class='page-link' href='javascript:void(0)' data-page='%d' %s>%d</a></li>", i, queryString, i));
			}
		}

		// 다음, 마지막 항목 만들기
		if (pageNo < pageCount) {
			linkString.append(String.format("<li class='page-item'><a class='page-link' href='javascript:void(0)' data-page='%d' %s>다음</a></li>", pageNo + 1, queryString));
			linkString.append(String.format("<li class='page-item'><a class='page-link' href='javascript:void(0)' data-page='%d' %s>마지막</a></li>", pageCount, queryString));
		} else {
			linkString.append("<li class='page-item disabled'><span class='page-link'>다음</span></li>");
			linkString.append("<li class='page-item disabled'><span class='page-link'>마지막</span></li>");
		}

		linkString.append("</ul>");

		return linkString.toString();
	}
}
