package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.service.cargo.ContractService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ContractServiceImpl implements ContractService {
    @Autowired
    private ContractDao contractDao;

    @Override
    public PageInfo<Contract> findByPage(ContractExample contractExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Contract> contractList = contractDao.selectByExample(contractExample);
        return new PageInfo<Contract>(contractList);
    }

    @Override
    public List<Contract> findAll(ContractExample contractExample) {
        List<Contract> contractList = contractDao.selectByExample(contractExample);
        return contractList;
    }

    @Override
    public Contract findById(String id) {
        Contract contract = contractDao.selectByPrimaryKey(id);
        return contract;
    }

    @Override
    public void save(Contract contract) {
        //设置主键id
        contract.setId(UUID.randomUUID().toString());
        //设置创建时间
        contract.setCreateTime(new Date());
        //设置更新时间
        contract.setUpdateTime(new Date());
        //设置状态
        contract.setState(0);


        // 初始化： 总金额为0
        contract.setTotalAmount(0d);
        // 初始化： 货物数、附件数
        contract.setProNum(0);
        contract.setExtNum(0);

        contractDao.insertSelective(contract);
    }

    @Override
    public void update(Contract contract) {
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void delete(String id) {
        contractDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Contract> selectByDeptId(String deptId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(contractDao.selectByDeptId(deptId));
    }
}
