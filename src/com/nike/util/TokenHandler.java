package com.nike.util;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pingidentity.opentoken.TokenException;

/**
 *
 * @author Aaron Anderson <aaron.anderson@idmworks.com>
 */
public class TokenHandler {
    // TODO: This should come from configuration information
    
    public final static String LOGIN_FORM = "http://www.nike.net/";
    public final static String AGENT_CONFIGURATION = TableConstants.AGENT_CONFIG_PATH;
    
    static Logger logger =  LogManager.getLogger(TokenHandler.class.getName());
    
    /**
     * Helper function to retrieve the username and renew the token.
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @return The username from the token
     * @throws IOException If the OpenToken agent configuration file is not found
     * @throws TokenCookieNotFoundException If the cookie is not found in the request
     * @throws TokenExpiredException If the token is expired
     * @throws TokenErrorException If an error occurs. This is thrown if the token cannot be renewed
     * 
     */
    public static String getUsernameAndRenew(HttpServletRequest request, HttpServletResponse response) throws IOException, TokenCookieNotFoundException, TokenExpiredException, TokenErrorException
    {
        // Search for the token in the cookies
        Cookie tokenCookie = null;
        String tokenString = null;

//        Enumeration<String> parameterNames = request.getParameterNames();
//
//		while (parameterNames.hasMoreElements()) {
//
//			String paramName = parameterNames.nextElement();
//			logger.info("ParameterName: " + paramName);
//
//			String[] paramValues = request.getParameterValues(TableConstants.COOKIE_NAME);
//			for (int i = 0; i < paramValues.length; i++) {
//				String paramValue = paramValues[i];
//				logger.info("ParameterValue: " + paramValue);
//			}
//
//		}
        
        tokenString = getTokenString(request, response);
        
        // This will throw a IOException if the agent configuration file is not found.
        TokenWrapper token = new TokenWrapper(AGENT_CONFIGURATION);
        
        // Parse the token
        try {
            token.read(tokenString);
        } catch(TokenException ex) {
            // Token expired.
            logger.info("Token expired");
            throw new TokenExpiredException("Token expired");
            // response.sendRedirect(LOGIN_FORM);
            // return null;
        }
        
        // Retreive the username from the token subject
        String username = token.getValueFromToken(TokenWrapper.TOKEN_SUBJECT);
        
        // Renew the token and update the cookie
        token.renew();

        String newToken;
        try {
            // Rebuild and encrypt the token
            newToken = token.getToken();
            logger.info("New token is " + newToken);
        } catch (TokenException ex) {
            throw new TokenErrorException("Unable to renew token");
            // return null;
        }

        // Set the cookie
        tokenCookie.setValue(newToken);
        response.addCookie(tokenCookie);
        
        // Return the username from the token
        return username;
    }
    
    public static String getTokenString(HttpServletRequest request, HttpServletResponse response)  throws IOException{
//    	Cookie[] cookies = request.getCookies();
//    	if (cookies == null) {
////            throw new TokenCookieNotFoundException("No cookies in found in request");
//    		response.sendRedirect(TableConstants.SSO_LOGIN_LINK);
//            return null;
//        }
//        
//        Cookie tokenCookie = null;
		String tokenString = request.getParameter(TableConstants.COOKIE_NAME);
//		for (Cookie cookie : cookies) {
//			logger.info(cookie.getName()+ "=" + cookie.getValue());
//            if (cookie.getName().equalsIgnoreCase(TableConstants.COOKIE_NAME)) {
//                tokenCookie = cookie;
//                tokenString = cookie.getValue();
//            }
//        }

        // Token is not present. Redirect to login form.
        if (tokenString == null) {
            logger.info("No cookie named " + TableConstants.COOKIE_NAME + " found");
//            throw new TokenCookieNotFoundException("No cookie named " + TableConstants.COOKIE_NAME + " found");
            // TODO: sendRedirect throws IOException. Catch here or allow pass-through to caller?
             response.sendRedirect(TableConstants.SSO_LOGIN_LINK);
             return null;
            
        }
        logger.debug("Found cookie. Token is" + tokenString);
        return tokenString;
    }
}
