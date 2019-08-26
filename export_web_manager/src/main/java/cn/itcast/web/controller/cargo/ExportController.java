package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.service.cargo.ExportProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductVo;
import cn.itcast.vo.ExportResult;
import cn.itcast.vo.ExportVo;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController {
    @Reference
    private ContractService contractService;
    @Reference
    private ExportService exportService;
    @Reference
    private ExportProductService exportProductService;

    /**
     * 进入合同管理页面
     * http://localhost:8080/cargo/export/contractList.do
     */
    @RequestMapping("/contractList")
    public String contractList(@RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "5") int pageSize) {
        ContractExample contractExample = new ContractExample();
        contractExample.setOrderByClause("create_time desc");
        ContractExample.Criteria criteria = contractExample.createCriteria();
        criteria.andStateEqualTo(1);
        criteria.andCompanyIdEqualTo(getLoginCompanyId());
        PageInfo<Contract> pageInfo = contractService.findByPage(contractExample, pageNum, pageSize);
        request.setAttribute("page", pageInfo);
        return "cargo/export/export-contractList";
    }

    /**
     * 进入出口报运页面
     * http://localhost:8080/cargo/export/list.do
     */
    @RequestMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "5") int pageSize) {
        ExportExample exportExample = new ExportExample();
        ExportExample.Criteria criteria = exportExample.createCriteria();
        criteria.andCompanyIdEqualTo(getLoginCompanyId());
        PageInfo<Export> pageInfo = exportService.findByPage(exportExample, pageNum, pageSize);
        //保存
        request.setAttribute("page", pageInfo);
        return "cargo/export/export-list";
    }

    /**
     * 新增报运
     * http://localhost:8080/cargo/export/toExport.do
     */
    @RequestMapping("/toExport")
    public String toExport(String id) {
        request.setAttribute("id", id);
        return "cargo/export/export-toExport";
    }

    /**
     * 添加/保存
     */
    @RequestMapping("/edit")
    public String edit(Export export) {
        export.setCompanyId(getLoginCompanyId());
        export.setCompanyName(getLoginCompanyName());
        if (export.getId() == null) {
            //添加
            exportService.save(export);
        } else {
            exportService.update(export);
        }
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 跳转到编辑页面
     * http://localhost:8080/cargo/export/toUpdate.do?id=2569e993-7b84-4d75-b018-077439b1eff5
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id) {
        //查询报运单
        Export export = exportService.findById(id);
        //查询货物单商品
        ExportProductExample exportProductExample = new ExportProductExample();
        exportProductExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> exportProductList = exportProductService.findAll(exportProductExample);
        request.setAttribute("eps", exportProductList);
        request.setAttribute("export", export);
        return "cargo/export/export-update";
    }

    /**
     * 出口报运列表，点击取消
     */
    @RequestMapping("/cancel")
    public String cancel(String id) {
        Export export = exportService.findById(id);
        export.setState(0);
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 出口报运列表，点击提交
     */
    @RequestMapping("/submit")
    public String submit(String id) {
        Export export = exportService.findById(id);
        export.setState(1);
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 电子报运
     *
     * @param id
     */
    @RequestMapping("/exportE")
    public String exportE(String id) {
        //查询报运单
        Export export = exportService.findById(id);
        //创建电子报运对象
        ExportVo exportVo = new ExportVo();
        //拷贝
        BeanUtils.copyProperties(export, exportVo);
        //设置属性
        exportVo.setExportId(id);
        //创建电子报运商品对象
        List<ExportProductVo> exportProductVoList = new ArrayList<>();
        //获取报运单商品列表
        List<ExportProduct> exportProductList = export.getExportProducts();
        //获取电子报运商品列表
       // List<ExportProductVo> exportProductVoList = exportVo.getProducts();
        //遍历报运单商品列表并赋值给电子报运商品列表
        for (ExportProduct exportProduct : exportProductList) {
            ExportProductVo epvo = new ExportProductVo();
            BeanUtils.copyProperties(exportProduct, epvo);
            //设置属性
            epvo.setExportProductId(exportProduct.getId());
            epvo.setExportId(id);

            exportProductVoList.add(epvo);
        }
        //给电子报运赋值电子报运商品列表
        exportVo.setProducts(exportProductVoList);
        //远程电子报运
        WebClient.create("http://localhost:9001/ws/export/user").post(exportVo);
        //获取电子报运结果
        ExportResult exportResult = WebClient.create("http://localhost:9001/ws/export/user/" + id)
                .get(ExportResult.class);
        exportService.updateExport(exportResult);
        return "redirect:/cargo/export/list.do";
    }
}
