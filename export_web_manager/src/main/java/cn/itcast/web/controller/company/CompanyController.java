package cn.itcast.web.controller.company;

import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Reference
    private CompanyService companyService;

    /**
     * 显示所有企业
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(
            Integer pageNum, Integer pageSize) {

        List<Company> list = companyService.findAll();
        ModelAndView mv = new ModelAndView();
        mv.addObject("list", list);
        mv.setViewName("company/company-list");
        return mv;
    }

    /**
     * 跳转到添加企业页面
     *
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd() {
        return "company/company-add";
    }

    /**
     * 保存或修改企业信息
     * 如果请求参数有id则是修改操作
     * 如果请求参数没有id则是添加操作
     */
    @RequestMapping("/edit")
    public String edit(Company company) {
        if (StringUtils.isEmpty(company.getId())) {
            //添加操作
            companyService.save(company);
        } else {
            //修改操作
            companyService.update(company);
        }
        return "redirect:/company/list.do";
    }

    /**
     * 跳转到更新页面，即添加页面
     */
    @RequestMapping("toUpdate")
    public String toUpdate(HttpServletRequest request) {
        //获得企业id
        String id = request.getParameter("id");
        //通过id查询企业
        Company company = companyService.findById(id);
        //把企业信息添加到请求域中
        request.setAttribute("company", company);
        //跳转页面
        return "company/company-update";
    }

    /**
     * 通过id删除企业
     */
    @RequestMapping("delete")
    public String delete(HttpServletRequest request) {
        //获取要删除的企业id
        String id = request.getParameter("id");
        //删除企业
        companyService.delete(id);
        //重定向到list页面
        return "redirect:/company/list.do";
    }
}
