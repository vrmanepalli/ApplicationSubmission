package com.nike.util;

import com.pingidentity.opentoken.AgentConfiguration;
import com.pingidentity.opentoken.TokenException;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
	static Logger logger =  LogManager.getLogger(App.class.getName());
	
    public static void setUpSAMLToken()
    {
        final String agentConfigurationFileName = TableConstants.AGENT_CONFIG_PATH;
        try
        {
            AgentConfiguration config = new AgentConfiguration(agentConfigurationFileName);
            logger.info(config.getPassword());
        } catch(IOException e) {
            logger.info("Agent Configuration File not found");
            return;
        }
        
        try
        {  
            TokenWrapper wrapper = new TokenWrapper(agentConfigurationFileName);
            wrapper.createNew("vasudevarao.manepalli@nike.com");
            String tokenStr = wrapper.getToken();
            logger.info("Token string: " + tokenStr);

            wrapper.read(tokenStr);
            logger.info("Created on: " + wrapper.getValueFromToken(TokenWrapper.TOKEN_NOT_BEFORE));
            logger.info("Good until: " + wrapper.getValueFromToken(TokenWrapper.TOKEN_NOT_ON_OR_AFTER));
            logger.info("Renew util: " + wrapper.getValueFromToken(TokenWrapper.TOKEN_RENEW_UNTIL));
            
//            wrapper.renew();
//            logger.info("Created on: " + wrapper.getValueFromToken(TokenWrapper.TOKEN_NOT_BEFORE));
//            logger.info("Good until: " + wrapper.getValueFromToken(TokenWrapper.TOKEN_NOT_ON_OR_AFTER));
//            logger.info("Renew util: " + wrapper.getValueFromToken(TokenWrapper.TOKEN_RENEW_UNTIL));
        } catch(TokenException e) {
            logger.info("Error: " + e.getMessage());
        } catch(IOException e) {
            logger.info("File not found");
        }
    }
}
