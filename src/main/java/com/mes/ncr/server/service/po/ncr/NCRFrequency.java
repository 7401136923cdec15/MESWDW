package com.mes.ncr.server.service.po.ncr;

import java.io.Serializable;


/**
 *不合格评审发生频次
 * 
 * @author ShrisJava
 *
 */
public class NCRFrequency implements Serializable{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 任务等级
	 */
	public int Level;
	/**
	 *等级
	 */
	public String LevelName = "";
	/**
	 * 不合格评审发生频次
	 */
	public Integer Frequency = 0;
	
	
	public Integer getFrequency() {
		return Frequency;
	}
	public void setFrequency(Integer frequency) {
		Frequency = frequency;
	}
	public int getLevel() {
		return Level;
	}
	public void setLevel(int level) {
		Level = level;
	}
	public String getLevelName() {
		return LevelName;
	}
	public void setLevelName(String levelName) {
		LevelName = levelName;
	}
	
	
	
}
