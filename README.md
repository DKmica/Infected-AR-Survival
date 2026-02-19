# INFECTED AR (Android MVP)

INFECTED AR is an offline-first Kotlin + Jetpack Compose MVP game/app with three pillars:
1. Live Infect camera face-zombification.
2. Upload-photo zombify editor + export/share.
3. Survival mini-game with camera-style presentation.

## Setup
1. Open in Android Studio Iguana+.
2. Let Gradle sync.
3. Run `:app` on Android 8.0+ (API 26 minimum).

## Tech Stack
- Kotlin, Coroutines, Flow
- Jetpack Compose Material 3 + Navigation Compose
- CameraX preview + analysis
- ML Kit Face Detection
- Room for infection library
- DataStore Preferences for onboarding/settings
- Media3 (detail playback placeholder)
- FileProvider export/share

## Permissions
- CAMERA (live infect, mini-game camera background, capture)
- RECORD_AUDIO (reserved for future reveal recording)
- VIBRATE (haptics)

## Architecture
```
com.infected.ar
  data/ (Room, repositories, DataStore)
  domain/ (models, use-cases)
  ui/ (theme, nav, screens/components)
  media/ (face processing, export/share)
  util/
```

## MVP Notes / Limitations
- Upload flow has full screen chain with placeholder crop stage and procedural overlays.
- Reveal export now writes a valid minimal GIF fallback and shares it; MP4 MediaCodec/Muxer is still planned next.
- Survival mini-game uses 2D rush overlay with tap scoring/combos.
- Includes 10 procedural vector overlay assets to swap with production art.

## Testing
- Unit: Room repository save/read.
- Unit: DataStore read/write.
- UI instrumentation skeleton: navigation smoke baseline.

## Next Steps
- Replace placeholders with production rendering pipeline + contour-aware eye placement.
- Add robust video reveal encoder and Media3 playback wiring.
- Improve permission education + denial UX copy.
- Add analytics/events and remote challenge content (optional backend phase).
