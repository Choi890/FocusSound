package com.focussound.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PresetEntity::class,
        FocusSessionEntity::class,
        SoundPatchEntity::class,
        UserTasteVectorEntity::class,
        ConditionSnapshotEntity::class,
        CompositionPatchEntity::class,
        CompositionNoteEntity::class,
        InstrumentPresetEntity::class,
        SampleZoneEntity::class
    ],
    version = 6,
    exportSchema = true
)
abstract class FocusSoundDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun soundPatchDao(): SoundPatchDao
    abstract fun userTasteVectorDao(): UserTasteVectorDao
    abstract fun conditionSnapshotDao(): ConditionSnapshotDao
    abstract fun compositionDao(): CompositionDao
    abstract fun instrumentDao(): InstrumentDao

    companion object {
        @Volatile
        private var instance: FocusSoundDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN patchId TEXT")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN patchName TEXT")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN highCut REAL NOT NULL DEFAULT 0.55")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN stereoWidth REAL NOT NULL DEFAULT 0.35")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN rainLayerAmount REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN padLayerAmount REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN conditionSnapshotId INTEGER")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS sound_patches (
                        id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        mode TEXT NOT NULL,
                        baseNoiseType TEXT NOT NULL,
                        brightness REAL NOT NULL,
                        warmth REAL NOT NULL,
                        movement REAL NOT NULL,
                        highCut REAL NOT NULL,
                        lowAmount REAL NOT NULL,
                        stereoWidth REAL NOT NULL,
                        rainLayerAmount REAL NOT NULL,
                        padLayerAmount REAL NOT NULL,
                        modulationDepth REAL NOT NULL,
                        modulationRateHz REAL NOT NULL,
                        targetFatigueScore INTEGER NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        createdAtMillis INTEGER NOT NULL,
                        lastUsedAtMillis INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_taste_vectors (
                        mode TEXT NOT NULL,
                        preferredBrightness REAL NOT NULL,
                        preferredWarmth REAL NOT NULL,
                        preferredMovement REAL NOT NULL,
                        preferredHighCut REAL NOT NULL,
                        preferredStereoWidth REAL NOT NULL,
                        preferredNoiseTypesJson TEXT NOT NULL,
                        preferredSessionMinutes INTEGER NOT NULL,
                        confidence REAL NOT NULL,
                        updatedAtMillis INTEGER NOT NULL,
                        PRIMARY KEY(mode)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS condition_snapshots (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sleepMinutes INTEGER,
                        sleepDebt INTEGER NOT NULL,
                        stepsToday INTEGER,
                        restingHeartRate INTEGER,
                        selfReportedFatigue TEXT NOT NULL,
                        selfReportedMood TEXT NOT NULL,
                        timeOfDay TEXT NOT NULL,
                        source TEXT NOT NULL,
                        createdAtMillis INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN compositionPatchId TEXT")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN compositionPatchName TEXT")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN compositionGenre TEXT")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN melodyDensity REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN rhythmDensity REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN harmonicComplexity REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN melodyAnnoying INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN rhythmAnnoying INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN harmonyLiked INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN tooRepetitive INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_taste_vectors ADD COLUMN preferredGenre TEXT NOT NULL DEFAULT 'AMBIENT_CODING'")
                db.execSQL("ALTER TABLE user_taste_vectors ADD COLUMN preferredTempoMin INTEGER NOT NULL DEFAULT 60")
                db.execSQL("ALTER TABLE user_taste_vectors ADD COLUMN preferredTempoMax INTEGER NOT NULL DEFAULT 82")
                db.execSQL("ALTER TABLE user_taste_vectors ADD COLUMN preferredMelodyDensity REAL NOT NULL DEFAULT 0.22")
                db.execSQL("ALTER TABLE user_taste_vectors ADD COLUMN preferredRhythmDensity REAL NOT NULL DEFAULT 0.12")
                db.execSQL("ALTER TABLE user_taste_vectors ADD COLUMN preferredHarmonicComplexity REAL NOT NULL DEFAULT 0.28")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS composition_patches (
                        id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        mode TEXT NOT NULL,
                        genre TEXT NOT NULL,
                        tempoBpm INTEGER NOT NULL,
                        keyRoot TEXT NOT NULL,
                        keyScaleType TEXT NOT NULL,
                        chordProgressionJson TEXT NOT NULL,
                        melodyDensity REAL NOT NULL,
                        rhythmDensity REAL NOT NULL,
                        harmonicComplexity REAL NOT NULL,
                        fatigueScore INTEGER NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        createdAtMillis INTEGER NOT NULL,
                        lastUsedAtMillis INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS composition_notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        patchId TEXT NOT NULL,
                        startBeat REAL NOT NULL,
                        durationBeats REAL NOT NULL,
                        midiNote INTEGER NOT NULL,
                        velocity REAL NOT NULL,
                        lane TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_composition_notes_patchId ON composition_notes(patchId)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN focusedWell INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN tooDark INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN useAgain INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE composition_patches ADD COLUMN padAmount REAL NOT NULL DEFAULT 0.58")
                db.execSQL("ALTER TABLE composition_patches ADD COLUMN moodKeywordsJson TEXT NOT NULL DEFAULT '[]'")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE composition_patches ADD COLUMN instrumentNamesJson TEXT NOT NULL DEFAULT '[]'")
                createInstrumentTables(db)
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sound_patches ADD COLUMN noiseLayerAmount REAL NOT NULL DEFAULT 1.0")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN category TEXT NOT NULL DEFAULT 'PIANO'")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN coldness REAL NOT NULL DEFAULT 0.2")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN licenseName TEXT NOT NULL DEFAULT '사용자 제공'")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN licenseSourceName TEXT NOT NULL DEFAULT '사용자 파일'")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN licenseSourceUrl TEXT")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN redistributionAllowed INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN commercialUseAllowed INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN attributionRequired INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE instrument_presets ADD COLUMN licenseNotes TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): FocusSoundDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FocusSoundDatabase::class.java,
                    "focus_sound_v2.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                    .also { instance = it }
            }
        }

        private fun createInstrumentTables(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS instrument_presets (
                    id TEXT NOT NULL,
                    name TEXT NOT NULL,
                    role TEXT NOT NULL,
                    sourceType TEXT NOT NULL,
                    sourcePath TEXT,
                    attackMillis INTEGER NOT NULL,
                    decayMillis INTEGER NOT NULL,
                    sustainLevel REAL NOT NULL,
                    releaseMillis INTEGER NOT NULL,
                    defaultVolume REAL NOT NULL,
                    brightness REAL NOT NULL,
                    warmth REAL NOT NULL,
                    importedAtMillis INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS sample_zones (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    presetId TEXT NOT NULL,
                    samplePath TEXT NOT NULL,
                    rootMidiNote INTEGER NOT NULL,
                    minMidiNote INTEGER NOT NULL,
                    maxMidiNote INTEGER NOT NULL,
                    minVelocity INTEGER NOT NULL,
                    maxVelocity INTEGER NOT NULL,
                    loopStartFrame INTEGER,
                    loopEndFrame INTEGER
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_sample_zones_presetId ON sample_zones(presetId)")
        }
    }
}
