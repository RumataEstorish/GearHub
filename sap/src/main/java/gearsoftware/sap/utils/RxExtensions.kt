@file:Suppress("unused")

package gearsoftware.sap.utils

import io.reactivex.rxjava3.core.CompletableEmitter
import io.reactivex.rxjava3.core.MaybeEmitter
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.SingleEmitter


fun <T : Any> SingleEmitter<T>.safeOnSuccess(data: T) {
    if (!this.isDisposed) {
        this.onSuccess(data)
    }
}

fun <T : Any> SingleEmitter<T>.safeOnError(error: Throwable) {
    if (!this.isDisposed) {
        this.onError(error)
    }
}

fun <T: Any> ObservableEmitter<T>.safeOnNext(data: T) {
    if (!this.isDisposed) {
        this.onNext(data)
    }
}

fun <T: Any> ObservableEmitter<T>.safeOnError(error: Throwable) {
    if (!this.isDisposed) {
        this.onError(error)
    }
}

fun <T: Any> ObservableEmitter<T>.safeOnComplete() {
    if (!this.isDisposed) {
        this.onComplete()
    }
}

fun CompletableEmitter.safeOnComplete() {
    if (!this.isDisposed) {
        this.onComplete()
    }
}

fun CompletableEmitter.safeOnError(error: Throwable) {
    if (!this.isDisposed) {
        this.onError(error)
    }
}

fun <T : Any> MaybeEmitter<T>.safeOnError(error: Throwable) {
    if (!this.isDisposed) {
        this.onError(error)
    }
}

fun <T : Any> MaybeEmitter<T>.safeOnSuccess(data: T) {
    if (!this.isDisposed) {
        this.onSuccess(data)
    }
}

fun <T : Any> MaybeEmitter<T>.safeOnComplete() {
    if (!this.isDisposed) {
        this.onComplete()
    }
}