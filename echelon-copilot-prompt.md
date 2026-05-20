# Echelon — Copilot Prompt Pack

A daily blind-ranking game. Desktop-only for the MVP. Mobile and per-item pictures are on the roadmap, not in v1.

---

## The initial scaffold prompt

Paste this into Copilot Chat (agent mode preferred) as a single message, in an empty repo opened in VS Code.

```
Build a Next.js 14 web app (App Router, TypeScript, Tailwind CSS, Framer Motion) called "Echelon" — a daily blind-ranking game in the visual mold of Wordle: clean, minimal, generous whitespace, subtle animations, one primary action visible at any time.

GAME LOOP
Each day, the player is shown 8 "things" one at a time, drawn from a curated pool. The set of 8 is deterministic for the day (same for everyone, derived from today's UTC date). Items are deliberately incommensurable — e.g., "chocolate milk", "the number 4", "Tuesday", "75°F", "the smell of rain", "your high school gym". The player ranks them by personal preference.

Mechanic — one-at-a-time insertion:
1. First item appears and takes position 1 by default.
2. Each subsequent item appears in a "current item" card at the top. The player chooses where it slots into the existing list using clickable "insert here" affordances between rows (no drag-and-drop in v1 — buttons are simpler, accessible, and feel cleaner).
3. Once placed, an item cannot be moved. That's the "blind" part — you commit before seeing what's next.
4. After all 8 are placed, the completion screen reveals their final ranking and offers a shareable result.

DATA (extensible toward future pictures)
- `data/items.json` is an array. Each entry is either:
    - a plain string like "chocolate milk", OR
    - an object: { "text": "chocolate milk", "imageUrl": "/items/chocolate-milk.jpg" }
  Normalize to { text, imageUrl? } on load. The MVP only uses `text` — `imageUrl` is reserved for a future enhancement so I can promote individual entries without re-formatting the file.
- Seed `items.json` with ~80 plain-string items spanning food, numbers, days of the week, weather, sensations, places, abstract concepts, and pop-culture flotsam. Aim for fun, ambiguous things that don't belong in the same category.
- Today's set: hash the UTC date (YYYY-MM-DD) with a stable seed, use it to deterministically shuffle the pool, take the first 8. Same date = same items everywhere.

PER-ITEM COLOR ASSIGNMENT (drives the share card)
- Each of today's 8 items is assigned a unique color from a fixed 8-color palette of emoji squares:
    🟥 🟧 🟩 🟦 🟪 🟫 ⬜ ⬛
  (No yellow — it's low-contrast on light backgrounds.)
- The mapping {item → color} is deterministic for the day (shuffle the palette using the same date seed), so every player on the same day sees the same color for the same item.
- The color-to-item mapping is NEVER shown in the shareable text. Inside the game, show a small color swatch next to each item in the ranked list so the player can recognize their own ranking when they see the share row later. The current-item card also shows the item's color as a subtle accent (e.g., a 6px colored bar at the bottom of the card).

VISUAL POLISH (Wordle-level discipline — this is a top-level constraint, not a nice-to-have)
- Treat Wordle as the reference. The product should look quiet and confident.
- Type: ONE sans-serif (Inter, loaded via next/font, or system font if Inter unavailable). One large display size for the current item (~clamp 2rem–2.75rem), one body size (~1rem), one small label size (~0.875rem). One weight for display (600), one for body (400).
- Layout: a centered column, max-width 640px. Header, current item, ranked list, and completion screen all share the same column. No sidebars, no nav chrome beyond a thin top strip with the wordmark and date.
- Color outside the per-item palette: a near-black foreground (#111827) and a near-white background (#FAFAF9) in light mode; inverted in dark mode via `prefers-color-scheme`. ONE accent color (a calm slate or muted indigo) for buttons and links. The emoji squares are the color of the page — everything else stays restrained.
- Borders: 1px, very low contrast. Corners: rounded-xl (12px). Shadows: none or barely-there (e.g., `shadow-sm` on the current-item card only).
- Subtle animations (use Framer Motion):
    - Current item: fade + scale-from-0.96 in over 200ms when it appears.
    - On placement: items below the chosen slot slide down (250ms, ease-out) to make room; the just-placed item lands with a gentle fade.
    - 300ms pause after placement before the next current item enters — gives the placement a beat to register.
    - Progress dots: the filled dot animates with a soft scale-in.
    - Completion: the 8-square share row pops in one square at a time, left to right, 90ms stagger.
- No bouncing, no big shadows, no gradients. If you reach for "fun" — stop. Wordle is fun without trying.

UI STRUCTURE (desktop only for MVP — assume viewport ≥ 1024px; don't spend effort on touch or narrow screens, but don't actively break either)
- Top strip: "Echelon" wordmark on the left, "May 19, 2026 · Item 3 of 8" on the right. Thin border-bottom.
- Current item: large card with the item text centered, color accent bar at bottom, subtle shadow.
- Ranked list below: numbered rows. Each row shows position number, item text, color swatch on the right. While placing, an "Insert here" affordance appears between each row and at the top/bottom — render as a hairline divider that thickens and shows a small "+ Insert here" label on hover.
- Progress dots above or below the ranked list: 8 dots, filled as items are placed.
- Completion screen replaces the game in-place: heading "Echelon — May 19", the 8-square share row prominently centered with the pop-in animation, the final labeled ranking below, "Copy" button, "Come back tomorrow" footnote.

SHARE CARD (color-coded, spoiler-free, single row)
- The share is intentionally minimal: a single row of 8 emoji squares representing the player's FINAL RANKING from position 1 to position 8, each rendered in that item's assigned color. The item-to-color mapping is never shown, so readers cannot decode what any color represents until they play themselves.
- Exact text format (preserve the blank lines, do not add anything):

    Here's my Echelon! [May 19]

    🟩🟦🟪🟥🟧⬜🟫⬛

    Make your ranking!
    Echelon.gg

  (The example row is illustrative — the actual row reflects today's color assignments and the player's ranking.)
- Date format inside the brackets: month name + day, no year (e.g., "May 19", "Jul 4").
- "Copy to clipboard" button on the completion screen. After click, briefly swap the button label to "Copied!" for 1.5s.

FILES TO CREATE
- `app/page.tsx` — page shell, renders <RankingGame />
- `app/layout.tsx` — base layout + metadata + favicon + Inter font
- `components/RankingGame.tsx` — state machine for the game (useReducer)
- `components/RankedList.tsx` — list + "insert here" slots, keyboard accessible
- `components/CurrentItemCard.tsx`
- `components/ProgressDots.tsx`
- `components/CompletionScreen.tsx` — share row + labeled ranking + copy button
- `lib/dailySet.ts` — deterministic item selection AND color assignment for a given date
- `lib/share.ts` — builds the share text from final ranking + color assignment; clipboard helper
- `lib/items.ts` — loads + normalizes `data/items.json` (string or object → { text, imageUrl? })
- `data/items.json` — seed pool (~80 plain strings)
- `tailwind.config.ts`, `app/globals.css` — Tailwind, color tokens, font setup
- `README.md` — how to run, how to add items, how the daily seed works

CODE QUALITY
- TypeScript strict mode on.
- No state libraries beyond React — `useReducer` for the game.
- Game state:
    type Item = { text: string; imageUrl?: string };
    type State = {
      pool: Item[];              // today's 8 items in the order they will appear
      itemColors: string[];      // emoji square per item, parallel to pool
      placedItems: Item[];       // current ranking
      currentIndex: number;      // index into pool of the item being placed
      finished: boolean;
    };
- Persist progress in `localStorage` keyed by date so reloads don't reset.
- Accessibility: "insert here" buttons must be keyboard-navigable (Tab + Enter). The current item announces itself via aria-live.
- Split components when they exceed ~150 lines.

DO NOT include yet:
- Mobile responsiveness, touch interactions, narrow-screen layouts
- Per-item images (the data shape supports them; rendering does not yet)
- Accounts, sign-in
- Global "crowd average" rankings, leaderboards
- Streaks
- Any backend or database
- A drag-and-drop library

When you finish: list the install + run commands, and confirm what to test manually.
```

---

## Why this prompt works

Six things make Copilot reliable instead of generic:

1. **A named mechanic with rules.** "Blind ranking" is ambiguous on its own. "One-at-a-time insertion, no undo, 8 items" is not.
2. **A visual reference (Wordle) as a top-level constraint.** Without it Copilot will produce a generic dashboard look. Saying "treat Wordle as the reference" anchors typography, density, and animation choices.
3. **An explicit file tree.** Otherwise logic scatters across whichever files it generated first.
4. **A concrete state shape, written in TypeScript.** Pinning the reducer down up front avoids the rewrite later.
5. **A negative list.** Mobile, images, accounts, streaks, backend, drag-and-drop are explicitly out — stops MVP-bloat.
6. **A seed strategy.** Deterministic daily selection (items AND colors) from the date means you ship before any backend, and friends' shares are comparable without spoilers.

---

## Follow-up prompts (paste one at a time, in order)

Once the scaffold runs and you've played a few rounds:

**1. Category diversity in the daily set.**
> Promote `data/items.json` entries from plain strings to objects with an added `category` field. Categories: food, number, day, weather, sensation, place, concept, media. Update `dailySet.ts` so each day's 8 must include at least 5 distinct categories — reshuffle if a candidate set violates that. Keep the string-or-object permissive loader for entries that don't have a category yet.

**2. PNG share card.**
> Add a "Download image" button next to "Copy" on the completion screen. Render the share text + the 8-square row as a 1200×630 PNG using HTML Canvas (no external image library, OG-image proportions). Use these hex values for the squares: 🟥 #DC2626, 🟧 #EA580C, 🟩 #16A34A, 🟦 #2563EB, 🟪 #9333EA, 🟫 #92400E, ⬜ #E5E7EB, ⬛ #1F2937. Match the in-app fonts and spacing.

**3. Per-item images.**
> Wire up the `imageUrl` field that already exists in the data shape. When present, render a 200×200 image with rounded corners + 1px ring above the item text in the current-item card, and a 40×40 thumbnail to the left of the item text in the ranked list. Items without `imageUrl` continue to show text only. Don't change the share card. Use Next.js `<Image>`. Store images in `public/items/`.

**4. Local history.**
> Add `/history` listing every date the player has finished locally (from localStorage), with the ranking and items for each day. Small "History" link in the footer of `/`.

**5. Mobile.**
> Make the layout work on phones (≥360px viewport). The "insert here" buttons need to be tap-friendly — increase to min-height 44px on touch. The current-item card scales down to fit the viewport. Test in Chrome devtools mobile emulation.

**6. First backend step.**
> Add Supabase. One `submissions` table: `date date`, `anon_id uuid`, `ranking text[]`. After a player finishes, POST their ranking with an anonymous cookie ID. On the completion screen, fetch the average position of each item among today's submissions and show "Your rank vs. Crowd" below the labeled ranking. No auth — just the anon cookie.

**7. Auth + streaks.**
> Add Supabase Auth with magic links (optional — anonymous play still works). Compute a streak from a signed-in user's completed dates. Show streak on the home screen and append to the share text as " · {N}-day streak".

**8. Editorial layer.**
> Add an admin-only `/curator` page (gated by an env var token) that lets me preview the next 14 days of daily sets, swap any item, and pin specific items to specific dates. Store overrides in a Supabase table that `dailySet.ts` consults before falling back to the seeded shuffle.

---

## Working with Copilot effectively

- **Always tell it the stack and the file structure.** Vagueness produces generic React.
- **Paste actual errors, not paraphrases.** "Build fails with `Type 'undefined' is not assignable…` in RankedList.tsx line 42" gets a fix; "it's broken" gets a guess.
- **For visuals, anchor on a reference.** "Like Wordle's completion screen." "Like Apple Settings rows." "Like the NYT Connections result view."
- **Commit between prompts.** Every successful step is a checkpoint. When Copilot rewrites something it shouldn't, `git reset --hard` and re-prompt with more constraints.
- **Resist asking for tests until the game is playable.** Then add tests for `dailySet.ts` (determinism of items AND color assignment) and `share.ts` (text generation) — those are the bits where regressions are hard to spot by playing.

---

## Things to think about as you grow it

- **The item pool is the product.** Tone, surprise, and ambiguity matter more than feature count. Budget time to curate.
- **Daily cadence creates pressure.** Once people show up daily, a bug on launch day is felt 10× harder. Add a `?date=YYYY-MM-DD` query param early so you can preview future days locally.
- **A leaderboard of "most controversial items" or "what the crowd disagreed about most today"** is a great hook later — the disagreement is the content.
- **Don't add accounts until you have a reason.** Streaks are a reason. Cross-device history is a reason. "Because every site has them" is not.
- **The 8-color palette caps items-per-day at 8.** If you ever want 9–12, you'll need a richer color set (circles 🔴🟠🟡🟢🔵🟣🟤⚫⚪) or numbered glyphs in the squares.
- **Images change the game meaningfully.** Once you add pictures, the game stops being purely-textual and starts to feel like a Pinterest board. Worth designing the picture rollout carefully — maybe images for some items but not others, or images that reveal only after placement.
