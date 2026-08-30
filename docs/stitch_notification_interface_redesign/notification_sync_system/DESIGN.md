---
name: Notification Sync System
colors:
  surface: '#f9f9ff'
  surface-dim: '#cfdaf2'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f0f3ff'
  surface-container: '#e7eeff'
  surface-container-high: '#dee8ff'
  surface-container-highest: '#d8e3fb'
  on-surface: '#111c2d'
  on-surface-variant: '#434655'
  inverse-surface: '#263143'
  inverse-on-surface: '#ecf1ff'
  outline: '#737686'
  outline-variant: '#c3c6d7'
  surface-tint: '#0053db'
  primary: '#004ac6'
  on-primary: '#ffffff'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#b4c5ff'
  secondary: '#516070'
  on-secondary: '#ffffff'
  secondary-container: '#d5e4f8'
  on-secondary-container: '#576676'
  tertiary: '#4e565b'
  on-tertiary: '#ffffff'
  tertiary-container: '#666f74'
  on-tertiary-container: '#e9f2f8'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#d5e4f8'
  secondary-fixed-dim: '#b9c8db'
  on-secondary-fixed: '#0e1d2b'
  on-secondary-fixed-variant: '#3a4858'
  tertiary-fixed: '#dbe4ea'
  tertiary-fixed-dim: '#bfc8ce'
  on-tertiary-fixed: '#141d21'
  on-tertiary-fixed-variant: '#3f484d'
  background: '#f9f9ff'
  on-background: '#111c2d'
  surface-variant: '#d8e3fb'
typography:
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 12px
  md: 24px
  lg: 40px
  xl: 64px
  container-max: 1200px
  gutter: 24px
---

## Brand & Style

The brand personality of this design system is that of a "calm assistant"—reliable, quiet, and exceptionally clear. Designed for users who may feel overwhelmed by digital clutter, the interface prioritizes psychological safety through a "Soft Minimalist" aesthetic. 

The UI avoids technical complexity, opting instead for a friendly and human-centric approach. By utilizing generous whitespace and a "less is more" philosophy, the design system ensures that notification management feels like a relief rather than a chore. The emotional response should be one of order and tranquility, moving away from the "red-dot" anxiety typical of notification centers.

## Colors

This design system utilizes a high-clarity blue and white palette to evoke feelings of trust and cleanliness. 

- **Primary Blue:** Used for the most important actions and active states. It is calibrated for high contrast against white backgrounds to ensure accessibility.
- **Surface Tones:** A range of very soft, desaturated blues are used for container backgrounds and secondary buttons, preventing the interface from feeling "stark" while maintaining a bright, open feel.
- **Typography:** Deep slate is used instead of pure black to reduce eye strain while maintaining a high contrast ratio for legibility.
- **Semantic Colors:** Success (Green) and Error (Red) are used sparingly and are always accompanied by icons to ensure information is communicated through more than just color.

## Typography

This design system uses **Plus Jakarta Sans** for all levels of the hierarchy. Its soft, rounded terminals and open counters make it exceptionally approachable and legible for beginner users.

- **Headlines:** Set with slight negative letter-spacing and a bold weight to create a clear "anchor" for the eye.
- **Body Text:** Uses a generous line-height to prevent "walls of text," ensuring that instructions are easy to parse at a glance.
- **Labels:** Used for buttons and small metadata, these are slightly weighted to ensure they remain distinct from body copy despite their smaller size.
- **Clarity:** Avoid all-caps styling to maintain a conversational, friendly tone.

## Layout & Spacing

The layout philosophy follows a **Fluid Grid** approach with an emphasis on safe margins to create a "breathable" interface.

- **Rhythm:** An 8px base unit drives all spacing decisions, ensuring consistent alignment and a predictable visual cadence.
- **Margins:** Desktop views utilize a maximum container width of 1200px with centered alignment. Mobile views require a minimum 20px side margin to ensure content doesn't feel cramped.
- **Negative Space:** Large gaps (40px+) are intentionally placed between major functional sections to clearly separate distinct tasks, such as "Active Syncs" from "Recent History."
- **Mobile Reflow:** On smaller screens, horizontal lists or side-by-side cards must stack vertically to maintain large tap targets and legibility.

## Elevation & Depth

To maintain a non-technical feel, this design system avoids complex stacking or floating layers. Instead, it uses **Tonal Layers** and **Low-Contrast Outlines**.

- **Surface Tiers:** The main background is pure white. Secondary containers (like notification cards) use a subtle light blue tint (`#F0F9FF`) to define their boundaries without the use of heavy shadows.
- **Soft Shadows:** Only the "Primary Action" (e.g., the main Sync button) is permitted a shadow. This shadow should be highly diffused, using a 10% opacity of the Primary Blue color rather than black, creating a "glow" rather than a "lift."
- **Dividers:** Use very faint, 1px lines in light blue to separate list items, ensuring the UI remains structured but soft.

## Shapes

The shape language is consistently **Rounded**, which removes "visual friction" and makes the app feel more inviting.

- **Primary Elements:** Standard buttons and input fields utilize a 0.5rem (8px) radius.
- **Large Containers:** Cards and modals use a 1rem (16px) radius to emphasize their role as "friendly containers" for information.
- **Status Indicators:** Items like "On/Off" toggles or category tags should use the `rounded-xl` or pill-shaped setting to differentiate them from functional buttons.

## Components

### Buttons
Primary buttons are solid Primary Blue with white text. Secondary buttons use a light blue background with Primary Blue text. All buttons have a minimum height of 48px to ensure they are easy to tap for all users.

### Notification Cards
Cards are the heart of the app. They should feature a large, colorful icon on the left (e.g., a blue bell for general sync, a green check for success), a clear headline, and a single line of descriptive text. Actions inside cards should be visible only on hover or via a clear "Manage" label.

### Progress Indicators
Instead of technical bars or percentages, use "Step Indicators" with human labels (e.g., "Looking for notifications..." instead of "Scanning ports...").

### Toggles
Switch components should be larger than standard OS defaults, with a clear "On" (Primary Blue) and "Off" (Light Gray) state, accompanied by a text label like "Enabled" or "Paused."

### Input Fields
Fields should have a thick, 2px border in light blue when focused, providing clear visual feedback that the user is currently interacting with that element. Label text should always sit above the input, never as placeholder text alone.