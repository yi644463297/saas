package cn.itcast.dao.system;

import cn.itcast.domain.system.User;

import java.util.List;


public interface UserDao {

	//根据企业id查询全部
	List<User> findAll(String companyId);

	//根据id查询
    User findById(String userId);

	//根据id删除
	void delete(String userId);

	//保存
	void save(User user);

	//更新
	void update(User user);

	/**
	 * 查询用户是否被用户角色表引用
	 * @param id
	 */
    int findUserRoleByUserId(String id);

	/**
	 * 删除用户所有角色
	 * @param id 用户id
	 */
	void deleteUserRole(String id);

	/**
	 * 给用户添加角色
	 * @param id 用户id
	 * @param roleIds
	 */
	void addRole(String id, String roleIds);

	/**
	 * 通过邮箱查询用户
	 * 优化，使用List集合封装数据，
	 * 好处： 提供系统容错能力； 更符合接口设计原则（条件查询时候，为了考虑通用性，所有最好返回集合）
	 * tip：主键查询一般使用User
	 * @param email
	 * @return
	 */
    List<User> findByEmail(String email);
}