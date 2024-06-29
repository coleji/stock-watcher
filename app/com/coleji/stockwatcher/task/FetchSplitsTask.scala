package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.DateUtil
import com.coleji.stockwatcher.StockWatcherTask
import com.coleji.stockwatcher.entity.entitydefinitions.PolygonSplit
import com.coleji.stockwatcher.remoteapi.polygon.splits.Splits
import com.coleji.stockwatcher.task.FetchFinancialsTask.API_FETCH_HOUR

import java.time.{LocalDate, ZonedDateTime}

object FetchSplitsTask extends StockWatcherTask {
	override def getNextRuntime: ZonedDateTime = DateUtil.setHour(ZonedDateTime.now().plusDays(1), API_FETCH_HOUR)

	protected override def taskAction(rc: UnlockedRequestCache): Unit = {
		val latestSplitQ = new PreparedQueryForSelect[LocalDate](Set(rc.companion)) {

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDate = rsw.getLocalDate(1)

			override def getQuery: String = "select max(execution_date) from s_p_splits"
		}

		val maxDate = Option(rc.executePreparedQueryForSelect(latestSplitQ).head).getOrElse(LocalDate.MIN)

		println("max date is: " + maxDate)

		val splits = Splits.getSplits(maxDate, appendLog)
//		println(splits)
		val toInsert = splits
			.filter(s => LocalDate.parse(s.execution_date).isAfter(maxDate))
			.map(s => PolygonSplit(LocalDate.parse(s.execution_date), s.ticker, s.split_from, s.split_to))
//		println(toInsert)
		rc.batchInsertObjects(toInsert)
	}
}
