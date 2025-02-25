package gearsoftware.gearhub.services.data

import gearsoftware.gearhub.services.SapPermission
import gearsoftware.gearhub.services.data.model.SapPermissionEntity
import toothpick.InjectConstructor

@InjectConstructor
class SapPermissionMapper {
    operator fun invoke(source: SapPermission, serviceName: String): SapPermissionEntity =
        SapPermissionEntity(
            serviceName = serviceName,
            permissions = source.permissions,
        )
}