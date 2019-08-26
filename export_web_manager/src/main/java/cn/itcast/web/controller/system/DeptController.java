package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/dept")
public class DeptController extends BaseController{
    @Autowired
    private DeptService deptService;

    /**
     * 显示所有部门
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

        PageInfo<Dept> pageInfo = deptService.findByPage(companyId, pageNum, pageSize);
        ModelAndView mv = new ModelAndView();
        mv.addObject("page", pageInfo);
        mv.setViewName("system/dept/dept-list");
        return mv;
    }

    /**
     * 跳转到添加部门
     *
     * @return
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd() {
        //公司id 先写死

        String companyId = getLoginCompanyId();
        //通过公司id查询所有部门
        List<Dept> deptList = deptService.findAll(companyId);
        ModelAndView mv = new ModelAndView();
        mv.addObject("deptList", deptList);
        mv.setViewName("/system/dept/dept-add");
        return mv;
    }

    /**
     * 添加/修改部门
     *
     * @param dept
     * @return
     */
    @RequestMapping("/edit")
    public String edit(Dept dept) {
        //先写死 获取公司id和名称
        String companyId = getLoginCompanyId();
        String companyName = getLoginCompanyName();

        //添加公司id和名称到部门对象
        dept.setCompanyId(companyId);
        dept.setCompanyName(companyName);

        //判断是添加还是修改部门
        if (StringUtils.isEmpty(dept.getId())) {
            //添加部门
            deptService.save(dept);
        } else {
            //修改部门
            deptService.update(dept);
        }
        return "redirect:/system/dept/list.do";
    }

    /**
     * 跳转到更新页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id) {
        //先写死，获取公司id
        String companyId = getLoginCompanyId();

        //通过部门id查询部门
        Dept dept = deptService.findById(id);
        //通过公司id查询所有部门
        List<Dept> deptList = deptService.findAll(companyId);
        //判断所有部门是否包括本部门
        //遍历
        for (int i = 0; i < deptList.size(); i++) {
            Dept d = deptList.get(i);
            if (d.getId().equals(id)) {
                //修改的部门id和部门列表的部门的id相同，应该删除
                deptList.remove(d);
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("dept", dept);
        mv.addObject("deptList", deptList);
        mv.setViewName("/system/dept/dept-update");
        return mv;
    }

    /**
     * 通过id删除部门
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String, String> delete(String id) {
        Map<String, String> map = new HashMap();
        boolean flag = deptService.delete(id);
//        int i = 10 / 0;
        System.out.println("aaa");
        if (flag) {
            //删除成功
            map.put("msg", "删除成功");
        } else {
            map.put("msg", "删除失败");
        }
        return map;
    }
}
