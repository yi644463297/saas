package cn.itcast.service.company;

import cn.itcast.domain.company.Company;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CompanyService {
    /**
     *  查询所有企业
     */
    List<Company> findAll();

    /**
     * 保存企业
     * @param company 企业
     */
    void save(Company company);

    /**
     * 更新企业
     * @param company
     */
    void update(Company company);

    /**
     * 通过id查询企业
     * @param id 主键
     */
    Company findById(String id);

    /**
     * 根据id删除企业
     * @param id
     */
    void delete(String id);

    /**
     * 分页查询
     */
    PageInfo<Company> findByPage(int pageNum, int pageSize);
}
