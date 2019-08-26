package cn.itcast.service.system.impl;

import cn.itcast.dao.system.DeptDao;
import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptDao deptDao;

    @Override
    public PageInfo<Dept> findByPage(String companyId, int pageNum, int pageSize) {
        //开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<Dept> list = deptDao.findAll();
        return new PageInfo<>(list);
    }

    @Override
    public List<Dept> findAll(String companyId) {
        return deptDao.findByCompanyId(companyId);
    }

    @Override
    public void save(Dept dept) {
        dept.setId(UUID.randomUUID().toString());
        deptDao.save(dept);
    }

    @Override
    public void update(Dept dept) {
        deptDao.update(dept);
    }

    @Override
    public Dept findById(String id) {
        return deptDao.findById(id);
    }

    @Override
    public boolean delete(String id) {

        List<Dept> deptList = deptDao.findByParentId(id);
        //判断该部门是否作为主键有被引用
        if (deptList.size() == 0) {
            //没有子部门，可以删除
            deptDao.delete(id);
            return true;
        } else {
            //有子部门，不能删除
            return false;
        }
    }
}
