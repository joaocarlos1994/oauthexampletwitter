package br.com.livro.rest.oauth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1Builder.FlowBuilder;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

@WebFilter("/rest/twitterv2/*")
public class TwitterOAuthFilter implements Filter {

	public static final String CONSUMER_KEY = null;
	public static final String CONSUMER_SECRET = null;
	
	@Override
	public void destroy() {	
	}
	
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse resp = (HttpServletResponse) response;
		
		final AccessToken accessToken = (AccessToken) req.getSession().getAttribute("acessToken");
		
		if (accessToken == null) {	
			final String oauth_verifier = req.getParameter("oauth_verifier");
			final String oauth_token = req.getParameter("oauth_token");
			if (oauth_verifier != null && oauth_token != null) {
				// Voltou do Twitter, verifica o codigo
				verify(req, oauth_verifier);
			} else {
				// Precisa rediricionar para o Twitter
				auth(req, resp);
			}
		}
		// Continua a requisicao
		chain.doFilter(req, resp);
	}
	
	// Valida o codigo verificador retornado pelo Twitter (depois do usuario autorizar)
	private void verify(final HttpServletRequest req, final String oauth_verifier) {
		AccessToken accessToken;
		OAuth1AuthorizationFlow authFlow = (OAuth1AuthorizationFlow) req.getSession().getAttribute("authFlow");
		accessToken = authFlow.finish(oauth_verifier);
		req.getSession().setAttribute("accessToken", accessToken);
	}
	
	// Inicia o fluxo de autorizacao (Redireciona a URL para o Twitter)
	private void auth(final HttpServletRequest req, final HttpServletResponse resp) {
		final String url = ServletUtil.getRequestURL(req);
		final OAuth1AuthorizationFlow authflow = getAuthorizationFlow(req, url);
		final String authorizationUri = authflow.start();
		resp.sendRedirect(authorizationUri);
	}

	// Cria o fluxo de autorizacao
	private OAuth1AuthorizationFlow getAuthorizationFlow(final HttpServletRequest req, final String callBackUri) {
		final ConsumerCredentials consumerCredentials = new ConsumerCredentials(CONSUMER_KEY, CONSUMER_SECRET);
		final FlowBuilder builder = OAuth1ClientSupport.builder(consumerCredentials)
				.authorizationFlow(
						"https://api.twitter.com/oauth/request_token", 
						"https://api.twitter.com/oauth/access_token", 
						"https://api.twitter.com/oauth/authorize");
		
		if (callBackUri != null) {
			builder.callbackUri(callBackUri);
		}
		
		final OAuth1AuthorizationFlow authFlow = builder.build();
		req.getSession().setAttribute("authFlow", authFlow);
		return authFlow;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {	
	}

}
