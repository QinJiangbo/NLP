package com.qinjiangbo.controller;

import com.qinjiangbo.service.BaiduVoiceService;
import com.qinjiangbo.service.ScoreProcessor;
import com.qinjiangbo.service.SentenceChecker;
import com.qinjiangbo.service.WordsChecker;
import com.qinjiangbo.util.HttpControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 8/21/16.
 */
@RestController
@RequestMapping(value = "/nlp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NLPController {

    @Autowired
    public BaiduVoiceService baiduVoiceService;
    @Autowired
    private ScoreProcessor scoreProcessor;

    @RequestMapping("/convertVoice.mo")
    public Map<String, Object> convertVoice(HttpServletRequest request, @RequestBody HashMap<String, Object> params) {
        String voiceUrl = params.get("voiceUrl").toString();
        if (StringUtils.isEmpty(voiceUrl)) {
            return HttpControllerUtils.createReturn(0, "必要参数不能为空![voiceUrl]");
        }
        String voiceWords = baiduVoiceService.convertVoice(voiceUrl);
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isEmpty(voiceWords)) {
            result = HttpControllerUtils.createReturn(0, "获取语音转换后的文字失败!");
            return result;
        }
        result.put("rtnCode", 1);
        result.put("rtnMsg", "成功获取语音转换后的文字!");
        result.put("voiceWords", voiceWords);
        return result;
    }

    @RequestMapping("/wordsCheck.mo")
    public Map<String, Object> wordsCheck(HttpServletRequest request, @RequestBody HashMap<String, Object> params) {
        String text = params.get("text").toString();
        if (StringUtils.isEmpty(text)) {
            return HttpControllerUtils.createReturn(0, "必要参数不能为空![text]");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(text)) {
            Map<String, Object> checkResult = scoreProcessor.process(text, null, null);
            result.put("misWords", checkResult.get("mispelledWords"));
            result.put("rtnCode", 1);
            result.put("rtnMsg", "成功获取拼写出现错误的单词!");
            return result;
        }
        return HttpControllerUtils.createReturn(0, "获取拼写出现错误的单词失败!");
    }

    @RequestMapping("/wordsSuggest.mo")
    public Map<String, Object> wordsSuggest(HttpServletRequest request, @RequestBody HashMap<String, Object> params) {
        String word = params.get("word").toString();
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> suggestWords = WordsChecker.suggestWords(word);
        if (suggestWords == null || suggestWords.size() == 0) {
            return HttpControllerUtils.createReturn(0, "获取拼写单词建议失败!");
        }
        result.put("suggestWords", suggestWords);
        result.put("rtnCode", 1);
        result.put("rtnMsg", "成功获取拼写单词建议!");
        return result;
    }

    @RequestMapping("/grammarCheck.mo")
    public Map<String, Object> grammarCheck(HttpServletRequest request, @RequestBody HashMap<String, Object> params) {
        String text = params.get("text").toString();
        Map<String, Object> result = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(text)) {
            Map<String, Object> checkResult = scoreProcessor.process(text, null, null);
            result.put("errorSens", checkResult.get("errorSens"));
            result.put("rtnCode", 1);
            result.put("rtnMsg", "成功获取语法错误的句子!");
            return result;
        }
        return HttpControllerUtils.createReturn(0, "获取语法错误的句子失败!");
    }

    @RequestMapping("/grammarSuggest.mo")
    public Map<String, Object> grammarSuggest(HttpServletRequest request, @RequestBody HashMap<String, Object> params) {
        String sentence = params.get("sentence").toString();
        Map<String, Object> result = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(sentence)) {
            List<String> suggestSens = SentenceChecker.ngramsCheck(sentence);
            StringBuilder stringBuilder = new StringBuilder();
            for (String suggest : suggestSens) {
                stringBuilder.append(suggest);
            }
            result.put("suggestSen", stringBuilder.toString());
            result.put("rtnCode", 1);
            result.put("rtnMsg", "成功获取句子语法建议!");
            return result;
        }
        return HttpControllerUtils.createReturn(0, "获取句子语法建议失败!");
    }

    @RequestMapping("/highScoreSentence.mo")
    public Map<String, Object> highScoreSentence(HttpServletRequest request, @RequestBody HashMap<String, Object> params) {
        String text = params.get("text").toString();
        Map<String, Object> result = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(text)) {
            Map<String, Object> checkResult = scoreProcessor.process(text, null, null);
            result.put("goodSens", checkResult.get("goodSens"));
            result.put("rtnCode", 1);
            result.put("rtnMsg", "成功获取伯克利高分句子!");
            return result;
        }
        return HttpControllerUtils.createReturn(0, "获取伯克利高分句子失败!");
    }

}
