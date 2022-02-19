package com.mes.ncr.server.service.po.rro;

import java.io.Serializable;

/**
 * 返修发生频次
 * 
 * @author ShrisJava
 *
 */
public class RROFrequency implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public int LineID = 0;

	/**
	 * 工位ID
	 */
	public int StationID;
	/**
	 * 工位
	 */
	public String Station = "";
	/**
	 * 返修频次
	 */
	public Integer Frequency = 0;

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public String getStation() {
		return Station;
	}

	public void setStation(String station) {
		Station = station;
	}

	public Integer getFrequency() {
		return Frequency;
	}

	public void setFrequency(Integer frequency) {
		Frequency = frequency;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

}
