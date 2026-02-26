# GitHub Actions Workflows

This document describes the CI/CD workflows configured for BoomingMusic.

## 📋 Available Workflows

### 1. **Android CI** (`android.yml`)
**Trigger:** Push or Pull Request to `master` or `stable` branches

**Jobs:**
- **check** (20 min timeout)
  - Kotlin compilation
  - Lint checks (fails on warnings)
  - Uploads lint reports as artifacts

- **build** (30 min timeout)
  - Builds Debug APK for both variants (normal/fdroid)
  - Verifies APK exists
  - Uploads APK artifacts for PRs

**Features:**
- ✅ Concurrent build cancellation (prevents resource waste)
- ✅ Gradle caching (faster builds)
- ✅ Java 21 (Zulu distribution)
- ✅ Both flavor variants tested

---

### 2. **Strict CI - Quality Gate** (`strict-ci.yml`)
**Trigger:** Push or Pull Request to `master` or `stable` branches

**Jobs:**

| Job | Timeout | Description |
|-----|---------|-------------|
| `kotlin-compile-check` | 20 min | Strict Kotlin compilation with stack traces |
| `lint-strict` | 30 min | Lint that fails on ANY warning |
| `build-debug` | 30 min | Debug APK for both variants |
| `code-quality` | 20 min | Dependency updates & project reports |
| `build-release` | 30 min | Release APK (unsigned) for both variants |
| `quality-gate-summary` | - | Final pass/fail summary |

**Quality Gate Rules:**
- ❌ Fails if Kotlin compilation has errors
- ❌ Fails if lint has ANY warnings
- ❌ Fails if debug build fails
- ✅ Code quality checks are informational (don't block)
- ✅ Release build must succeed

**Artifacts:**
- Lint reports (HTML + XML)
- Debug APKs (retained 14 days)
- Release APKs unsigned (retained 14 days)
- Mapping files (retained 7 days)

---

### 3. **Build Release APK** (`release.yml`)
**Trigger:** Git tag push (e.g., `v1.2.2`)

**Jobs:**
- **build**: Builds signed release APKs for both variants
- **publish-release**: Creates GitHub Release with APKs

**Requirements:**
- `SIGNING_KEY` secret (base64 encoded keystore)
- `SIGNING_PROPERTIES` secret (base64 encoded properties)

---

## 🚀 How to Use

### For Contributors:
1. Create a pull request
2. Wait for CI checks to pass
3. Download test APKs from artifacts (if needed)

### For Maintainers:
1. Review PR with passing CI
2. Merge to master
3. Create tag for release: `git tag v1.2.2 && git push origin v1.2.2`

### Manual Trigger:
Go to **Actions** tab → Select workflow → **Run workflow**

---

## ⚙️ Configuration

### Java Version
All workflows use **Java 21 (Zulu)** for compatibility with Gradle 8.13+

### Memory Settings
```bash
_JAVA_OPTIONS: "-Xmx4g"
```

### Timeouts
- Compilation: 20 minutes
- Lint: 30 minutes
- Build: 30 minutes

### Caching
Gradle dependencies are cached between runs for faster builds.

---

## 📊 Understanding Results

### ✅ Success
All jobs completed without errors. APKs available in artifacts.

### ⚠️ Warnings
Lint warnings will **fail** the build. Check lint reports.

### ❌ Failure
1. Click on the failed job
2. Expand the step that failed
3. Review error message
4. Download artifacts for detailed reports

---

## 🔧 Troubleshooting

### Build fails with "Java 17+ required"
Ensure the workflow is using Java 21 (check `setup-java` step).

### Lint fails but code is correct
- Run `./gradlew lintDebug` locally to see details
- Fix warnings or add `@SuppressLint` if appropriate

### APK not found
Check the variant name in the path:
- `app/build/outputs/apk/normal/debug/`
- `app/build/outputs/apk/fdroid/debug/`

### Workflow not running
1. Go to **Actions** tab
2. Enable workflows if this is a new fork
3. Check branch name (must be `master` or `stable`)

---

## 📝 Best Practices

1. **Always run lint locally** before pushing:
   ```bash
   ./gradlew lintDebug
   ```

2. **Test both variants** if your changes affect permissions:
   ```bash
   ./gradlew assembleNormalDebug assembleFdroidDebug
   ```

3. **Check artifact size** - large APKs may indicate issues

4. **Review lint reports** even if CI passes

---

## 🔐 Secrets Required

For release workflow (`release.yml`):

| Secret | Description | Format |
|--------|-------------|--------|
| `SIGNING_KEY` | Keystore file | Base64 encoded `.jks` |
| `SIGNING_PROPERTIES` | Signing config | Base64 encoded `keystore.properties` |

### Creating Secrets:
```bash
# Encode keystore
base64 -w 0 booming_keystore.jks | pbcopy

# Encode properties
base64 -w 0 keystore.properties | pbcopy
```

Then paste into GitHub → Settings → Secrets and variables → Actions

---

## 📈 Future Improvements

- [ ] Add automated testing (unit tests, instrumentation tests)
- [ ] Add dependency vulnerability scanning
- [ ] Add automated Play Store upload
- [ ] Add beta release channel
- [ ] Add changelog generation
