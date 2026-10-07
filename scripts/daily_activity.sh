#!/usr/bin/env bash
# Builds a digest of everything that happened in this repo on one day
# (commits, issues/PRs opened or closed, comments by ANYONE, reviews,
# stars, forks) and commits it to activity-log.md.
# If nothing happened, it logs "no new activity" and points at the last change.
#
# Env: GH_TOKEN, GITHUB_REPOSITORY (set by Actions)
#      LOG_OFFSET (default +0530)   LOG_DAY (default: yesterday at that offset)
#      ACTIVITY_PAT  optional personal access token: adds an "Elsewhere on GitHub"
#                    section (your activity on other repos + others' comments on
#                    threads you're part of). Public repos only unless INCLUDE_PRIVATE=1
#                    - keep it 0 if this repo is public, or private-repo details leak.
#      DRY_RUN=1 prints the entry instead of committing
set -euo pipefail

: "${GH_TOKEN:?GH_TOKEN required}"
REPO="${GITHUB_REPOSITORY:?GITHUB_REPOSITORY required}"
OFFSET="${LOG_OFFSET:-+0530}"   # IST; fixed offset avoids tzdata differences
OFFSET_SECS=19800

day="${LOG_DAY:-$(date -u -d "@$(( $(date -u +%s) + OFFSET_SECS - 86400 ))" +%F)}"
start=$(date -u -d "$day 00:00 $OFFSET" +%Y-%m-%dT%H:%M:%SZ)
end=$(date -u -d "$day 00:00 $OFFSET + 1 day" +%Y-%m-%dT%H:%M:%SZ)
LOG="activity-log.md"

if [ -f "$LOG" ] && grep -qx "### $day" "$LOG"; then
  echo "Already logged $day"; exit 0
fi

api() { gh api --paginate "$@" 2>/dev/null || true; }
JQ_DEFS='def snip: (. // "") | gsub("[\\r\\n]+";" ") | if length>140 then .[0:140]+"…" else . end;
         def inwin: select(. >= $s and . < $e);'
jqr() { jq -r --arg s "$start" --arg e "$end" "$JQ_DEFS $1"; }

tmp=$(mktemp -d); trap 'rm -rf "$tmp"' EXIT

# Commits (skip our own automated ones)
api "repos/$REPO/commits?since=$start&until=$end&per_page=100" | jqr '
  .[] | select((.commit.message | startswith("chore(daily)")) | not)
  | "- 🔨 commit `\(.sha[0:7])` by @\(.author.login // .commit.author.name): \(.commit.message | split("\n")[0])"' >> "$tmp/out" || true

# Issues + PRs (the issues endpoint returns both)
api "repos/$REPO/issues?state=all&since=$start&per_page=100" | jq -s 'add // []' > "$tmp/issues.json"
jqr '.[] | (if .pull_request then "PR" else "issue" end) as $k
  | (select(.created_at | inwin) | "- 📝 @\(.user.login) opened \($k) #\(.number): \(.title)"),
    (select(.closed_at != null and (.closed_at | inwin)) | "- ✅ \($k) #\(.number) closed: \(.title)")' \
  < "$tmp/issues.json" >> "$tmp/out" || true

# Comments by anyone: issue/PR conversation + inline review comments
api "repos/$REPO/issues/comments?since=$start&per_page=100" | jqr '
  .[] | select(.created_at | inwin)
  | "- 💬 @\(.user.login) commented on #\(.issue_url | split("/") | last): \(.body | snip)"' >> "$tmp/out" || true
api "repos/$REPO/pulls/comments?since=$start&per_page=100" | jqr '
  .[] | select(.created_at | inwin)
  | "- 💬 @\(.user.login) review comment on PR #\(.pull_request_url | split("/") | last): \(.body | snip)"' >> "$tmp/out" || true

# PR reviews
for n in $(jq -r '.[] | select(.pull_request) | .number' "$tmp/issues.json"); do
  api "repos/$REPO/pulls/$n/reviews?per_page=100" | jqr --arg n "$n" '
    .[] | select(.submitted_at != null and (.submitted_at | inwin))
    | "- 🔍 @\(.user.login) \(.state | ascii_downcase | gsub("_";" ")) PR #'"$n"'" + (if (.body // "") != "" then ": \(.body | snip)" else "" end)' \
    >> "$tmp/out" || true
done

# Stars and forks
api -H "Accept: application/vnd.github.star+json" "repos/$REPO/stargazers?per_page=100" | jqr '
  .[] | select(.starred_at | inwin) | "- ⭐ @\(.user.login) starred the repo"' >> "$tmp/out" || true
api "repos/$REPO/forks?sort=newest&per_page=100" | jqr '
  .[] | select(.created_at | inwin) | "- 🍴 @\(.owner.login) forked the repo"' >> "$tmp/out" || true

# ---- Account-wide section (only when ACTIVITY_PAT is set) ----
: > "$tmp/ext"
if [ -n "${ACTIVITY_PAT:-}" ]; then
  pub_only=true;  scope="is:public"
  if [ "${INCLUDE_PRIVATE:-0}" = "1" ]; then pub_only=false; scope=""; fi
  pat() { GH_TOKEN="$ACTIVITY_PAT" gh api --paginate "$@" 2>/dev/null || true; }
  me=$(GH_TOKEN="$ACTIVITY_PAT" gh api user -q .login)

  # Your own actions on other repos
  pat "users/$me/events?per_page=100" | jq -r --arg s "$start" --arg e "$end" --arg me "$REPO" --argjson po "$pub_only" "$JQ_DEFS"'
    .[] | select(.created_at | inwin) | select(.repo.name != $me) | select(($po | not) or .public)
    | .repo.name as $r
    | if   .type == "PushEvent"                     then "- 🔨 pushed \(.payload.size // (.payload.commits // [] | length)) commit(s) to \($r)"
      elif .type == "IssueCommentEvent"             then "- 💬 you commented on \($r)#\(.payload.issue.number): \(.payload.comment.body | snip)"
      elif .type == "PullRequestReviewCommentEvent" then "- 💬 you left a review comment on \($r)#\(.payload.pull_request.number): \(.payload.comment.body | snip)"
      elif .type == "PullRequestReviewEvent"        then "- 🔍 you reviewed \($r)#\(.payload.pull_request.number)"
      elif .type == "PullRequestEvent"              then "- 📝 PR \(.payload.action) \($r)#\(.payload.pull_request.number): \(.payload.pull_request.title)"
      elif .type == "IssuesEvent"                   then "- 📝 issue \(.payload.action) \($r)#\(.payload.issue.number): \(.payload.issue.title)"
      elif .type == "WatchEvent"                    then "- ⭐ you starred \($r)"
      elif .type == "ForkEvent"                     then "- 🍴 you forked \($r)"
      else "- \(.type | sub("Event$";"")) on \($r)" end' >> "$tmp/ext" || true

  # Other people's replies/comments on threads you're involved in
  pat -X GET "search/issues" -f q="involves:$me $scope updated:>=$start" -f per_page=50 \
    | jq -r '(.items? // [])[] | "\(.repository_url | sub(".*/repos/";""))\t\(.number)"' | sort -u | head -50 \
    | while IFS=$'\t' read -r r n; do
        [ "$r" = "$REPO" ] && continue
        pat "repos/$r/issues/$n/comments?since=$start&per_page=100" | jq -r --arg s "$start" --arg e "$end" --arg me "$me" --arg r "$r" --arg n "$n" "$JQ_DEFS"'
          .[] | select(.created_at | inwin) | select(.user.login != $me)
          | "- 💬 @\(.user.login) commented on \($r)#\($n): \(.body | snip)"'
      done >> "$tmp/ext" || true
  sort -u -o "$tmp/ext" "$tmp/ext"
fi

touch "$tmp/out"

{
  echo "### $day"
  if [ -s "$tmp/out" ]; then
    cat "$tmp/out"
  else
    last=$(git log --no-merges --invert-grep --grep='^chore(daily)' -1 --format='%h %s' 2>/dev/null || true)
    if [ -n "$last" ]; then echo "- no new activity in this repo; last change was \`$last\`"
    else echo "- no activity recorded in this repo"; fi
  fi
  if [ -s "$tmp/ext" ]; then
    echo; echo "#### Elsewhere on GitHub"; cat "$tmp/ext"
  fi
  echo
} > "$tmp/entry"

n=$(cat "$tmp/out" "$tmp/ext" | grep -c '^- ' || true)
if [ "$n" -gt 0 ]; then msg="chore(daily): $day - $n activity item(s)"
else msg="chore(daily): $day - no new activity"; fi

if [ "${DRY_RUN:-0}" = "1" ]; then cat "$tmp/entry"; exit 0; fi

[ -f "$LOG" ] || printf '# Activity log\n\nAuto-generated daily digest of repo activity (days are IST, UTC%s).\n\n' "$OFFSET" > "$LOG"
cat "$tmp/entry" >> "$LOG"
git add -A
git commit -m "$msg"
git push
