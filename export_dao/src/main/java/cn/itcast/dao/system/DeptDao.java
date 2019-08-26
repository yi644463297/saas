package cn.itcast.dao.system;

import cn.itcast.domain.system.Dept;

import java.util.List;

public interface DeptDao {
    /**
     * 查询所有部门
     */
    List<Dept> findAll();

    /**
     * 根据id查询部门
     */
    Dept findById(String id);

    /**
     * 通过公司id查询所有部门
     * @param companyId 公司id
     * @return 所有部门
     */
    List<Dept> findByCompanyId(String companyId);

    /**
     * 添加部门
     * @param dept
     */
    void save(Dept dept);

    /**
     * 更新部门
     * @param dept
     */
    void update(Dept dept);

    /**
     * 通过id删除部门
     * @param id
     */
    void delete(String id);

    /**
     * 通过id查询所有子部门
     * @param parentId 父部门id
     */
    List<Dept> findByParentId(String parentId);
}
