package com.nike.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Aaron Anderson <aaron.anderson@idmworks.com>
 */
class TokenCookieNotFoundException extends Exception {
	static Logger logger =  LogManager.getLogger(TokenCookieNotFoundException.class.getName());
	
    public TokenCookieNotFoundException() {
    	logger.error("Token parameter is not found.");
    }
 
    public TokenCookieNotFoundException(String msg) {
        super(msg);
        logger.error(msg);
    }
}

class TokenExpiredException extends Exception {
	static Logger logger =  LogManager.getLogger(TokenExpiredException.class.getName());
    public TokenExpiredException() {
    	logger.error("Token is expired.");
    }
 
    public TokenExpiredException(String msg) {
        super(msg);
        logger.error(msg);
    }
}

class TokenErrorException extends Exception {
	static Logger logger =  LogManager.getLogger(TokenErrorException.class.getName());
    public TokenErrorException() {
    	logger.error("Token Error exception.");
    }
 
    public TokenErrorException(String msg) {
        super(msg);
        logger.error(msg);
    }
}