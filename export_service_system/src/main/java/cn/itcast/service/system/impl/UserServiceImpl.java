package cn.itcast.service.system.impl;

import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public PageInfo<User> findByPage(String companyId, int pageNum, int pageSize) {
        //开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userDao.findAll(companyId);
        return new PageInfo<>(list);
    }

    @Override
    public List<User> findAll(String companyId) {
        return userDao.findAll(companyId);
    }

    @Override
    public void save(User user) {
        user.setId(UUID.randomUUID().toString());
        //md5加盐加密
        String newPWS = new Md5Hash(user.getPassword(), user.getEmail()).toString();
        user.setPassword(newPWS);
        userDao.save(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public User findById(String id) {
        return userDao.findById(id);
    }

    @Override
    public boolean delete(String id) {
        //查询是否有外键引用
        int i = userDao.findUserRoleByUserId(id);
        if (i == 0) {
            userDao.delete(id);
            return true;
        }
        return false;
    }

    @Override
    public void updateUserRole(String userId, String roleIds) {
        //删除用户所有角色
        userDao.deleteUserRole(userId);

        //获取所有角色id
        if (!StringUtils.isEmpty(roleIds) && roleIds.trim() != "") {
            String[] ids = roleIds.split(",");
            for (String id : ids) {
                //给用户添加新的角色
                userDao.addRole(userId, id);
            }
        }
    }

    @Override
    public List<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
