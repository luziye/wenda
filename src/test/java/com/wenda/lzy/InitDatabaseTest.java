package com.wenda.lzy;

import com.wenda.lzy.dao.QuestionDao;
import com.wenda.lzy.dao.UserDao;
import com.wenda.lzy.model.Question;
import com.wenda.lzy.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/init-schema.sql")
public class InitDatabaseTest {

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;
    @Test
    public void initDatabase(){
        Random random=new Random();
        for (int i=0;i<10;i++){
            User user=new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userDao.addUser(user);

            user.setPassword("123456");
            userDao.updatePassword(user);

            Question question=new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            question.setCreatedDate(date);
            question.setUserId(i + 1);
            question.setTitle(String.format("TITLE{%d}", i));
            question.setContent(String.format("Balaababalalalal Content %d", i));
            questionDao.addQuestion(question);

        }
        Assert.assertEquals("123456",userDao.selectById(1).getPassword());

        userDao.deleteUser(2);
        Assert.assertNull(userDao.selectById(2));

        List<Question> questions=questionDao.selectLatestQuestions(0,0,10);
        System.out.print(questions);
    }
}
