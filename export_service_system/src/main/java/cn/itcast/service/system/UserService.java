package cn.itcast.service.system;

import cn.itcast.domain.system.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface UserService {
    /**
     * 分页查询
     */
    PageInfo<User> findByPage(String companyId, int pageNum, int pageSize);

    /**
     * 通过公司id查询所有员工
     * @param companyId 公司id
     * @return 所有部门
     */
    List<User> findAll(String companyId);

    /**
     * 添加员工
     * @param user 员工对象
     */
    void save(User user);

    /**
     * 更新部门
     * @param user 员工对象
     */
    void update(User user);

    /**
     * 通过id查询员工
     * @param id 主键
     */
    User findById(String id);

    /**
     * 通过id删除员工
     * @param id
     */
    boolean delete(String id);

    /**
     * 保存用户角色
     * @param id 用户id
     * @param roleIds 多个角色id，用逗号隔开
     */
    void updateUserRole(String id, String roleIds);

    /**
     * 通过邮箱查询用户
     * @param email
     * @return
     */
    List<User> findByEmail(String email);
}
