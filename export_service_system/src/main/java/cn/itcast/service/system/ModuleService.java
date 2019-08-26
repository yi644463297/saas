package cn.itcast.service.system;

import cn.itcast.domain.system.Module;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ModuleService {
    /**
     * 分页查询
     */
    PageInfo<Module> findByPage(String companyId, int pageNum, int pageSize);

    /**
     * 通过公司id查询所有权限
     * @param companyId 公司id
     * @return 所有部门
     */
    List<Module> findAll(String companyId);

    /**
     * 添加权限
     * @param module 权限对象
     */
    void save(Module module);

    /**
     * 更新权限
     * @param module 权限对象
     */
    void update(Module module);

    /**
     * 通过id查询权限
     * @param id 主键
     */
    Module findById(String id);

    /**
     * 通过id删除权限
     * @param id
     */
    boolean delete(String id);

    /**
     * 通过用户id查询权限
     * @param userId
     */
    List<Module> findByUserId(String userId);
}
