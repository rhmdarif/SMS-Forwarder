---
name: Modern Utility
colors:
  surface: '#fcf8ff'
  surface-dim: '#dcd8e5'
  surface-bright: '#fcf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f2ff'
  surface-container: '#f0ecf9'
  surface-container-high: '#eae6f4'
  surface-container-highest: '#e4e1ee'
  on-surface: '#1b1b24'
  on-surface-variant: '#464555'
  inverse-surface: '#302f39'
  inverse-on-surface: '#f3effc'
  outline: '#777587'
  outline-variant: '#c7c4d8'
  surface-tint: '#4d44e3'
  primary: '#3525cd'
  on-primary: '#ffffff'
  primary-container: '#4f46e5'
  on-primary-container: '#dad7ff'
  inverse-primary: '#c3c0ff'
  secondary: '#4648d4'
  on-secondary: '#ffffff'
  secondary-container: '#6063ee'
  on-secondary-container: '#fffbff'
  tertiary: '#7e3000'
  on-tertiary: '#ffffff'
  tertiary-container: '#a44100'
  on-tertiary-container: '#ffd2be'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e2dfff'
  primary-fixed-dim: '#c3c0ff'
  on-primary-fixed: '#0f0069'
  on-primary-fixed-variant: '#3323cc'
  secondary-fixed: '#e1e0ff'
  secondary-fixed-dim: '#c0c1ff'
  on-secondary-fixed: '#07006c'
  on-secondary-fixed-variant: '#2f2ebe'
  tertiary-fixed: '#ffdbcc'
  tertiary-fixed-dim: '#ffb695'
  on-tertiary-fixed: '#351000'
  on-tertiary-fixed-variant: '#7b2f00'
  background: '#fcf8ff'
  on-background: '#1b1b24'
  surface-variant: '#e4e1ee'
typography:
  display:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 48px
  max-width: 1280px
---

## Brand & Style

The design system is engineered for high-performance notification management, targeting power users who value efficiency and clarity. The brand personality is "The Sophisticated Tool"—it feels technical and precise like a developer environment, yet remains accessible and friendly through soft edges and generous whitespace. 

The visual style blends **Minimalism** with **Modern Corporate** influences. It prioritizes information density without sacrificing legibility. Every pixel serves a functional purpose, using high contrast and systematic alignment to facilitate rapid scanning of high-volume data. The emotional response is one of calm control over digital noise.

## Colors

The palette is anchored by a vibrant Indigo primary, used strategically for actions and brand presence. The background utilizes a subtle off-white to reduce screen glare during extended use, while pure white is reserved for high-priority card surfaces to create distinct visual separation.

Status colors (Green, Red, Amber) follow standard semantic patterns but are slightly desaturated to maintain the professional utility aesthetic. Text hierarchy is established through "Deep Slate" for high-contrast readability and "Muted Slate" for secondary metadata, ensuring the most critical information is processed first.

## Typography

This design system utilizes **Inter** exclusively to ensure maximum legibility across different resolutions. The hierarchy is highly structured: bold headings establish the content architecture, while distinct, uppercase labels are used for technical metadata (e.g., timestamps, notification sources, or priority tags).

For mobile devices, the `display` and `headline-lg` sizes should scale down by 15% to maintain screen real estate, while body text remains consistent at 14-16px for comfortable reading. Line heights are generous to prevent visual clutter in data-heavy views.

## Layout & Spacing

The layout philosophy follows a **Fluid Grid** model with an 8px base unit. In utility-heavy views, density is increased using the 4px "xs" unit. 

- **Mobile:** Single column layout with 16px side margins. Cards span the full width of the viewport minus margins.
- **Desktop:** 12-column grid system with a maximum container width of 1280px. Gutters are fixed at 16px to maintain a compact, technical feel.
- **Scanning:** Content follows a strict vertical rhythm. Related notification elements (icon, title, time) should be grouped with 8px spacing, while distinct notification cards are separated by 12px or 16px.

## Elevation & Depth

Visual hierarchy in this design system is achieved through **Tonal Layers** supplemented by **Ambient Shadows**.

1.  **Level 0 (Background):** #F9FAFB. Used for the app canvas.
2.  **Level 1 (Cards/Surfaces):** Pure #FFFFFF. These elements use a subtle 1px border (#E5E7EB) and a soft, low-opacity shadow (Offset 0 2px, Blur 4px, 5% opacity Black) to appear slightly lifted.
3.  **Level 2 (Modals/Popovers):** Pure #FFFFFF with a more pronounced, diffused shadow (Offset 0 10px, Blur 20px, 10% opacity Black) to indicate a temporary overlay.

Avoid heavy blurs or glassmorphism to keep the interface feeling snappy and high-performance.

## Shapes

The shape language is defined by a consistent 16px (1rem) corner radius for primary containers and cards. This large radius softens the "technical" nature of the app, making it feel modern and approachable. 

- **Primary Cards:** 16px (rounded-lg).
- **Secondary Buttons & Inputs:** 8px (rounded-md) to maintain a sense of precision for interactive elements.
- **Chips/Status Badges:** Fully rounded (pill) to distinguish them from structural elements.

## Components

- **Buttons:** Primary buttons use the Indigo background with white text. Ghost buttons use Slate text and a subtle 1px border.
- **Notification Cards:** Use 16px padding. Icons should be 24x24px minimalist line style, placed in a 40x40px rounded container with a light tint of the status color.
- **Input Fields:** 8px radius with a 1px #D1D5DB border. On focus, the border transitions to Indigo with a subtle 3px outer glow in 10% opacity Indigo.
- **Status Chips:** Small, pill-shaped badges using 10% opacity of the status color for the background and the full-strength color for text.
- **Lists:** Notification lists should use subtle dividers (#F3F4F6) only when necessary; otherwise, use 8px of vertical spacing between cards to create separation.
- **Priority Indicators:** Use a vertical 4px bar on the far left edge of a card to denote urgency (Error/Warning colors).