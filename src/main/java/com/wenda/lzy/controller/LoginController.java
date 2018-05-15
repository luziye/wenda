package com.wenda.lzy.controller;

import com.wenda.lzy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg/"}, method = RequestMethod.POST)
    public String register(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String password) {
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("msg")) {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
            return "redirect:/";
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/login"}, method = RequestMethod.POST)
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme", defaultValue = "false")boolean rememberme) {
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("msg")) {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
            return "redirect:/";
        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            return "login";
        }
    }

}
