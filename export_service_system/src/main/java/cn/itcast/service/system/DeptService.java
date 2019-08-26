package cn.itcast.service.system;

import cn.itcast.domain.system.Dept;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface DeptService {
    /**
     * 分页查询
     * @param companyId 公司id
     * @param pageNum 当前页
     * @param pageSize 每页大小
     * @return 分页数据
     */
    PageInfo<Dept> findByPage(String companyId, int pageNum, int pageSize);

    /**
     * 通过公司id查询所有部门
     * @param companyId 公司id
     * @return 所有部门
     */
    List<Dept> findAll(String companyId);

    /**
     * 添加部门
     * @param dept 部门对象
     */
    void save(Dept dept);

    /**
     * 更新部门
     * @param dept 部门对象
     */
    void update(Dept dept);

    /**
     * 通过id查询部门
     * @param id 主键
     */
    Dept findById(String id);

    /**
     * 通过id删除部门
     * @param id
     */
    boolean delete(String id);
}
