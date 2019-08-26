package cn.itcast.web.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;

public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //获取登录的帐号
        String username = (String) token.getPrincipal();
        //获取登录的密码
        String password = String.valueOf((char[])token.getCredentials());
        //对登录密码进行加盐（用户名）加密
        String md5Password = new Md5Hash(password, username).toString();
        //获取数据库的密码
        String dbPassword = (String) info.getCredentials();
        return dbPassword.equals(md5Password);
    }

}
