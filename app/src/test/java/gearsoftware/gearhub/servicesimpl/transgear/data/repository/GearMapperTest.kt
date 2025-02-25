package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import org.junit.Assert
import org.junit.Test
import gearsoftware.gearhub.servicesimpl.transgear.Direction
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import gearsoftware.gearhub.servicesimpl.transgear.data.model.gear.FileTransfer

class GearMapperTest {
    @Test
    fun toGearTest() {

        val file = FilesTransferRequest(name = "TestName", path = "TestPath", size = 100)
        val res = GearMapper.toGear(file)

        Assert.assertEquals(file.name, res.name)
        Assert.assertEquals(file.path, res.path)
        Assert.assertEquals(file.size, res.size)
    }

    @Test
    fun fromGearTest() {
        val file = FileTransfer("TestName", "TestPath", 100)
        val res = GearMapper.fromGear(file)

        Assert.assertEquals(file.name, res.name)
        Assert.assertEquals(file.path, res.path)
        Assert.assertEquals(file.size, res.size)
        Assert.assertEquals(Direction.DOWNLOAD, res.direction)
    }
}