/*
 * Created on 15/07/2014 by fisherj
 * 
 * Copyright (c) 2005-2014 Public Transport Victoria (PTV)
 * State Government of Victoria, Australia
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of PTV.
 */

package net.jadefisher.statusmonitor.model;

public class BasicAuthentication
{
    private String username;

    private String password;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

}
