package com.doteyplay.game.domain.user;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import com.doteyplay.core.bhns.BOServiceManager;
import com.doteyplay.core.bhns.gateway.IGateWayService;
import com.doteyplay.game.domain.gamebean.UserBean;
import com.doteyplay.game.domain.role.Role;
import com.doteyplay.net.message.AbstractMessage;

/**
 */

public class User
{
	private static Logger logger = Logger.getLogger(User.class);

	private UserBean userBean;

	/**
	 * sessionId
	 */
	private long sessionId;
	
	private Role role;

	public User(String name, String password, String phone, String recommend)
	{
		UserBean userBean = new UserBean();
		userBean.setName(name);
		userBean.setPassword(password);
	}
	
	public User()
	{
		
	}

	/**
	 * 用户发送消息主函数
	 * 
	 * @param msg
	 */

	/**
	 * @param msg
	 */
	public void sendMsg(AbstractMessage msg)
	{
		if (msg == null)
		{
			String e = "用户发送消息错误-消息为空:id=" + userBean.getName() + ",name="
					+ this.userBean.getName();
			logger.error(e, new NullPointerException(e));
			return;
		}
		if (sessionId <= 0l)
		{
			logger.error("session == null :" + this.toString());
			return;
		}
		try
		{
			IoBuffer buf = msg.encodeIoBuffer();
			if (buf != null)
			{
				byte endpointId = (byte) (sessionId >>> 56);
				IGateWayService gateWayService = BOServiceManager
						.findDefaultService(IGateWayService.PORTAL_ID,
								endpointId);

				IoBuffer buffer = msg.encodeIoBuffer();
				int byteLength = buffer.limit();
				byte[] tmpMessageBytes = new byte[byteLength];
				buffer.get(tmpMessageBytes, 0, byteLength);

				gateWayService.sendMessage(tmpMessageBytes, sessionId);
			}
		} catch (Exception e)
		{
			logger.error("发送消息时出错:" + msg, e);
		}
	}

	// ********************** Common Methods ********************** //

	@Override
	public String toString()
	{
		return userBean.toString();
	}

	// ********************** property Methods ********************** //
	public long getId()
	{
		return userBean.getId();
	}

	public UserBean getUserBean()
	{
		return userBean;
	}

	public void setUserBean(UserBean userBean)
	{
		this.userBean = userBean;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public long getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(long sessionId)
	{
		this.sessionId = sessionId;
	}
}
