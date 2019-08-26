package cn.itcast.web.controller.system;

import cn.itcast.domain.system.SysLog;
import cn.itcast.service.system.SysLogService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/system/log")
public class SysLogController extends BaseController{
    @Autowired
    private SysLogService sysLogService;

    /**
     * 分页显示日志
     */
    @RequestMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "5") int pageSize) {
        String companyId = getLoginCompanyId();
        PageInfo<SysLog> pageInfo = sysLogService.findByPage(companyId, pageNum, pageSize);
        request.setAttribute("page", pageInfo);
        return "/system/log/log-list";
    }
}
