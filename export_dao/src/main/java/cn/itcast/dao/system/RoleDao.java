package cn.itcast.dao.system;

import cn.itcast.domain.system.Role;

import java.util.List;


public interface RoleDao {

    //根据id查询
    Role findById(String id);

    //查询全部
    List<Role> findAll(String companyId);

	//根据id删除
    void delete(String id);

	//添加
    void save(Role role);

	//更新
    void update(Role role);

    /**
     *查询角色是否被角色用户表引用
     */
    int findRoleUserByRoleId(String id);

    /**
     * 通过角色id查询角色的所有权限
     * @return 返回
     */
    List<String> findRoleModuleByRoleId(String id);

    /**
     * 删除角色所有权限
     * @param roleId
     */
    void deleteModule(String roleId);

    /**
     * 给角色添加权限
     */
    void addModule(String roleId,String moduleId);

    /**
     * 通过用户id查询角色
     */
    List<Role> findRoleByUserId(String id);
}