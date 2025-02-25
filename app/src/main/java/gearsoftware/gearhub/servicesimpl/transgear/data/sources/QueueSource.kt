package gearsoftware.gearhub.servicesimpl.transgear.data.sources

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gearsoftware.gearhub.servicesimpl.transgear.FilesTransferRequest
import toothpick.InjectConstructor

@InjectConstructor
class QueueSource(
        private val prefs: SharedPreferences
) : IQueueSource {

    companion object {
        private const val FILES_TRANSFER_LIST = "FILES_TRANSFER_LIST"
    }

    private val filesList: MutableSet<FilesTransferRequest> = mutableSetOf()

    init {
        val str = prefs.getString(FILES_TRANSFER_LIST, null)
        val listType = object : TypeToken<MutableSet<FilesTransferRequest>>() {}.type
        if (!str.isNullOrBlank()) {
            filesList.addAll(Gson().fromJson(str, listType))
        }
    }

    override val isEmpty: Boolean
        get() = filesList.isEmpty()

    override fun getByTransferId(id: Int): FilesTransferRequest? =
            filesList.firstOrNull { it.id == id }

    override fun getByFileName(name: String): FilesTransferRequest? =
            filesList.firstOrNull { it.name == name }

    override fun getByFilePath(path: String): FilesTransferRequest? =
            filesList.firstOrNull { it.path == path }

    override fun removeByTransferId(id: Int) {
        filesList.firstOrNull { it.id == id }?.let { filesList.remove(it) }
        save()
    }

    override fun clear() {
        filesList.clear()
        save()
    }

    override fun getList() =
            filesList.toList()

    override fun add(files: List<FilesTransferRequest>) {
        filesList.addAll(files)
        save()
    }

    override fun add(file: FilesTransferRequest) {
        filesList.add(file)
        save()
    }

    override fun contains(file: FilesTransferRequest) =
            filesList.contains(file)

    private fun save() {
        if (filesList.isEmpty()) {
            prefs.edit().remove(FILES_TRANSFER_LIST).apply()
            return
        }
        prefs.edit().putString(FILES_TRANSFER_LIST, Gson().toJson(filesList)).apply()
    }

    override fun remove(filesTransferRequest: FilesTransferRequest?) {
        filesTransferRequest?.let {
            filesList.filter { f -> f != it }
                    .let {
                        filesList.clear()
                        filesList.addAll(it)
                    }
            save()
        }
    }
}