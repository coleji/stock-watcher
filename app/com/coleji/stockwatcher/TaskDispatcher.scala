package com.coleji.stockwatcher

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.stockwatcher.task.{FetchDailyOHLCsTask, FetchDividendsTask, FetchFinancialsTask, FetchSplitsTask}
import org.slf4j.LoggerFactory
import play.api.inject.ApplicationLifecycle

import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import scala.collection.mutable
import scala.concurrent.Future

class TaskDispatcher @Inject()(lifecycle: ApplicationLifecycle){
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	val taskNextRuntimes: mutable.Map[StockWatcherTask, ZonedDateTime] = mutable.Map(
		(FetchFinancialsTask, FetchFinancialsTask.getNextRuntime),
		(FetchDividendsTask, FetchDividendsTask.getNextRuntime),
		(FetchSplitsTask, FetchSplitsTask.getNextRuntime),
		(FetchDailyOHLCsTask, FetchDailyOHLCsTask.getNextRuntime)
	)

	logger.info("TASK SCHEDULE: ")
	logger.info(taskNextRuntimes.toString())

	lifecycle.addStopHook(() => Future.successful({
		logger.info("****************************    Stop hook: stopping tasks  **********************")
		TaskDispatcher.shutDownRequested.set(true)
	}))

	def start()(implicit PA: PermissionsAuthority): Unit = {
		var didRun = false
		val RUN_ONLY_ONCE = PA.customParams.getString("task-runner-only-once").toBoolean
		logger.info("Run once? " + RUN_ONLY_ONCE)
		////////////////////////////////////
		// force them all to run now

		if (RUN_ONLY_ONCE) {
			taskNextRuntimes.foreach(t => {
				taskNextRuntimes(t._1) = ZonedDateTime.now().minusHours(1)
			})
		}
		/////////////////////
		if (!TaskDispatcher.tasksRunning.get()) {
			TaskDispatcher.tasksRunning.set(true)
			logger.info("TaskDispatcher.start()")
			while ((!RUN_ONLY_ONCE || !didRun) && !TaskDispatcher.shutDownRequested.get()) {
				loop()
				didRun=true
				Thread.sleep(3000)
			}
			logger.info("TaskDispatcher stopped for shutdown!")
		}
	}


	private def loop()(implicit PA: PermissionsAuthority): Unit = {
		logger.info("Looking for tasks to run....")
		var foundTask = true
		while (foundTask) {
			taskNextRuntimes.find(t => t._2.isBefore(ZonedDateTime.now())) match {
				case Some((task, _)) => {
					logger.info("<<<<<<<<<<<<    STARTING TASK")
					task.run(PA.rootRC.assertUnlocked)
					logger.info(">>>>>>>>>>>>  FINISHED TASK")
					taskNextRuntimes(task) = task.getNextRuntime
				}
				case None => foundTask = false
			}
		}
		logger.info(taskNextRuntimes.toString()
		)
	}
}

object TaskDispatcher {
	private var shutDownRequested = new AtomicBoolean(false)
	private var tasksRunning = new AtomicBoolean(false)
}
