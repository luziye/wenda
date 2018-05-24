package com.wenda.lzy.service;

import com.wenda.lzy.dao.QuestionDao;
import com.wenda.lzy.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;

    public List<Question> getLatestQuestion(int userId, int offset, int limit) {
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }

    public Question selectQuestionById(int id){
        return questionDao.selectQuestionById(id);
    }

    public int addQuestion(Question question) {

        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //敏感词过滤
        return questionDao.addQuestion(question) > 0 ? question.getId() : 0;
    }

}
