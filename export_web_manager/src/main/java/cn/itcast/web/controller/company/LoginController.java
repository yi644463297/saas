package cn.itcast.web.controller.company;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import cn.itcast.web.controller.BaseController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LoginController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 登录
     */
    @RequestMapping("/login")
    public String login(String email, String password) {
        Subject subject = SecurityUtils.getSubject();
        try {
            //构造用户密码令牌
            UsernamePasswordToken upToken = new UsernamePasswordToken(email,password);
            //使用subject登录
            subject.login(upToken);
            //4.通过shiro获取用户对象
            User user = (User) subject.getPrincipal();
            //保存用户信息到session
            session.setAttribute("user",user);
            //获取用户的权限
            List<Module> moduleList = moduleService.findByUserId(user.getId());
            session.setAttribute("modules",moduleList);

            return "/home/main";
        } catch (Exception e) {
            e.printStackTrace();
            //登录失败跳转页面
            request.setAttribute("error","用户名或者密码错误");
            return "forward:login.jsp";
        }
    }

    @RequestMapping("/home")
    public String home() {
        return "home/home";
    }

    /**
     * 注销登录
     */
    @RequestMapping("/logout")
    public String logout() {
        // shiro也提供了退出方法(清除shiro的认证信息)
        SecurityUtils.getSubject().logout();
        //销毁session
        //session.invalidate();
        //不能使用redirect
        return "forward:/login.jsp";
    }

}
