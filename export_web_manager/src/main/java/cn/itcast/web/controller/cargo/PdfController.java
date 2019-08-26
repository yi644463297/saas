package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.Export;
import cn.itcast.domain.cargo.ExportProduct;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.BeanMapUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.*;

@Controller
@RequestMapping("/cargo/export")
public class PdfController extends BaseController {
    /**
     * 1.入门案例，展示pdf
     * http://localhost:8080/cargo/export/exportPdf.do?id=2569e993-7b84-4d75-b018-077439b1eff5
     */
    @RequestMapping("/exportPdf1")
    public void exportPdf1() throws Exception {
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test01.jasper");
        JasperPrint jasperPrint = JasperFillManager.fillReport(in, new HashMap<>(), new JREmptyDataSource());
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }
    /**
     * 2.JDBC数据源 A 配置数据源
     */
    @RequestMapping("/exportPdf2")
    public void exportPdf2() throws Exception {
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test03.jasper");

        //2. 创建JasperPrint对象
        // 构造参数
        Map<String,Object> map = new HashMap<>();
        map.put("username","老王");
        map.put("email","lw@export.cn");
        map.put("company","传智播客");
        map.put("dept","Java教研部");

        JasperPrint jasperPrint = JasperFillManager.fillReport(in, map, new JREmptyDataSource());
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 3. 数据填充 - 通过JDBC数据源填充模板数据
     */
    @Autowired
    private DataSource dataSource;
    @RequestMapping("/exportPdf3")
    public void exportPdf3() throws Exception {
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test04_jdbc.jasper");

        //获取连接对象
        Connection connection = dataSource.getConnection();
        //2. 创建JasperPrint对象
        JasperPrint jasperPrint = JasperFillManager.fillReport(in, null, connection);
        //3. 以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 4. 数据填充 - 通过javabean数据源填充模板数据
     */
    @RequestMapping("/exportPdf4")
    public void exportPdf4() throws Exception {
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test05_javabean.jasper");

        List<User> list = new ArrayList<>();
        for(int i=0;i<10;i++) {
            User user = new User();
            user.setUserName("name"+i);
            user.setEmail(i+"@qq.com");
            user.setCompanyName("企业"+i);
            user.setDeptName("部门"+i);
            list.add(user);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        //2. 创建JasperPrint对象
        JasperPrint jasperPrint = JasperFillManager.fillReport(in, new HashMap<>(), dataSource);
        //3. 以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 5. 分组
     */
    @RequestMapping("/exportPdf5")
    public void exportPdf5() throws Exception {
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test06_group.jasper");

        List<User> list = new ArrayList<>();
        for(int i=0;i<10;i++) {
            for (int j = 0; j < 5; j++) {
                User user = new User();
                user.setUserName("name"+j);
                user.setEmail(i+"@qq.com");
                user.setCompanyName("企业"+i);
                user.setDeptName("部门"+j);
                list.add(user);
            }
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        //2. 创建JasperPrint对象
        JasperPrint jasperPrint = JasperFillManager.fillReport(in, new HashMap<>(), dataSource);
        //3. 以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 6. 饼图
     */
    @RequestMapping("/exportPdf6")
    public void exportPdf6() throws Exception {
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test07_chart.jasper");

        //构造数据列表的数据源对象
        List list = new ArrayList();
        for(int i=0;i<6;i++) {
            Map map = new HashMap();
            map.put("title","标题"+i);
            map.put("value",new Random().nextInt(100));
            list.add(map);
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        //2. 创建JasperPrint对象
        JasperPrint jasperPrint = JasperFillManager.fillReport(in, new HashMap<>(), dataSource);
        //3. 以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 7. 导出货运单
     */
    @Reference
    private ExportService exportService;
    @RequestMapping("/exportPdf")
    @ResponseBody
    public void exportPdf(String id) throws Exception {
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/export.jasper");

        //根据id查询货运单
        Export export = exportService.findById(id);
        //转换成map
        Map<String, Object> map = BeanMapUtils.beanToMap(export);
        //获得商品
        List<ExportProduct> list = export.getExportProducts();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
        //2. 创建JasperPrint对象
        JasperPrint jasperPrint = JasperFillManager.fillReport(in, map, dataSource);
        //设置下载
        response.setContentType("application/pdf;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename=export.pdf");
        //3. 以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }
}
