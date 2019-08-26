package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Module;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.ModuleService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
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
@RequestMapping("/system/module")
public class ModuleController extends BaseController {
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private DeptService deptService;

    /**
     * 分页显示所有权限
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

        PageInfo<Module> pageInfo = moduleService.findByPage(companyId, pageNum, pageSize);
        ModelAndView mv = new ModelAndView();
        mv.addObject("page", pageInfo);
        mv.setViewName("system/module/module-list");
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

        //通过公司id查询所有权限
        List<Module> moduleList = moduleService.findAll(companyId);
        ModelAndView mv = new ModelAndView();

        mv.addObject("menus", moduleList);
        mv.setViewName("/system/module/module-add");
        return mv;
    }

    /**
     * 添加/修改权限
     *
     * @param module
     * @return
     */
    @RequestMapping("/edit")
    public String edit(Module module) {
        //先写死 获取公司id和名称
        String companyId = getLoginCompanyId();
        String companyName = getLoginCompanyName();


        //判断是添加还是修改权限
        if (StringUtils.isEmpty(module.getId())) {
            //添加权限
            moduleService.save(module);
        } else {
            //修改权限
            moduleService.update(module);
        }
        return "redirect:/system/module/list.do";
    }

    /**
     * 跳转到更新页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id) {
        //先写死，获取公司id
        String companyId = getLoginCompanyId();

        //查询module
        Module module = moduleService.findById(id);
        //通过公司id查询所有权限
        List<Module> moduleList = moduleService.findAll(companyId);
        //判断所有权限是否包括本权限
        //遍历
        for (int i = 0; i < moduleList.size(); i++) {
            Module d = moduleList.get(i);
            if (d.getId().equals(id)) {
                //修改的权限id和权限列表的权限的id相同，应该删除
                moduleList.remove(d);
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("menus", moduleList);
        mv.addObject("module", module);
        mv.setViewName("/system/module/module-update");
        return mv;
    }

    /**
     * 通过id删除角色
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String, String> delete(String id) {
        Map<String, String> map = new HashMap<>();
        boolean b = moduleService.delete(id);
        if (b) {
            map.put("msg", "删除成功");
        } else {
            map.put("msg", "删除失败，该值可能被引用，如有疑问，请联系管理员");
        }
        return map;
    }
}
