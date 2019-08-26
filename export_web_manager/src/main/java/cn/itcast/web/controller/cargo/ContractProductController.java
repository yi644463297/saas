package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.ContractProduct;
import cn.itcast.domain.cargo.ContractProductExample;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.FileUploadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController {

    @Reference
    private ContractProductService contractProductService;
    @Reference
    private FactoryService factoryService;
    @Reference
    private ContractService contractService;
    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 显示货物列表
     * http://localhost:8080/cargo/contractProduct/list.do?contractId=2
     */
    @RequestMapping("/list")
    public String list(String contractId, @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize) {
        //显示货物分页信息
        ContractProductExample contractProductExample = new ContractProductExample();
        contractProductExample.createCriteria().andContractIdEqualTo(contractId);
        PageInfo<ContractProduct> page = contractProductService.findByPage(contractProductExample, pageNum, pageSize);
        request.setAttribute("page", page);
        //显示货物工厂信息
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList", factoryList);
        //保存contractId
        request.setAttribute("contractId", contractId);
        return "/cargo/product/product-list";
    }

    /**
     * 添加/修改货物
     */
    @RequestMapping("/edit")
    public String edit(ContractProduct contractProduct, MultipartFile productPhoto) {
        //根据是否有contractProductId判断是修改还是添加
        if (StringUtils.isEmpty(contractProduct.getId())) {
            if (productPhoto != null) {
                try {
                    String url = "http://" + fileUploadUtil.upload(productPhoto);
                    // 保存url
                    contractProduct.setProductImage(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //添加
            contractProductService.save(contractProduct);
        } else {
            //修改
            contractProductService.update(contractProduct);
        }
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractProduct.getContractId();
    }

    /**
     * 进入修改页面
     * http://localhost:8080/cargo/contractProduct/toUpdate.do?id=7
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id) {
        //查询购销合同货物
        ContractProduct contractProduct = contractProductService.findById(id);
        //获取所有货物厂家
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList", factoryList);
        request.setAttribute("contractProduct", contractProduct);
        return "/cargo/product/product-update";
    }

    /**
     * 删除
     * http://localhost:8080/cargo/contractProduct/delete.do?id=7&contractId=2
     *
     * @param id         合同货物id
     * @param contractId 合同id
     */
    @RequestMapping("/delete")
    public String delete(String id, String contractId) {
        contractProductService.delete(id);
        //带合同id参数跳转
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractId;
    }

    /**
     * 跳到上传货物页面
     * http://localhost:8080/cargo/contractProduct/toImport.do?contractId=2
     */
    @RequestMapping("/toImport")
    public String toImport(String contractId) {
        request.setAttribute("contractId", contractId);
        return "cargo/product/product-import";
    }

    @RequestMapping("/import")
    public String importExcel(String contractId, MultipartFile file) throws IOException {
        //创建工作簿
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        //创建表
        Sheet sheet = workbook.getSheetAt(0);
        //获取行数
        int rows = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < rows; i++) {
            //创建行
            Row row = sheet.getRow(i);
            //创建货物对象
            ContractProduct cp = new ContractProduct();
            cp.setContractId(contractId);
            //获取第2-9列
            cp.setFactoryName(row.getCell(1).getStringCellValue());
            cp.setProductNo(row.getCell(2).getStringCellValue());
            cp.setCnumber((int) row.getCell(3).getNumericCellValue());
            cp.setPackingUnit(row.getCell(4).getStringCellValue());
            cp.setLoadingRate(row.getCell(5).getNumericCellValue() + "");
            cp.setBoxNum((int) row.getCell(6).getNumericCellValue());
            cp.setPrice(row.getCell(7).getNumericCellValue());
            cp.setProductDesc(row.getCell(8).getStringCellValue());
            cp.setProductRequest(row.getCell(9).getStringCellValue());
            //设置工厂
            Factory factory = factoryService.findByName(cp.getFactoryName());
            if (factory != null) {
                cp.setFactoryId(factory.getId());
            }
            //保存货物
            contractProductService.save(cp);

        }
        return "cargo/product/product-import";

    }


}
