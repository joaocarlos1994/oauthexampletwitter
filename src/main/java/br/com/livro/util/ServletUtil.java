package br.com.livro.util;

import javax.servlet.http.HttpServletRequest;

public class ServletUtil {

	public static String getRequestURL(final HttpServletRequest req) {
		final StringBuffer sb = req.getRequestURL();
		final String queryString = req.getQueryString();
		if (queryString != null) {
			sb.append("?").append(queryString);
		}
		final String url = sb.toString();
		return url;
	}

}
