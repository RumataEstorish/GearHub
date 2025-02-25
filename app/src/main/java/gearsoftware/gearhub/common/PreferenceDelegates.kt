@file:Suppress("unused")

package gearsoftware.gearhub.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun stringPreference(key: String) = object : ReadWriteProperty<IPreferences, String> {

    override fun getValue(thisRef: IPreferences, property: KProperty<*>): String {
        return thisRef.sharedPreferences.getString(key, "") ?: ""
    }

    override fun setValue(thisRef: IPreferences, property: KProperty<*>, value: String) {
        thisRef.sharedPreferences.edit().putString(key, value).apply()
    }
}

fun booleanPreference(key: String, defaultValue: Boolean = false) = object : ReadWriteProperty<IPreferences, Boolean> {

    override fun getValue(thisRef: IPreferences, property: KProperty<*>): Boolean {
        return thisRef.sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun setValue(thisRef: IPreferences, property: KProperty<*>, value: Boolean) {
        thisRef.sharedPreferences.edit().putBoolean(key, value).apply()
    }
}

fun intPreference(key: String, defaultValue: Int = 0) =
    object : ReadWriteProperty<IPreferences, Int> {

        override fun getValue(thisRef: IPreferences, property: KProperty<*>): Int {
            return thisRef.sharedPreferences.getInt(key, defaultValue)
        }

        override fun setValue(thisRef: IPreferences, property: KProperty<*>, value: Int) {
            thisRef.sharedPreferences.edit().putInt(key, value).apply()
        }
    }

@Suppress("unused")
fun floatPreference(key: String) = object : ReadWriteProperty<IPreferences, Float> {

    override fun getValue(thisRef: IPreferences, property: KProperty<*>): Float {
        return thisRef.sharedPreferences.getFloat(key, 0f)
    }

    override fun setValue(thisRef: IPreferences, property: KProperty<*>, value: Float) {
        thisRef.sharedPreferences.edit().putFloat(key, value).apply()
    }
}

@Suppress("unused")
fun longPreference(key: String) = object : ReadWriteProperty<IPreferences, Long> {

    override fun getValue(thisRef: IPreferences, property: KProperty<*>): Long {
        return thisRef.sharedPreferences.getLong(key, 0L)
    }

    override fun setValue(thisRef: IPreferences, property: KProperty<*>, value: Long) {
        thisRef.sharedPreferences.edit().putLong(key, value).apply()
    }
}

@Suppress("unused")
fun stringSetPreference(key: String) =
    object : ReadWriteProperty<IPreferences, Set<String>> {

        override fun getValue(thisRef: IPreferences, property: KProperty<*>): Set<String> {
            return thisRef.sharedPreferences.getStringSet(key, setOf()) ?: setOf()
        }

        override fun setValue(
            thisRef: IPreferences,
            property: KProperty<*>,
            value: Set<String>
        ) {
            thisRef.sharedPreferences.edit().putStringSet(key, value).apply()
        }
    }

inline fun <reified T> objectListPreference(key: String, gson: Gson) =
    object : ReadWriteProperty<IPreferences, List<T>> {

        override fun getValue(
            thisRef: IPreferences,
            property: KProperty<*>
        ): List<T> {
            val emptyList = Gson().toJson(emptyList<T>())
            val json = thisRef.sharedPreferences.getString(key, emptyList) ?: emptyList
            return fromJsonToList(gson, json, T::class.java)
        }

        override fun setValue(
            thisRef: IPreferences,
            property: KProperty<*>,
            value: List<T>
        ) {
            thisRef.sharedPreferences.edit().putString(key, gson.toJson(value)).apply()
        }
    }

fun <T> fromJsonToList(gson: Gson, json: String, elementClass: Class<T>): List<T> {
    return gson.fromJson(
        json,
        TypeToken.getParameterized(List::class.java, elementClass).type
    )
}

inline fun <reified T : Enum<*>> enumPreference(key: String, defaultValue: T) =
    object : ReadWriteProperty<IPreferences, T> {

        override fun getValue(thisRef: IPreferences, property: KProperty<*>): T {
            val value = thisRef.sharedPreferences.getInt(key, defaultValue.ordinal)
            return T::class.java.enumConstants!!.first { it.ordinal == value }
        }

        override fun setValue(thisRef: IPreferences, property: KProperty<*>, value: T) {
            thisRef.sharedPreferences.edit().putInt(key, value.ordinal).apply()
        }
    }

inline fun <reified T> objectPreference(key: String, defaultValue: T, gson: Gson) =
    object : ReadWriteProperty<IPreferences, T> {

        override fun getValue(
            thisRef: IPreferences,
            property: KProperty<*>
        ): T {
            val value = thisRef.sharedPreferences.getString(key, "")
            return if (!value.isNullOrEmpty()) {
                gson.fromJson(value, T::class.java)
            } else {
                defaultValue
            }
        }

        override fun setValue(
            thisRef: IPreferences,
            property: KProperty<*>,
            value: T
        ) {
            thisRef.sharedPreferences.edit().putString(key, gson.toJson(value)).apply()
        }
    }
