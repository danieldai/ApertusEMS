package com.eltvpp.auth.form;

/**
 * 用户登录对象
 * 
 * @author eltvpp
 */
public class LoginBody
{
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 浏览器唯一标识
     */
    private String browserFlag;

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

    public String getBrowserFlag() {
        return browserFlag;
    }

    public void setBrowserFlag(String browserFlag) {
        this.browserFlag = browserFlag;
    }
}
