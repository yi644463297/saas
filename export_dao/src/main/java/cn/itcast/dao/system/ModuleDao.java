package cn.itcast.dao.system;

import cn.itcast.domain.system.Module;

import java.util.List;

public interface ModuleDao {

    //根据id查询
    Module findById(String moduleId);

    //根据id删除
    void delete(String moduleId);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询全部
    List<Module> findAll();

    /**
     * 查询module是否被引用
     */
    int findRoleModuleByModuleId(String id);

    /**
     * 通过用户id查询权限
     * @return
     */
    List<Module> findByUserId(String userId);

    /**
     * 通过belong字段查询权限
     * @param belong
     */
    List<Module> findByBelong(int belong);
}