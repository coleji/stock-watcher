package com.coleji.stockwatcher

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.stockwatcher.task.{FetchDividendsTask, FetchSplitsTask}
import play.api.inject.ApplicationLifecycle

import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import scala.concurrent.Future

class TaskDispatcher @Inject()(lifecycle: ApplicationLifecycle)(){
	lifecycle.addStopHook(() => Future.successful({
		println("****************************    Stop hook: stopping tasks  **********************")
		TaskDispatcher.shutDownRequested.set(true)
	}))

	def start(PA: PermissionsAuthority): Unit = {
		if (!TaskDispatcher.tasksRunning.get()) {
			TaskDispatcher.tasksRunning.set(true)
			println("TaskDispatcher.start()")
			while (!TaskDispatcher.shutDownRequested.get()) {
				loop(PA)
				Thread.sleep(3000)
			}
			println("TaskDispatcher stopped for shutdown!")
		}
	}

	private def loop(PA: PermissionsAuthority): Unit = {
		println("Looking for tasks to run....")
		FetchDividendsTask.run(PA.rootRC.assertUnlocked)
//		FetchSplitsTask.run(PA.rootRC.assertUnlocked)
//		FetchDailyOHLCsTask.run(PA.rootRC.assertUnlocked)
	}
}

object TaskDispatcher {
	private var shutDownRequested = new AtomicBoolean(false)
	private var tasksRunning = new AtomicBoolean(false)
}