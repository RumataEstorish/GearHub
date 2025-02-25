package gearsoftware.sap.data

import io.reactivex.rxjava3.core.Scheduler

interface ISchedulers {
    val io: Scheduler
    val computation: Scheduler
    val single: Scheduler
    val newThread: Scheduler
    val mainThread: Scheduler
}