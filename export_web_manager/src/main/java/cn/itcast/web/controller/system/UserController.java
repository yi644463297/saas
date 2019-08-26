package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Role;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.RoleService;
import cn.itcast.service.system.UserService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.MailUtil;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;

    /**
     * 显示所有用户
     *
     * @param pageNum  当前页
     * @param pageSize 页面大小
     * @return
     */
    @RequestMapping("/list")
    @RequiresPermissions("用户管理")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {

        /*Subject subject = SecurityUtils.getSubject();
        subject.checkPermission("用户管理");*/
        //获取公司id
        String companyId = getLoginCompanyId();

        PageInfo<User> pageInfo = userService.findByPage(companyId, pageNum, pageSize);
        ModelAndView mv = new ModelAndView();
        mv.addObject("page", pageInfo);
        mv.setViewName("system/user/user-list");
        return mv;
    }

    /**
     * 跳转到添加用户
     *
     * @return
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd() {
        //公司id 先写死
        String companyId = getLoginCompanyId();

        //查询公司所有部门
        List<Dept> deptList = deptService.findAll(companyId);
        //通过公司id查询所有用户
        ModelAndView mv = new ModelAndView();
        mv.addObject("deptList", deptList);
        mv.setViewName("/system/user/user-add");
        return mv;
    }

    /**
     * 添加/修改用户
     *
     * @param user
     * @return
     */
    @RequestMapping("/edit")
    public String edit(User user) {
        //先写死 获取公司id和名称
        String companyId = getLoginCompanyId();
        String companyName = getLoginCompanyName();

        //添加公司id和名称到用户对象
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);

        //判断是添加还是修改用户
        if (StringUtils.isEmpty(user.getId())) {
            //添加用户
            userService.save(user);
            //发送一封通知邮件
            String to = user.getEmail();
            String subject = "恭喜您加入saas-export大家庭";
            String content = "欢迎使用saas-export系统，您的登录账号："+to+",登录密码为："+user.getPassword();
            try {
                MailUtil.sendMsg(to,subject,content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //修改用户
            userService.update(user);
        }
        return "redirect:/system/user/list.do";
    }

    /**
     * 跳转到更新页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id) {
        //先写死，获取公司id
        String companyId = getLoginCompanyId();

        List<Dept> deptList = deptService.findAll(companyId);

        //通过用户id查询用户
        User user = userService.findById(id);
        //通过公司id查询所有用户
        List<User> userList = userService.findAll(companyId);
        //判断所有用户是否包括本用户
        //遍历
        for (int i = 0; i < userList.size(); i++) {
            User d = userList.get(i);
            if (d.getId().equals(id)) {
                //修改的部门id和部门列表的部门的id相同，应该删除
                userList.remove(d);
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("user", user);
        mv.addObject("userList", userList);
        mv.addObject("deptList", deptList);
        mv.setViewName("/system/user/user-update");
        return mv;
    }

    /**
     * 通过id删除部门
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String, String> delete(String id) {
        Map<String, String> map = new HashMap<>();
        boolean b = userService.delete(id);
        if (b) {
            map.put("msg", "删除成功");
        } else {
            map.put("msg", "删除失败");
        }
        return map;
    }

    /**
     * 给用户添加角色(一):显示用户拥有的所有角色
     * <p>
     * http://localhost:8080/system/user/roleList.do?id=0
     */
    @RequestMapping("/roleList")
    public String roleList(String id) {
        //通过id查询用户
        User user = userService.findById(id);
        //查询所有角色
        List<Role> roleList = roleService.findAll(getLoginCompanyId());

        //查询用户拥有的所有角色
        List<Role> userRoleList = roleService.findRoleByUserId(id);
        //转换成字符串 用户拥有的所有角色的id
        String userRoleStr = "";
        for (Role role : userRoleList) {
            userRoleStr += role.getId();
        }

        request.setAttribute("user", user);
        request.setAttribute("roleList", roleList);
        request.setAttribute("userRoleStr", userRoleStr);
        return "/system/user/user-role";
    }

    /**
     * 保存用户角色
     * @param userId 用户id
     * @param roleIds 多个用户角色id，用逗号隔开
     */
    @RequestMapping("/changeRole")
    public String changeRole(String userId,String roleIds) {
        userService.updateUserRole(userId, roleIds);
        return "redirect:/system/user/list.do";
    }
}
