# PROJECT HANDOFF — GYM

## 0. المرحلة الحالية

**Phase: 01E — Room Database Foundation**
(المراحل السابقة **01A — Project Bootstrap**، **01B — Navigation Foundation**،
**01C — Dependency Injection Foundation**، و**01D — Core User Models** مغلقة رسميًا وناجحة عبر
GitHub Actions — التفاصيل في القسم 7)

---

## 1. نظرة عامة على المشروع

**GYM** هو تطبيق Android مخطط له أن يكون **منتجًا عامًا قابلًا للنشر**، **باللغة العربية
فقط** (Arabic-only, RTL). الفكرة الأساسية للمنتج الكامل مستقبلًا تعتمد على:

- **User Profile** (ملف المستخدم)
- **Goal** (الهدف الرياضي/الصحي)
- **Preferences** (تفضيلات المستخدم)

✅ اعتبارًا من Phase 01D، تم تعريف هذه المحاور الثلاثة كنماذج domain نقية (بدون تخزين، بدون
UI). ✅ اعتبارًا من Phase 01E، أصبح لهذه النماذج الثلاثة تخزين فعلي عبر Room (انظر القسم 4.9).
كل ميزة مستقبلية (Workout, Nutrition, Recovery, Home) يجب أن **تقرأ** بياناتها من هذه النماذج
المركزية عبر طبقة الحقن (Hilt، من 01C) بدلاً من بيانات ثابتة أو مكررة. راجع القسم 4 للتفاصيل
الكاملة.

---

## 2. Tech Stack

| المكوّن | الإصدار / التفصيل |
|---|---|
| اللغة | Kotlin 1.9.24 |
| UI Toolkit | Jetpack Compose (BOM 2024.09.00) |
| Navigation | androidx.navigation:navigation-compose:2.7.7 (منذ 01B) |
| Dependency Injection | Hilt 2.51.1 (منذ 01C) |
| Persistence | Room 2.6.1 (جديد في 01E) |
| Android Gradle Plugin (AGP) | 8.5.2 |
| Gradle | 8.7 (عبر Gradle Wrapper) |
| Java Toolchain | Java 17 |
| compileSdk / targetSdk | 34 |
| minSdk | 24 |
| Compose Compiler Extension | 1.5.14 |

المشروع لا يزال خاليًا من: DataStore, AppCompat, Material Components الكلاسيكية.

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

## 4.8 نماذج المستخدم المركزية (Core User Models) — جديد في 01D

### 4.8.1 نظرة عامة

تم إضافة الحزمة `app/src/main/java/com/gym/app/domain/model/` وتحتوي على النماذج المركزية
الثلاثة (User Profile + Goal + Preferences) بالإضافة إلى كل الأنواع المساعدة (enums وvalue
types) التي تحتاجها. هذه المرحلة **domain فقط**: لا تخزين (Room/DataStore)، لا UI، لا أي ربط
بـ Hilt بعد. الهدف الوحيد: تعريف الشكل النهائي للبيانات التي ستُبنى حولها كل الميزات المستقبلية.

### 4.8.2 الملفات والمسارات

كل الملفات التالية جديدة، داخل `app/src/main/java/com/gym/app/domain/model/`:

```
UserProfile.kt          — النموذج المركزي لملف المستخدم
Goal.kt                 — النموذج المركزي للهدف
UserPreferences.kt       — النموذج المركزي للتفضيلات (يحتوي أيضًا على ReminderPreferences)
Gender.kt                — enum
ExperienceLevel.kt       — enum
ActivityLevel.kt         — enum
PrimaryGoal.kt           — enum
WorkoutLocation.kt       — enum
BudgetLevel.kt           — enum
UnitSystem.kt            — enum
Weekday.kt               — enum (لأيام التمرين المفضلة/أيام الراحة)
TimeOfDay.kt             — value type (وقت اليوم: ساعة + دقيقة)، بديل مستقل عن أي إطار عمل
                           لتمثيل الأوقات (preferredWorkoutTime, sleepTime, wakeTime)
```

### 4.8.3 UserProfile

`data class UserProfile` يحتوي: `userId`, `name`, `age`, `gender` (nullable، لأنه مطلوب فقط
لبعض الحسابات المستقبلية)، `heightCm`, `currentWeightKg`, `experienceLevel`, `activityLevel`.
الطول والوزن يُخزَّنان بوحدات metric دائمًا كمصدر حقيقة داخلي واحد — تفضيل الوحدات
(`UserPreferences.unitSystem`) يؤثر فقط على طريقة **العرض** مستقبلًا، وليس على التخزين هنا.

يحتوي على تحقق (`init` block + `require`) لـ: `userId`/`name` غير فارغين، عمر ضمن مدى منطقي
(10–100)، طول ووزن موجبين وواقعيين. لا توجد قيم افتراضية شخصية — كل قيمة يجب تمريرها صراحة.

### 4.8.4 Goal

`data class Goal` يحتوي: `goalId`, `userId`, `primaryGoal` (enum من 6 قيم: Weight Gain, Weight
Loss, Muscle Building, Strength, Maintenance, General Fitness)، `targetWeightKg` (nullable)،
`goalStartDate`/`targetDate` (كـ epoch-day `Long`، لتجنّب اعتماد `java.time` أو أي نوع Android
داخل الـ domain)، و`secondaryGoals` (قائمة أهداف إضافية).

يحتوي دالة `isTargetWeightApplicable()` (ودالة مصاحبة في الـ companion object) توضّح صراحة أن
`targetWeightKg` منطقي فقط لأهداف معينة (Weight Gain/Loss/Muscle Building) وليس لكل الأهداف.
التحقق يشمل: IDs غير فارغة، `targetDate >= goalStartDate` عند وجودها، عدم تكرار `primaryGoal`
داخل `secondaryGoals`.

### 4.8.5 UserPreferences

`data class UserPreferences` يحتوي: `workoutLocation`, `preferredWorkoutDays`/`restDays` (كـ
`Set<Weekday>`, مع تحقق أنهما غير متقاطعين)، `preferredWorkoutTime` (`TimeOfDay?`),
`availableEquipment` (قائمة نصوص بسيطة — تصنيف كامل للمعدات مؤجّل لميزة Workout مستقبلًا),
`preferredMealCount` (بتحقق ضمن مدى منطقي)، `sleepTime`/`wakeTime` (`TimeOfDay?`),
`budgetLevel`, `enabledSupplements` (قائمة نصوص)، `reminderPreferences`
(`ReminderPreferences` — نموذج فرعي صغير لتفعيل/تعطيل كل نوع تذكير)، و`unitSystem`.

### 4.8.6 قواعد التصميم المتبعة في كل النماذج

- **Immutable بالكامل:** كل نموذج هو `data class` بخصائص `val` فقط — لا يوجد `var` واحد في
  كامل حزمة `domain.model`.
- **بدون أي اعتماد على Android/Room/DataStore/Hilt:** تم التحقق آليًا (بحث عن `import android`,
  `import androidx`, وكلمات Room/DataStore/Dagger/Hilt) — لا يوجد أي استخدام فعلي، فقط إشارات
  توثيقية داخل التعليقات تشرح هذا القيد نفسه.
- **بدون بيانات شخصية Hardcoded:** لا قيمة افتراضية لعمر/وزن/طول/اسم محدد. الأمثلة الوحيدة
  لقيم رقمية هي داخل ملفات الاختبار (`src/test`)، وهي بيانات اختبار وليست بيانات إنتاج.
- **جاهزة لدعم مستخدمين متعددين مستقبلًا:** كل نموذج يحمل `userId` (و`Goal` يحمل أيضًا
  `goalId` منفصل)، تمهيدًا لدعم ملفات محلية متعددة، تسجيل دخول، ومزامنة سحابية لاحقًا — دون أي
  تنفيذ فعلي لهذه الميزات الآن.
- **Value types عند الحاجة فقط:** `TimeOfDay` أُضيف كنوع صغير مستقل عن المنصة لتمثيل وقت اليوم
  (بدل الاعتماد على نوع Android أو java.time)، لكن لم تُنشأ عشرات الـ wrapper classes بدون
  فائدة حقيقية — بقية الحقول (مثل `availableEquipment`, `enabledSupplements`) بقيت كقوائم
  نصوص بسيطة لأن تصنيفها الكامل هو مسؤولية ميزة مستقبلية، وليس هذه المرحلة التأسيسية.

### 4.8.7 اختبارات الوحدة (Unit Tests) المُضافة

داخل `app/src/test/java/com/gym/app/domain/model/`:

```
UserProfileTest.kt       — قيم صالحة/غير صالحة للعمر والطول والوزن، nullable gender،
                           سلوك immutability عبر copy()
GoalTest.kt               — قيم صالحة/غير صالحة، isTargetWeightApplicable لكل الأهداف،
                           ترتيب التواريخ، تكرار primaryGoal في secondaryGoals
UserPreferencesTest.kt    — تقاطع أيام التمرين/الراحة، مدى عدد الوجبات، قيم زمنية nullable،
                           reminderPreferences الافتراضية
TimeOfDayTest.kt          — حدود الساعة/الدقيقة، ofOrNull()
```

لم تُضَف اختبارات بلا فائدة حقيقية (لا اختبارات "تعبئة" لمجرد زيادة العدد).

### 4.8.8 لم يتم تنفيذه في 01D (بالتصميم)

لا Room، لا DataStore، لا Entities، لا DAOs، لا Repositories، لا Mappers، لا أي UI
(Onboarding/Profile)، لا منطق Home/Workout/Nutrition/Recovery حقيقي، لا ربط Hilt لهذه
النماذج بعد (سيأتي عند تنفيذ الـ Repositories في مرحلة لاحقة بعد 01E).

---

## 4.9 أساس قاعدة بيانات Room (Room Database Foundation) — جديد في 01E

### 4.9.1 نظرة عامة

تمت إضافة تخزين فعلي عبر Room للنماذج المركزية الثلاثة (User Profile + Goal + Preferences)
المُعرَّفة في 01D. هذه المرحلة تخزين فقط: لا DataStore، لا Onboarding UI، لا Repositories
حقيقية، لا أي منطق ميزة. الهدف الوحيد: إثبات أن كل نموذج domain يمكن حفظه وقراءته بشكل صحيح
عبر SQLite (من خلال Room)، مع الحفاظ على النماذج نفسها نقية تمامًا دون أي Room annotation.

### 4.9.2 Gradle Dependencies

في `app/build.gradle.kts`:

- `androidx.room:room-runtime:2.6.1`
- `androidx.room:room-ktx:2.6.1`
- `kapt("androidx.room:room-compiler:2.6.1")`
- `androidx.room:room-testing:2.6.1` (androidTestImplementation، لاختبارات قاعدة البيانات
  المُدمَجة)
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3` (test و androidTest، لدعم `runTest`)

**سبب اختيار Room 2.6.1 تحديدًا:** آخر خط إصدارات Room المبني بشكل ناضج وموثّق حول `kapt`
(وليس K2/KSP فقط)، متوافق تمامًا مع Kotlin 1.9.24، AGP 8.5.2، وHilt 2.51.1 الحاليين — لا حاجة
لأي ترقية غير متعلقة بهذه المرحلة (لا لـ Kotlin، لا لـ AGP، لا لـ Hilt).

`versionName` تم رفعه إلى `"0.4.0-phase01e"`.

### 4.9.3 الملفات والمسارات

```
app/src/main/java/com/gym/app/data/local/entity/UserProfileEntity.kt       (جديد)
app/src/main/java/com/gym/app/data/local/entity/GoalEntity.kt              (جديد)
app/src/main/java/com/gym/app/data/local/entity/UserPreferencesEntity.kt   (جديد)
app/src/main/java/com/gym/app/data/local/converters/GoalTypeConverters.kt  (جديد)
app/src/main/java/com/gym/app/data/local/dao/UserProfileDao.kt             (جديد)
app/src/main/java/com/gym/app/data/local/dao/GoalDao.kt                    (جديد)
app/src/main/java/com/gym/app/data/local/dao/UserPreferencesDao.kt         (جديد)
app/src/main/java/com/gym/app/data/local/AppDatabase.kt                    (جديد)
app/src/main/java/com/gym/app/core/di/DatabaseModule.kt                    (جديد)
app/src/main/java/com/gym/app/data/mapper/UserProfileMapper.kt             (جديد)
app/src/main/java/com/gym/app/data/mapper/GoalMapper.kt                    (جديد)
app/src/main/java/com/gym/app/data/mapper/UserPreferencesMapper.kt         (جديد)

app/src/test/java/com/gym/app/data/mapper/UserProfileMapperTest.kt         (جديد)
app/src/test/java/com/gym/app/data/mapper/GoalMapperTest.kt                (جديد)
app/src/test/java/com/gym/app/data/mapper/UserPreferencesMapperTest.kt     (جديد)
app/src/androidTest/java/com/gym/app/data/local/AppDatabaseTest.kt         (جديد)
```

### 4.9.4 Entities

كل Entity يطابق نموذج الـ domain المقابل حقلاً بحقل، لكنه نوع منفصل تمامًا:

- **`UserProfileEntity`** — `@PrimaryKey val userId: String` (بدون autogenerate، لأن `userId`
  هو الهوية المستقرة للنموذج بالفعل). `gender`, `experienceLevel`, `activityLevel` تُخزَّن
  كـ `String` (اسم الـ enum عبر `.name`) بدل ordinal، لتفادي تلف البيانات المخزَّنة عند إعادة
  ترتيب أي enum مستقبلًا.
- **`GoalEntity`** — `@PrimaryKey val goalId: String`، مع `@Index` على `userId` (بدون foreign
  key صارم بعد — ذلك سيفرض قاعدة ملكية/عدد أهداف لكل مستخدم لم تُطلب في هذه المرحلة التأسيسية).
  `secondaryGoals` (`List<String>`) يُخزَّن كعمود نصي واحد مفصول بفواصل عبر
  `GoalTypeConverters`.
- **`UserPreferencesEntity`** — `@PrimaryKey val userId: String` (صف واحد لكل مستخدم حاليًا،
  يطابق استخدام النموذج الفعلي اليوم). المجموعات (`preferredWorkoutDays`, `restDays`,
  `availableEquipment`, `enabledSupplements`) تُخزَّن كأعمدة نصية مفصولة بفواصل. كل `TimeOfDay?`
  (`preferredWorkoutTime`, `sleepTime`, `wakeTime`) يُفكَّك إلى زوج `Int?` (`...Hour`/
  `...Minute`) بدل تشفيره كنص. `ReminderPreferences` (نموذج فرعي ثابت الشكل) يُفكَّك إلى 4
  أعمدة `Boolean` مباشرة بدل JSON.

### 4.9.5 DAOs

كل DAO يحتوي فقط على العمليات الأساسية المطلوبة للأساس التخزيني:

- `upsert(...)` — إدراج أو استبدال
- `getByUserId(...)` / `getByGoalId(...)` — قراءة مرة واحدة (`suspend`)
- `observeByUserId(...)` — مراقبة عبر `Flow`
- `deleteByUserId(...)` / `deleteByGoalId(...)` — حذف

لا توجد أي دوال استعلام إضافية غير ضرورية لهذه المرحلة.

### 4.9.6 AppDatabase و DatabaseModule

- **`AppDatabase`** (`app/src/main/java/com/gym/app/data/local/AppDatabase.kt`) — `@Database`
  واحدة تضم الـ Entities الثلاثة فقط، `version = 1`، `exportSchema = false` (خيار صريح لهذه
  المرحلة التأسيسية؛ سيُعاد النظر فيه عند الحاجة الفعلية لـ migrations).
- **`DatabaseModule`** (`app/src/main/java/com/gym/app/core/di/DatabaseModule.kt`) — Hilt
  module (`@Module @InstallIn(SingletonComponent::class)`) يوفّر `AppDatabase` (عبر
  `Room.databaseBuilder`) وكل DAO، جاهزة لحقنها مباشرة في طبقة Repositories القادمة (01F). تم
  استخدام `@Module`/`@Provides` هنا (وليس `@Inject` مباشرة) لأن `AppDatabase` لا يُبنى عبر
  constructor عادي، بل عبر `Room.databaseBuilder`.

### 4.9.7 Mappers (Domain ↔ Entity)

`UserProfileMapper.kt`, `GoalMapper.kt`, `UserPreferencesMapper.kt` — دوال `toEntity()` /
`toDomain()` كـ extension functions نقية، خارج كل من طبقة الـ domain وطبقة الـ Room تمامًا.
**لا يوجد أي Room annotation داخل حزمة `domain.model`** — تم التحقق آليًا. النماذج المركزية لم
تُعدَّل إطلاقًا في هذه المرحلة (تحقق بايت-لبايت مع نسخة الـ ZIP المرجعية لـ 01D).

### 4.9.8 اختبارات الوحدة والاختبارات المُدمَجة (Unit + Instrumented Tests)

**اختبارات الوحدة** (`app/src/test/java/com/gym/app/data/mapper/`) — لا تحتاج Android/Room
فعليًا، تختبر فقط صحة التحويل domain ↔ entity:
```
UserProfileMapperTest.kt       — جولة كاملة، gender nullable، تخزين enum كـ name
GoalMapperTest.kt               — جولة كاملة، secondaryGoals فارغة/غير فارغة، حقول nullable
UserPreferencesMapperTest.kt    — جولة كاملة، TimeOfDay nullable، تقاطع أيام العمل/الراحة
```

**اختبار مُدمَج (Instrumented)** (`app/src/androidTest/java/com/gym/app/data/local/`) — يحتاج
SQLite حقيقي عبر Android Test Runner، لذلك لا يمكن تشغيله في بيئة Sandbox الحالية:
```
AppDatabaseTest.kt — حفظ وقراءة كل Entity، الحفاظ على علاقة userId عبر الجداول الثلاثة معًا،
                     upsert يستبدل الصف الصحيح، حذف يزيل الصف فعليًا، قراءة صف غير موجود تُرجع
                     null
```

لم تُضَف اختبارات بلا فائدة حقيقية.

### 4.9.9 لم يتم تنفيذه في 01E (بالتصميم)

لا DataStore، لا Onboarding UI، لا Repositories حقيقية (فقط DAOs وMappers خام)، لا Use Cases،
لا أي منطق Home/Workout/Nutrition/Recovery، لا تعديل على أي نموذج domain، لا بدء لأي عمل من
Phase 01F.

---

## 5. قاعدة اللغة العربية فقط (Arabic-only Rule) — لم تتغيّر

كل نص ظاهر للمستخدم يأتي حصريًا من `res/values/strings.xml` عبر `stringResource(...)`. تمت
إضافة سلسلة عربية واحدة جديدة فقط (`start_di_status`) لعرض حالة الحقن التقني، ولا يوجد أي نص
إنجليزي ظاهر للمستخدم. لا يوجد `res/values-ar/` ولا أي دعم لتعدد اللغات.

---

## 6. دعم RTL — لم يتغيّر

`GymTheme` (من 01B) لا يزال يفرض `LocalLayoutDirection.Rtl` صراحة. لم يُمس شيء في هذا الجزء
خلال 01C أو 01D (نماذج الـ domain في 01D لا علاقة لها بـ UI أو RTL إطلاقًا).

---

## 7. حالة Build و GitHub Actions

### Phase 01A — Project Bootstrap
✅ **مغلقة رسميًا.** GitHub Actions نجح، تم توليد الـ Debug APK بنجاح.

### Phase 01B — Navigation Foundation
✅ **مغلقة رسميًا.** GitHub Actions نجح.

### Phase 01C — Dependency Injection Foundation
✅ **مغلقة رسميًا.** GitHub Actions نجح.

### Phase 01D — Core User Models
✅ **مغلقة رسميًا.** GitHub Actions نجح.

### Phase 01E — Room Database Foundation (هذه المرحلة)
⏳ **لم يتم تشغيل Build أو Unit Tests أو الاختبارات المُدمَجة فعليًا محليًا.** بيئة التنفيذ
الحالية (Sandbox) لا تملك اتصال إنترنت، ولا Android SDK، ولا Gradle، ولا حتى Kotlin compiler
(`kotlinc`) مثبت مسبقًا — تم التحقق من هذا فعليًا مرة أخرى قبل البدء في هذه المرحلة.

**ما تم فعله بدلاً من Build/Test فعلي (تحقق استاتيكي):** راجع القسم "ملاحظة صادقة حول حالة
Build لمرحلة 01E" في `PROJECT_PHASE.md` للتفاصيل الكاملة لكل خطوات التحقق الاستاتيكي المُنفَّذة
لهذه المرحلة (توازن الأقواس، نقاء `domain.model`، مقارنة بايت-لبايت مع الملفات السابقة، عدم
وجود Repositories/DataStore/Onboarding، إلخ).

**الحالة الحقيقية في `PROJECT_PHASE.md`:** `Status: Pending GitHub Validation` — التزامًا
بالصدق وعدم الادعاء بنجاح Build أو Tests لم يحدثا فعليًا محليًا. سيتم تأكيد النجاح بعد أول
Push إلى main عبر GitHub Actions.

---

## 7.1 المشاكل المُصلَحة سابقًا (Known Fixed Issues)

**هذا قسم دائم.** يجب الحفاظ عليه في كل مرحلة قادمة، وإضافة أي مشكلة مُصلَحة جديدة إليه —
لا تُحذف أي مشكلة سابقة من هنا.

### KNOWN FIX #1 — Gradle Version Mismatch (اكتُشف ومعروف منذ بداية GitHub Actions)

**المشكلة:** فشل build سابق عبر GitHub Actions لأن الـ workflow استخدم إصدار Gradle غير محدَّد
صراحةً، فاستخدم GitHub Actions تلقائيًا Gradle 9.6.1 — وهو غير متوافق مع إعداد المشروع الحالي:

- Android Gradle Plugin: 8.5.2
- Kotlin: 1.9.24
- Gradle المطلوب: 8.7

كان الخطأ يتضمن رسائل مشابهة لـ:
`Configuration.fileCollection(org.gradle.api.specs.Spec)` و `BuildFlowServiceProperty`.

**الحل:** تثبيت (pin) إصدار Gradle صراحةً إلى `8.7` داخل
`.github/workflows/build-apk.yml`، عبر:

```yaml
- name: Setup Gradle 8.7
  uses: gradle/actions/setup-gradle@v4
  with:
    gradle-version: '8.7'

- name: Generate Gradle Wrapper
  run: gradle wrapper --gradle-version 8.7 --distribution-type bin
```

**ممنوع فعله مستقبلًا (إلا بمرحلة مخصصة صريحة تتحقق من التوافق الكامل):**
- إزالة `gradle-version: '8.7'` الصريح
- تغييره إلى إصدار غير محدَّد (latest)
- ترقية Gradle بمعزل عن باقي السلسلة
- ترقية Android Gradle Plugin بمعزل عن باقي السلسلة
- ترقية Kotlin بمعزل عن باقي السلسلة

تم التحقق من هذا الإصلاح فعليًا وبنجاح عبر GitHub Actions في المراحل 01A حتى 01D، وتم الحفاظ
عليه دون أي تغيير في 01E (تحقق بايت-لبايت، راجع القسم 4.9.2 و`PROJECT_PHASE.md`).

---

## 8. الملفات المُضافة في هذه المرحلة (01D)

```
app/src/main/java/com/gym/app/domain/model/UserProfile.kt         (جديد)
app/src/main/java/com/gym/app/domain/model/Goal.kt                 (جديد)
app/src/main/java/com/gym/app/domain/model/UserPreferences.kt      (جديد؛ يحتوي أيضًا على
                                                                     ReminderPreferences)
app/src/main/java/com/gym/app/domain/model/Gender.kt                (جديد)
app/src/main/java/com/gym/app/domain/model/ExperienceLevel.kt       (جديد)
app/src/main/java/com/gym/app/domain/model/ActivityLevel.kt         (جديد)
app/src/main/java/com/gym/app/domain/model/PrimaryGoal.kt           (جديد)
app/src/main/java/com/gym/app/domain/model/WorkoutLocation.kt       (جديد)
app/src/main/java/com/gym/app/domain/model/BudgetLevel.kt           (جديد)
app/src/main/java/com/gym/app/domain/model/UnitSystem.kt            (جديد)
app/src/main/java/com/gym/app/domain/model/Weekday.kt               (جديد)
app/src/main/java/com/gym/app/domain/model/TimeOfDay.kt             (جديد)

app/src/test/java/com/gym/app/domain/model/UserProfileTest.kt       (جديد في 01D)
app/src/test/java/com/gym/app/domain/model/GoalTest.kt              (جديد في 01D)
app/src/test/java/com/gym/app/domain/model/UserPreferencesTest.kt   (جديد في 01D)
app/src/test/java/com/gym/app/domain/model/TimeOfDayTest.kt         (جديد في 01D)
```

## 8.1 الملفات المُضافة في مرحلة 01E

```
app/src/main/java/com/gym/app/data/local/entity/UserProfileEntity.kt       (جديد)
app/src/main/java/com/gym/app/data/local/entity/GoalEntity.kt              (جديد)
app/src/main/java/com/gym/app/data/local/entity/UserPreferencesEntity.kt   (جديد)
app/src/main/java/com/gym/app/data/local/converters/GoalTypeConverters.kt  (جديد)
app/src/main/java/com/gym/app/data/local/dao/UserProfileDao.kt             (جديد)
app/src/main/java/com/gym/app/data/local/dao/GoalDao.kt                    (جديد)
app/src/main/java/com/gym/app/data/local/dao/UserPreferencesDao.kt         (جديد)
app/src/main/java/com/gym/app/data/local/AppDatabase.kt                    (جديد)
app/src/main/java/com/gym/app/core/di/DatabaseModule.kt                    (جديد)
app/src/main/java/com/gym/app/data/mapper/UserProfileMapper.kt             (جديد)
app/src/main/java/com/gym/app/data/mapper/GoalMapper.kt                    (جديد)
app/src/main/java/com/gym/app/data/mapper/UserPreferencesMapper.kt         (جديد)

app/src/test/java/com/gym/app/data/mapper/UserProfileMapperTest.kt         (جديد)
app/src/test/java/com/gym/app/data/mapper/GoalMapperTest.kt                (جديد)
app/src/test/java/com/gym/app/data/mapper/UserPreferencesMapperTest.kt     (جديد)
app/src/androidTest/java/com/gym/app/data/local/AppDatabaseTest.kt         (جديد)
```

## 9. الملفات المُعدَّلة في مرحلة 01D

```
PROJECT_PHASE.md, PROJECT_HANDOFF.md, NEXT_TASK.md — تحديث التوثيق فقط
```

**لم يتم لمس أي ملف كود مصدري آخر في 01D.** تحديدًا: `settings.gradle.kts`, `gradle.properties`,
`gradle/wrapper/*`, `gradlew`, `gradlew.bat`, `.gitignore`, `build.gradle.kts` (root),
`app/build.gradle.kts`, `.github/workflows/build-apk.yml`, `app/proguard-rules.pro`,
`AndroidManifest.xml`, `strings.xml`, `themes.xml`, أيقونة `ic_launcher.xml`, `Routes.kt`,
`AppNavHost.kt`, `Theme.kt`, `GymApplication.kt`, `MainActivity.kt`,
`core/di/AppInfoProvider.kt`, `StartViewModel.kt`, `StartScreen.kt`, وأي من شاشات
Home/Workout/Progress/Settings. تم التحقق من هذا بمقارنة بايت-لبايت مع الـ ZIP المرجعي
لمرحلة 01C.

## 9.1 الملفات المُعدَّلة في مرحلة 01E

```
app/build.gradle.kts   — إضافة Room dependencies فقط (runtime, ktx, kapt compiler, testing,
                          coroutines-test) ورفع versionName إلى "0.4.0-phase01e". لم يُمس أي
                          سطر آخر (Compose, Hilt, Navigation, compileSdk/minSdk/targetSdk,
                          kotlinOptions, compose Options, إلخ — كلها كما هي بالضبط).
PROJECT_PHASE.md, PROJECT_HANDOFF.md, NEXT_TASK.md — تحديث التوثيق
```

**لم يتم لمس أي ملف آخر في 01E.** تحديدًا: `build.gradle.kts` (root)،
`.github/workflows/build-apk.yml` (لا يزال مثبَّتًا على `gradle-version: '8.7'`)،
`settings.gradle.kts`, `gradle.properties`, `gradle/wrapper/*`, `gradlew`, `gradlew.bat`,
`.gitignore`, كل ملفات `domain/model/` (تحقق بايت-لبايت)، `navigation/`, `ui/`,
`GymApplication.kt`, `MainActivity.kt`, `core/di/AppInfoProvider.kt`. تم التحقق من هذا بمقارنة
بايت-لبايت مع الـ ZIP المرجعي لمرحلة 01D (راجع "ملاحظة صادقة حول حالة Build لمرحلة 01E" في
`PROJECT_PHASE.md`).

---

## 10. قاعدة معمارية المنتج المستقبلية — النماذج أصبح لها تخزين الآن (01E)

بنية المنتج الكاملة مستقبلًا تعتمد جوهريًا على:

**User Profile + Goal + Preferences**

✅ منذ Phase 01D، هذه النماذج الثلاثة موجودة فعليًا كـ domain models نقية في
`app/src/main/java/com/gym/app/domain/model/` (راجع القسم 4.8). ✅ منذ Phase 01E، أصبح لهذه
النماذج تخزين فعلي عبر Room (Entities + DAOs + AppDatabase + Mappers، راجع القسم 4.9)، متاح
للحقن عبر Hilt (`DatabaseModule`). كل ميزة مستقبلية يجب أن **تقرأ** بياناتها الخاصة بالمستخدم
من هذه النماذج المركزية — عبر طبقة Repositories ستُبنى في Phase 01F فوق DAOs/Mappers الحالية —
بدلاً من استخدام بيانات ثابتة معزولة أو حالة مستخدم مكررة في كل ميزة على حدة. أمثلة:

- **Workout** سيعتمد على: User Profile + Goal + Preferences
- **Nutrition** سيعتمد على: User Profile + Goal + Preferences
- **Recovery** سيعتمد على: User Profile + Preferences + سجل التمارين + سجل النوم + سجل الألم
- **Home** سيجمّع (aggregate) معلومات من ميزات مختلفة للمستخدم النشط الحالي

⚠️ **مهم:** التخزين نفسه (Entities/DAOs/Database) موجود الآن، لكن **لا Repositories حقيقية بعد**
تربط طبقة الـ domain بطبقة الـ Room بشكل نظيف قابل للاستخدام من ViewModels — ذلك سيأتي في
Phase 01F — User Repositories.

---

## 11. ما لم يتم تنفيذه (بالتصميم — ممنوع حتى الآن)

- [x] ~~Hilt~~ — تم في 01C (أساس فقط، بدون Repositories حقيقية)
- [x] ~~User Profile (Model)~~ — تم في 01D (domain model فقط، بدون تخزين)
- [x] ~~Goal (Model)~~ — تم في 01D (domain model فقط، بدون تخزين)
- [x] ~~Preferences (Model)~~ — تم في 01D (domain model فقط، بدون تخزين)
- [x] ~~Room / Database~~ — تم في 01E (Entities + DAOs + AppDatabase + Mappers، بدون
      Repositories حقيقية بعد)
- [ ] DataStore
- [ ] Repositories حقيقية (تربط الـ domain بـ Room بشكل نظيف للاستخدام من ViewModels)
- [ ] Onboarding
- [ ] أي منطق Workout / Nutrition / Smart Assistant حقيقي
- [ ] Login / Cloud Sync
- [ ] دعم لغات متعددة أو تبديل لغة
- [ ] أي بيانات مستخدم وهمية (Fake user data) أو معلومات شخصية Hardcoded
- [ ] تشغيل Build/Test فعلي ناجح داخل بيئة Sandbox (سيتم التحقق عبر GitHub Actions)
- [ ] أي عمل من Phase 01F

---

## 12. قواعد للمطوّر/المرحلة التالية (Rules for the Next Developer)

1. **لا تعيد بناء المشروع من الصفر.** هذا المستودع على GitHub هو مصدر الحقيقة الوحيد بين
   المراحل والجلسات المستقبلية.
2. **لا تحذف أو تستبدل** أي ميزة تعمل حاليًا (Gradle setup, Workflow, بنية التنقل, أساس
   الحقن, نماذج الـ domain, أساس Room) إلا إذا طُلب ذلك صراحة.
3. **لا تُدرِج أي بيانات مستخدم Hardcoded** في أي مكان — كل بيانات المستخدم الحقيقية يجب أن
   تأتي لاحقًا من النموذج المركزي (User Profile + Goal + Preferences، الموجود الآن في
   `domain/model/` مع تخزين Room في `data/local/`) عبر طبقة الحقن وRepositories مستقبلية
   (Phase 01F)، وليس من قيم ثابتة داخل الكود.
4. **كل نص ظاهر للمستخدم يجب أن يكون عربيًا فقط** عبر `res/values/strings.xml` — ممنوع أي
   نص Hardcoded داخل ملفات Kotlin.
5. **لا تُنشئ** `res/values-ar/strings.xml` أو أي مجلد لغة إضافي.
6. **لا تُضِف DataStore قبل مرحلتها المخصصة.** أساس Room (01E) موجود الآن، لكن DataStore لا
   يزال خارج النطاق حتى تُطلَب صراحةً.
7. **حافظ على نماذج الـ domain نقية:** لا Room annotations، لا DataStore، لا Android
   framework types، لا Hilt داخل حزمة `domain.model` — التحويل من/إلى Room يتم حصريًا عبر
   `data/mapper/` (راجع القسم 4.9.7)، ليس داخل النماذج نفسها.
8. **GitHub يبقى مصدر الحقيقة (Source of Truth)** — لا تدّعِ نجاح Build أو Tests إلا بعد تأكيد
   فعلي عبر GitHub Actions أو تنفيذ محلي حقيقي.
9. **لا تبدأ أي مرحلة تالية** (مثل 01F) دون طلب صريح من المستخدم.
10. **حافظ على قسم "المشاكل المُصلَحة سابقًا" (Known Fixed Issues، القسم 7.1) دائمًا** — لا
    تحذف أي عنصر منه، وأضِف أي مشكلة جديدة تُكتشف وتُصلَح إلى نفس القسم.

