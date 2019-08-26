package cn.itcast.web.shiro;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String email = upToken.getUsername();
        //查询邮箱是否存在
        List<User> userList = userService.findByEmail(email);
        User user;
        if (userList != null && userList.size() > 0) {
            user = userList.get(0);
            return new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
        }

        return null;
    }

    /**
     * 授权访问校验
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录的用户
        User user = (User) principalCollection.getPrimaryPrincipal();
        //获取该用户的权限
        List<Module> moduleList = moduleService.findByUserId(user.getId());
        //创建set集合存储权限
        Set<String> permissions = new HashSet<>();
        //遍历权限，将权限放入到set集合中
        for (Module module : moduleList) {
            permissions.add(module.getName());
        }
        SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
        sai.addStringPermissions(permissions);
        return sai;
    }
}
