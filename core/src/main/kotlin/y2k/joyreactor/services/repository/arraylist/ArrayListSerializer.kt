package y2k.joyreactor.services.repository.arraylist

import y2k.joyreactor.common.ApplicationDataVersion
import y2k.joyreactor.platform.Platform
import y2k.joyreactor.services.repository.Dto
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.reflect.KClass

object ArrayListSerializer {

    fun <T : Dto> loadFromDisk(dataSet: ArrayListDataSet<T>) {
        getFile(dataSet.type)
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

    fun <T : Dto> saveToDisk(dataSet: ArrayListDataSet<T>) {
        getFile(dataSet.type)
            .outputStream()
            .let { ObjectOutputStream(it) }
            .use { stream -> dataSet.forEach { stream.writeObject(it) } }
    }

    private fun getFile(type: KClass<*>): File {
        return File(Platform.instance.currentDirectory, "${type.java.simpleName}.$ApplicationDataVersion.db")
    }
}