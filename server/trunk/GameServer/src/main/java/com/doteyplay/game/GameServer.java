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
	 * 服务器启动成功时间戳
	 */
	private static long startUpTime = 0L;

	private static GlobalUserCache cache = GlobalUserCache.getInstance();

	/**
	 * 服务器当前人数状态
	 */
	private static ServerStatus serverStatus = ServerStatus.CLOSED;

	/**
	 * 服务器启动时的时间戳
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
		logger.error("系统进程关闭！System.exit(0)");
		System.exit(0);
	}

	/**
	 * 准备启动服务的数据
	 */
	@Override
	public boolean initialize()
	{

		try
		{
			// 加载服务参数
			ConfigService.getInstance().onReady(SERVER_CONFIG_FILE);
//			// 服务项
			ServiceService.getInstance().onReady();
			// 要在加载了配置之后
			MessageRegistry.getInstance().loadConfig(
					MessageCommands.MESSAGE_NUM);

			if (ServiceService.isGatewayService())
				ServerService.getInstance().onReady();
			// // // DataStore数据支持
			 DataStoreService.getInstance().onReady();
			
			if (ServiceService.isWorldService())
			{
				// 支持系统
				SupportService.getInstance().onReady();// 世界服务之外应该是不需要加载的
			}

			logger.error("init finished");
			return true;
		} catch (Exception e)
		{
			logger.error("初始化GameServer失败", e);
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
				// 启动游戏通讯服务器
				ServerService.getInstance().onStart();
			}
			// 更新服务器状态为空闲
			serverStatus = ServerStatus.EMPTY;

			return true;
		} catch (Exception e)
		{
			logger.error("启动服务失败", e);
		}
		return false;
	}

	/**
	 * 设置服务器状态为关闭，GM工具关服时调用
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
	 * 关闭游戏服务器
	 */
	@Override
	public boolean stopService()
	{

		// 关闭游戏连接服务
		try
		{
			ServerService.getInstance().onDown();
			logger.error("gameCommService.shutdown:ok");
		} catch (Exception e)
		{
			logger.error("关闭游戏连接服务错误:", e);
		} catch (Error e)
		{
			logger.error("关闭游戏连接服务Error:", e);
		}

		if (ServiceService.isWorldService())
		{ 
			// 关闭ServerTaskManager
			try
			{
				ScheduleTaskService.getInstance().onDown();
				logger.error("ServerTaskManager.shutdown:ok");
			} catch (Exception e)
			{
				logger.error("关闭游戏连接服务错误:", e);
			} catch (Error e)
			{
				logger.error("关闭游戏连接服务Error:", e);
			}

			// 关闭游戏日志服务
			try
			{
				SupportService.getInstance().onDown();
				logger.error("logService.shutdown:ok");
			} catch (Exception e)
			{
				logger.error("关闭游戏日志服务错误:", e);
			} catch (Error e)
			{
				logger.error("关闭游戏日志服务Error:", e);
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
			logger.error("============当前版本启动时间：" + getCurrentTime()
					+ "============");
			// 加载配置信息

			gameServer.initialize();
			
		} catch (Exception e)
		{
			logger.error("服务初始失败!", e);
			ready = false;
		}
		if (ready)
		{
			gameServer.startService();
			startUpTime = System.currentTimeMillis();
			logger.error("游戏服务启动成功: ");
		}
	}

	public static void main(String[] args)
	{
		if (args != null && args.length > 0)
			SERVER_CONFIG_FILE = args[0];
		startGameServer();
	}

	/**
	 * 当前启动服务器时间
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
			logger.error("游戏服务启动时获取启动时间失败，错误信息：" + e.getMessage(), e);
		}
		return currentTime;
	}

	/**
	 * 更新游戏数据库的区服务状态
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
	// * 杀掉(踢下线)用户,不留影子
	// */
	// public static final void killUser(int userId) {
	// cache.killUser(userId);
	// }
	//
	// /**
	// * 响应会话关闭事件
	// *
	// * @param session
	// */
	// public static final void onSessionClosed(IoSession session) {
	// cache.onSessionClosed(session);
	// }

	/**
	 * 获取服务器当前状态
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
