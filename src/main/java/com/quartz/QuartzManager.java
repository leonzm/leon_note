package com.quartz;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 定时任务类
 *
 * http://jayyanzhang2010.iteye.com/blog/1771341
 *
 */
public class QuartzManager {
	
	private static final Logger LOG = Logger.getLogger(QuartzManager.class);

	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();  
    private static String JOB_GROUP_NAME = "my_jobgroup";  
    private static String TRIGGER_GROUP_NAME = "my_triggergroup";  
	
    /** 
     * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名 
     * 
     * @param jobName 任务名 
     * @param jobClass 任务
     * @param time 时间设置，参考quartz说明文档 
     * @throws SchedulerException 
     * @throws ParseException 
     */  
	public static void addJob(String jobName, Class<?> jobClass, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, jobClass);// 任务名，任务组，任务执行类
			// 触发器
			CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
			trigger.setCronExpression(time);// 触发器时间设定
			sched.scheduleJob(jobDetail, trigger);
			// 启动
			if (!sched.isShutdown()) {
				sched.start();
			}
		} catch (Exception e) {
			LOG.warn("添加一个定时任务异常", e);
			throw new RuntimeException(e);
		}
	}
    
	/** 
     * 添加一个定时任务 
     * 
     * @param jobName 任务名 
     * @param jobGroupName 任务组名 
     * @param triggerName 触发器名 
     * @param triggerGroupName 触发器组名 
     * @param jobClass 任务 
     * @param time 时间设置，参考quartz说明文档 
     * @throws SchedulerException 
     * @throws ParseException 
     */  
	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<?> jobClass, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName, jobGroupName, jobClass);// 任务名，任务组，任务执行类
			// 触发器
			CronTrigger trigger = new CronTrigger(triggerName, triggerGroupName);// 触发器名,触发器组
			trigger.setCronExpression(time);// 触发器时间设定
			sched.scheduleJob(jobDetail, trigger);
		} catch (Exception e) {
			LOG.warn("添加一个定时任务异常", e);
			throw new RuntimeException(e);
		}
	}
    
	/** 
     * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名) 
     * 
     * @param jobName 
     * @param time 
     */  
	public static void modifyJobTime(String jobName, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(jobName, TRIGGER_GROUP_NAME);
			if (trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				JobDetail jobDetail = sched.getJobDetail(jobName, JOB_GROUP_NAME);
				Class<?> objJobClass = jobDetail.getJobClass();
				removeJob(jobName);

				addJob(jobName, objJobClass, time);
			}
		} catch (Exception e) {
			LOG.warn("修改一个任务的触发时间异常", e);
			throw new RuntimeException(e);
		}
	}
	
	/** 
     * 修改一个任务的触发时间 
     * 
     * @param triggerName 
     * @param triggerGroupName 
     * @param time 
     */  
	public static void modifyJobTime(String triggerName, String triggerGroupName, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerName, triggerGroupName);
			if (trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				CronTrigger ct = (CronTrigger) trigger;
				// 修改时间
				ct.setCronExpression(time);
				// 重启触发器
				sched.resumeTrigger(triggerName, triggerGroupName);
			}
		} catch (Exception e) {
			LOG.warn("修改一个任务的触发时间异常", e);
			throw new RuntimeException(e);
		}
	}
	
	/** 
     * 移除一个任务(使用默认的任务组名，触发器名，触发器组名) 
     * 
     * @param jobName 
     */  
	public static void removeJob(String jobName) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(jobName, TRIGGER_GROUP_NAME);// 停止触发器
			sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);// 移除触发器
			sched.deleteJob(jobName, JOB_GROUP_NAME);// 删除任务
		} catch (Exception e) {
			LOG.warn("移除一个任务异常", e);
			throw new RuntimeException(e);
		}
	}
	
	/** 
     * 移除一个任务 
     * 
     * @param jobName 
     * @param jobGroupName 
     * @param triggerName 
     * @param triggerGroupName 
     */  
    public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {  
        try {  
            Scheduler sched = gSchedulerFactory.getScheduler();  
            sched.pauseTrigger(triggerName, triggerGroupName);// 停止触发器  
            sched.unscheduleJob(triggerName, triggerGroupName);// 移除触发器  
            sched.deleteJob(jobName, jobGroupName);// 删除任务  
        } catch (Exception e) {  
        	LOG.warn("移除一个任务异常", e);
            throw new RuntimeException(e);  
        }
    }  
  
    /** 
     * 启动所有定时任务 
     */  
    public static void startJobs() {  
        try {  
            Scheduler sched = gSchedulerFactory.getScheduler();  
            sched.start();  
        } catch (Exception e) {  
            LOG.warn("启动所有定时任务异常", e);
            throw new RuntimeException(e);  
        }  
    }  
  
    /** 
     * 关闭所有定时任务 
     */  
    public static void shutdownJobs() {  
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            if(!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            LOG.warn("关闭所有定时任务异常", e);
            throw new RuntimeException(e);  
        }
    }
    
    public static void main(String[] args) {
    	addJob("test111", MyJob.class, "* * * * * ? *"); // 每秒一次，不管上次有没有执行完
    	
	}
    
}

/**
 * 
quartz 时间配置规则
格式: [秒] [分] [小时] [日] [月] [周] [年]

 序号	说明	 是否必填	 允许填写的值	允许的通配符
 1	 秒	 是	 0-59 	  , - * /
 2	 分	 是	 0-59	  , - * /
 3	小时	 是	 0-23	  , - * /
 4	 日	 是	 1-31	  , - * ? / L W
 5	 月	 是	 1-12 or JAN-DEC	  , - * /
 6	 周	 是	 1-7 or SUN-SAT	  , - * ? / L #
 7	 年	 否	 empty 或 1970-2099	 , - * /

通配符说明:
* 表示所有值. 例如:在分的字段上设置 "*",表示每一分钟都会触发。
? 表示不指定值。使用的场景为不需要关心当前设置这个字段的值。例如:要在每月的10号触发一个操作，但不关心是周几，所以需要周位置的那个字段设置为"?" 具体设置为 0 0 0 10 * ?
- 表示区间。例如 在小时上设置 "10-12",表示 10,11,12点都会触发。
, 表示指定多个值，例如在周字段上设置 "MON,WED,FRI" 表示周一，周三和周五触发
/ 用于递增触发。如在秒上面设置"5/15" 表示从5秒开始，每增15秒触发(5,20,35,50)。在月字段上设置'1/3'所示每月1号开始，每隔三天触发一次。
L 表示最后的意思。在日字段设置上，表示当月的最后一天(依据当前月份，如果是二月还会依据是否是润年[leap]), 在周字段上表示星期六，相当于"7"或"SAT"。如果在"L"前加上数字，则表示该数据的最后一个。例如在周字段上设置"6L"这样的格式,则表示“本月最后一个星期五"
W 表示离指定日期的最近那个工作日(周一至周五). 例如在日字段上设置"15W"，表示离每月15号最近的那个工作日触发。如果15号正好是周六，则找最近的周五(14号)触发, 如果15号是周未，则找最近的下周一(16号)触发.如果15号正好在工作日(周一至周五)，则就在该天触发。如果指定格式为 "1W",它则表示每月1号往后最近的工作日触发。如果1号正是周六，则将在3号下周一触发。(注，"W"前只能设置具体的数字,不允许区间"-").
小提示	
'L'和 'W'可以一组合使用。如果在日字段上设置"LW",则表示在本月的最后一个工作日触发(一般指发工资 ) 
# 序号(表示每月的第几个周几)，例如在周字段上设置"6#3"表示在每月的第三个周六.注意如果指定"#5",正好第五周没有周六，则不会触发该配置(用在母亲节和父亲节再合适不过了)
小提示	
周字段的设置，若使用英文字母是不区分大小写的 MON 与mon相同.

       
常用示例:
 
0 0 12 * * ?	每天12点触发
0 15 10 ? * *	每天10点15分触发
0 15 10 * * ?	每天10点15分触发
0 15 10 * * ? *	每天10点15分触发
0 15 10 * * ? 2005	2005年每天10点15分触发
0 * 14 * * ?	每天下午的 2点到2点59分每分触发
0 0/5 14 * * ?	每天下午的 2点到2点59分(整点开始，每隔5分触发)
0 0/5 14,18 * * ?
 
每天下午的 2点到2点59分(整点开始，每隔5分触发)
每天下午的 18点到18点59分(整点开始，每隔5分触发)
0 0-5 14 * * ?	每天下午的 2点到2点05分每分触发
0 10,44 14 ? 3 WED	3月分每周三下午的 2点10分和2点44分触发
0 15 10 ? * MON-FRI	从周一到周五每天上午的10点15分触发
0 15 10 15 * ?	每月15号上午10点15分触发
0 15 10 L * ?	每月最后一天的10点15分触发
0 15 10 ? * 6L	每月最后一周的星期五的10点15分触发
0 15 10 ? * 6L 2002-2005	从2002年到2005年每月最后一周的星期五的10点15分触发
0 15 10 ? * 6#3	每月的第三周的星期五开始触发
0 0 12 1/5 * ?	每月的第一个中午开始每隔5天触发一次
0 11 11 11 11 ?	每年的11月11号 11点11分触发(光棍节)
*/