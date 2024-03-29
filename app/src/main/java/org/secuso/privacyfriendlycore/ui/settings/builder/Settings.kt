package org.secuso.privacyfriendlycore.ui.settings.builder

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.secuso.privacyfriendlycore.ui.settings.RadioPreference
import org.secuso.privacyfriendlycore.ui.settings.SettingData
import org.secuso.privacyfriendlycore.ui.settings.SwitchPreference

class Settings(
    val settings: MutableList<SettingData<*>> = mutableListOf(),
    private val preferences: SharedPreferences,
    private val resources: Resources
) {

    private fun track(dependency: String?): MutableState<Boolean> {
        if (dependency == null) {
            return mutableStateOf(true)
        }
        val state = settings.find { it.key == dependency }?.state
            ?: throw IllegalStateException("Dependency $dependency not found. Dependencies must be in the same category and precede the setting")
        if (state.value !is Boolean) {
            throw IllegalStateException("A Setting can only depend on Boolean-Settings")
        }
        return state as MutableState<Boolean>
    }

    @Suppress("Unused")
    fun switch(initializer: SingleSetting<Boolean>.() -> Unit) {
        val setting = SingleSetting<Boolean>(resources)
            .apply(initializer)
            .compose(
                state = { data ->
                    mutableStateOf(preferences.getBoolean(data.key!!, data.default!!))
                },
                enabled = { track(it) }
            ) { settingDSL ->
                { data ->
                    SwitchPreference(
                        data = data,
                        enabled = data.enable,
                        update = {
                            preferences.edit().putBoolean(data.key, it).apply()
                            data.state.value = it
                            settingDSL.onUpdate?.let { onUpdate -> onUpdate(it) }
                        }
                    )
                }
            }
        this.settings.add(setting)
    }

    @Suppress("Unused")
    fun radioString(initializer: SingleSetting<String>.() -> Unit) {
        val setting = SingleSetting<String>(resources)
            .apply(initializer)
            .compose(state = { data ->
                mutableStateOf(preferences.getString(data.key!!, data.default!!)!!)
            },
                enabled = { track(it) }
            ) { settingDSL ->
                { data ->
                    RadioPreference(
                        data = data,
                        enabled = data.enable,
                        update = {
                            preferences.edit().putString(data.key, it).apply()
                            data.state.value = it
                            settingDSL.onUpdate?.let { onUpdate -> onUpdate(it) }
                        }
                    )
                }
            }
        this.settings.add(setting)
    }

    @Suppress("Unused")
    fun radioInt(initializer: SingleSetting<Int>.() -> Unit) {
        val setting = SingleSetting<Int>(resources)
            .apply(initializer)
            .compose(state = { data ->
                mutableStateOf(preferences.getInt(data.key!!, data.default!!))
            },
                enabled = { track(it) }
            ) { settingDSL ->
                { data ->
                    RadioPreference(
                        data = data,
                        enabled = data.enable,
                        update = {
                            preferences.edit().putInt(data.key, it).apply()
                            data.state.value = it
                            settingDSL.onUpdate?.let { onUpdate -> onUpdate(it) }
                        }
                    )
                }
            }
        this.settings.add(setting)
    }
}