package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.RoleService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 分页显示所有角色
     *
     * @param pageNum  当前页
     * @param pageSize 页面大小
     * @return
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        //先写死
        String companyId = getLoginCompanyId();

        PageInfo<Role> pageInfo = roleService.findByPage(companyId, pageNum, pageSize);
        ModelAndView mv = new ModelAndView();
        mv.addObject("page", pageInfo);
        mv.setViewName("system/role/role-list");
        return mv;
    }

    /**
     * 跳转到添加角色
     *
     * @return
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd() {
        //公司id 先写死
        String companyId = getLoginCompanyId();

        //查询公司所有角色
        List<Dept> deptList = deptService.findAll(companyId);
        //通过公司id查询所有角色
        List<Role> roleList = roleService.findAll(companyId);
        ModelAndView mv = new ModelAndView();
        mv.addObject("roleList", roleList);
        mv.addObject("deptList", deptList);
        mv.setViewName("/system/role/role-add");
        return mv;
    }

    /**
     * 添加/修改角色
     *
     * @param role
     * @return
     */
    @RequestMapping("/edit")
    public String edit(Role role) {
        //先写死 获取公司id和名称
        String companyId = getLoginCompanyId();
        String companyName = getLoginCompanyName();

        //添加公司id和名称到角色对象
        role.setCompanyId(companyId);
        role.setCompanyName(companyName);

        //判断是添加还是修改角色
        if (StringUtils.isEmpty(role.getId())) {
            //添加角色
            roleService.save(role);
        } else {
            //修改角色
            roleService.update(role);
        }
        return "redirect:/system/role/list.do";
    }

    /**
     * 跳转到更新页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id) {
        String companyId = getLoginCompanyId();

        List<Dept> deptList = deptService.findAll(companyId);

        //通过id查询角色
        Role role = roleService.findById(id);
        //通过公司id查询所有角色
        List<Role> roleList = roleService.findAll(companyId);
        //判断所有角色是否包括本角色
        //遍历
        for (int i = 0; i < roleList.size(); i++) {
            Role d = roleList.get(i);
            if (d.getId().equals(id)) {
                //修改的角色id和角色列表的角色的id相同，应该删除
                roleList.remove(d);
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("role", role);
        mv.addObject("roleList", roleList);
        mv.addObject("deptList", deptList);
        mv.setViewName("/system/role/role-update");
        return mv;
    }

    /**
     * 通过id删除角色
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String, String> delete(String id) {
        Map<String, String> map = new HashMap<>();
        boolean b = roleService.delete(id);
        if (b) {
            map.put("msg", "删除成功");
        } else {
            map.put("msg", "删除失败，该值可能被引用，如有疑问，请联系管理员");
        }
        return map;
    }

    /**
     * 跳转到角色权限页面
     */
    @RequestMapping("/roleModule")
    @ResponseBody
    public ModelAndView roleModule(String roleid) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("system/role/role-module");
        Role role = roleService.findById(roleid);
        mv.addObject("role", role);
        return mv;
    }

    /**
     * 权限管理
     * http://localhost:8080/system/role/roleModule.do?roleid=4
     */
    @RequestMapping("/getZNodes")
    @ResponseBody
    public List<Map<String, Object>> getZNodes(String roleid) {
        //用于存放返回值
        List<Map<String, Object>> mapList = new ArrayList<>();
        //用于存放角色的权限
        List<Module> list = new ArrayList<>();
        //查询所有权限
        List<Module> moduleList = moduleService.findAll(getLoginCompanyId());
        //查询当前角色所有权限id
        List<String> moduleIds = roleService.findRoleModuleByRoleId(roleid);
        //遍历所有权限id获取所有权限
        for (String moduleId : moduleIds) {
            //通过id查询权限
            Module module = moduleService.findById(moduleId);
            list.add(module);
        }
        //遍历所有权限
        for (Module module : moduleList) {
            //判断角色是否拥有该权限
            Map<String, Object> map = new HashMap<>();
            map.put("id", module.getId());
            map.put("pId", module.getParentId());
            map.put("name", module.getName());
            if (module.getCtype() != null && module.getCtype() < 2) {
                //说明权限不是最低级菜单
                map.put("open", true);
            }
            if (list.contains(module)) {
                //角色拥有该权限
                map.put("checked", true);
            }
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 保存角色权限
     */
    @RequestMapping("/saveModule")
    public String saveModule(String moduleIdStr,String roleId) {
        //通过角色id删除角色所有权限
        roleService.deleteModule(roleId);
        //添加角色权限
        roleService.addModule(roleId,moduleIdStr);
        return "redirect:/system/role/list.do";
    }
}
