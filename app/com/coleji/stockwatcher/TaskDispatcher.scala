package com.coleji.stockwatcher

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.stockwatcher.TaskDispatcher.{RUN_MODE_ONCE, RUN_MODE_SCHEDULE}
import com.coleji.stockwatcher.task.{CalcEpsTask, DailySummaryEmailTask, FetchDailyOHLCsTask, FetchDividendsTask, FetchFinancialsTask, FetchSplitsTask}
import org.slf4j.LoggerFactory
import play.api.inject.ApplicationLifecycle

import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import scala.collection.mutable
import scala.concurrent.Future

class TaskDispatcher @Inject()(lifecycle: ApplicationLifecycle){
	private val logger = LoggerFactory.getLogger(this.getClass.getName)

	private var taskNextRuntimes: mutable.Map[StockWatcherTask, ZonedDateTime] = mutable.Map(
		// fetch
		(FetchFinancialsTask, FetchFinancialsTask.getNextRuntime),
		(FetchDividendsTask, FetchDividendsTask.getNextRuntime),
		(FetchSplitsTask, FetchSplitsTask.getNextRuntime),
		(FetchDailyOHLCsTask, FetchDailyOHLCsTask.getNextRuntime),

		// calc
		(CalcEpsTask, CalcEpsTask.getNextRuntime),

		// email
		(DailySummaryEmailTask, ZonedDateTime.now)
	)

	logger.info("TASK SCHEDULE: ")

	lifecycle.addStopHook(() => Future.successful({
		logger.info("****************************    Stop hook: stopping tasks  **********************")
		TaskDispatcher.shutDownRequested.set(true)
	}))

	def start()(implicit PA: PermissionsAuthority): Unit = {
		var didRun = false
		val runMode = PA.customParams.getString("task-runner-mode").toInt

		if (runMode == RUN_MODE_ONCE) {
			// if running once, force them all to run now
			println(taskNextRuntimes.size)
			taskNextRuntimes = taskNextRuntimes.map(t => (t._1, ZonedDateTime.now().minusHours(1)))
//			taskNextRuntimes.foreach(t => {
//				println("updating " + t._1.getClass.getCanonicalName)
//				taskNextRuntimes(t._1) =
//			})
		}

		logger.info(taskNextRuntimes.toString())

		if (!TaskDispatcher.tasksRunning.get()) {
			TaskDispatcher.tasksRunning.set(true)
			logger.info("*** TaskDispatcher.start()")
			var count = 0
			while (
				(
					runMode == RUN_MODE_SCHEDULE ||
					(runMode == RUN_MODE_ONCE && !didRun)
				) &&
				!TaskDispatcher.shutDownRequested.get()
			) {
				loop(count)
				count = count+1
				didRun=true
				Thread.sleep(3000)
			}
			logger.info("TaskDispatcher stopped for shutdown!")
		}
	}


	private def loop(count: Int)(implicit PA: PermissionsAuthority): Unit = {
//		logger.info("Looking for tasks to run....")
		var foundTask = true
		while (foundTask) {
//			println("tasks: " + taskNextRuntimes.size)
//			println("matching tasks: " + taskNextRuntimes.count(t => t._2.isBefore(ZonedDateTime.now())))
			taskNextRuntimes.find(t => t._2.isBefore(ZonedDateTime.now())) match {
				case Some((task, _)) => {
					val start = System.currentTimeMillis()
					logger.info("<<<<<<<<<<<<    STARTING TASK " + task.getClass.getCanonicalName)
					PA.rootRC.withTransaction(() => Right(task.run(PA.rootRC.assertUnlocked)))
					logger.info(">>>>>>>>>>>>  FINISHED TASK " + task.getClass.getCanonicalName + "; runtime " + (System.currentTimeMillis() - start))
					logger.info(ZonedDateTime.now().toString)
					taskNextRuntimes(task) = task.getNextRuntime
				}
				case None => foundTask = false
			}
		}
		if (count % 200 == 0) {
			taskNextRuntimes.foreach(t => {
				logger.info(t._2.toString + " => " + t._1.getClass.getSimpleName)
			})

		}
	}
}

object TaskDispatcher {
	private val shutDownRequested = new AtomicBoolean(false)
	private val tasksRunning = new AtomicBoolean(false)

	val RUN_MODE_OFF: Int = 0
	val RUN_MODE_ONCE: Int = 1
	val RUN_MODE_SCHEDULE: Int = 2
}
