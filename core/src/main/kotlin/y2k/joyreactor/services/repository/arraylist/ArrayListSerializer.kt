package y2k.joyreactor.services.repository.arraylist

import y2k.joyreactor.platform.Platform
import y2k.joyreactor.services.repository.Dto
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object ArrayListSerializer {

    private val version = 1

    fun <T : Dto> loadFromDisk(dataSet: ArrayListDataSet<T>) {
        getFile(dataSet)
            .let { if (it.exists()) it else null }
            ?.let { file ->
                file.inputStream()
                    .let { ObjectInputStream(it) }
                    .use { stream ->
                        while (true) {
                            try {
                                @Suppress("UNCHECKED_CAST")
                                dataSet.add(stream.readObject() as T)
                            } catch(e: EOFException) {
                                break
                            }
                        }
                    }
            }
    }

    fun saveToDisk(dataSet: ArrayListDataSet<*>) {
        getFile(dataSet)
            .outputStream()
            .let { ObjectOutputStream(it) }
            .use { stream -> dataSet.forEach { stream.writeObject(it) } }
    }

    private fun getFile(datasSet: ArrayListDataSet<*>): File {
        return File(Platform.instance.currentDirectory, "${datasSet.javaClass.simpleName}.$version.db")
    }
}