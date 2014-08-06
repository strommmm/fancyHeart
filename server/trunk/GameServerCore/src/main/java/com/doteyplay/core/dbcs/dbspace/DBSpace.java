package com.doteyplay.core.dbcs.dbspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doteyplay.core.dbcs.DBCSConst;
import com.doteyplay.core.dbcs.config.DbRuleReader;
import com.doteyplay.core.util.PackageScaner;

/**
 * ���ݿ�������Ϣ���� �������ӡ�ӳ��������Ϣ
 * 
 * @author 
 * 
 */
public class DBSpace
{
	private static String DB_CONFIG_PATH = DBCSConst.DB_CONFIG_PATH;
	private static final String DB_RULE_CONFIG = DBCSConst.DB_RULE_CONFIG;
	private static final String SQL_MAP_CONFIG_SUFFIX = DBCSConst.SQL_MAP_CONFIG_SUFFIX;
	private static final String GAMEDATA_SQL_MAP_CONFIG_SUFFIX = DBCSConst.GAMEDATA_SQL_MAP_CONFIG_SUFFIX;

	private static List<DBZone> dbZoneList = new ArrayList<DBZone>();
	private static Map<Integer, DBZone> dbZoneMap = new HashMap<Integer, DBZone>();
	private static DBZone defaultDBZone;
	private static DBZone gameDataDBZone;
	private static Map<String, IDBZoneRule> ruleMap = new HashMap<String, IDBZoneRule>();

	public static IDBZoneRule getZoneRule(String rulename)
	{
		return ruleMap.get(rulename);
	}

	public static void regRule(IDBZoneRule rule)
	{
		if (rule == null)
			return;

		ruleMap.put(rule.getName(), rule);
	}

	public static Collection<DBZone> getDBZones()
	{
		return dbZoneList;
	}
	
	public static DBZone getGameDataDBZone()
	{
		return gameDataDBZone;
	}

	public static DBZone getDBZone(int dbid)
	{
		return dbZoneMap.get(dbid);
	}

	public static DBZone getDefaultDBZone()
	{
		return defaultDBZone;
	}

	public static boolean initialize(String dbconfigpath)
	{
		try
		{
			if (dbconfigpath != null)
				DB_CONFIG_PATH = dbconfigpath;
			System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
			System.setProperty("java.naming.factory.url.pkgs", "org.apache.naming");

			String[] mapperPackageNameList = PackageScaner.scanNamespaceFiles(DB_CONFIG_PATH,
					SQL_MAP_CONFIG_SUFFIX,false,false);
			for (String mapperPackageName : mapperPackageNameList)
			{
				DBZone tmpDBZone = new DBZone();
				tmpDBZone.loadConfig(DB_CONFIG_PATH + "/" + mapperPackageName);
				if (!dbZoneMap.containsKey(tmpDBZone.getZoneId()))
				{
					tmpDBZone.loadSqlMap();
					if (dbZoneList.size() == 0)
						defaultDBZone = tmpDBZone;
					else if (tmpDBZone.getZoneId() == 0)
						defaultDBZone = tmpDBZone;
					dbZoneMap.put(tmpDBZone.getZoneId(), tmpDBZone);
					dbZoneList.add(tmpDBZone);
				}
				else
				{
					// TODO ��ʵ��
				}
			}
			
			 mapperPackageNameList = PackageScaner.scanNamespaceFiles(DB_CONFIG_PATH,
					 GAMEDATA_SQL_MAP_CONFIG_SUFFIX,false,false);
			
			if(mapperPackageNameList != null && mapperPackageNameList.length == 1)
			{
				gameDataDBZone = new DBZone();
				gameDataDBZone.loadConfig(DB_CONFIG_PATH +"/" + mapperPackageNameList[0]);	
				gameDataDBZone.loadSqlMap();
			}

			DbRuleReader tmpDbRuleReader = new DbRuleReader(DB_CONFIG_PATH + '/' + DB_RULE_CONFIG);
			tmpDbRuleReader.doRead();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

}