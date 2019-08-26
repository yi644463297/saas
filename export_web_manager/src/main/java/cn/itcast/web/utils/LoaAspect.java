package cn.itcast.web.utils;

import cn.itcast.domain.system.SysLog;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

@Component
@Aspect
public class LoaAspect {
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletRequest request;

    @Around(value = "execution(* cn.itcast.web.controller.*.*.*(..))")
    public Object log(ProceedingJoinPoint jp) {
        SysLog sysLog = new SysLog();
        sysLog.setId(UUID.randomUUID().toString());
        User user = (User) session.getAttribute("user");
        if (user != null) {
            sysLog.setUserName(user.getUserName());
            sysLog.setCompanyId(user.getCompanyId());
            sysLog.setCompanyName(user.getCompanyName());
        }
        sysLog.setIp(request.getLocalAddr());
        sysLog.setTime(new Date());
        String fullClassName = jp.getTarget().getClass().getName();
        String methodName = jp.getSignature().getName();
        sysLog.setMethod(methodName);
        sysLog.setAction(fullClassName);
        try {
            Object result = jp.proceed();
            //保存日志记录
            sysLogService.save(sysLog);
            return result;
        } catch (Throwable throwable) {
//            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }
}
