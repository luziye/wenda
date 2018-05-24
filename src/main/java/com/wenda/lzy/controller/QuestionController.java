package com.wenda.lzy.controller;

import com.wenda.lzy.model.HostHolder;
import com.wenda.lzy.model.Question;
import com.wenda.lzy.service.QuestionService;
import com.wenda.lzy.service.SensitiveService;
import com.wenda.lzy.service.UserService;
import com.wenda.lzy.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {

        try {
            Question question = new Question();
            question.setCreatedDate(new Date());
            question.setContent(sensitiveService.filter(content));
            question.setTitle(sensitiveService.filter(title));
            if (hostHolder.getUser() == null) {
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return WendaUtil.getJSONString(0);
            }

        } catch (
                Exception e)

        {
            logger.error("增加题目失败", e.getMessage());
        }
        return WendaUtil.getJSONString(1, "增加问题失败");
    }

    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String questionDetail(@PathVariable("qid") int qid, Model model) {
        Question question = questionService.selectQuestionById(qid);
        model.addAttribute("question", question);
        model.addAttribute("user", userService.getUser(question.getUserId()));
        return "detail";
    }

}
