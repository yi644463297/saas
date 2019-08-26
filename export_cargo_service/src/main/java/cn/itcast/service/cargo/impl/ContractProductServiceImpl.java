package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ContractProductDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.vo.ContractProductVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service(timeout = 100000)
public class ContractProductServiceImpl implements ContractProductService {
    @Autowired
    private ContractProductDao contractProductDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ExtCproductDao extCproductDao;

    @Override
    public PageInfo<ContractProduct> findByPage(ContractProductExample contractProductExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<ContractProduct>(contractProductDao.selectByExample(contractProductExample));
    }

    @Override
    public List<ContractProduct> findAll(ContractProductExample contractProductExample) {
        return contractProductDao.selectByExample(contractProductExample);
    }

    @Override
    public ContractProduct findById(String id) {
        return contractProductDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(ContractProduct contractProduct) {
        //设置id
        contractProduct.setId(UUID.randomUUID().toString());
        //设置货物价格
        contractProduct.setAmount(contractProduct.getPrice() * contractProduct.getCnumber());

        //采用分散计算，更新购销合同总价 总价格=总价格+新添加货物价格
        //查询旧购销合同
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        if (contract != null) {
            //设置总价格
            contract.setTotalAmount(contract.getTotalAmount() + contractProduct.getPrice() * contractProduct.getCnumber());
            //更新购销合同货物数
            contract.setProNum(contract.getProNum() + contractProduct.getCnumber());
        }
        //更新购销合同总价
        contractDao.updateByPrimaryKeySelective(contract);


        contractProductDao.insertSelective(contractProduct);
    }

    @Override
    public void update(ContractProduct contractProduct) {
        //设置货物总价
        contractProduct.setAmount(contractProduct.getPrice() * contractProduct.getCnumber());

        //采用分散计算，更新购销合同总价 总价格=总价格+修改后货物价格-修改前货物价格
        //查询旧购销合同
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //查询修改前货物价格
        ContractProduct oldContractProduct = contractProductDao.selectByPrimaryKey(contractProduct.getId());
        //设置总价格
        contract.setTotalAmount(contract.getTotalAmount() + contractProduct.getPrice() * contractProduct.getCnumber() -
                oldContractProduct.getAmount());
        //更新购销合同货物/附件数
        contract.setProNum(contract.getProNum() + contractProduct.getCnumber() - oldContractProduct.getCnumber());
        //更新购销合同总价
        contractDao.updateByPrimaryKeySelective(contract);

        //更新
        contractProductDao.updateByPrimaryKeySelective(contractProduct);
    }

    @Override
    public void delete(String id) {
        //获取货物
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(id);
        //通过id获得合同
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //查询出货物所有的附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractProductIdEqualTo(id);
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);
        //定义附件价格总和
        Double extAmount = 0d;
        //遍历所有附件
        if (extCproductList != null && extCproductList.size() > 0) {
            for (ExtCproduct extCproduct : extCproductList) {
                //附件价格叠加
                extAmount += extCproduct.getAmount();
                //修改合同附件数
                contract.setExtNum(contract.getExtNum() - extCproduct.getCnumber());
                //删除附件
                extCproductDao.deleteByPrimaryKey(extCproduct.getId());
            }
        }

        //修改合同总价 总价=原价格-货物价格-所有附件价格
        contract.setTotalAmount(contract.getTotalAmount() - contractProduct.getAmount() - extAmount);
        //修改合同货物数
        contract.setProNum(contract.getProNum() - contractProduct.getCnumber());

        //删除货物
        contractProductDao.deleteByPrimaryKey(id);
        //修改合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public List<ContractProductVo> findByShipTime(String companyId, String shipTime) {
        return contractProductDao.findByShipTime(companyId, shipTime);
    }
}
