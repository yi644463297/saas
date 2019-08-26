package cn.itcast.service.system.impl;

import cn.itcast.dao.system.ModuleDao;
import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleDao moduleDao;
    @Autowired
    private UserDao userDao;

    @Override
    public PageInfo<Module> findByPage(String companyId, int pageNum, int pageSize) {
        //开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<Module> list = moduleDao.findAll();
        return new PageInfo<>(list);
    }

    @Override
    public List<Module> findAll(String companyId) {
        return moduleDao.findAll();
    }

    @Override
    public void save(Module module) {
        module.setId(UUID.randomUUID().toString());
        moduleDao.save(module);
    }

    @Override
    public void update(Module module) {
        moduleDao.update(module);
    }

    @Override
    public Module findById(String id) {
        return moduleDao.findById(id);
    }

    @Override
    public boolean delete(String id) {
        //查询是否有外键引用
        int i = moduleDao.findRoleModuleByModuleId(id);
        if (i == 0) {
            moduleDao.delete(id);
            return true;
        }
        return false;
    }

    /**
     * 根据登录的用户id查询用户所具有的所有权限（模块，菜单）
     *   1.根据用户id查询用户
     *   2.根据用户degree级别判断
     *   3.如果degree==0 （内部的sass管理）
     *      根据模块中的belong字段进行查询，belong = "0";
     *   4.如果degree==1 （租用企业的管理员）
     *      根据模块中的belong字段进行查询，belong = "1";
     *   5.其他的用户类型
     *      借助RBAC的数据库模型，多表联合查询出结果
     */
    @Override
    public List<Module> findByUserId(String userId) {
        User user = userDao.findById(userId);
        if (user.getDegree() == 0) {
            //内部的sass管理
            return moduleDao.findByBelong(0);
        } else if (user.getDegree() == 1) {
            //租用企业的管理员
            return moduleDao.findByBelong(1);
        } else {
            //普通人员
            return moduleDao.findByUserId(userId);
        }
    }
}
