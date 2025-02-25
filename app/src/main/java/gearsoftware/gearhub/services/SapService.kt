package gearsoftware.gearhub.services

import androidx.annotation.Keep
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Keep
annotation class SapService(
    val name: String,
    val haveSettings: Boolean = false,
    val haveAuthorization: Boolean = false,
    val storeUrl: String,
    val description: KClass<out SapServiceDescription>
)