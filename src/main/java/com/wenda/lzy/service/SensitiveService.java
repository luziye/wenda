package com.wenda.lzy.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader inputStreamReader=new InputStreamReader(is);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

            String lineText;
            while ((lineText=bufferedReader.readLine())!=null){
                addWord(lineText.trim());
            }
            inputStreamReader.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());
        }
    }
    //增加敏感词到前缀树 abc
    public void addWord(String lineText){
        TrieNode tempNode=rootNode;
        for (int i=0;i<lineText.length();i++){
            Character c=lineText.charAt(i);
            TrieNode node=tempNode.getSubNode(c);
            if (node==null){
                node=new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode=node;
            if (i==lineText.length()-1){
                tempNode.setkeywordEnd(true);
            }
        }
    }

    private class TrieNode {
        private boolean end = false;
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        void setkeywordEnd(boolean end) {
            this.end = end;
        }

        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeyWordEnd() {
            return end;
        }
    }
    private TrieNode rootNode=new TrieNode();

    public String filter(String text){
        if (StringUtils.isEmpty(text)){
            return text;
        }
        StringBuilder sb=new StringBuilder();
        String replacement="***";
        TrieNode tempNode=rootNode;
        int begin=0;
        int position=0;

        while(position<text.length()){
            char c=text.charAt(position);
            tempNode=tempNode.getSubNode(c);
            if (tempNode==null){
                sb.append(text.charAt(begin));
                position=begin+1;
                begin=position;
                tempNode=rootNode;
            }else if(tempNode.isKeyWordEnd()){
                sb.append(replacement);
                position=position+1;
                begin=position;
                tempNode=rootNode;
            }else {
                ++position;
            }
        }
        sb.append(text.substring(begin));
        return sb.toString();
    }

    public static void main(String[] args) {
        SensitiveService s=new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
        System.out.println(s.filter("你好色情"));
    }

}
