package cn.itcast.web.controller.stat;

import cn.itcast.service.stat.StatService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stat")
public class StatController extends BaseController {
    @Reference
    private StatService statService;

    /**
     * 进入统计分析的三个页面，公用同一个转发方法
     * http://localhost:8080/stat/toCharts.do?chartsType=factory
     * http://localhost:8080/stat/toCharts.do?chartsType=sell
     * http://localhost:8080/stat/toCharts.do?chartsType=online
     */

    @RequestMapping("/toCharts")
    public String toCharts(String chartsType) {
        return "stat/stat-" + chartsType;
    }

    /**
     * 需求1：根据生产厂家统计货物销售金额
     */
    @RequestMapping("/getFactorySale")
    @ResponseBody
    public List<Map<String, Object>> getFactorySale() {
        return statService.getFactorySale(getLoginCompanyId());
    }

    /**
     * 需求2：产品销售排行，前5
     */
    @RequestMapping("/getProductSale")
    @ResponseBody       // 自动把方法返回对象转换为json格式
    public List<Map<String, Object>> getProductSale(){
        return statService.getProductSale(getLoginCompanyId(), 5);
    }

    /**
     * 需求3：按小时统计访问人数
     */
    @RequestMapping("/getOnline")
    @ResponseBody       // 自动把方法返回对象转换为json格式
    public List<Map<String, Object>> getOnline(){
        return statService.getOnline();
    }
}
