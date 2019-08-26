package cn.itcast.dao.cargo;

import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
public class CargoDaoTest {
    @Autowired
    private FactoryDao factoryDao;

    /**
     * 普通更新, 如果对象某些属性没有设置值，有可能会导致数据丢失。
     * 生成的SQL如下：
     * update co_factory set ctype = ?, full_name = ?, factory_name = ?,
     * contacts = ?, phone = ?, mobile = ?, fax = ?, address = ?, inspector = ?,
     * remark = ?, order_no = ?, state = ?, create_by = ?, create_dept = ?,
     * create_time = ?, update_by = ?,update_time = ?
     * where id = ?
     */
    @Test
    public void update(){
        Factory factory = new Factory();
        factory.setId("999"); // 此id在数据库不存在，这里重点是观察生成的sql
        factory.setCreateTime(new Date());
        factory.setUpdateTime(new Date());
        factory.setState(1);
        factoryDao.updateByPrimaryKeySelective(factory);
    }

    /**
     * 动态SQL更新,
     * 生成的SQL如下：
     * update co_factory SET state = ?, create_time = ?, update_time = ? where id = ?
     */
    @Test
    public void dynamicSQL(){
        Factory factory = new Factory();
        factory.setId("999");
        factory.setCreateTime(new Date());
        factory.setUpdateTime(new Date());
        factory.setState(0);
        factoryDao.updateByPrimaryKeySelective(factory);
    }

    /**
     * 多条件查询
     * select id, ctype, full_name, factory_name, contacts, phone, mobile, fax, address,
     * inspector, remark, order_no, state, create_by, create_dept, create_time,
     * update_by, update_time
     * from co_factory WHERE ( factory_name = ? and address like ? )
     */
    @Test
    public void findByExample(){
        FactoryExample example = new FactoryExample();
        FactoryExample.Criteria criteria = example.createCriteria();
        criteria.andFactoryNameEqualTo("华艺");
        criteria.andAddressLike("%莞%");
        List<Factory> list = factoryDao.selectByExample(example);
        System.out.println(list);
    }
}