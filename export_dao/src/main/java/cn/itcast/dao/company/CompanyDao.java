package cn.itcast.dao.company;

import cn.itcast.domain.company.Company;

import java.util.List;

public interface CompanyDao {
    /**
     * 查询全部企业
     * @return
     */
    List<Company> findAll();

    /**
     * 保存企业
     * @param company
     */
    void save(Company company);

    /**
     * 更新企业
     * @param company
     */
    void update(Company company);

    /**
     * 通过id查找企业
     * @param id 主键
     */
    Company findById(String id);

    /**
     * 根据id删除企业
     * @param id
     */
    void delete(String id);
}
