package com.mes.ncr.server.serviceimpl.utils;

import java.util.Calendar;

public class MESInstance {
	public int ID = 0;
    public String Name = "";
    public String BasicDB = "";
    public String DFSSheetDB = "";
    public String FactoryDB = "";
    public int Status = 0;                             //0.正常；1.数据库实例不存在；
    public int Max_Limit = 1;                          //最大用户数限制
    public int Online_Limit = 1;                       //在线用户数限制
    public int Onlines = 0;                            //在线用户数
    public Calendar ExpireTime = Calendar.getInstance();       //有效期
    public String MESVersion = "";                     //MES版本
    public String ProductKey = "";                     //产品授权码
    public int ShiftID = 0;                            //当前班次
    public int MES_CreateInstance(int wID, String wDBVersion)
    {
        int wErrorCode = 0;
        this.ExpireTime = Calendar.getInstance();
        this.ExpireTime.add(Calendar.YEAR, 100);
        return wErrorCode;
    }
}
