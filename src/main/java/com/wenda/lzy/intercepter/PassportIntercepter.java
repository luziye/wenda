package com.wenda.lzy.intercepter;

import com.wenda.lzy.dao.LoginTicketDao;
import com.wenda.lzy.dao.UserDao;
import com.wenda.lzy.model.HostHolder;
import com.wenda.lzy.model.LoginTicket;
import com.wenda.lzy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportIntercepter implements HandlerInterceptor {
    @Autowired
    LoginTicketDao loginTicketDao;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        Cookie[] x = request.getCookies();
        if (x != null) {
            for (Cookie c : x) {
                if (c.getName().equals("ticket")) {
                    ticket = c.getValue();
                    break;
                }
            }
        }
        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDao.selectTicket(ticket);
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                return true;
            }
            User user = userDao.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
