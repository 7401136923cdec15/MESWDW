package com.mes.ncr.server.service.mesenum;

public enum FPCProductTransport {
	  /**
	   * SCH
	   */
	  Default(0, "默认"),
	  /**
	   * SCH
	   */
	  Whole(1, "整车"),
	  /**
	   * APP
	   */
	  Body(2, "车体"),
	  /**
	   * APP
	   */
	  Bottom(3, "转向架"),
	  /**
	   * APP
	   */
	  Bottom_T(4, "假台车");
	
	  private int value;
	  private String lable;

	  private FPCProductTransport(int value, String lable) {
	    this.value = value;
	    this.lable = lable;
	  }

	  /**
	   * 通过 value 的数值获取枚举实例
	   *
	   * @param val
	   * @return
	   */
	  public static FPCProductTransport getEnumType(int val) {
	    for (FPCProductTransport type : FPCProductTransport.values()) {
	      if (type.getValue() == val) {
	        return type;
	      }
	    }
	    return null;
	  }

	  public int getValue() {
	    return value;
	  }

	  public String getLable() {
	    return lable;
	  }
	}
