package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.*;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductResult;
import cn.itcast.vo.ExportResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service(timeout = 100000)
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ExportDao exportDao;
    @Autowired
    private ContractProductDao contractProductDao;
    @Autowired
    private ExportProductDao exportProductDao;
    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ExtEproductDao extEproductDao;
    @Autowired
    private ContractDao contractDao;

    @Override
    public PageInfo<Export> findByPage(ExportExample exportExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<Export>(exportDao.selectByExample(exportExample));
    }

    @Override
    public void updateExport(ExportResult exportResult) {
        //获得报运单id
        String exportId = exportResult.getExportId();
        //查询报运单
        Export export = exportDao.selectByPrimaryKey(exportId);
        //设置：报运状态、备注
        export.setState(exportResult.getState());
        export.setRemark(exportResult.getRemark());
        exportDao.updateByPrimaryKeySelective(export);

        //修改报运商品
        Set<ExportProductResult> exportProductResultSet = exportResult.getProducts();
        if (exportProductResultSet != null && exportProductResultSet.size() > 0) {
            for (ExportProductResult exportProductResult : exportProductResultSet) {
                ExportProduct exportProduct = new ExportProduct();
                exportProduct.setId(exportProductResult.getExportProductId());
                exportProduct.setTax(exportProductResult.getTax());
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
    }

    @Override
    public Export findById(String id) {
        Export export = exportDao.selectByPrimaryKey(id);
        //查询报运单商品
        ExportProductExample example = new ExportProductExample();
        example.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> exportProductList = exportProductDao.selectByExample(example);
        //商品列表
        List<ExportProduct> list = new ArrayList<>();
        if (exportProductList != null && exportProductList.size() > 0) {
            /*for (ExportProduct exportProduct : exportProductList) {
                //添加货物到报运单
                list.add(exportProduct);
            }*/
            //代替上面的遍历赋值
            list.addAll(exportProductList);
        }
        export.setExportProducts(list);
        return export;
    }

    @Override
    public void save(Export export) {
        //设置报运单id、制单日期
        export.setId(UUID.randomUUID().toString());
        export.setInputDate(new Date());
        //设置合同号
        StringBuilder contractNos = new StringBuilder();

        //修改货物数量和附件数量
        //获取报运单所有购销合同id
        String contractIds = export.getContractIds();
        if (contractIds != null) {
            //所有购销合同
            String[] ids = contractIds.split(",");
            //遍历购销合同id
            for (int i = 0; i < ids.length; i++) {
                //获得购销合同
                Contract contract = contractDao.selectByPrimaryKey(ids[i]);
                //设置购销合同状态
                contract.setState(2);
                //保存购销合同
                contractDao.updateByPrimaryKeySelective(contract);
                //报运单合同号拼接
                contractNos.append(contract.getContractNo()).append(" ");
                //设置查询货物条件
                ContractProductExample example = new ContractProductExample();
                example.createCriteria().andContractIdEqualTo(ids[i]);
                //获取当前购销合同的所有货物
                List<ContractProduct> contractProductList = contractProductDao.selectByExample(example);
                if (contractProductList != null && contractProductList.size() > 0) {
                    //遍历当前购销合同所有货物
                    for (ContractProduct contractProduct : contractProductList) {
                        //转换到报运商品
                        ExportProduct exportProduct = new ExportProduct();
                        BeanUtils.copyProperties(contractProduct, exportProduct);
                        //设置报运商品其他属性
                        exportProduct.setId(UUID.randomUUID().toString());
                        exportProduct.setExportId(export.getId());
                        //保存报运商品
                        exportProductDao.insertSelective(exportProduct);
                        //报运单货物数量加1
                        if (export.getProNum() == null) {
                            export.setProNum(1);
                        } else {
                            export.setProNum(export.getProNum() + 1);
                        }
                        //获取当前货物的所有附件
                        //设置当前货物的附件查询条件
                        ExtCproductExample extCproductExample = new ExtCproductExample();
                        //根据货物id查询附件
                        extCproductExample.createCriteria().andContractIdEqualTo(contractProduct.getContractId());
                        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);
                        if (extCproductList != null && extCproductList.size() > 0) {
                            //遍历当前货物所有附件
                            for (ExtCproduct extCproduct : extCproductList) {
                                //转换成报运商品附件
                                ExtEproduct extEproduct = new ExtEproduct();
                                BeanUtils.copyProperties(extCproduct, extEproduct);
                                //设置报运商品附件其他属性
                                extEproduct.setId(UUID.randomUUID().toString());
                                extEproduct.setExportId(export.getId());
                                extEproduct.setExportProductId(exportProduct.getId());
                                //保存报运附件
                                extEproductDao.insertSelective(extEproduct);
                                //报运单附件数加1
                                if (export.getExtNum() == null) {
                                    export.setExtNum(1);
                                } else {
                                    export.setExtNum(export.getExtNum() + 1);
                                }
                            }
                        } else {
                            //没有附件，设置报运单附件为0
                            export.setExtNum(0);
                        }
                    }
                }
            }
            //设置合同号
            export.setCustomerContract(contractNos.toString());
            //设置报运单状态
            export.setState(0);

        }
        exportDao.insertSelective(export);
    }

    @Override
    public void update(Export export) {
        List<ExportProduct> exportProductList = export.getExportProducts();
        for (ExportProduct exportProduct : exportProductList) {
            exportProductDao.updateByPrimaryKeySelective(exportProduct);
        }
        exportDao.updateByPrimaryKeySelective(export);
    }

    @Override
    public void delete(String id) {
        exportDao.deleteByPrimaryKey(id);
    }
}
