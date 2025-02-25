package gearsoftware.gearhub.services

import androidx.annotation.Keep
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Keep
annotation class SapPermissionService(
    val sapPermission: KClass<out SapServicePermissions>
)
