package com.focussound.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focussound.instrument.AdsrParams
import com.focussound.instrument.InstrumentCategory
import com.focussound.instrument.InstrumentLicenseInfo
import com.focussound.instrument.InstrumentPreset
import com.focussound.instrument.InstrumentRole
import com.focussound.instrument.InstrumentSourceType

@Entity(tableName = "instrument_presets")
data class InstrumentPresetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val role: String,
    val category: String,
    val sourceType: String,
    val sourcePath: String?,
    val attackMillis: Int,
    val decayMillis: Int,
    val sustainLevel: Float,
    val releaseMillis: Int,
    val defaultVolume: Float,
    val brightness: Float,
    val warmth: Float,
    val coldness: Float,
    val licenseName: String,
    val licenseSourceName: String,
    val licenseSourceUrl: String?,
    val redistributionAllowed: Boolean,
    val commercialUseAllowed: Boolean,
    val attributionRequired: Boolean,
    val licenseNotes: String,
    val importedAtMillis: Long
) {
    fun toDomain(zones: List<SampleZoneEntity>): InstrumentPreset = InstrumentPreset(
        id = id,
        name = name,
        role = enumValueOrDefault(role, InstrumentRole.MELODY),
        category = enumValueOrDefault(category, InstrumentCategory.PIANO),
        sourceType = enumValueOrDefault(sourceType, InstrumentSourceType.SYNTH_FALLBACK),
        sourcePath = sourcePath,
        sampleZones = zones.map { it.toDomain() },
        adsr = AdsrParams(attackMillis, decayMillis, sustainLevel, releaseMillis),
        defaultVolume = defaultVolume,
        brightness = brightness,
        warmth = warmth,
        coldness = coldness,
        license = InstrumentLicenseInfo(
            licenseName = licenseName,
            sourceName = licenseSourceName,
            sourceUrl = licenseSourceUrl,
            redistributionAllowed = redistributionAllowed,
            commercialUseAllowed = commercialUseAllowed,
            attributionRequired = attributionRequired,
            notes = licenseNotes
        ),
        importedAtMillis = importedAtMillis
    )
}

fun InstrumentPreset.toEntity(): InstrumentPresetEntity = InstrumentPresetEntity(
    id = id,
    name = name,
    role = role.name,
    category = category.name,
    sourceType = sourceType.name,
    sourcePath = sourcePath,
    attackMillis = adsr.attackMillis,
    decayMillis = adsr.decayMillis,
    sustainLevel = adsr.sustainLevel,
    releaseMillis = adsr.releaseMillis,
    defaultVolume = defaultVolume,
    brightness = brightness,
    warmth = warmth,
    coldness = coldness,
    licenseName = license.licenseName,
    licenseSourceName = license.sourceName,
    licenseSourceUrl = license.sourceUrl,
    redistributionAllowed = license.redistributionAllowed,
    commercialUseAllowed = license.commercialUseAllowed,
    attributionRequired = license.attributionRequired,
    licenseNotes = license.notes,
    importedAtMillis = importedAtMillis
)
