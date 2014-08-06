package com.doteyplay.game.constants.chat;
/**
 * @className:GMConstant.java
 * @classDescription:
 * @author:Tom.Zheng
 * @createTime:2014��7��9�� ����3:41:28
 */
public enum ChatConstant {
	
	//----------------------����GM��Ʒ�����ĸ�ʽ��
	//goods[10000,100001,10002][100x1,101x2,103x3,104x1] 
	//�����ַ�������˼��ָ.goods[roleId,roleId,roleId][��ƷIdx����,��ƷIdx����,��ƷIdx����]
	GOODS_SUFFIX("goods",0),
	/**
	 * money[10000,100001,10002][100]
	 * �����ַ�������˼��ָ.goods[roleId,roleId,roleId][�������] 
	 */
	MONEY_SUFFIX ("money",1),
	/**
	 * exp[10000,100001,10002][100]
	 * �����ַ�������˼��ָ.exp[roleId,roleId,roleId][����ֵ] 
	 */
	EXP_SUFFIX("exp",1),
	/**
	 * energy[10000,100001,10002][100]
	 * �����ַ�������˼��ָ.exp[roleId,roleId,roleId][����ֵ] 
	 */
	ENERGY_SUFFIX("energy",1),;

	private String suffix;
	
	private int  dataType;
	private ChatConstant(String name,int dataType) {
		this.suffix = name;
		this.dataType= dataType;
		// TODO Auto-generated constructor stub
	}
	public String getSuffix() {
		return suffix;
	}
	public int getDataType() {
		return dataType;
	}
	
	public boolean isGoodsType(){
		return dataType == 0;
	}
	public boolean isDataValueType(){
		return dataType == 1;
	}
	
	
}