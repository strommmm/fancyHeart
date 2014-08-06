package com.doteyplay.game.startservice;

import org.apache.log4j.Logger;

import com.doteyplay.core.configloader.TaskHandlerRegistry;
import com.doteyplay.core.server.service.IServerService;
import com.doteyplay.core.server.service.IServerServiceException;
import com.doteyplay.core.util.servertask.ServerTaskManager;

public class ScheduleTaskService implements IServerService
{
	private static final Logger logger = Logger
			.getLogger(ScheduleTaskService.class);
	private static ScheduleTaskService service = new ScheduleTaskService();

	private ScheduleTaskService()
	{
	}

	public static ScheduleTaskService getInstance()
	{
		return service;
	}

	@Override
	public void onDown() throws IServerServiceException
	{
		ServerTaskManager.getInstance().stopService();
	}

	@Override
	public void onReady() throws IServerServiceException
	{

	}

	@Override
	public void onStart() throws IServerServiceException
	{
		logger.error("������ؼƻ�����");
		/**
		 * ��������
		 */
		ServerTaskManager.getInstance().initialize(
				TaskHandlerRegistry.getInstance().getTaskInfo());
		ServerTaskManager.getInstance().startService();

	}

}