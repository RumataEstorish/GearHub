package gearsoftware.gearhub.servicesimpl.transgear

interface ITransGearView : ISapWatchCommunicationSource{
    fun clearTransferList()
    fun showTransferProgress(progress: Int, currentFile: String?, files: List<String>)
    fun updateTransferList(filesLeft: Int, currentTransferPath: String, fileNames: List<String>)
}