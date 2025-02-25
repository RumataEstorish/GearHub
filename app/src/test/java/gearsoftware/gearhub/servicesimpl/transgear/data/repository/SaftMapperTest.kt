package gearsoftware.gearhub.servicesimpl.transgear.data.repository

import org.junit.Assert
import org.junit.Test
import gearsoftware.gearhub.servicesimpl.transgear.data.model.saft.SaftReceive

class SaftMapperTest {
    @Test
    fun fromSaftTest() {
        val src = SaftReceive(id = 1, name = "Test", path = "TestPath")
        val res = SaftMapper.fromSaft(src)

        Assert.assertEquals(src.id, res.id)
        Assert.assertEquals(src.name, res.name)
        Assert.assertEquals(src.path, res.path)
    }
}