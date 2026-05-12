MAGNETAR ORPHEUS — EXTREME UI DESIGN SPECIFICATION

The application is a premium dark-mode chromatic instrument tuner for Android, designed for both phone and tablet. The visual identity must feel like a serious professional audio instrument: dark graphite surfaces, precise measurement geometry, neon green/cyan signal accents, restrained amber/red warning zones, clean typography, high contrast, and no playful or childish visuals. The app must look like a modern musician’s tool, closer to a premium oscilloscope/tuner dashboard than to a casual mobile app.

The entire interface uses a near-black background, not pure black. The base background color is #05080A. Elevated panels use #0B1115. Higher cards use #10171D. Subtle separators and card borders use #1B2A31 with low opacity. The primary “in tune / active / healthy signal” color is neon green #35F58A. The secondary signal color is cyan-green #20D6C7. Warning zones use amber #FFD84A. Strong out-of-tune zones use red #FF4B4B. Primary text uses #F2F5F4. Secondary text uses #8D989E. Muted text uses #5E686E. No large white surfaces are allowed. No bright colorful gradients except extremely subtle green/cyan glows around active tuning elements.

The visual style must be clean, technical, symmetrical, and highly readable at a glance. The musician must be able to understand the tuning state in under one second. The main information hierarchy is: first, tuning accuracy; second, detected note; third, exact frequency; fourth, cents deviation; fifth, mode/reference settings; sixth, secondary analytics like waveform, history, and pitch stability. The UI must never visually compete with the main tuning meter.

The phone layout is portrait-first. The screen is vertically divided into five main zones: header, chromatic note row, main tuning meter, signal/status section, and bottom controls. The tablet layout is landscape-first. It uses a two-column adaptive layout: the left 65 percent of the width contains the full primary tuner experience, and the right 35 percent contains secondary panels such as note history, pitch stability, instrument/tuning, and quick presets. The tablet must not simply stretch the phone UI; it must use the additional space intelligently while preserving the same visual identity.

HEADER DESIGN

The top header sits below the Android status bar. It has a height around 88 dp on phones and 72 dp on tablets. The header background is the same as the main background, with a very subtle bottom separator line in #1B2A31 at 40 percent opacity. On the left there is a hamburger menu icon in muted white, size 28 dp, with a touch target of 48 dp. In the center there is the Magnetar Orpheus brand. The logo consists of a small vertical waveform mark made of 4 to 6 thin vertical bars in neon green/cyan, followed by the word MAGNETAR in uppercase, letter-spaced, white, modern sans-serif, medium weight. Below MAGNETAR, centered and smaller, the word ORPHEUS appears in neon green with wide letter spacing. On the right there is a settings gear icon, muted white, size 26 dp, with a 48 dp touch target. The header must feel calm and premium, not crowded.

CHROMATIC NOTE ROW

Below the header there is a horizontal chromatic note selector row. It is contained in a thin dark elevated strip with height around 72 dp on phones and 64 dp on tablets. The row displays the note names E, F, F#, G, G#, A, A#, B, C evenly distributed across the width. Text is uppercase-style musical notation, font size around 18 sp on phones and 16 sp on tablets, color #8D989E when inactive. The currently detected note is highlighted with a rounded rectangle pill, approximately 56 dp wide and 48 dp high on phone, using a translucent green background #35F58A at 12 to 18 percent opacity, a 1 dp border in #35F58A at 60 percent opacity, and active text in #F2F5F4 or #BFFFD8. A tiny neon green dot below the active note reinforces the selected state. The row must be readable but visually secondary to the main gauge.

MAIN TUNING GAUGE

The central element is a large semicircular tuning gauge occupying the dominant part of the screen. On phone, the gauge should start around 28 dp below the chromatic row and span roughly 85 to 90 percent of screen width. On tablet, the gauge should occupy the upper-middle of the left column and span roughly 80 percent of the left column width. The gauge represents cents deviation from -100 to +100. The arc begins at the lower-left side, rises through the center, and ends at the lower-right side, forming a wide semicircle. The center top represents 0 cents. The left side represents flat values; the right side represents sharp values.

The gauge contains many small tick marks along the arc. Minor ticks are thin, 1 dp, muted gray #5E686E at 60 percent opacity. Major ticks are thicker, 2 to 3 dp, longer, and brighter. The labels shown around the arc are -100 on the far left in red, -50 on the upper-left in light gray, 0 at the top center in light gray, +50 on the upper-right in light gray, and +100 on the far right in red. The ±20 zones are labeled in amber, with “-20” on the left and “+20” on the right. The colored arc logic is strict: the central region around -5 to +5 cents is neon green and visually indicates the perfect tuning zone; the region from roughly ±5 to ±20 is green fading toward amber or represented by amber tick emphasis; the region beyond ±20 becomes amber and then red near ±50 to ±100. The red extremes must signal “clearly wrong,” but they must not dominate the UI.

The gauge needle is the main precision indicator. It is a thin vertical or radial indicator aligned with the current cents value. At 0 cents it is perfectly centered under the “0” label. The needle uses #35F58A or #20D6C7, with a subtle glow. It has a small triangular pointer at the arc and a thin line extending downward toward the note display. The needle must animate smoothly and not jump abruptly. The UI should visually suggest stability, precision, and calmness.

CENTER NOTE DISPLAY

Inside and below the gauge sits the detected note display. The note letter is huge and dominant, for example “A”, in neon green #35F58A, using a bold geometric sans-serif font. On phone, the note letter should be around 110 to 140 sp depending on screen size. On tablet, around 120 to 160 sp within the left column. The octave number appears to the lower-right of the note letter, for example “4”, smaller but still large, around 48 to 64 sp on phone. The note and octave together read as “A4”, but visually the note letter is dominant.

Below the note display is the exact measured frequency, for example “440.1 Hz”. This uses primary white text #F2F5F4, large but clearly secondary to the note, around 34 to 42 sp on phone and 36 to 46 sp on tablet. Below frequency is the cents deviation text, for example “+2 cents”. If the value is inside the good tuning range, it is green. If close but not perfect, it becomes amber. If strongly out of tune, it becomes red. The text must include the sign for positive and negative values. At exactly tuned or near zero, it may display “0 cents” or “in tune” depending on UX decision, but the numeric value must remain available.

MICROPHONE AND WAVEFORM SECTION

Below the note/frequency/cents area there is a rounded card showing microphone status and signal activity. The card uses surface color #0B1115 or #10171D, rounded corners around 22 to 28 dp, and a 1 dp border in #1B2A31. On phone it spans almost full width with horizontal padding around 20 to 24 dp. On tablet it stays within the left column and aligns with the bottom controls. The left side contains a circular microphone icon button, 48 dp, with a green outline or translucent green fill when active. Under or beside it, the label “MIC ACTIVE” appears in small uppercase green text, around 11 to 12 sp. The right side contains a live waveform or input level visualization. The waveform is a thin horizontal signal line in green/cyan, with vertical amplitude bars or smooth waveform segments. It should look alive but not chaotic. If the microphone is inactive, the waveform becomes muted gray and the label changes to “MIC OFF” or “WAITING FOR INPUT”.

BOTTOM CONTROL CARDS

At the bottom of the phone layout there are two main rounded control cards side by side. The first card is “TUNER MODE” and shows the value “Chromatic”. The second card is “REFERENCE PITCH” and shows “A4 = 440 Hz”. Each card has an icon on the left, a small uppercase label in muted text, a larger value in white, and a dropdown chevron on the right. Cards use #0B1115 with rounded corners around 22 dp and subtle border #1B2A31. The cards must look tappable but not loud. On tablet, these controls can appear as three cards along the bottom of the left column: TUNER MODE, REFERENCE PITCH, and CALIBRATION. Calibration may show “440.0 Hz”.

TABLET RIGHT PANEL

On tablet landscape, the right side column is a secondary dashboard. It must not distract from the main tuner. It uses stacked cards with consistent spacing, rounded corners, and low-contrast borders. The top card is NOTE HISTORY. It shows the last detected notes with note name, octave, frequency, cents deviation, and time. Example rows: A4 440.0 Hz +2c 10:42:21; E4 329.6 Hz -1c 10:41:56; G4 392.0 Hz +3c 10:41:28. Positive cents are green, negative small deviations can be amber or muted red depending on severity. The note history card has a small trash icon in the top-right and a “VIEW FULL HISTORY” action at the bottom.

Below note history is PITCH STABILITY. This card shows a small live graph with a horizontal center line at 0 cents and vertical range markers around +20 and -20. The graph line is green/cyan and should show slight movement around center. A small “LIVE” label appears in green at the top-right. The graph is not the main tuning indicator; it is a secondary stability diagnostic.

Below that is INSTRUMENT / TUNING. It shows a small guitar icon, the selected instrument “Guitar”, and the selected tuning preset “Standard (EADGBE)”, both as dropdown-style fields. Below it is QUICK PRESETS. It lists Standard A4 = 440 Hz, Orchestral A4 = 442 Hz, and Baroque A4 = 415 Hz. The selected preset has a small green star or active indicator. There is a plus button for adding a preset and a “MANAGE PRESETS” action at the bottom.

SPACING AND GEOMETRY

Use generous padding and avoid overcrowding. Phone horizontal padding should be 20 to 24 dp. Tablet outer padding should be 32 to 40 dp. Component spacing should be consistent: 12 dp for tight internal gaps, 16 dp for related groups, 24 dp for major section separation. Cards should use rounded corners between 20 and 28 dp. Borders must be thin and subtle. Shadows should be minimal because the app is dark; depth should come from surface layering, borders, and subtle glow, not heavy drop shadows.

TYPOGRAPHY

Use a clean modern sans-serif. The typography should feel technical and premium. Large values use bold or semi-bold. Labels use uppercase, small size, letter spacing around 0.08 to 0.16 em, and muted color. Important numeric values must use tabular or visually stable digits if possible, so frequency and cents do not visually jump. The note letter must be the largest text on screen. Frequency is second. Cents is third. Labels must never overpower the measurement.

STATE COLORS

If cents is between -5 and +5, the system is “in tune”. The needle, central arc, note, and cents text are green. If cents is between -20 and -5 or between +5 and +20, the system is “near tune”. The needle can remain green/cyan but the deviation text or side ticks should show amber. If cents is below -20 or above +20, the system is “out of tune”. The relevant side of the arc becomes more visually active in amber/red, and the cents text becomes amber or red depending on severity. The app must clearly communicate direction: negative means flat, positive means sharp. The left side of the gauge is flat; the right side is sharp.

RESPONSIVE BEHAVIOR

On compact phone width, use a single-column vertical layout. The main gauge and note display must remain centered. Secondary analytics like history and stability are hidden or accessible through navigation, not shown on the main phone screen. On expanded tablet width, use a two-column layout. The left column contains the same main tuner experience as phone, but wider and more relaxed. The right column contains note history, pitch stability, instrument/tuning, and presets. The top header spans the full width on tablet.

ANIMATION BEHAVIOR

The tuning needle must move smoothly using spring or tween animation. It should feel precise but not nervous. Tiny pitch fluctuations should be dampened visually. The note display may change with a subtle fade or scale transition, not a dramatic animation. The waveform should animate continuously when microphone input is active. The active note in the chromatic row should move or fade smoothly when the detected note changes. The glow around the center tuning zone may gently intensify when the pitch is stable and near 0 cents.

ACCESSIBILITY AND USABILITY

The app must remain usable in dark environments such as stage, rehearsal room, or bedroom. Text contrast must be high. The main note and frequency must be readable from arm’s length. Touch targets must be at least 48 dp. The UI must not rely only on color; direction and numeric cents must also indicate flat/sharp state. The central gauge must show both direction and magnitude clearly. The musician should not need to interpret complex graphs during tuning.

VISUAL DO-NOTS

Do not use bright white backgrounds. Do not use cartoonish icons. Do not use skeuomorphic wooden guitar textures. Do not use excessive gradients. Do not use too many colors. Do not make the waveform larger than the main note. Do not make settings compete with the tuner. Do not make the tablet version just a stretched phone screen. Do not hide the exact frequency. Do not show only a needle without numeric cents. Do not use a tiny note display. Do not overload the phone screen with history and analytics.

FINAL VISUAL TARGET

The final result should look like a premium dark Android tuner app named Magnetar Orpheus. The phone version should feel focused, immediate, and performance-oriented: a huge semicircular tuning gauge, a massive A4 note display, exact Hz, cents deviation, mic waveform, and two bottom control cards. The tablet version should feel like the professional expanded cockpit of the same app: main tuner on the left, note history and pitch stability on the right, with the same dark graphite surfaces and neon green/cyan precision language. The design must communicate: professional, musical, precise, calm, technical, fast, and trustworthy.
