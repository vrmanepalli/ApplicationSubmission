package com.nike.util;

import com.pingidentity.opentoken.Agent;
import com.pingidentity.opentoken.AgentConfiguration;
import com.pingidentity.opentoken.TokenException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.collections.MultiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Aaron Anderson <aaron.anderson@idmworks.com>
 */
public class TokenWrapper {
    private Map data;
    private AgentConfiguration config;
    private Agent agent;
    
    private static final String TOKEN_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final Locale TOKEN_DATE_LOCALE = Locale.US;
    
    public static final String TOKEN_SUBJECT = Agent.TOKEN_SUBJECT;
    public static final String TOKEN_NOT_BEFORE = Agent.TOKEN_NOT_BEFORE;
    public static final String TOKEN_NOT_ON_OR_AFTER = Agent.TOKEN_NOT_ON_OR_AFTER;
    public static final String TOKEN_RENEW_UNTIL = Agent.TOKEN_RENEW_UNTIL;
    static Logger logger =  LogManager.getLogger(TokenWrapper.class.getName());
    
    /**
     * Constructor for a new TokenWrapper object.
     * @param agentConfigurationFileName
     * @throws TokenException
     * @throws IOException 
     */
    public TokenWrapper(String agentConfigurationFileName) throws IOException {
        config = new AgentConfiguration(agentConfigurationFileName);
        agent = new Agent(config);
    }

    /**
     * Parses and loads data from the given token string.
     * A TokenException is thrown if the TOKEN_NOT_ON_OR_AFTER.
     * @param tokenStr The token
     * @throws TokenException 
     */
    public MultiMap read(String tokenStr) throws TokenException {
        data = agent.readTokenToMultiMap(tokenStr);
        logger.info("Successful reading of token.");
        return (MultiMap) data;
    }
    
    /**
     * Create a new token.
     * This overwrites any existing data stored in this class. The token lifetime
     * dates are set based on the data in the agent configuration file.
     * @param subject The token subject, generally the user ID.
     * @throws TokenException 
     */
    public void createNew(String subject) throws TokenException {
        data = new HashMap();
        data.put(TOKEN_SUBJECT, subject);
        data.put(TOKEN_NOT_BEFORE, formatTokenDate(Calendar.getInstance()));
        
        Calendar not_on_or_after = Calendar.getInstance();
        not_on_or_after.add(Calendar.SECOND, config.getTokenLifetime());
        data.put(Agent.TOKEN_NOT_ON_OR_AFTER, formatTokenDate(not_on_or_after));
        
        Calendar renew_util = Calendar.getInstance();
        renew_util.add(Calendar.SECOND, config.getRenewUntilLifetime());
        data.put(Agent.TOKEN_RENEW_UNTIL, formatTokenDate(renew_util));
    }
    
    /**
     * Returns an encrypted token that can be set in a cookie
     * @return A string containing the token
     * @throws TokenException 
     */
    public String getToken() throws TokenException {
        return agent.writeToken(data);
    }
    
    /**
     * Return the key/value pairs from the token as a Map
     * @return The token data
     */
    public Map getTokenData() {
        return data;
    }
    
    /**
     * Returns the value for a specified key
     * @param key The key
     * @return 
     */
    public String getValueFromToken(String key) {
        if (data.containsKey(key)) {
            return (String)data.get(key);
        }
        else {
            return null;
        }
    }
    
    /**
     * Renew the supplied token
     * @param agentConfigurationFileName Filename to the agent configuration file
     * @param tokenStr The token
     * @return The updated token
     * @throws IOException
     * @throws TokenException 
     */
    public static String renew(String agentConfigurationFileName, String tokenStr) throws IOException, TokenException {
        TokenWrapper token = new TokenWrapper(agentConfigurationFileName);
        token.read(tokenStr);
        token.renew();
        return token.getToken();
    }
    
    /**
     * Renew the token based on the agent configuration file
     */
    public void renew() {
        // Get the token lifetime (in seconds) renewal amount from the 
        // configuration and add it to now.
        Calendar newTokenLifetime = Calendar.getInstance();
        newTokenLifetime.add(Calendar.SECOND, config.getTokenLifetime());
        data.put(Agent.TOKEN_NOT_ON_OR_AFTER, formatTokenDate(newTokenLifetime));
    }

    /**
     * Determines if the session lifetime of the token has been exceeded.
     * @param tokenStr The token
     * @return True if the token's session lifetime is in the future otherwise False
     * @throws IOException
     * @throws TokenException 
     */
    public static boolean isSessionValid(String agentConfigurationFileName, String tokenStr) throws IOException, TokenException {
        TokenWrapper token = new TokenWrapper(agentConfigurationFileName);
        token.read(tokenStr);
        return token.isSessionValid();
    }
    
    /**
     * Determines if the session lifetime of the token has been exceeded.
     * @return True if the token's session lifetime is in the future otherwise False
     * @throws IOException
     * @throws TokenException 
     */
    public boolean isSessionValid() throws TokenException {
        if (getDate(Agent.TOKEN_RENEW_UNTIL).compareTo(Calendar.getInstance()) < 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Determines if the token lifetime of the token has been exceeded.
     * @param tokenStr The token
     * @return True if the token's session lifetime is in the future otherwise False
     * @throws IOException
     * @throws TokenException 
     */
    public static boolean isTokenValid(String agentConfigurationFileName, String tokenStr) throws IOException, TokenException {
        TokenWrapper token = new TokenWrapper(agentConfigurationFileName);
        token.read(tokenStr);
        return token.isTokenValid();
    }
    
    /**
     * Determines if the session lifetime of the token has been exceeded.
     * @param tokenStr The string representation of the token
     * @return True if the token's session lifetime is in the future otherwise False
     * @throws IOException
     * @throws TokenException 
     */
    public boolean isTokenValid() throws TokenException {
        if (getDate(Agent.TOKEN_NOT_ON_OR_AFTER).compareTo(Calendar.getInstance()) < 0) {
            return true;
        }
        return false;
    }

    /**
     * Serializes a Calendar object in to a properly formatted string
     * @param date A Calendar object
     * @return A properly formatted string
     */
    private String formatTokenDate(Calendar date) {
        SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat(TOKEN_DATE_FORMAT, TOKEN_DATE_LOCALE);
        ISO8601DATEFORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(" --> " + ISO8601DATEFORMAT.format(date.getTime()));
        return ISO8601DATEFORMAT.format(date.getTime());
    }
    
    /**
     * Parse a ISO8601 formated date string from one of they key value pairs in
     * the token in to a Calendar object
     * @param key The key of one of the values in the token
     * @return A Calendar object
     * @throws TokenException 
     */
    private Calendar getDate(String key) throws TokenException {
        SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat(TOKEN_DATE_FORMAT, TOKEN_DATE_LOCALE);
        try {
            if (data.containsKey(key)) {
                ISO8601DATEFORMAT.parse((String)data.get(key));
            }
        } catch(ParseException ex) {
            throw new TokenException("Unable to parse " + key, ex);
        }
        return ISO8601DATEFORMAT.getCalendar();
    }
}