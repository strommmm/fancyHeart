package com.doteyplay.support.auth;

import com.doteyplay.game.config.ServerConfig;
import com.doteyplay.luna.client.AsynchronismClientManager;
import com.doteyplay.luna.client.ConnectionInfo;
import com.doteyplay.luna.common.action.BaseAction;
import com.doteyplay.luna.common.message.EncoderMessage;
import com.doteyplay.support.ISupportService;

public class AuthService implements ISupportService
{

	private static final long MAX_TIME_OUT = 5 * 1000;
	/**
	 * ��֤����������Ϣ
	 */
	private static final ConnectionInfo CONNECTION_INFO = new ConnectionInfo(
			ServerConfig.AUTH_SERVER_IP, ServerConfig.AUTH_SERVER_PORT, 120,
			MAX_TIME_OUT);

	/**
	 * ��������
	 */
	public static AuthService instance = new AuthService();

	private AsynchronismClientManager manager = new AsynchronismClientManager();

	/**
	 * ��ʼ��ͬ�������Ŀͻ��˹�����Manager
	 */
	private AuthService()
	{
		manager.initial(CONNECTION_INFO, AuthController.getInstance());
		
		AuthHandler.getInstance().init(this);
	}

	/**
	 * ��õ����ķ�ʽ
	 * 
	 * @return
	 */
	public static AuthService getInstance()
	{
		if (instance == null)
			instance = new AuthService();
		return instance;
	}

	/**
	 * ͬ��������Ϣ�ķ���
	 * 
	 * @param encoderMessage
	 * @return
	 */
	public void invoke(EncoderMessage encoderMessage)
	{
		if (encoderMessage == null)
			return;
		if (!manager.invoke(encoderMessage))
		{
			BaseAction action = AuthController.getInstance().getAction(
					encoderMessage.getCommandId());
			if (action != null)
				action.doAction(encoderMessage.getRoleId());
		}
	}
}