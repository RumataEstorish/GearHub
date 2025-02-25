package gearsoftware.sap.data

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import toothpick.InjectConstructor

@InjectConstructor
internal class SchedulerProvider : ISchedulers {
    override val io: Scheduler
        get() = Schedulers.io()

    override val computation: Scheduler
        get() = Schedulers.computation()

    override val single: Scheduler
        get() = Schedulers.single()

    override val newThread: Scheduler
        get() = Schedulers.newThread()

    override val mainThread: Scheduler
        get() = AndroidSchedulers.mainThread()

}