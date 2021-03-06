package com.doteyplay.game.domain.item;

import com.doteyplay.game.config.template.ItemDataObject;
import com.doteyplay.game.domain.gamebean.ItemBean;
import com.doteyplay.game.util.excel.TemplateService;

public class RoleItem
{
	private ItemBean bean;
	private ItemDataObject data;
	
	private long petId;

	public RoleItem(ItemBean bean)
	{
		this.bean = bean;
		this.data = TemplateService.getInstance().get(bean.getItemId(), ItemDataObject.class);
	}

	public ItemBean getBean()
	{
		return bean;
	}

	public void setBean(ItemBean bean)
	{
		this.bean = bean;
	}

	public ItemDataObject getData()
	{
		return data;
	}

	public void setData(ItemDataObject data)
	{
		this.data = data;
	}

	public long getPetId()
	{
		return petId;
	}

	public void setPetId(long petId)
	{
		this.petId = petId;
	}

	// *************************************************************************
	public static RoleItem createRoleItem(long roleId, int itemId, int itemNum)
	{
		ItemBean bean = new ItemBean();
		bean.setRoleId(roleId);
		bean.setItemId(itemId);
		bean.setItemNum(itemNum);
		return new RoleItem(bean);
	}
	
	public static RoleItem closeRoleItem(ItemBean bean)
	{
		ItemBean newBean = new ItemBean();
		newBean.setRoleId(bean.getRoleId());
		newBean.setItemId(bean.getItemId());
		newBean.setItemNum(bean.getItemNum());
		return new RoleItem(bean);
	}
}
