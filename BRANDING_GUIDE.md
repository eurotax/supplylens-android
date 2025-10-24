# SupplyLens Brand Guidelines - Android Implementation

## 🎨 Color Palette

### Primary Colors (Gold)
- **Gold**: `#D4AF37` - Main brand color for CTAs, highlights
- **Gold Light**: `#E1BC48` - Lighter accent, hover states
- **Gold Dark**: `#9E7C17` - Darker accent, pressed states

### Background Colors
- **Background Dark**: `#0B0F14` - Main app background
- **Surface Dark**: `#111827` - Cards, elevated surfaces

### Functional Colors
- **Accent Blue**: `#38BDF8` - Secondary CTA, links (alternative to gold)
- **Success**: `#22C55E` - Positive states, confirmations
- **Warning**: `#F59E0B` - Alerts, cautions
- **Error**: `#EF4444` - Errors, destructive actions

### Text Colors
- **Text Primary**: `#FFFFFF` - Main text
- **Text Secondary**: `#D1D5DB` - Secondary text, descriptions
- **Text Tertiary**: `#9CA3AF` - Tertiary text, captions

---

## 🖼️ App Icon

### Adaptive Icon (Android 8.0+)
- **Foreground**: Gold "SL" monogram with rounded frame
- **Background**: Dark (#0B0F14)
- **Safe Zone**: 72dp circle (33% from each edge)

### Legacy Icon (Android 7.1 and below)
- **Format**: XML drawable (vector-based)
- **Fallback**: Located in `drawable/ic_launcher_legacy.xml`

### Files Structure
```
res/
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml (Adaptive Icon config)
│   └── ic_launcher_round.xml (Round Adaptive Icon)
├── mipmap-mdpi/ → mipmap-xxxhdpi/
│   ├── ic_launcher.xml (Legacy fallback)
│   └── ic_launcher_round.xml (Legacy round fallback)
└── drawable/
    ├── ic_launcher_foreground.xml (SL monogram)
    ├── ic_launcher_background.xml (Dark background)
    ├── ic_launcher_legacy.xml (Legacy vector icon)
    └── splash_icon.xml (Splash screen icon)
```

---

## 🚀 Splash Screen

### Android 12+ (SplashScreen API)
- **Background**: `#0B0F14` (background_dark)
- **Icon**: `splash_icon.xml` - 288dp gold SL monogram
- **Duration**: 1-2 seconds (Material Design guideline)
- **Theme**: `Theme.SupplyLens.Splash`

### Implementation
```kotlin
// MainActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen() // BEFORE super.onCreate()
    super.onCreate(savedInstanceState)
    // ...
}
```

---

## 📱 System UI

### Status Bar
- **Color**: `#0B0F14` (CarbonBlack)
- **Icons**: Light (white)
- **Configuration**: `windowLightStatusBar = false`

### Navigation Bar
- **Color**: `#0B0F14` (CarbonBlack)
- **Icons**: Light (white)
- **Configuration**: `windowLightNavigationBar = false`

---

## 🔤 Typography

### Display Styles (Hero Sections)
- **Display Large**: Bold, 57sp, -0.25sp letter spacing
- **Display Medium**: Bold, 45sp, 0sp letter spacing
- **Display Small**: Bold, 36sp, 0sp letter spacing

### Headline Styles
- **Headline Large**: SemiBold, 32sp
- **Headline Medium**: SemiBold, 28sp
- **Headline Small**: SemiBold, 24sp

### Title Styles
- **Title Large**: SemiBold, 22sp
- **Title Medium**: Medium, 16sp, 0.15sp letter spacing
- **Title Small**: Medium, 14sp, 0.1sp letter spacing

### Body Styles
- **Body Large**: Regular, 16sp, 0.5sp letter spacing
- **Body Medium**: Regular, 14sp, 0.25sp letter spacing
- **Body Small**: Regular, 12sp, 0.4sp letter spacing

### Label Styles
- **Label Large**: Medium, 14sp, 0.1sp letter spacing
- **Label Medium**: Medium, 12sp, 0.5sp letter spacing
- **Label Small**: Medium, 11sp, 0.5sp letter spacing

---

## 🎯 Usage Guidelines

### When to Use Gold
- Primary CTAs (buttons, FABs)
- Important highlights
- Brand elements (logo, borders)
- Success states (when appropriate)

### When to Use Blue Accent
- Secondary CTAs
- Links
- Interactive elements that need distinction from gold
- Info states

### When to Use Dark Backgrounds
- Main screen backgrounds (`background_dark`)
- Cards and surfaces (`surface_dark`)
- Overlays (with transparency)

### Text Hierarchy
1. **Primary**: Headings, main content (`text_primary`)
2. **Secondary**: Descriptions, metadata (`text_secondary`)
3. **Tertiary**: Captions, timestamps (`text_tertiary`)

---

## 📐 Design Principles

### Premium Dark Theme
- High contrast for readability
- Gold accents for luxury feel
- Minimal use of bright colors
- Consistent spacing and elevation

### Accessibility
- WCAG AA contrast ratios maintained
- Text minimum 14sp for body content
- Touch targets minimum 48dp
- Clear visual feedback for interactions

---

## 🛠️ Development Notes

### Color Resources
- **App Module**: `app/res/values/colors.xml`
- **Core UI Module**: `core/ui/src/main/java/.../theme/Color.kt`

### Theme Configuration
- **XML Theme**: `app/res/values/themes.xml`
- **Compose Theme**: `core/ui/src/main/java/.../theme/Theme.kt`

### Build Configuration
- **Splash Screen API**: `androidx.core:core-splashscreen:1.0.1`
- **Material 3**: `androidx.compose.material3:material3`

---

## ✅ Implementation Checklist

- [x] Color palette defined in `colors.xml`
- [x] Adaptive Icon configured
- [x] Legacy Icon fallback created
- [x] Splash Screen implemented (Android 12+)
- [x] Status Bar styling configured
- [x] Navigation Bar styling configured
- [x] Typography system complete
- [x] Theme applied to MainActivity
- [x] Dark mode enforced

---

## 📞 Support

For questions or issues regarding branding implementation:
- Review this document first
- Check `/design_assets/` for original design files
- Consult Material Design 3 guidelines for edge cases

**Last Updated**: October 2025
**Version**: 1.0.0
