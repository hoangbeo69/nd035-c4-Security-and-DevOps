package com.example.demo.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import com.auth0.jwt.JWT;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JWTAuthenticationFilter
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private static final Logger JWTLogger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  /**
   *
   * @param req
   * @param res
   * @return
   * @throws AuthenticationException
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException {
    try {
      User credentials = new ObjectMapper().readValue(req.getInputStream(), User.class);
      JWTLogger.info("User {} try to get authentication!", credentials.getUsername());
      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(credentials.getUsername(),
              credentials.getPassword(), new ArrayList<>()));
    } catch (IOException e) {
      JWTLogger.error("Failed to get Authentication, reason: {}", e.getMessage());
      throw new RuntimeException("Error: Failed to get Authentication, reason: {}", e);
    }
  }

  /**
   *
   * @param req
   * @param res
   * @param chain
   * @param auth
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain, Authentication auth) throws IOException, ServletException {
    String token = JWT.create().withSubject(
            ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
        .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
    res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
  }

}
