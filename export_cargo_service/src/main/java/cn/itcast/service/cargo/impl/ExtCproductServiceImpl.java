package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.domain.cargo.ExtCproductExample;
import cn.itcast.service.cargo.ExtCproductService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service(timeout = 100000)
public class ExtCproductServiceImpl implements ExtCproductService {
    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ContractDao contractDao;

    @Override
    public PageInfo<ExtCproduct> findByPage(ExtCproductExample extCproductExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(extCproductDao.selectByExample(extCproductExample));
    }

    @Override
    public List<ExtCproduct> findAll(ExtCproductExample extCproductExample) {
        return extCproductDao.selectByExample(extCproductExample);
    }

    @Override
    public ExtCproduct findById(String id) {
        return extCproductDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(ExtCproduct extCproduct) {
        //添加附件id
        extCproduct.setId(UUID.randomUUID().toString());
        //计算附件总价格
        Double amount = 0d;
        if (extCproduct.getPrice() != null && extCproduct.getCnumber() != null) {
            amount = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        extCproduct.setAmount(amount);
        //修改合同总价格和附件
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        contract.setTotalAmount(contract.getTotalAmount() + amount);
        contract.setExtNum(contract.getExtNum() + 1);
        //保存合同
        contractDao.updateByPrimaryKeySelective(contract);
        //保存附件
        extCproductDao.insertSelective(extCproduct);
    }

    @Override
    public void update(ExtCproduct extCproduct) {
        //获取旧附件
        ExtCproduct ext = extCproductDao.selectByPrimaryKey(extCproduct.getId());
        //计算附件总价格
        Double amount = 0d;
        if (extCproduct.getPrice() != null && extCproduct.getCnumber() != null) {
            amount = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        extCproduct.setAmount(amount);
        //修改合同总价格
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        contract.setTotalAmount(contract.getTotalAmount() + amount - ext.getAmount());
        contractDao.updateByPrimaryKeySelective(contract);
        extCproductDao.updateByPrimaryKeySelective(extCproduct);
    }

    @Override
    public void delete(String id) {
        //查询出附件
        ExtCproduct extCproduct = extCproductDao.selectByPrimaryKey(id);
        //更新合同总价格和附件数
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        contract.setTotalAmount(contract.getTotalAmount() - extCproduct.getAmount());
        contract.setExtNum(contract.getExtNum() - 1);
        contractDao.updateByPrimaryKeySelective(contract);
        extCproductDao.deleteByPrimaryKey(id);
    }
}
