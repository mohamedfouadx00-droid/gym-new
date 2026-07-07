# PROJECT HANDOFF — GYM

## 0. المرحلة الحالية

**Phase: 01C — Dependency Injection Foundation**
(المرحلتان السابقتان **01A — Project Bootstrap** و **01B — Navigation Foundation**
مغلقتان رسميًا وناجحتان عبر GitHub Actions — التفاصيل في القسم 6)

---

## 1. نظرة عامة على المشروع

**GYM** هو تطبيق Android مخطط له أن يكون **منتجًا عامًا قابلًا للنشر**، **باللغة العربية
فقط** (Arabic-only, RTL). الفكرة الأساسية للمنتج الكامل مستقبلًا تعتمد على:

- **User Profile** (ملف المستخدم)
- **Goal** (الهدف الرياضي/الصحي)
- **Preferences** (تفضيلات المستخدم)

⚠️ هذه المحاور الثلاثة **لم تُنفَّذ بعد** — ستأتي في مرحلة مخصصة لاحقة (Phase 01D وما بعدها،
انظر القسم 9).

---

## 2. Tech Stack

| المكوّن | الإصدار / التفصيل |
|---|---|
| اللغة | Kotlin 1.9.24 |
| UI Toolkit | Jetpack Compose (BOM 2024.09.00) |
| Navigation | androidx.navigation:navigation-compose:2.7.7 (منذ 01B) |
| Dependency Injection | Hilt 2.51.1 (جديد في 01C) |
| Android Gradle Plugin (AGP) | 8.5.2 |
| Gradle | 8.7 (عبر Gradle Wrapper) |
| Java Toolchain | Java 17 |
| compileSdk / targetSdk | 34 |
| minSdk | 24 |
| Compose Compiler Extension | 1.5.14 |

المشروع لا يزال خاليًا من: Room, DataStore, AppCompat, Material Components الكلاسيكية.

---

## 3. معمارية التنقل (Navigation Architecture) — من 01B، لم تتغيّر

### 3.1 تعريف الـ Routes

الملف: `app/src/main/java/com/gym/app/navigation/Routes.kt`

`sealed class Routes(val route: String)` مع خمسة routes: `Start, Home, Workout, Progress,
Settings`. لم يتغيّر شيء هنا في 01C.

### 3.2 AppNavHost

الملف: `app/src/main/java/com/gym/app/navigation/AppNavHost.kt` — لم يتغيّر في 01C.
`NavHost` مركزي واحد، بداية التنقل `Routes.Start.route`.

### 3.3 مسؤولية MainActivity

الملف: `app/src/main/java/com/gym/app/MainActivity.kt`

لا يزال **نقطة دخول فقط**: يطبّق `GymTheme` ويستدعي `AppNavHost()`. الإضافة الوحيدة في 01C
هي annotation واحدة: `@AndroidEntryPoint` (مطلوبة من Hilt لتفعيل حقن الاعتماديات داخل
الـ Activity وأي Composable تابع لها عبر `hiltViewModel()`). لا يوجد أي منطق Feature جديد.

### 3.4 الشاشات (Screens)

كل الشاشات لا تزال شاشات اختبار تنقل تقنية مؤقتة. التعديل الوحيد في 01C هو في
`StartScreen.kt` (انظر القسم 4.4).

---

## 4. معمارية الحقن (Dependency Injection Architecture) — جديد في 01C

### 4.1 نظرة عامة

تم إضافة أساس Hilt نظيف يهيّئ المشروع لاستقبال Repositories وDatabase وDAOs وDataStore
وUse Cases لاحقًا، دون تنفيذ أي منها الآن. الهدف الوحيد لهذه المرحلة: إثبات أن السلسلة
الكاملة تعمل: **Application injection → Activity injection → ViewModel injection**، مع بقاء
التنقل (01B) يعمل والمشروع قابل للـ build.

### 4.2 Gradle Setup

- **Root** `build.gradle.kts`: أُضيف `id("org.jetbrains.kotlin.kapt") version "1.9.24" apply
  false` و `id("com.google.dagger.hilt.android") version "2.51.1" apply false`.
- **App** `app/build.gradle.kts`:
  - تطبيق البلجنز: `org.jetbrains.kotlin.kapt`, `com.google.dagger.hilt.android`.
  - Dependencies جديدة:
    - `com.google.dagger:hilt-android:2.51.1`
    - `kapt("com.google.dagger:hilt-android-compiler:2.51.1")`
    - `androidx.hilt:hilt-navigation-compose:1.2.0` (لدعم `hiltViewModel()` مع Navigation
      Compose)
    - `androidx.lifecycle:lifecycle-runtime-compose:2.8.4` (لدعم
      `collectAsStateWithLifecycle()`)
  - إضافة block: `kapt { correctErrorTypes = true }` (مطلوب من Hilt).
  - `versionName` تم رفعه إلى `"0.3.0-phase01c"`.

**سبب اختيار Hilt 2.51.1 تحديدًا:** آخر خط إصدارات Hilt المبني بشكل كامل حول `kapt` (وليس
K2/KSP فقط)، وهو متوافق تمامًا مع Kotlin 1.9.24 و AGP 8.5.2 الحاليين — لا حاجة لأي ترقية غير
متعلقة بهذه المرحلة.

### 4.3 Application Class

الملف: `app/src/main/java/com/gym/app/GymApplication.kt` (جديد)

```kotlin
@HiltAndroidApp
class GymApplication : Application()
```

مسجّلة في `AndroidManifest.xml` عبر `android:name=".GymApplication"` على عنصر
`<application>`.

### 4.4 الاعتمادية التقنية (Technical Dependency)

الملف: `app/src/main/java/com/gym/app/core/di/AppInfoProvider.kt` (جديد)

```kotlin
@Singleton
class AppInfoProvider @Inject constructor() {
    fun getApplicationName(): String = "GYM"
    fun getTechnicalLabel(): String = "phase-01c-di-foundation"
}
```

لا تحتوي أي بيانات مستخدم أو ملف شخصي أو هدف أو وزن ثابت — فقط تسميات تقنية داخلية. **لم
يُنشأ أي Hilt Module لها**: بما أنها class ملموسة (concrete) بـ constructor مُعلَّم بـ
`@Inject` وبدون interface، يستطيع Hilt بناءها مباشرة دون أي module. إنشاء module هنا كان
سيمثّل تجريدًا (abstraction) غير ضروري.

### 4.5 ViewModel للتحقق

الملف: `app/src/main/java/com/gym/app/ui/screens/StartViewModel.kt` (جديد)

```kotlin
@HiltViewModel
class StartViewModel @Inject constructor(
    appInfoProvider: AppInfoProvider
) : ViewModel() {
    val uiState: StateFlow<StartUiState> = ...
}
```

يستقبل `AppInfoProvider` عبر الـ constructor فقط ليثبت أن حقن ViewModel يعمل. لا يحتوي أي
منطق ميزة حقيقي — فقط `StateFlow<StartUiState>` بسيط غير قابل للتغيير من الخارج.

### 4.6 الربط بالشاشة التقنية

الملف: `app/src/main/java/com/gym/app/ui/screens/StartScreen.kt` (مُعدَّل)

تمت إضافة:
```kotlin
viewModel: StartViewModel = hiltViewModel()
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

وسطر نص تقني صغير (سلسلة عربية جديدة `start_di_status` في `strings.xml`) يظهر فقط عندما
تكون حالة الحقن جاهزة، لإثبات أن البيانات تصل فعليًا من الـ ViewModel المحقون. لا يوجد أي
تحويل لبقية الشاشات إلى ViewModels — تمامًا كما طُلب.

### 4.7 كيف يعمل الحقن حاليًا (ملخص السلسلة الكاملة)

```
GymApplication (@HiltAndroidApp)
        ↓ ينشئ SingletonComponent
MainActivity (@AndroidEntryPoint)
        ↓ hiltViewModel() داخل StartScreen
StartViewModel (@HiltViewModel)
        ↓ يستقبل عبر الـ constructor
AppInfoProvider (@Singleton, @Inject constructor)
```

لا Repositories، لا Database، لا Use Cases بعد — البنية جاهزة لاستقبالها لاحقًا فقط.

---

## 5. قاعدة اللغة العربية فقط (Arabic-only Rule) — لم تتغيّر

كل نص ظاهر للمستخدم يأتي حصريًا من `res/values/strings.xml` عبر `stringResource(...)`. تمت
إضافة سلسلة عربية واحدة جديدة فقط (`start_di_status`) لعرض حالة الحقن التقني، ولا يوجد أي نص
إنجليزي ظاهر للمستخدم. لا يوجد `res/values-ar/` ولا أي دعم لتعدد اللغات.

---

## 6. دعم RTL — لم يتغيّر

`GymTheme` (من 01B) لا يزال يفرض `LocalLayoutDirection.Rtl` صراحة. لم يُمس شيء في هذا الجزء
خلال 01C.

---

## 7. حالة Build و GitHub Actions

### Phase 01A — Project Bootstrap
✅ **مغلقة رسميًا.** GitHub Actions نجح، تم توليد الـ Debug APK بنجاح.

### Phase 01B — Navigation Foundation
✅ **مغلقة رسميًا.** GitHub Actions نجح.

### Phase 01C — Dependency Injection Foundation (هذه المرحلة)
⏳ **لم يتم تشغيل Build فعلي محليًا.** بيئة التنفيذ الحالية (Sandbox) لا تملك اتصال إنترنت
ولا Android SDK ولا Gradle مثبت مسبقًا (نفس القيد من المرحلتين السابقتين، تم التحقق منه
فعليًا مرة أخرى).

**ما تم فعله بدلاً من Build فعلي (تحقق استاتيكي):**
- التحقق من صحة XML (`xmllint`) لـ `strings.xml` و `AndroidManifest.xml`
- مطابقة كل `R.string.*` مُستخدم مع كل تعريف في `strings.xml` (لا نقص)
- التحقق من توازن الأقواس `{ } ( )` في **كل** ملف Kotlin في المشروع (12 ملفًا)، بما فيها
  الملفات الجديدة
- مراجعة يدوية دقيقة لكل الـ imports، ترتيب البلجنز، وتوافق إصدارات Hilt/kapt/Kotlin/AGP
- التأكد أن `.github/workflows/build-apk.yml` **لم يُمس إطلاقًا**
- التأكد من عدم وجود `local.properties` أو أي أسرار/مفاتيح
- التأكد من عدم إضافة Room أو DataStore
- التأكد من عدم وجود أي بيانات مستخدم Hardcoded

**الحالة الحقيقية في `PROJECT_PHASE.md`:** `Status: Pending GitHub Validation` — التزامًا
بالصدق وعدم الادعاء بنجاح لم يحدث فعليًا محليًا. سيتم تأكيد النجاح بعد أول Push إلى main عبر
GitHub Actions.

---

## 8. الملفات المُضافة في هذه المرحلة (01C)

```
app/src/main/java/com/gym/app/GymApplication.kt                  (جديد)
app/src/main/java/com/gym/app/core/di/AppInfoProvider.kt         (جديد)
app/src/main/java/com/gym/app/ui/screens/StartViewModel.kt       (جديد)
```

## 9. الملفات المُعدَّلة في هذه المرحلة (01C)

```
build.gradle.kts (root)                — إضافة plugin kapt + Hilt Gradle plugin
app/build.gradle.kts                    — تطبيق البلجنز، dependencies Hilt/hilt-navigation-
                                           compose/lifecycle-runtime-compose، kapt block،
                                           تحديث versionName
app/src/main/AndroidManifest.xml        — إضافة android:name=".GymApplication"
app/src/main/java/com/gym/app/MainActivity.kt — إضافة @AndroidEntryPoint فقط
app/src/main/java/com/gym/app/ui/screens/StartScreen.kt — ربط بـ StartViewModel عبر
                                           hiltViewModel() + سطر نص تقني عربي واحد
app/src/main/res/values/strings.xml     — إضافة سلسلة عربية واحدة: start_di_status
PROJECT_PHASE.md, PROJECT_HANDOFF.md, NEXT_TASK.md — تحديث التوثيق
```

**لم يتم لمس:** `settings.gradle.kts`, `gradle.properties`, `gradle/wrapper/*`, `gradlew`,
`gradlew.bat`, `.gitignore`, `.github/workflows/build-apk.yml`, `app/proguard-rules.pro`,
أيقونة `ic_launcher.xml`, `themes.xml`, `Routes.kt`, `AppNavHost.kt`, `Theme.kt`, وأي من
شاشات Home/Workout/Progress/Settings.

---

## 10. قاعدة معمارية المنتج المستقبلية (مهم جدًا — لا تُنفَّذ الآن)

بنية المنتج الكاملة مستقبلًا تعتمد جوهريًا على:

**User Profile + Goal + Preferences**

هذا هو النموذج المركزي (Central Product Model). كل ميزة مستقبلية يجب أن **تقرأ** بياناتها
الخاصة بالمستخدم من هذا النموذج المركزي عبر طبقة الحقن (Hilt) التي أُسست في هذه المرحلة —
بدلاً من استخدام بيانات ثابتة معزولة أو حالة مستخدم مكررة في كل ميزة على حدة. أمثلة:

- **Workout** سيعتمد على: User Profile + Goal + Preferences
- **Nutrition** سيعتمد على: User Profile + Goal + Preferences
- **Recovery** سيعتمد على: User Profile + Preferences + سجل التمارين + سجل النوم + سجل الألم
- **Home** سيجمّع (aggregate) معلومات من ميزات مختلفة للمستخدم النشط الحالي

**لم يتم تنفيذ أي من هذه النماذج بعد** — لا Models، لا Database، لا حتى Interfaces. ستُبنى
في Phase 01D وما بعدها، منفصلة تمامًا عن أساس الحقن الحالي (01C).

---

## 11. ما لم يتم تنفيذه (بالتصميم — ممنوع حتى الآن)

- [x] ~~Hilt~~ — تم في 01C (أساس فقط، بدون Repositories حقيقية)
- [ ] Room / أي Database
- [ ] DataStore
- [ ] Onboarding
- [ ] User Profile (Model)
- [ ] Goal (Model)
- [ ] Preferences (Model)
- [ ] أي منطق Workout / Nutrition / Smart Assistant حقيقي
- [ ] Login / Cloud Sync
- [ ] دعم لغات متعددة أو تبديل لغة
- [ ] أي بيانات مستخدم وهمية (Fake user data) أو معلومات شخصية Hardcoded
- [ ] تشغيل Build فعلي ناجح داخل بيئة Sandbox (سيتم التحقق عبر GitHub Actions)
- [ ] أي عمل من Phase 01D

---

## 12. قواعد للمطوّر/المرحلة التالية (Rules for the Next Developer)

1. **لا تعيد بناء المشروع من الصفر.** هذا المستودع على GitHub هو مصدر الحقيقة الوحيد بين
   المراحل والجلسات المستقبلية.
2. **لا تحذف أو تستبدل** أي ميزة تعمل حاليًا (Gradle setup, Workflow, بنية التنقل, أساس
   الحقن) إلا إذا طُلب ذلك صراحة.
3. **لا تُدرِج أي بيانات مستخدم Hardcoded** في أي مكان — كل بيانات المستخدم الحقيقية يجب أن
   تأتي لاحقًا من النموذج المركزي (User Profile + Goal + Preferences) عبر طبقة الحقن، وليس
   من قيم ثابتة داخل الكود.
4. **كل نص ظاهر للمستخدم يجب أن يكون عربيًا فقط** عبر `res/values/strings.xml` — ممنوع أي
   نص Hardcoded داخل ملفات Kotlin.
5. **لا تُنشئ** `res/values-ar/strings.xml` أو أي مجلد لغة إضافي.
6. **لا تُنفِّذ نماذج المنتج (User Profile/Goal/Preferences) قبل مرحلتها المخصصة (01D وما
   بعدها).**
7. **GitHub يبقى مصدر الحقيقة (Source of Truth)** — لا تدّعِ نجاح Build إلا بعد تأكيد فعلي
   عبر GitHub Actions أو تنفيذ محلي حقيقي.
8. **لا تبدأ أي مرحلة تالية** (مثل 01D) دون طلب صريح من المستخدم.
