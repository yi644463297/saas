package cn.itcast.service.system.impl;

import cn.itcast.dao.system.RoleDao;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize) {
        //开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<Role> list = roleDao.findAll(companyId);
        return new PageInfo<>(list);
    }

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(companyId);
    }

    @Override
    public void save(Role role) {
        role.setId(UUID.randomUUID().toString());
        roleDao.save(role);
    }

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id);
    }

    @Override
    public boolean delete(String id) {
        //查询是否有外键引用
        int i = roleDao.findRoleUserByRoleId(id);
        List<String> list = roleDao.findRoleModuleByRoleId(id);
        if (i == 0 && list.size() == 0) {
            roleDao.delete(id);
            return true;
        }
        return false;
    }

    @Override
    public List<String> findRoleModuleByRoleId(String id) {
        return roleDao.findRoleModuleByRoleId(id);
    }

    @Override
    public void deleteModule(String roleId) {
        roleDao.deleteModule(roleId);
    }

    @Override
    public void addModule(String roleId, String moduleIdStr) {
        //判空
        if (moduleIdStr != null && moduleIdStr != "") {
            //获取所有权限id
            String[] moduleIds = moduleIdStr.split(",");
            for (String moduleId : moduleIds) {
                //添加权限
                roleDao.addModule(roleId, moduleId);
            }
        }
    }

    @Override
    public List<Role> findRoleByUserId(String id) {
        return roleDao.findRoleByUserId(id);
    }
}
