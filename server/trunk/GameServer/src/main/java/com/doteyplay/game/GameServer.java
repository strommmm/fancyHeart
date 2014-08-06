package com.doteyplay.game;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.doteyplay.core.classloader.ClassOverride;
import com.doteyplay.core.classloader.Test;
import com.doteyplay.core.configloader.MessageRegistry;
import com.doteyplay.core.server.ServerStatus;
import com.doteyplay.core.service.AbstractService;
import com.doteyplay.core.service.ServiceManager;
import com.doteyplay.game.domain.common.TextObjectManager;
import com.doteyplay.game.service.runtime.GlobalUserCache;
import com.doteyplay.game.startservice.ConfigService;
import com.doteyplay.game.startservice.DataStoreService;
import com.doteyplay.game.startservice.ScheduleTaskService;
import com.doteyplay.game.startservice.ServerService;
import com.doteyplay.game.startservice.ServiceService;
import com.doteyplay.game.startservice.SupportService;

/**
 */
public class GameServer extends AbstractService
{
	private static final Logger logger = Logger.getLogger(GameServer.class
			.getName());

	public static String SERVER_CONFIG_FILE = "server-config.xml";
	public static GameServer instance;
	/**
	 * �����������ɹ�ʱ���
	 */
	private static long startUpTime = 0L;

	private static GlobalUserCache cache = GlobalUserCache.getInstance();

	/**
	 * ��������ǰ����״̬
	 */
	private static ServerStatus serverStatus = ServerStatus.CLOSED;

	/**
	 * ����������ʱ��ʱ���
	 */
	public final static long timestamp = System.currentTimeMillis();

	private GameServer()
	{
		super(ServiceManager.SERVICE_ID_ROOT);
	}

	public static GameServer getInstance()
	{
		if (instance == null)
		{
			instance = new GameServer();
		}
		return instance;
	}

	public void systemExit()
	{
		logger.error("ϵͳ���̹رգ�System.exit(0)");
		System.exit(0);
	}

	/**
	 * ׼���������������
	 */
	@Override
	public boolean initialize()
	{

		try
		{
			// ���ط������
			ConfigService.getInstance().onReady(SERVER_CONFIG_FILE);
//			// ������
			ServiceService.getInstance().onReady();
			// Ҫ�ڼ���������֮��
			MessageRegistry.getInstance().loadConfig(
					MessageCommands.MESSAGE_NUM);

			if (ServiceService.isGatewayService())
				ServerService.getInstance().onReady();
			// // // DataStore����֧��
			 DataStoreService.getInstance().onReady();
			
			if (ServiceService.isWorldService())
			{
				// ֧��ϵͳ
				SupportService.getInstance().onReady();// �������֮��Ӧ���ǲ���Ҫ���ص�
			}

			logger.error("init finished");
			return true;
		} catch (Exception e)
		{
			logger.error("��ʼ��GameServerʧ��", e);
		}
		return false;
	}

	@Override
	public boolean startService()
	{
		try
		{

			if (logger.isDebugEnabled())
			{
				logger.debug("startup:" + serverStatus);
			}

			ScheduleTaskService.getInstance().onStart();
			if(ServiceService.isGatewayService())
			{
				// ������ϷͨѶ������
				ServerService.getInstance().onStart();
			}
			// ���·�����״̬Ϊ����
			serverStatus = ServerStatus.EMPTY;

			return true;
		} catch (Exception e)
		{
			logger.error("��������ʧ��", e);
		}
		return false;
	}

	/**
	 * ���÷�����״̬Ϊ�رգ�GM���߹ط�ʱ����
	 */
	public void setStatusClosed()
	{
		serverStatus = ServerStatus.CLOSED;
		if (logger.isDebugEnabled())
		{
			logger.debug("shutdown:" + serverStatus);
		}
	}

	/**
	 * �ر���Ϸ������
	 */
	@Override
	public boolean stopService()
	{

		// �ر���Ϸ���ӷ���
		try
		{
			ServerService.getInstance().onDown();
			logger.error("gameCommService.shutdown:ok");
		} catch (Exception e)
		{
			logger.error("�ر���Ϸ���ӷ������:", e);
		} catch (Error e)
		{
			logger.error("�ر���Ϸ���ӷ���Error:", e);
		}

		if (ServiceService.isWorldService())
		{ 
			// �ر�ServerTaskManager
			try
			{
				ScheduleTaskService.getInstance().onDown();
				logger.error("ServerTaskManager.shutdown:ok");
			} catch (Exception e)
			{
				logger.error("�ر���Ϸ���ӷ������:", e);
			} catch (Error e)
			{
				logger.error("�ر���Ϸ���ӷ���Error:", e);
			}

			// �ر���Ϸ��־����
			try
			{
				SupportService.getInstance().onDown();
				logger.error("logService.shutdown:ok");
			} catch (Exception e)
			{
				logger.error("�ر���Ϸ��־�������:", e);
			} catch (Error e)
			{
				logger.error("�ر���Ϸ��־����Error:", e);
			}
		}
		System.gc();

		return true;
	}

	public static void startGameServer()
	{

		GameServer gameServer = GameServer.getInstance();
		boolean ready = true;
		try
		{
			logger.error("============��ǰ�汾����ʱ�䣺" + getCurrentTime()
					+ "============");
			// ����������Ϣ

			gameServer.initialize();
			
		} catch (Exception e)
		{
			logger.error("�����ʼʧ��!", e);
			ready = false;
		}
		if (ready)
		{
			gameServer.startService();
			startUpTime = System.currentTimeMillis();
			logger.error("��Ϸ���������ɹ�: ");
		}
	}

	public static void main(String[] args)
	{
		if (args != null && args.length > 0)
			SERVER_CONFIG_FILE = args[0];
		startGameServer();
	}

	/**
	 * ��ǰ����������ʱ��
	 * 
	 * @return
	 */
	private static final String getCurrentTime()
	{
		String currentTime = "";
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			currentTime = sdf.format(new Date());
		} catch (Exception e)
		{
			logger.error("��Ϸ��������ʱ��ȡ����ʱ��ʧ�ܣ�������Ϣ��" + e.getMessage(), e);
		}
		return currentTime;
	}

	/**
	 * ������Ϸ���ݿ��������״̬
	 */
	public static void updateServerStatus(int roleCount)
	{
		try
		{
			if (serverStatus == ServerStatus.CLOSED)
				return;
			ServerStatus status = ServerStatus.getByPlayerCount(roleCount);
			if (status != serverStatus)
			{
				serverStatus = status;
			}
		} catch (Exception e)
		{
			logger.error(e.toString(), e);
		}
	}

	// /**
	// * ɱ��(������)�û�,����Ӱ��
	// */
	// public static final void killUser(int userId) {
	// cache.killUser(userId);
	// }
	//
	// /**
	// * ��Ӧ�Ự�ر��¼�
	// *
	// * @param session
	// */
	// public static final void onSessionClosed(IoSession session) {
	// cache.onSessionClosed(session);
	// }

	/**
	 * ��ȡ��������ǰ״̬
	 * 
	 * @return the serverStatus
	 */
	public static ServerStatus getServerStatus()
	{
		return serverStatus;
	}

	public static long getStartUpTime()
	{
		return startUpTime;
	}
}