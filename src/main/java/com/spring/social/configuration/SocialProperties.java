package com.spring.social.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({ @PropertySource("classpath:social-cfg.properties") })
@ConfigurationProperties(prefix = "social")
public class SocialProperties {

	private String autoSignup;
    private String googleClientId;
    private String googleClientSecret;
    private String googleScope;
    private String twitterConsumerKey;
    private String twitterConsumerSecret;
    
	public String getAutoSignup() {
		return autoSignup;
	}
	public void setAutoSignup(String autoSignup) {
		this.autoSignup = autoSignup;
	}
	public String getGoogleClientId() {
		return googleClientId;
	}
	public void setGoogleClientId(String googleClientId) {
		this.googleClientId = googleClientId;
	}
	public String getGoogleClientSecret() {
		return googleClientSecret;
	}
	public void setGoogleClientSecret(String googleClientSecret) {
		this.googleClientSecret = googleClientSecret;
	}
	public String getGoogleScope() {
		return googleScope;
	}
	public void setGoogleScope(String googleScope) {
		this.googleScope = googleScope;
	}
	public String getTwitterConsumerKey() {
		return twitterConsumerKey;
	}
	public void setTwitterConsumerKey(String twitterConsumerKey) {
		this.twitterConsumerKey = twitterConsumerKey;
	}
	public String getTwitterConsumerSecret() {
		return twitterConsumerSecret;
	}
	public void setTwitterConsumerSecret(String twitterConsumerSecret) {
		this.twitterConsumerSecret = twitterConsumerSecret;
	}
    
    
    
}