package com.coleji.stockwatcher

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.stockwatcher.task.{FetchDailyOHLCsTask, FetchDividendsTask, FetchFinancialsTask, FetchSplitsTask}
import play.api.inject.ApplicationLifecycle

import java.time.{LocalTime, ZonedDateTime}
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import scala.collection.mutable
import scala.concurrent.Future

class TaskDispatcher @Inject()(lifecycle: ApplicationLifecycle){
	val taskNextRuntimes: mutable.Map[StockWatcherTask, ZonedDateTime] = mutable.Map(
		(FetchFinancialsTask, FetchFinancialsTask.getNextRuntime),
		(FetchDividendsTask, FetchDividendsTask.getNextRuntime),
		(FetchSplitsTask, FetchSplitsTask.getNextRuntime),
		(FetchDailyOHLCsTask, FetchDailyOHLCsTask.getNextRuntime)
	)

	println("TASK SCHEDULE: ")
	println(taskNextRuntimes)

	lifecycle.addStopHook(() => Future.successful({
		println("****************************    Stop hook: stopping tasks  **********************")
		TaskDispatcher.shutDownRequested.set(true)
	}))

	def start()(implicit PA: PermissionsAuthority): Unit = {
		var didRun = false
		val RUN_ONLY_ONCE = PA.customParams.getString("task-runner-only-once").toBoolean
//		taskNextRuntimes.foreach(t => {
//			taskNextRuntimes(t._1) = ZonedDateTime.now()
//		})
		if (!TaskDispatcher.tasksRunning.get()) {
			TaskDispatcher.tasksRunning.set(true)
			println("TaskDispatcher.start()")
			while ((!RUN_ONLY_ONCE || !didRun) && !TaskDispatcher.shutDownRequested.get()) {
				loop()
				didRun=true
				Thread.sleep(3000)
			}
			println("TaskDispatcher stopped for shutdown!")
		}
	}


	private def loop()(implicit PA: PermissionsAuthority): Unit = {
		println("Looking for tasks to run....")
		taskNextRuntimes.find(t => t._2.isBefore(ZonedDateTime.now())) match {
			case Some((task, _)) => {
				task.run(PA.rootRC.assertUnlocked)
				taskNextRuntimes(task) = task.getNextRuntime
			}
			case None =>
		}
		println(taskNextRuntimes)
	}
}

object TaskDispatcher {
	private var shutDownRequested = new AtomicBoolean(false)
	private var tasksRunning = new AtomicBoolean(false)
}
