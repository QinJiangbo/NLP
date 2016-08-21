package com.qinjiangbo.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.qinjiangbo.util.Mode;
import com.qinjiangbo.util.ParamsConfig;
import com.qinjiangbo.util.Type;
import com.qinjiangbo.util.WeightConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@link com.qinjiangbo.service.PostProcessor PostProcessor}是用来进行文章评估的类<br>
 * 文章评估主要分为六大模块进行，分别是<br>
 * <ul>
 * <li>1. conventions 拼写和语法</li>
 * <li>2. word choice 词汇运用</li>
 * <li>3. organization 组织结构</li>
 * <li>4. ideas&content 内容与思想</li>
 * <li>5. fluency 流畅度</li>
 * <li>6. voice 文体</li>
 * </ul>
 * 需要说明的是目前第五和第六模块模型还未建立，故先不做处理
 *
 */
@Service
public class PostProcessor {
	
	private String[] keyWords = null;
	private Type type = null;
	
	@Autowired
	private PreProcessor preProcessor;
	
	/**
	 * 后置处理器，根据配置对作文进行评分
	 * @param text
	 * @return
	 */
	public Map<String, Object> process(String text, WeightConfig weightConfig, ParamsConfig paramsConfig) {
		//预处理
		preProcessor.process(text);
		List<String> Sentences = preProcessor.getSentences();
		List<String> words = preProcessor.getWords();
		Map<String, Object> scores = new HashMap<String, Object>();
		Map<String, Object> scoreConvention = processConventions(Sentences, words, weightConfig, paramsConfig);
		Map<String, Object> scoreConventions = new HashMap<String, Object>();
		scoreConventions.put("score11", scoreConvention.get("score11"));
		scoreConventions.put("score12", scoreConvention.get("score12"));
		scores.putAll(scoreConventions);
		Map<String, Object> scoreWordChoice = processWordChoice(words, weightConfig, paramsConfig);
		scores.putAll(scoreWordChoice);
		Map<String, Object> scoreOrganization = processOrganization(text, words, weightConfig, paramsConfig);
		scores.putAll(scoreOrganization);
		Map<String, Object> scoreIdeasAndContent = processIdeasAndContent(words, weightConfig, paramsConfig);
		scores.putAll(scoreIdeasAndContent);
		Map<String, Object> scoreFluency = processFluency(Sentences, weightConfig, paramsConfig);
		scores.putAll(scoreFluency);
		Float total = 0.00f;
		Set<String> keys = scores.keySet();
		for(String key: keys) {
			total += Float.valueOf(scores.get(key).toString());
		}
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		String totalScore = decimalFormat.format(total);
		scores.put("totalScore", totalScore);
		scores.put("goodSens", scoreConvention.get("goodSens"));
		scores.put("errorSens", scoreConvention.get("errorSens"));
		scores.put("mispelledWords", scoreConvention.get("mispelledWords"));
		return scores;
	}
	
	//1. conventions 拼写和语法
	private Map<String, Object> processConventions(List<String> sentences, List<String> words, WeightConfig weightConfig, ParamsConfig paramsConfig) {
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> errorSens = new ArrayList<String>();
		List<String> goodSens = new ArrayList<String>();
		int numberS = sentences.size(), valCountS = 0;
		int numberW = words.size(), valCountW = 0;
		//检查语法正确性
		Map<String, Float> scores = SentenceChecker.grammarCheck(sentences);
		Set<Entry<String, Float>> entrySet = scores.entrySet();
		//以-50分为基准,大于-50的即为有效句子,小于-50分即为无效句子
		for(Entry<String, Float> entry : entrySet) {
			if (entry.getValue().floatValue() > -25) {
				goodSens.add(entry.getKey());
			}
			if (entry.getValue().floatValue() > -50) {
				valCountS++;
			} else {
				errorSens.add(entry.getKey());
			}
		}
		Float scoreS = ((float)valCountS / numberS) * weightConfig.getConventions().getWeight() * weightConfig.getSubSentence().getWeight();
		//检查单词拼写错误
		List<String> mispelledWords = WordsChecker.spellCheck(words);
		valCountW = mispelledWords.size();
		Float scoreW = (1-(float)valCountW / numberW) * weightConfig.getConventions().getWeight() * weightConfig.getSubWord().getWeight();
		result.put("score11", decimalFormat.format(scoreS));
		result.put("score12", decimalFormat.format(scoreW));
		result.put("errorSens", errorSens);
		result.put("goodSens", goodSens);
		result.put("mispelledWords", mispelledWords);
		return result;
	}
	
	//2. word choice 词汇运用
	private Map<String, Object> processWordChoice(List<String> words, WeightConfig weightConfig, ParamsConfig paramsConfig) {
		int numberW = words.size();
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		Map<String, Object> result = new HashMap<String, Object>();
		//超过N个字母的单词数量
		int overNum = WordsChecker.countWordsWithMode(Mode.MORETHAN, words, paramsConfig);
		Float scoreOver = ((float) overNum / numberW) * weightConfig.getWordChoice().getWeight() * weightConfig.getSubOverN().getWeight();
		//低于N个字母的单词
		int belowNum = WordsChecker.countWordsWithMode(Mode.LESSTHAN, words, paramsConfig);
		Float scorebeLow = (1 - (float) belowNum / numberW) * weightConfig.getWordChoice().getWeight() * weightConfig.getSubBelowN().getWeight();
		//全文单词重复率
		int repeatNum = WordsChecker.repeatCheck(words, paramsConfig);
		Float scoreRepeat = (1 - (float) repeatNum / numberW) * weightConfig.getWordChoice().getWeight() * weightConfig.getSubRepeat().getWeight();
		result.put("score21", decimalFormat.format(scoreOver));
		result.put("score22", decimalFormat.format(scorebeLow));
		result.put("score23", decimalFormat.format(scoreRepeat));
		return result;
	}
	
	//3. organization 组织结构
	private Map<String, Object> processOrganization(String text, List<String> words, WeightConfig weightConfig, ParamsConfig paramsConfig) {
		int numberW = words.size();
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		Map<String, Object> result = new HashMap<String, Object>();
		//逻辑词库匹配
		int logicNum = WordsChecker.logicsCheck(text);
		float standardRate = paramsConfig.getLogicMatchRate();
		float actualRate = (float) logicNum / numberW;
		float scoreL = (actualRate / standardRate) > 1.0f ? 1.0f : (actualRate / standardRate);
		Float scoreLogic = scoreL * weightConfig.getOrganization().getWeight() * weightConfig.getSubLogic().getWeight();
		//全文单词数量检测
		int count = WordsChecker.contentLengthCheck(type, text, paramsConfig);
		Float scoreLength = 0.0f;
		if(type == Type.LARGETEXT) {
			if(count > 0) {
				int MIN = paramsConfig.getMinWordNum();
				scoreLength = (1 - (float) count / MIN) * weightConfig.getOrganization().getWeight() * weightConfig.getSubWordCount().getWeight();
			}else{
				scoreLength = weightConfig.getOrganization().getWeight() * weightConfig.getSubWordCount().getWeight();
			}
		}
		else {
			if(count > 0) {
				int MIN = paramsConfig.getMinWordNum();
				int MAX = paramsConfig.getMaxWordNum();
				int min = paramsConfig.getMinBorderNum();
				int max = paramsConfig.getMaxBorderNum();
				if(numberW < MIN && numberW > min) {
					scoreLength = ((float) (numberW - max) / MIN) * weightConfig.getOrganization().getWeight() * weightConfig.getSubWordCount().getWeight();
				}
				else if(numberW > MAX && numberW < max) {
					scoreLength = ((float) (max - numberW) / (max - MAX)) * weightConfig.getOrganization().getWeight() * weightConfig.getSubWordCount().getWeight();
				}
				else{
					scoreLength = 0.0f;
				}
			}
			else{
				scoreLength = weightConfig.getOrganization().getWeight() * weightConfig.getSubWordCount().getWeight();
			}
		}
		result.put("score31", decimalFormat.format(scoreLogic));
		result.put("score32", decimalFormat.format(scoreLength));
		return result;
	}
	
	//4. ideas&content 内容与思想
	private Map<String, Object> processIdeasAndContent(List<String> words, WeightConfig weightConfig, ParamsConfig paramsConfig) {
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		Map<String, Object> result = new HashMap<String, Object>();
		int numberW = words.size();
		int count = WordsChecker.keyWordsCheck(words, keyWords);
		float standardRate = paramsConfig.getThemeMatchRate();
		float actualRate = (float)count / numberW;
		float keywordScore = (actualRate / standardRate) > 1.0f ? 1.0f : (actualRate / standardRate);
		Float scoreKeyWord = keywordScore * weightConfig.getIdeasContent().getWeight() * weightConfig.getSubWordMatch().getWeight();
		result.put("score41", decimalFormat.format(scoreKeyWord));
		return result;
	}
	
	//5. fluency 
	private Map<String, Object> processFluency(List<String> Sentences, WeightConfig weightConfig, ParamsConfig paramsConfig) {
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		Map<String, Object> result = new HashMap<String, Object>();
		//(1) Berkely句子分值平均值
		int avgBerkelyScore = SentenceChecker.avgBerkelyScore(Sentences);
		int berkelyScoreHigh = paramsConfig.getBerkelyScoreHigh();
		int berkelyScoreLow = paramsConfig.getBerkelyScoreLow();
		float berkelyScore = 0.0f;
		if(avgBerkelyScore > berkelyScoreHigh) {
			berkelyScore = 1.0f;
		}
		else if(avgBerkelyScore < berkelyScoreLow) {
			berkelyScore = 0.0f;
		}
		else {
			berkelyScore = Math.abs((float) avgBerkelyScore / (berkelyScoreHigh - berkelyScoreLow));
		}
		Float scoreOverN = berkelyScore * weightConfig.getFlucency().getWeight() * weightConfig.getSubBerkelyScore().getWeight();
		result.put("score51", decimalFormat.format(scoreOverN));
		//(2) 全文句子和预设内容库要点匹配率
		int totalNum = Sentences.size();
		int matchNum = SentenceChecker.fluencyCheck(Sentences, paramsConfig);
		float matchRate = (float) matchNum / totalNum;
		float standardRate = paramsConfig.getSentenceWordMatchRate();
		float matchScore = (matchRate / standardRate) > 1.0f ? 1.0f :matchRate / standardRate;
		Float scoreSentenceMatch = matchScore * weightConfig.getFlucency().getWeight() * weightConfig.getSubSentenceMatch().getWeight();
		result.put("score52", decimalFormat.format(scoreSentenceMatch));
		return result;
	}
	
	
	public String[] getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String[] keyWords) {
		this.keyWords = keyWords;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
}