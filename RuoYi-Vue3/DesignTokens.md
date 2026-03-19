Design Tokens - Monitor UI modernization (Phase 1 analysis)

Overview
- This document captures design tokens observed across the monitor pages (operlog, logininfor, online, job) and related UI elements in the project. It informs a centralized design system for consistent cyan/teal accents and modern business styling.

Colors (cyan/teal family)
- Teal primary accent: #06b6d4 (applied to primary actions on monitor pages)
- Hover/active teal tint: #0eaec3 (secondary hover state)
- Existing supportive colors observed in tokens (from design system):
  - color-primary defaults in Element Plus overrides: #409EFF (base, but overridden in monitor pages for cyan emphasis)
  - alternative cyan palette used in some components: #20B2AA (observed in ruoyi.scss as cyan-toned button states)

Typography and spacing (observed patterns)
- Form labels: bold label style with metrics around 68px label width in some components; form item labels appear heavier than body text for clarity.
- Table headers: consistent height around 40px; text color defaults to dark slate (#515a6e in many components or inherited from theme).
- Card and panel paddings: card padding around 12px-16px; table and form spacing follow ruoyi design system spacing utilities (mb8, mt5, etc.).

Borders and shadows
- Cards use subtle shadows (hover state) and rounded corners (12px) for a refined, production-ready feel.
- Borders use rgba teal hints on card borders to emphasize section separation without being intrusive.

Component usage notes
- Monitor pages now wrap content in an El Card with monitor-card class to apply consistent visuals.
- Primary actions (搜索/导出/删除等) are tinted with #06b6d4 via overrides to reinforce cyan/teal branding.

Recommendations
- Centralize tokens in a single SCSS file (e.g., src/design-tokens/_colors.scss, _typography.scss, _spacing.scss) and export to components via an import chain.
- Create a small utility mixin for cyan-teal primary buttons to ensure consistency across pages.
- If design tokens are extended to other views, follow the same card-based pattern and teal emphasis for primary actions.
