package com.spring.social.social;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import com.spring.social.dao.AppUserDAO;
import com.spring.social.entity.AppUser;
 
public class ConnectionSignUpImpl implements ConnectionSignUp {
 
    private AppUserDAO appUserDAO;
 
    public ConnectionSignUpImpl(AppUserDAO appUserDAO) {
        this.appUserDAO = appUserDAO;
    }
 
    // After logging in social networking.
    // This method will be called to create a corresponding App_User record
    // if it does not already exist.
    @Override
    public String execute(Connection<?> connection) {
 
        AppUser account = appUserDAO.createAppUser(connection);
        return account.getUserName();
    }
 
}