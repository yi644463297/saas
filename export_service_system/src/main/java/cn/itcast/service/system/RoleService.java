package cn.itcast.service.system;

import cn.itcast.domain.system.Role;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface RoleService {
    /**
     * 分页查询
     */
    PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize);

    /**
     * 通过公司id查询所有角色
     * @param companyId 公司id
     * @return 所有部门
     */
    List<Role> findAll(String companyId);

    /**
     * 添加角色
     * @param role 角色对象
     */
    void save(Role role);

    /**
     * 更新角色
     * @param role 角色对象
     */
    void update(Role role);

    /**
     * 通过id查询角色
     * @param id 主键
     */
    Role findById(String id);

    /**
     * 通过id删除角色
     * @param id
     */
    boolean delete(String id);

    /**
     * 通过id查询角色所有权限
     */
    List<String> findRoleModuleByRoleId(String id);

    /**
     * 删除角色所有权限
     * @param roleId 角色id
     */
    void deleteModule(String roleId);

    /**
     * 给角色添加权限
     * @param roleId 角色id
     * @param moduleIdStr 权限id字符串，用逗号隔开
     */
    void addModule(String roleId,String moduleIdStr);

    /**
     * 通过用户id查询所有角色
     * @param id
     */
    List<Role> findRoleByUserId(String id);
}
