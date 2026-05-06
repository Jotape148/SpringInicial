package br.com.joaoteixeira.produtosapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenAccessFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "X-API-TOKEN";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final String tokenPermitido;

    public TokenAccessFilter(@Value("${api.security.token}") String tokenPermitido) {
        this.tokenPermitido = tokenPermitido;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenRecebido = obterToken(request);

        if (!tokenPermitido.equals(tokenRecebido)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"mensagem\":\"Acesso negado: token ausente ou invalido\"}");
            return;
        }

        response.setHeader("X-ACESSO", "permitido");
        filterChain.doFilter(request, response);
    }

    private String obterToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (token != null && !token.isBlank()) {
            return token;
        }

        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    private boolean isPublicPath(String uri) {
        return uri.startsWith("/h2-console");
    }
}
