package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.domain.cargo.ExtCproductExample;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import cn.itcast.service.cargo.ExtCproductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/cargo/extCproduct")
public class ExtCproductController extends BaseController {
    @Reference
    private ExtCproductService extCproductService;
    @Reference
    private FactoryService factoryService;

    /**
     * http://localhost:8080/cargo/extCproduct/list.do?
     * contractId
     * contractProductId
     * 附件列表
     */
    @RequestMapping("/list")
    public String list(String contractId, String contractProductId,
                       @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize) {
        //查询货物的附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractProductIdEqualTo(contractProductId);
        PageInfo<ExtCproduct> pageInfo = extCproductService.findByPage(extCproductExample, pageNum, pageSize);
        request.setAttribute("page", pageInfo);
        //查询附件工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList", factoryList);

        request.setAttribute("contractId", contractId);
        request.setAttribute("contractProductId", contractProductId);
        return "cargo/extc/extc-list";
    }

    /**
     * 跳转修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id) {
        //查询附件
        ExtCproduct extCproduct = extCproductService.findById(id);
        //查询工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        request.setAttribute("extCproduct", extCproduct);
        request.setAttribute("factoryList", factoryList);

        return "cargo/extc/extc-update";
    }

    /**
     * 添加/修改附件
     * /cargo/extCproduct/edit.do
     */
    @RequestMapping("/edit")
    public String edit(ExtCproduct extCproduct) {
        //设置公司id和名称
        extCproduct.setCompanyId(getLoginCompanyId());
        extCproduct.setCompanyName(getLoginCompanyName());
        //判断是添加还是修改
        if (extCproduct.getId() == null) {
            extCproductService.save(extCproduct);
        } else {
            extCproductService.update(extCproduct);
        }
        return "redirect:/cargo/extCproduct/list.do?contractId=" + extCproduct.getContractId() +
                "&contractProductId=" + extCproduct.getContractProductId();
    }

    /**
     * 删除附件
     */
    @RequestMapping("/delete")
    public String delete(ExtCproduct extCproduct) {
        extCproductService.delete(extCproduct.getId());
        return "redirect:/cargo/extCproduct/list.do?contractId=" + extCproduct.getContractId() +
                "&contractProductId=" + extCproduct.getContractProductId();
    }


}
