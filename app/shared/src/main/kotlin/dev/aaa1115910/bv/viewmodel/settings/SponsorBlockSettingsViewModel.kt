package dev.aaa1115910.bv.viewmodel.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockActionType
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockCategories
import dev.aaa1115910.bv.entity.sponsorblock.SponsorBlockColors
import dev.aaa1115910.bv.util.Prefs
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SponsorBlockSettingsViewModel : ViewModel() {
    private val logger = KotlinLogging.logger {}
    
    // SponsorBlock enabled state
    var enabled by mutableStateOf(false)
        private set

    // Category actions
    val categoryActions = mutableStateMapOf<String, SponsorBlockActionType>()
    
    // Category colors
    val categoryColors = mutableStateMapOf<String, String>()
    
    init {
        // Automatically load settings when ViewModel is created
        loadSettings()
    }
    
    fun loadSettings() {
        logger.info { "Loading SponsorBlock settings" }
        
        // Load enabled state
        enabled = Prefs.enableSponsorBlock
        
        // Load category actions
        categoryActions.clear()
        SponsorBlockCategories.ALL_CATEGORIES_ORDERED.forEach { category ->
            val action = Prefs.sponsorBlockUserActions[category] 
                ?: SponsorBlockCategories.DEFAULT_ACTIONS[category] 
                ?: SponsorBlockActionType.DO_NOTHING
            categoryActions[category] = action
        }
        
        // Load category colors
        categoryColors.clear()
        SponsorBlockCategories.ALL_CATEGORIES_ORDERED.forEach { category ->
            val color = Prefs.sponsorBlockUserColors[category] 
                ?: SponsorBlockColors.DefaultCategoryColorsHex[category]
            if (color != null) {
                categoryColors[category] = color
            }
        }
    }

    fun setEnableSponsorBlock(enable: Boolean) {
        enabled = enable
        logger.info { "SponsorBlock enabled: $enable" }
    }

    fun setActionForCategory(category: String, action: SponsorBlockActionType) {
        categoryActions[category] = action
        logger.info { "Set action for category $category: $action" }
    }

    fun setColorForCategory(category: String, colorHex: String) {
        categoryColors[category] = colorHex
        logger.info { "Set color for category $category: $colorHex" }
    }

    fun saveSettings() {
        logger.info { "Saving SponsorBlock settings" }
        
        // Save enabled state
        Prefs.enableSponsorBlock = enabled
        logger.info { "Saved enableSponsorBlock: $enabled" }
        
        // Save category actions
        val actionsToSave = mutableMapOf<String, SponsorBlockActionType>()
        categoryActions.forEach { (category, action) ->
            actionsToSave[category] = action
        }
        Prefs.sponsorBlockUserActions = actionsToSave
        logger.info { "Saved sponsorBlockUserActions: $actionsToSave" }
        
        // Save category colors
        val colorsToSave = mutableMapOf<String, String>()
        categoryColors.forEach { (category, color) ->
            colorsToSave[category] = color
        }
        Prefs.sponsorBlockUserColors = colorsToSave
        logger.info { "Saved sponsorBlockUserColors: $colorsToSave" }
        
        logger.info { "SponsorBlock settings saved successfully" }
    }

    fun resetToDefaults() {
        categoryActions.clear()
        categoryColors.clear()
        // enabled state could also be reset to a default if desired, e.g., true
        // enabled = true
    }
}
