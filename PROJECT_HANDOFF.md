# PROJECT HANDOFF — GYM

## 0. المرحلة الحالية

**Phase: 01H-02B — Startup & Basic Onboarding**
(المراحل السابقة **01A — Project Bootstrap**، **01B — Navigation Foundation**،
**01C — Dependency Injection Foundation**، **01D — Core User Models**،
**01E — Room Database Foundation**، **01F — User Repositories**، و
**01G — DataStore Foundation** مغلقة رسميًا وناجحة عبر GitHub Actions — التفاصيل في القسم 7)

---

## 1. نظرة عامة على المشروع

**GYM** هو تطبيق Android مخطط له أن يكون **منتجًا عامًا قابلًا للنشر**، **باللغة العربية
فقط** (Arabic-only, RTL). الفكرة الأساسية للمنتج الكامل مستقبلًا تعتمد على:

- **User Profile** (ملف المستخدم)
- **Goal** (الهدف الرياضي/الصحي)
- **Preferences** (تفضيلات المستخدم)

✅ اعتبارًا من Phase 01D، تم تعريف هذه المحاور الثلاثة كنماذج domain نقية (بدون تخزين، بدون
UI). ✅ اعتبارًا من Phase 01E، أصبح لهذه النماذج الثلاثة تخزين فعلي عبر Room (انظر القسم 4.9).
✅ اعتبارًا من Phase 01F، أصبحت هذه النماذج الثلاثة قابلة للوصول عبر طبقة Repositories نظيفة
(انظر القسم 4.10) تفصل الـ domain عن Room بشكل كامل، وتُحقن عبر Hilt.
✅ اعتبارًا من Phase 01G، أُضيف أساس Preferences DataStore منفصل تمامًا (انظر القسم 4.11)
لتخزين حالة تطبيق بسيطة فقط (`onboardingCompleted`, `activeUserId`) — لا يكرر أو يستبدل بيانات
Room الخاصة بـ User Profile/Goal/Preferences.
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

المشروع لا يزال خاليًا من: AppCompat, Material Components الكلاسيكية.

**ملاحظة (01F):** لم تُضَف أي مكتبة جديدة في هذه المرحلة — طبقة الـ Repositories الجديدة تعتمد
حصريًا على Room وHilt وKotlin Coroutines الموجودة بالفعل منذ 01C/01E. تم فقط رفع `versionName`
إلى `"0.5.0-phase01f"`.

**ملاحظة (01G):** أُضيفت مكتبة واحدة فقط: `androidx.datastore:datastore-preferences:1.1.1`
(Preferences DataStore، بدون Proto DataStore، بدون kapt/KSP). لا اعتماد جديد على Kotlin/AGP/
Hilt/Room أبعد مما هو موجود بالفعل. تم رفع `versionName` إلى `"0.6.0-phase01g"`.

**ملاحظة (01H-02B):** لم تُضَف أي مكتبة جديدة في هذه المرحلة — كل ما أُضيف (App Start Logic،
Onboarding sub-graph، شاشة Basic Profile Input) يعتمد حصريًا على Compose/Navigation/Hilt/Room/
DataStore الموجودة بالفعل منذ 01B–01G. لم يُرفَع `versionName` في هذه المرحلة (لا تغيير على
`app/build.gradle.kts` إطلاقًا).

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

## 4.10 طبقة الـ Repositories (User Repositories) — جديد في 01F

### 4.10.1 نظرة عامة

تمت إضافة طبقة Repositories نظيفة تفصل الـ domain (01D) تمامًا عن Room (01E). هذه الطبقة هي
الآن الواجهة الوحيدة الموصى باستخدامها من أي ViewModel/Use Case مستقبلي للوصول إلى بيانات
User Profile + Goal + Preferences — بدلاً من حقن DAOs مباشرة. هذه المرحلة **repositories فقط**:
لا DataStore، لا Onboarding، لا UI، لا Use Cases، لا منطق ميزات حقيقي.

### 4.10.2 الملفات والمسارات

```
app/src/main/java/com/gym/app/domain/repository/UserProfileRepository.kt       (جديد — interface)
app/src/main/java/com/gym/app/domain/repository/GoalRepository.kt              (جديد — interface)
app/src/main/java/com/gym/app/domain/repository/UserPreferencesRepository.kt   (جديد — interface)

app/src/main/java/com/gym/app/data/repository/UserProfileRepositoryImpl.kt       (جديد)
app/src/main/java/com/gym/app/data/repository/GoalRepositoryImpl.kt              (جديد)
app/src/main/java/com/gym/app/data/repository/UserPreferencesRepositoryImpl.kt   (جديد)

app/src/main/java/com/gym/app/core/di/RepositoryModule.kt   (جديد — Hilt @Binds module)

app/src/test/java/com/gym/app/data/local/fake/FakeUserProfileDao.kt          (جديد)
app/src/test/java/com/gym/app/data/local/fake/FakeGoalDao.kt                 (جديد)
app/src/test/java/com/gym/app/data/local/fake/FakeUserPreferencesDao.kt      (جديد)

app/src/test/java/com/gym/app/data/repository/UserProfileRepositoryImplTest.kt       (جديد)
app/src/test/java/com/gym/app/data/repository/GoalRepositoryImplTest.kt              (جديد)
app/src/test/java/com/gym/app/data/repository/UserPreferencesRepositoryImplTest.kt   (جديد)
```

### 4.10.3 واجهات الـ Repository (Domain Layer)

كل واجهة في `domain/repository/` تُعرِّف فقط العمليات ذات المعنى الفعلي لهذه المرحلة التأسيسية،
وتُرجِع نماذج domain نقية فقط (لا Room entities تظهر خارج `data/`):

- **`UserProfileRepository`** — `observeByUserId(userId): Flow<UserProfile?>`,
  `getByUserId(userId): UserProfile?`, `save(userProfile)`, `deleteByUserId(userId)`.
- **`GoalRepository`** — يعكس أن المستخدم قد يملك أكثر من هدف عبر الزمن (نفس افتراض
  `GoalDao` من 01E): `observeByUserId(userId): Flow<List<Goal>>`,
  `getByUserId(userId): List<Goal>`, `getByGoalId(goalId): Goal?`, `save(goal)`,
  `deleteByGoalId(goalId)`.
- **`UserPreferencesRepository`** — صف واحد لكل مستخدم (يطابق `UserPreferencesDao`):
  `observeByUserId(userId): Flow<UserPreferences?>`, `getByUserId(userId): UserPreferences?`,
  `save(userPreferences)`, `deleteByUserId(userId)`.

لا توجد أي دالة إضافية غير مطلوبة (لا بحث، لا فرز، لا تصفية متقدمة) — هذه مرحلة تأسيسية فقط.

### 4.10.4 التنفيذات (Data Layer)

كل تنفيذ (`*RepositoryImpl` في `data/repository/`) يستقبل الـ DAO المقابل له عبر الـ constructor
(`@Inject constructor`) فقط — بدون أي منطق تحويل داخلي؛ التحويل بين domain وEntity يتم حصريًا
عبر دوال `toEntity()`/`toDomain()` الموجودة بالفعل في `data/mapper/` منذ 01E
(`UserProfileMapper`, `GoalMapper`, `UserPreferencesMapper`). لم تُعدَّل أي دالة Mapper موجودة،
ولم يُضَف أي منطق تحويل جديد داخل طبقة الـ Repository نفسها.

القراءة عبر `Flow` (`observeByUserId`) تُنفَّذ بتطبيق `.map { it?.toDomain() }` (أو
`.map { list -> list.map { it.toDomain() } }` لحالة `GoalRepository`) مباشرة على الـ `Flow` الذي
يُرجعه الـ DAO — دون أي حالة (state) إضافية أو تخزين مؤقت (caching) في هذه المرحلة.

### 4.10.5 ربط Hilt (`RepositoryModule`)

الملف: `app/src/main/java/com/gym/app/core/di/RepositoryModule.kt`

`abstract class` بـ `@Module @InstallIn(SingletonComponent::class)` يستخدم `@Binds` (وليس
`@Provides`) لربط كل واجهة بتنفيذها:

```kotlin
@Binds
@Singleton
abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository
```

تم اختيار `@Binds` تحديدًا لأن كل تنفيذ يملك بالفعل `constructor` معلَّم بـ `@Inject` يستقبل الـ
DAO مباشرة (والـ DAOs نفسها متوفرة في الرسم البياني لـ Hilt عبر `DatabaseModule` من 01E) — هذا
هو الأسلوب الأقل تعقيدًا والموصى به رسميًا لربط تنفيذ جاهز بواجهته، بعكس `@Provides` الذي كان
سيتطلب استدعاء الـ constructor يدويًا بلا داعٍ (كما هو مستخدم في `DatabaseModule` فقط لأن
`AppDatabase` لا يُبنى عبر constructor عادي).

### 4.10.6 اختبارات الوحدة (Repository Unit Tests)

بما أن المشروع لا يحتوي على أي مكتبة Mocking (لا Mockito، لا MockK) وبما أن اختبارات Room
الحقيقية تحتاج بيئة instrumented (كما في `AppDatabaseTest` من 01E)، تم اتباع نفس الأسلوب المتبع
فعليًا في المشروع: اختبارات وحدة بسيطة بـ JUnit 4 فقط + DAOs وهمية (fakes) في الذاكرة، دون أي
اعتماد على Room أو Android Instrumentation:

- **`FakeUserProfileDao`, `FakeGoalDao`, `FakeUserPreferencesDao`**
  (`app/src/test/java/com/gym/app/data/local/fake/`) — تنفيذ بسيط لكل واجهة DAO باستخدام
  `MutableStateFlow<Map<...>>` كمخزن في الذاكرة، يحاكي فقط شكل الاستعلامات الحقيقية (بدون أي
  SQL فعلي). سلوك SQLite الفعلي مغطّى بالفعل عبر `AppDatabaseTest` (01E)، فلا داعي لتكراره هنا.
- **`UserProfileRepositoryImplTest`, `GoalRepositoryImplTest`,
  `UserPreferencesRepositoryImplTest`** (`app/src/test/java/com/gym/app/data/repository/`) —
  تتحقق أن كل `*RepositoryImpl` يُفوِّض بشكل صحيح لواجهة الـ DAO، ويحوّل بشكل صحيح عبر
  Mapper الموجود، ويحافظ على دلالات `userId`/`goalId` (بما في ذلك تعدد الأهداف لكل مستخدم في
  حالة `Goal`): حفظ ثم قراءة، استبدال عند نفس المعرّف، المراقبة عبر `Flow` (`first()` مع
  `runTest`)، والحذف. لم تُضَف اختبارات بلا فائدة حقيقية.

### 4.10.7 لم يتم تنفيذه في 01F (بالتصميم)

لا DataStore، لا Onboarding UI، لا أي شاشة UI جديدة، لا Use Cases، لا منطق ميزات حقيقي
(Workout/Nutrition/Recovery/Home/Smart Assistant)، لا Login، لا Cloud Sync، لا تعديل على أي
نموذج domain أو Entity أو Mapper أو DAO موجود من 01D/01E، لا بدء لأي عمل من Phase 01G.

---

## 4.11 أساس DataStore (DataStore Foundation) — جديد في 01G

### 4.11.1 نظرة عامة

تمت إضافة أساس Preferences DataStore منفصل تمامًا عن Room، مخصص حصريًا لحالة تطبيق بسيطة
(simple app-level state) — وليس بديلاً أو تكرارًا لبيانات User Profile/Goal/UserPreferences
المخزَّنة في Room منذ 01E. هذه المرحلة **DataStore فقط**: لا Onboarding UI، لا App Start Logic،
لا شاشات جديدة، لا منطق ميزات حقيقي.

الحقول المخزَّنة (نموذج `AppState`):

- `onboardingCompleted: Boolean` — هل انتهى تدفق onboarding مرة واحدة أم لا. هذه حالة تدفق
  تطبيق (app flow state)، وليست بيانات ملف مستخدم/هدف/تفضيلات.
- `activeUserId: String?` — أي مستخدم محلي (بمعرّف `userId` يطابق
  `UserProfile.userId`) هو النشط حاليًا. هذا **مؤشر (pointer)** إلى صف مخزَّن في Room، وليس
  تكرارًا لبياناته.

**لم تُضَف** إعداد وحدات (units setting) إلى DataStore رغم وجود `UnitSystem` مُعرَّف بوضوح منذ
01D — لأنه ينتمي إلى `UserPreferences` (صف Room لكل مستخدم)، وليس حالة تطبيق بسيطة مستقلة عن
المستخدم؛ إضافته هنا كانت ستُكرِّر بيانات Room بدلاً من إكمالها.

### 4.11.2 Gradle Dependencies

في `app/build.gradle.kts`: مكتبة واحدة جديدة فقط —
`androidx.datastore:datastore-preferences:1.1.1` (Preferences DataStore، وليس Proto
DataStore). لا تحتاج kapt أو KSP، فلا حاجة لأي تعديل على إعداد `kapt {}` الحالي. `versionName`
تم رفعه إلى `"0.6.0-phase01g"`.

### 4.11.3 الملفات والمسارات

```
app/src/main/java/com/gym/app/domain/appstate/AppState.kt              (جديد)
app/src/main/java/com/gym/app/domain/appstate/AppStateRepository.kt    (جديد — interface)
app/src/main/java/com/gym/app/data/appstate/AppStateRepositoryImpl.kt  (جديد)
app/src/main/java/com/gym/app/core/di/DataStoreModule.kt               (جديد)
app/src/main/java/com/gym/app/core/di/AppStateRepositoryModule.kt      (جديد)

app/src/test/java/com/gym/app/data/appstate/AppStateRepositoryImplTest.kt   (جديد)
```

### 4.11.4 `AppState` (Domain Layer)

`data class AppState(val onboardingCompleted: Boolean, val activeUserId: String?)` في حزمة
جديدة `domain/appstate/` (منفصلة عمدًا عن `domain/model/` الخاصة بـ User Profile/Goal/
Preferences، لتوضيح أن هذا نوع مختلف تمامًا من البيانات). immutable بالكامل، بدون أي اعتماد
على DataStore/Android/Hilt — نفس قاعدة `domain/model/` من 01D. يحتوي `companion object` بقيمة
`INITIAL` تمثّل حالة التثبيت الجديد (`onboardingCompleted = false`, `activeUserId = null`).

### 4.11.5 `AppStateRepository` (Domain Layer)

واجهة في `domain/appstate/` تُعرِّف:

- `val appState: Flow<AppState>` — مراقبة تفاعلية لكامل الحالة.
- `suspend fun setOnboardingCompleted(completed: Boolean)`.
- `suspend fun setActiveUserId(userId: String?)` — تمرير `null` يمسح المستخدم النشط.

مثل واجهات `domain/repository/` من 01F، لا تظهر أي أنواع DataStore/Preferences في هذا التوقيع
— نماذج domain وFlow/suspend من Kotlin/Coroutines فقط.

### 4.11.6 `AppStateRepositoryImpl` (Data Layer)

في `data/appstate/` (حزمة منفصلة عن `data/repository/` الخاصة بـ Room، لنفس سبب الفصل في
الـ domain layer). يستقبل `DataStore<Preferences>` عبر الـ constructor (`@Inject constructor`)
فقط. يحوّل بين `AppState` ومفاتيح `Preferences` خام (`booleanPreferencesKey`,
`stringPreferencesKey`) داخل هذا الملف حصريًا — لا تسرّب لأي نوع DataStore إلى الواجهة أو
النموذج. لا يلمس `AppDatabase` أو أي DAO إطلاقًا.

### 4.11.7 ربط Hilt

- **`DataStoreModule`** (`core/di/DataStoreModule.kt`) — `@Module @InstallIn
  (SingletonComponent::class)` يوفّر `DataStore<Preferences>` عبر `preferencesDataStore`
  property delegate (باسم ملف `"app_state"`)، مطلوب `@Provides` (وليس `@Inject` مباشرة) لنفس
  سبب `DatabaseModule` من 01E: الـ instance لا يُبنى عبر constructor عادي.
- **`AppStateRepositoryModule`** (`core/di/AppStateRepositoryModule.kt`) — `@Module` منفصل
  (وليس جزءًا من `RepositoryModule` من 01F) يستخدم `@Binds` لربط `AppStateRepository`
  بـ `AppStateRepositoryImpl`، بنفس أسلوب `RepositoryModule`. أُبقي منفصلاً عمدًا لتوضيح أن
  `AppStateRepository` ليس جزءًا من عائلة الـ Repositories المدعومة بـ Room.

### 4.11.8 اختبارات الوحدة (Unit Tests)

`AppStateRepositoryImplTest` (`app/src/test/java/com/gym/app/data/appstate/`) — يستخدم
`DataStore<Preferences>` حقيقي عبر `PreferenceDataStoreFactory.create` مع ملف مؤقت (temp
file)، بدلاً من الـ instance المعتمد على Android Context في `DataStoreModule` — لأن هذه
اختبارات وحدة JVM بحتة (بدون Android Instrumentation)، بنفس منطق `FakeUserProfileDao` من 01F
(تجنّب اعتماد فعلي على منصة Android في اختبارات الوحدة). يغطي: القيم الافتراضية عند عدم وجود
بيانات محفوظة، تحديث/عكس `onboardingCompleted`، تخزين/مسح `activeUserId`، واستقلالية الحقلين
عن بعضهما.

### 4.11.9 لم يتم تنفيذه في 01G (بالتصميم)

لا Onboarding UI، لا App Start Logic، لا أي شاشة UI جديدة، لا Use Cases، لا منطق ميزات حقيقي
(Workout/Nutrition/Recovery/Home/Smart Assistant)، لا Login، لا Cloud Sync، لا تعديل على أي
نموذج domain أو Entity أو Mapper أو DAO أو Repository موجود من 01D/01E/01F، لا تكرار لأي بيانات
Room (تحديدًا: لم يُضَف إعداد وحدات/units إلى DataStore، لأنه ينتمي لـ `UserPreferences` في
Room — انظر 4.11.1)، لا بدء لأي عمل من Phase 01H.

---

## 4.12 بدء التشغيل و Onboarding الأساسي (Startup & Basic Onboarding) — جديد في 01H-02B

### 4.12.1 نظرة عامة

تم دمج ثلاث مراحل صغيرة في هذه الجلسة الواحدة:

1. **01H — App Start Logic**: منطق قرار التوجيه عند بدء التشغيل، بالاعتماد حصريًا على
   `AppStateRepository` (01G).
2. **02A — Onboarding Flow Foundation**: بنية تنقل أساسية جديدة (sub-graph) لتدفق onboarding،
   مبنية فوق `Routes`/`AppNavHost` الموجودين (01B) دون إعادة بنائهما.
3. **02B — Basic Profile Input**: أول شاشة onboarding حقيقية، تجمع بيانات الملف الشخصي الأساسية
   عبر `UserProfileRepository` (01F) وتخزّن `activeUserId` عبر `AppStateRepository` (01G).

لا Use Cases منفصلة أُضيفت هنا: منطق التوجيه والتحقق بسيط بما يكفي ليعيش مباشرة داخل
ViewModels (`StartViewModel`, `OnboardingBasicProfileViewModel`) دون طبقة تنسيق إضافية —
بنفس الأسلوب الذي اتبعته `StartViewModel` الأصلية منذ 01C.

### 4.12.2 حالات بدء التشغيل الثلاث (App Start Logic)

الملف: `app/src/main/java/com/gym/app/ui/screens/StartViewModel.kt` (مُعدَّل جذريًا)

`StartViewModel` يقرأ `AppStateRepository.appState.first()` مرة واحدة فقط عند بدء التشغيل
(داخل `viewModelScope.launch`، أي على coroutine خلفي — لا حجب للـ main thread)، ثم يحسم واحدة
من ثلاث نتائج ممثَّلة بـ `sealed class StartDestination`:

- **`StartDestination.Undetermined`** — القيمة الابتدائية فقط، طالما القراءة من DataStore لم
  تكتمل بعد. `StartScreen` لا يتنقل في هذه الحالة، بل يعرض مؤشر تحميل بسيط فقط.
- **`StartDestination.Home`** — عندما `onboardingCompleted == true` و`activeUserId != null`
  (الحالة 2).
- **`StartDestination.Onboarding`** — في كل حالة أخرى: إما `onboardingCompleted == false`
  (الحالة 1)، أو `onboardingCompleted == true` لكن `activeUserId == null` (الحالة 3 — العودة
  الآمنة إلى onboarding).

منطق القرار نفسه معزول بالكامل عن الرسم (`StartScreen` لا يحتوي أي شرط `if
(onboardingCompleted...)`)، تلبيةً لمتطلب فصل منطق القرار عن رسم الواجهة.

### 4.12.3 الربط بالشاشة (Routing فقط، بدون منطق قرار في الـ UI)

الملف: `app/src/main/java/com/gym/app/ui/screens/StartScreen.kt` (مُعدَّل جذريًا)

`StartScreen` أصبح شاشة توجيه بحتة: يراقب `viewModel.destination` عبر
`collectAsStateWithLifecycle()`، ويستخدم `LaunchedEffect(destination)` للتنقل الفعلي مرة واحدة
فقط عند تغيّر القيمة إلى `Home` أو `Onboarding` (مع `popUpTo(Routes.Start.route) { inclusive =
true }` بحيث لا يمكن الرجوع إلى شاشة البداية عبر زر الرجوع). أثناء `Undetermined` يُعرَض فقط
`CircularProgressIndicator` وسلسلة نصية عربية (`start_loading`) — لا أزرار تنقل تقنية قديمة
بعد الآن (تمت إزالتها لأن هذه الشاشة لم تعد شاشة اختبار تنقل يدوي).

### 4.12.4 Onboarding Flow Foundation (بنية التنقل)

الملف: `app/src/main/java/com/gym/app/navigation/Routes.kt` (مُعدَّل)

أُضيف `sealed class Onboarding(route: String) : Routes(route)` متداخل داخل `Routes` الحالي
(دون حذف أو تعديل أي `Routes` موجود: `Start, Home, Workout, Progress, Settings` كما هي بالضبط)
يحتوي:

- `Onboarding.Graph` — مسار الـ sub-graph نفسه (`"onboarding_graph"`).
- `Onboarding.BasicProfile` — الشاشة الأولى (`"onboarding_basic_profile"`).
- `Onboarding.NextPlaceholder` — عنصر نائب لما بعد حفظ الملف الشخصي، مخصص لـ Goal Setup
  المستقبلي (02C).

الملف: `app/src/main/java/com/gym/app/navigation/AppNavHost.kt` (مُعدَّل)

أُضيف بلوك `navigation(route = Routes.Onboarding.Graph.route, startDestination =
Routes.Onboarding.BasicProfile.route) { ... }` بعد الـ `composable` الخمسة الموجودة، دون أي
تعديل عليها. هذا يجعل onboarding مجموعة فرعية (nested graph) معزولة تمامًا، جاهزة لاستقبال
خطوات onboarding مستقبلية (Goal Setup، Workout Preferences، Lifestyle Preferences) داخل نفس
البلوك دون لمس بقية `AppNavHost`.

دعم الرجوع (back navigation) يعمل تلقائيًا عبر السلوك الافتراضي لـ `NavHost`/`NavController`
(زر الرجوع في `OnboardingNextPlaceholderScreen` غير مُضاف حاليًا لأنه عنصر نائب فقط، لكن التنقل
للخلف من شاشة الملف الشخصي إلى ما قبلها لا معنى له هنا لأن `Start` أُزيل من الـ back stack عبر
`popUpTo(inclusive = true)` — وهذا مقصود: onboarding لا يجب أن يعود بالمستخدم إلى شاشة التوجيه
المؤقتة).

### 4.12.5 Basic Profile Input (الشاشة + الـ ViewModel)

الملفات الجديدة:

```
app/src/main/java/com/gym/app/ui/screens/OnboardingBasicProfileViewModel.kt   (جديد)
app/src/main/java/com/gym/app/ui/screens/OnboardingBasicProfileScreen.kt      (جديد)
app/src/main/java/com/gym/app/ui/screens/OnboardingNextPlaceholderScreen.kt   (جديد)
```

**الحقول المُجمَّعة** — فقط الحقول الموجودة فعليًا في `UserProfile` (01D)، بدون أي حقل مكرر أو
مخترَع: `name`, `age`, `gender` (اختياري، مع خيار "تفضّل عدم التحديد")، `heightCm`,
`currentWeightKg`, `experienceLevel`, `activityLevel`.

**فصل حالة الواجهة عن منطق التخزين:** `OnboardingBasicProfileUiState` هو `data class` يحمل كل
قيم الحقول كنصوص خام (لإتاحة كتابة جزئية قبل التحقق)، بالإضافة إلى أعلام خطأ منفصلة لكل حقل
(`nameError`, `ageError`, `heightError`, `weightError`) وعلمي حالة (`isSaving`,
`saveComplete`). التحويل من نص خام إلى نوع domain صحيح (`Int`/`Double`) والتحقق منه يحدث فقط
داخل `OnboardingBasicProfileViewModel.onSaveClicked()` — الشاشة نفسها (`Composable`) لا تحتوي
أي منطق تحقق أو تحويل.

**التحقق (Validation):** يُعاد استخدام دوال التحقق الموجودة فعليًا في `UserProfile` (بدلاً من
تكرار المنطق): `UserProfile.isValidAge(age)`, `UserProfile.isValidHeight(height)`,
`UserProfile.isValidWeight(weight)`، بالإضافة إلى `trim().isBlank()` للاسم. عند فشل أي حقل، لا
يُستدعى الحفظ إطلاقًا، وتُحدَّث فقط أعلام الخطأ المعروضة عبر `supportingText` تحت كل حقل
(رسائل عربية من `strings.xml`، مثل `onboarding_field_age_error` التي تعرض المدى الصالح فعليًا
عبر `UserProfile.MIN_AGE`/`MAX_AGE` بدل أرقام ثابتة مكررة يدويًا).

**حفظ الملف الشخصي والـ userId:**

```kotlin
val userId = UUID.randomUUID().toString()
val profile = UserProfile(userId = userId, name = trimmedName, age = age!!, ...)
userProfileRepository.save(profile)
appStateRepository.setActiveUserId(userId)
```

- `userId` يُنشأ عبر `java.util.UUID.randomUUID()` — لا قيمة ثابتة مثل `"user1"` في أي مسار.
- الحفظ يتم حصريًا عبر `UserProfileRepository.save(...)` (01F) — لا لمس لـ `AppDatabase` أو أي
  DAO مباشرة من هذه الشاشة أو الـ ViewModel.
- نفس الـ `userId` يُخزَّن كـ `activeUserId` عبر `AppStateRepository.setActiveUserId(userId)`
  (01G) — لا لمس لـ `DataStore<Preferences>` مباشرة.
- **`onboardingCompleted` لا يُضبط إلى `true` في أي مكان ضمن هذه المرحلة** — يبقى كما كان
  (`false` لتثبيت جديد)، تنفيذًا صريحًا لمتطلب عدم إكمال onboarding بعد.

بعد نجاح الحفظ (`saveComplete = true`)، تتنقل الشاشة عبر `LaunchedEffect` إلى
`Routes.Onboarding.NextPlaceholder.route` (مع `popUpTo(BasicProfile) { inclusive = true }`،
بحيث لا يمكن العودة لملء نفس الملف الشخصي مرتين عبر زر الرجوع).

### 4.12.6 الشاشة النائبة التالية (`OnboardingNextPlaceholderScreen`)

شاشة بسيطة جدًا تعرض فقط عنوانين نصيين عربيين يؤكدان أن الملف الشخصي حُفظ وأن الخطوة التالية
(تحديد الهدف) ستأتي قريبًا. لا تحتوي أي منطق أو تنقل تلقائي إضافي — ستُستبدَل بالكامل عند تنفيذ
Goal Setup (02C).

### 4.12.7 قاعدة اللغة العربية والـ RTL

كل النصوص الجديدة (شاشة التحميل، عناوين الحقول، رسائل الأخطاء، خيارات الجنس/الخبرة/النشاط، زر
"حفظ ومتابعة"، عنوان/نص الشاشة النائبة) أُضيفت حصريًا إلى `res/values/strings.xml` عبر
`stringResource(...)` — لا نص إنجليزي أو Hardcoded داخل أي ملف Kotlin. لم يُنشأ
`res/values-ar/`. اتجاه RTL موروث تلقائيًا من `GymTheme` (01B) دون أي تعديل عليه.

### 4.12.8 لم يتم تنفيذه في 01H-02B (بالتصميم)

لا Goal Setup، لا Workout Preferences، لا Lifestyle Preferences، لا إكمال onboarding
(`onboardingCompleted` يبقى `false`)، لا منطق Home حقيقي (لا تزال `HomeScreen` شاشة اختبار
تنقل)، لا Workout/Nutrition/Smart Assistant حقيقي، لا Networking، لا Login، لا Cloud Sync، لا
Use Cases منفصلة، لا تعديل على أي نموذج domain/Entity/Mapper/Repository/DataStore موجود من
01D–01G، لا إضافة أي مكتبة جديدة، لا بدء لأي عمل من Phase 02C.

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

### Phase 01E — Room Database Foundation
✅ **مغلقة رسميًا.** GitHub Actions نجح.

### Phase 01F — User Repositories
✅ **مغلقة رسميًا.** GitHub Actions نجح.

### Phase 01G — DataStore Foundation
✅ **مغلقة رسميًا.** GitHub Actions نجح.

### Phase 01H-02B — Startup & Basic Onboarding (هذه المرحلة)
⏳ **لم يتم تشغيل Build أو Unit Tests فعليًا محليًا.** بيئة التنفيذ الحالية (Sandbox) لا تملك
اتصال إنترنت، ولا Android SDK، ولا ملف `gradle-wrapper.jar` (تم التحقق فعليًا: `./gradlew
--version` فشل بـ `ClassNotFoundException`)، ولا Kotlin compiler مثبت مسبقًا — نفس القيد
الموجود في كل المراحل السابقة.

**ما تم فعله بدلاً من Build/Test فعلي (تحقق استاتيكي):** راجع القسم "ملاحظة صادقة حول حالة
Build لمرحلة 01H-02B" في `PROJECT_PHASE.md` للتفاصيل الكاملة.

**الحالة الحقيقية في `PROJECT_PHASE.md`:** `Status: Pending GitHub Validation`.

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

## 8.2 الملفات المُضافة في مرحلة 01F

```
app/src/main/java/com/gym/app/domain/repository/UserProfileRepository.kt       (جديد)
app/src/main/java/com/gym/app/domain/repository/GoalRepository.kt              (جديد)
app/src/main/java/com/gym/app/domain/repository/UserPreferencesRepository.kt   (جديد)

app/src/main/java/com/gym/app/data/repository/UserProfileRepositoryImpl.kt       (جديد)
app/src/main/java/com/gym/app/data/repository/GoalRepositoryImpl.kt              (جديد)
app/src/main/java/com/gym/app/data/repository/UserPreferencesRepositoryImpl.kt   (جديد)

app/src/main/java/com/gym/app/core/di/RepositoryModule.kt   (جديد)

app/src/test/java/com/gym/app/data/local/fake/FakeUserProfileDao.kt          (جديد)
app/src/test/java/com/gym/app/data/local/fake/FakeGoalDao.kt                 (جديد)
app/src/test/java/com/gym/app/data/local/fake/FakeUserPreferencesDao.kt      (جديد)

app/src/test/java/com/gym/app/data/repository/UserProfileRepositoryImplTest.kt       (جديد)
app/src/test/java/com/gym/app/data/repository/GoalRepositoryImplTest.kt              (جديد)
app/src/test/java/com/gym/app/data/repository/UserPreferencesRepositoryImplTest.kt   (جديد)
```

## 8.3 الملفات المُضافة في مرحلة 01G

```
app/src/main/java/com/gym/app/domain/appstate/AppState.kt               (جديد)
app/src/main/java/com/gym/app/domain/appstate/AppStateRepository.kt     (جديد)
app/src/main/java/com/gym/app/data/appstate/AppStateRepositoryImpl.kt   (جديد)
app/src/main/java/com/gym/app/core/di/DataStoreModule.kt                (جديد)
app/src/main/java/com/gym/app/core/di/AppStateRepositoryModule.kt       (جديد)

app/src/test/java/com/gym/app/data/appstate/AppStateRepositoryImplTest.kt   (جديد)
```

## 8.4 الملفات المُضافة في مرحلة 01H-02B

```
app/src/main/java/com/gym/app/ui/screens/OnboardingBasicProfileViewModel.kt   (جديد)
app/src/main/java/com/gym/app/ui/screens/OnboardingBasicProfileScreen.kt      (جديد)
app/src/main/java/com/gym/app/ui/screens/OnboardingNextPlaceholderScreen.kt   (جديد)
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

## 9.2 الملفات المُعدَّلة في مرحلة 01F

```
app/build.gradle.kts   — رفع versionName فقط إلى "0.5.0-phase01f". سطر واحد فقط تغيَّر
                          (تحقق diff فعلي، راجع الملاحظة الصادقة أدناه). لم تُضَف أي
                          dependency جديدة — طبقة الـ Repositories تعتمد حصريًا على
                          Room/Hilt/Coroutines الموجودة بالفعل منذ 01C/01E.
PROJECT_PHASE.md, PROJECT_HANDOFF.md, NEXT_TASK.md — تحديث التوثيق
```

**لم يتم لمس أي ملف آخر في 01F.** تحديدًا: `build.gradle.kts` (root)،
`.github/workflows/build-apk.yml` (إن وُجد لاحقًا — لا يزال يجب أن يبقى مثبَّتًا على
`gradle-version: '8.7'`)، `settings.gradle.kts`, `gradle.properties`, `gradle/wrapper/*`,
`gradlew`, `gradlew.bat`, `.gitignore`، كل ملفات `domain/model/` (تحقق بايت-لبايت)، كل ملفات
`data/local/entity/`, `data/local/dao/`, `data/local/converters/`, `data/local/AppDatabase.kt`,
`data/mapper/` (تحقق بايت-لبايت — لم يُعدَّل أي Mapper موجود)، `core/di/DatabaseModule.kt`,
`core/di/AppInfoProvider.kt`, `navigation/`, `ui/`, `GymApplication.kt`, `MainActivity.kt`.
تم التحقق من هذا بمقارنة بايت-لبايت مع الـ ZIP المرجعي لمرحلة 01E (راجع "ملاحظة صادقة حول حالة
Build لمرحلة 01F" في `PROJECT_PHASE.md`) — الفرق الوحيد المكتشف في كامل المشروع هو سطر
`versionName` في `app/build.gradle.kts`.

## 9.3 الملفات المُعدَّلة في مرحلة 01G

```
app/build.gradle.kts   — إضافة dependency واحدة فقط
                          (androidx.datastore:datastore-preferences:1.1.1) ورفع versionName
                          إلى "0.6.0-phase01g". لم يُمس أي سطر آخر (Compose, Hilt, Navigation,
                          Room, compileSdk/minSdk/targetSdk, kotlinOptions, kapt block، إلخ —
                          كلها كما هي بالضبط).
PROJECT_PHASE.md, PROJECT_HANDOFF.md, NEXT_TASK.md — تحديث التوثيق
```

**لم يتم لمس أي ملف آخر في 01G.** تحديدًا: `build.gradle.kts` (root)،
`.github/workflows/build-apk.yml` (إن وُجد لاحقًا — لا يزال يجب أن يبقى مثبَّتًا على
`gradle-version: '8.7'`)، `settings.gradle.kts`, `gradle.properties`, `gradle/wrapper/*`,
`gradlew`, `gradlew.bat`, `.gitignore`، كل ملفات `domain/model/` (تحقق بايت-لبايت)، كل ملفات
`data/local/entity/`, `data/local/dao/`, `data/local/converters/`, `data/local/AppDatabase.kt`,
`data/mapper/`, `domain/repository/`, `data/repository/` (تحقق بايت-لبايت — طبقة الـ
Repositories من 01F لم تُعدَّل)، `core/di/DatabaseModule.kt`, `core/di/RepositoryModule.kt`,
`core/di/AppInfoProvider.kt`, `navigation/`, `ui/`, `GymApplication.kt`, `MainActivity.kt`,
`AndroidManifest.xml`. تم التحقق من هذا بمقارنة بايت-لبايت مع الـ ZIP المرجعي لمرحلة 01F (راجع
"ملاحظة صادقة حول حالة Build لمرحلة 01G" في `PROJECT_PHASE.md`) — الفرق الوحيد المكتشف في كامل
المشروع (بخلاف الملفات الجديدة أعلاه) هو `app/build.gradle.kts` كما هو موضّح.

## 9.4 الملفات المُعدَّلة في مرحلة 01H-02B

```
app/src/main/java/com/gym/app/navigation/Routes.kt        — إضافة sealed class Onboarding
                                                              متداخل فقط (Graph, BasicProfile,
                                                              NextPlaceholder). لم تُحذف أو
                                                              تُعدَّل أي Routes موجودة
                                                              (Start, Home, Workout, Progress,
                                                              Settings).
app/src/main/java/com/gym/app/navigation/AppNavHost.kt     — إضافة بلوك navigation() واحد
                                                              لتدفق onboarding فقط، بعد الـ
                                                              composable الخمسة الموجودة دون أي
                                                              تعديل عليها.
app/src/main/java/com/gym/app/ui/screens/StartViewModel.kt — إعادة كتابة جذرية: من ViewModel
                                                              تقني تحقق فقط من عمل الحقن (01C)
                                                              إلى منطق قرار بدء التشغيل الفعلي
                                                              (Phase 01H)، بالاعتماد على
                                                              AppStateRepository (01G).
app/src/main/java/com/gym/app/ui/screens/StartScreen.kt    — إعادة كتابة جذرية: من شاشة اختبار
                                                              تنقل يدوي (أزرار للانتقال لكل
                                                              وجهة) إلى شاشة توجيه بحتة تراقب
                                                              StartViewModel.destination
                                                              وتتنقل تلقائيًا.
app/src/main/res/values/strings.xml                         — إضافة سلاسل نصية عربية جديدة فقط
                                                              (شاشة التحميل + شاشة Basic Profile
                                                              Input + الشاشة النائبة). لم تُحذف
                                                              أو تُعدَّل أي سلسلة موجودة.
PROJECT_PHASE.md, PROJECT_HANDOFF.md, NEXT_TASK.md          — تحديث التوثيق
```

**لم يتم لمس أي ملف آخر في 01H-02B.** تحديدًا: `app/build.gradle.kts` (لم تُضَف أي dependency
جديدة، لم يتغيّر `versionName`)، `build.gradle.kts` (root)، `.github/workflows/build-apk.yml`
(إن وُجد لاحقًا)، `settings.gradle.kts`, `gradle.properties`, `gradle/wrapper/*`, `gradlew`,
`gradlew.bat`, `.gitignore`، كل ملفات `domain/model/`, `domain/repository/`, `domain/appstate/`
(تحقق بايت-لبايت)، كل ملفات `data/local/*`, `data/mapper/*`, `data/repository/*`,
`data/appstate/*` (تحقق بايت-لبايت — طبقتا الـ Repositories من 01F وDataStore من 01G لم
تُعدَّلا)، `core/di/*` (كل الوحدات الموجودة كما هي)، `AndroidManifest.xml`, `GymApplication.kt`,
`MainActivity.kt`, `ui/theme/Theme.kt`, وشاشات `HomeScreen.kt`/`WorkoutScreen.kt`/
`ProgressScreen.kt`/`SettingsScreen.kt`/`NavTestScreenContent.kt` (لا تزال شاشات اختبار تنقل
كما هي بالضبط).

---

## 10. قاعدة معمارية المنتج المستقبلية — النماذج أصبح لها Repositories الآن (01F)، وحالة
التطبيق البسيطة أصبح لها DataStore (01G)

بنية المنتج الكاملة مستقبلًا تعتمد جوهريًا على:

**User Profile + Goal + Preferences**

✅ منذ Phase 01D، هذه النماذج الثلاثة موجودة فعليًا كـ domain models نقية في
`app/src/main/java/com/gym/app/domain/model/` (راجع القسم 4.8). ✅ منذ Phase 01E، أصبح لهذه
النماذج تخزين فعلي عبر Room (Entities + DAOs + AppDatabase + Mappers، راجع القسم 4.9)، متاح
للحقن عبر Hilt (`DatabaseModule`). ✅ منذ Phase 01F، أصبحت هذه النماذج قابلة للوصول عبر طبقة
Repositories نظيفة (`domain/repository/` + `data/repository/` + `RepositoryModule`، راجع
القسم 4.10) تفصل الـ domain تمامًا عن Room. كل ميزة مستقبلية يجب أن **تقرأ** بياناتها الخاصة
بالمستخدم من هذه الـ Repositories — عبر حقن `UserProfileRepository`/`GoalRepository`/
`UserPreferencesRepository` (لا الـ DAOs مباشرة، ولا الـ Entities) — بدلاً من استخدام بيانات
ثابتة معزولة أو حالة مستخدم مكررة في كل ميزة على حدة. أمثلة:

- **Workout** سيعتمد على: User Profile + Goal + Preferences
- **Nutrition** سيعتمد على: User Profile + Goal + Preferences
- **Recovery** سيعتمد على: User Profile + Preferences + سجل التمارين + سجل النوم + سجل الألم
- **Home** سيجمّع (aggregate) معلومات من ميزات مختلفة للمستخدم النشط الحالي

✅ منذ Phase 01G، حالة التطبيق البسيطة (`onboardingCompleted`, `activeUserId`) أصبحت متاحة عبر
`AppStateRepository` (`domain/appstate/` + `data/appstate/` + `DataStoreModule`/
`AppStateRepositoryModule`، راجع القسم 4.11)، مدعومة بـ Preferences DataStore بدلاً من Room.
✅ منذ Phase 01H-02B، منطق بدء التشغيل الفعلي (`StartViewModel`/`StartScreen`، راجع القسم
4.12) **يقرأ** من `AppStateRepository` — وليس من DataStore مباشرة، ولا من قيم ثابتة — ويحسم
واحدة من ثلاث نتائج توجيه (Home / Onboarding / قيد التحديد). كذلك أصبح هناك أول شاشة onboarding
حقيقية (Basic Profile Input) تحفظ عبر `UserProfileRepository` وتُحدِّث `activeUserId` عبر
`AppStateRepository`.

⚠️ **مهم:** لا تزال `onboardingCompleted` غير مضبوطة على `true` في أي مكان — ستبقى كذلك حتى
تكتمل خطوات onboarding المتبقية (Goal Setup 02C فما بعدها) وتقرر صراحة أن التدفق انتهى. لا
Use Cases منفصلة بعد فوق طبقة الـ Repositories/AppStateRepository لأي ميزة حقيقية أخرى
(Workout/Nutrition/Recovery/Home الفعلية).

---

## 11. ما لم يتم تنفيذه (بالتصميم — ممنوع حتى الآن)

- [x] ~~Hilt~~ — تم في 01C (أساس فقط، بدون Repositories حقيقية)
- [x] ~~User Profile (Model)~~ — تم في 01D (domain model فقط، بدون تخزين)
- [x] ~~Goal (Model)~~ — تم في 01D (domain model فقط، بدون تخزين)
- [x] ~~Preferences (Model)~~ — تم في 01D (domain model فقط، بدون تخزين)
- [x] ~~Room / Database~~ — تم في 01E (Entities + DAOs + AppDatabase + Mappers، بدون
      Repositories حقيقية بعد)
- [x] ~~Repositories حقيقية~~ — تم في 01F (تربط الـ domain بـ Room بشكل نظيف عبر
      `domain/repository/` + `data/repository/` + Hilt `@Binds`، بدون Use Cases بعد)
- [x] ~~DataStore~~ — تم في 01G (أساس Preferences DataStore فقط لحالة تطبيق بسيطة —
      `onboardingCompleted`, `activeUserId` — عبر `domain/appstate/` + `data/appstate/` +
      Hilt، منفصل تمامًا عن Room، بدون Onboarding UI أو App Start Logic بعد)
- [x] ~~App Start Logic~~ — تم في 01H (منطق قرار التوجيه الثلاثي، معزول في `StartViewModel`،
      عبر `AppStateRepository`)
- [x] ~~Onboarding Flow Foundation~~ — تم في 02A (`Routes.Onboarding` sub-graph + تسجيله في
      `AppNavHost`)
- [x] ~~Basic Profile Input~~ — تم في 02B (أول شاشة onboarding حقيقية، تحفظ عبر
      `UserProfileRepository` وتُحدِّث `activeUserId`، بدون إكمال onboarding)
- [ ] Use Cases (تنسيق منطق ميزات حقيقي فوق الـ Repositories، لغير التوجيه/الحفظ البسيط أعلاه)
- [ ] Goal Setup (Phase 02C)
- [ ] Workout Preferences / Lifestyle Preferences
- [ ] إكمال onboarding (`onboardingCompleted = true`)
- [ ] أي منطق Workout / Nutrition / Smart Assistant حقيقي
- [ ] Login / Cloud Sync
- [ ] دعم لغات متعددة أو تبديل لغة
- [ ] أي بيانات مستخدم وهمية (Fake user data) أو معلومات شخصية Hardcoded
- [ ] تشغيل Build/Test فعلي ناجح داخل بيئة Sandbox (سيتم التحقق عبر GitHub Actions)
- [ ] أي عمل من Phase 02C

---

## 12. قواعد للمطوّر/المرحلة التالية (Rules for the Next Developer)

1. **لا تعيد بناء المشروع من الصفر.** هذا المستودع على GitHub هو مصدر الحقيقة الوحيد بين
   المراحل والجلسات المستقبلية.
2. **لا تحذف أو تستبدل** أي ميزة تعمل حاليًا (Gradle setup, Workflow, بنية التنقل, أساس
   الحقن, نماذج الـ domain, أساس Room, طبقة الـ Repositories) إلا إذا طُلب ذلك صراحة.
3. **لا تُدرِج أي بيانات مستخدم Hardcoded** في أي مكان — كل بيانات المستخدم الحقيقية يجب أن
   تأتي لاحقًا من النموذج المركزي (User Profile + Goal + Preferences، الموجود الآن في
   `domain/model/` مع تخزين Room في `data/local/` وطبقة Repositories في `domain/repository/`
   و`data/repository/`) عبر طبقة الحقن، وليس من قيم ثابتة داخل الكود.
4. **كل نص ظاهر للمستخدم يجب أن يكون عربيًا فقط** عبر `res/values/strings.xml` — ممنوع أي
   نص Hardcoded داخل ملفات Kotlin.
5. **لا تُنشئ** `res/values-ar/strings.xml` أو أي مجلد لغة إضافي.
6. **أساس DataStore (01G) منفصل تمامًا عن Room — حافظ على هذا الفصل.** لا تُضِف بيانات
   User Profile/Goal/Preferences إلى DataStore، ولا تُضِف حالة تطبيق بسيطة جديدة (مثل
   `onboardingCompleted`/`activeUserId`) إلى Room. استخدم `AppStateRepository`
   (`domain/appstate/`) فقط لحالة التطبيق البسيطة، ولا توسّعه إلا بحقول تنتمي فعليًا لنفس
   الفئة (حالة تدفق تطبيق، وليست بيانات مستخدم/هدف/تفضيلات).
7. **حافظ على نماذج الـ domain نقية:** لا Room annotations، لا DataStore، لا Android
   framework types، لا Hilt داخل حزمة `domain.model` — التحويل من/إلى Room يتم حصريًا عبر
   `data/mapper/` (راجع القسم 4.9.7)، ليس داخل النماذج نفسها. كذلك حافظ على واجهات
   `domain/repository/` و `domain/appstate/` نقية: لا Room entities، لا DataStore/Preferences
   types، ولا Hilt annotations تظهر في توقيعات هذه الواجهات، فقط نماذج domain وFlow/suspend من
   Kotlin/Coroutines.
8. **GitHub يبقى مصدر الحقيقة (Source of Truth)** — لا تدّعِ نجاح Build أو Tests إلا بعد تأكيد
   فعلي عبر GitHub Actions أو تنفيذ محلي حقيقي.
9. **لا تبدأ أي مرحلة تالية** (مثل 02C — Goal Setup) دون طلب صريح من المستخدم.
10. **حافظ على قسم "المشاكل المُصلَحة سابقًا" (Known Fixed Issues، القسم 7.1) دائمًا** — لا
    تحذف أي عنصر منه، وأضِف أي مشكلة جديدة تُكتشف وتُصلَح إلى نفس القسم.
11. **استخدم الـ Repositories (`domain/repository/`) دائمًا من أي ViewModel/Use Case مستقبلي
    — لا الـ DAOs مباشرة.** طبقة الـ Repositories (01F) هي الآن الحد الفاصل الرسمي بين طبقة
    البيانات (Room) وأي طبقة أعلى.
12. **استخدم `AppStateRepository` (`domain/appstate/`، 01G) لأي حالة تطبيق بسيطة مستقبلية
    (مثل App Start Logic في 01H) — لا الوصول إلى `DataStore<Preferences>` مباشرة من خارج
    `data/appstate/`.**

## KNOWN FIX #2 — MATERIAL3 EXPOSED DROPDOWN COMPILATION

**Problem:** GitHub Actions failed during `:app:compileDebugKotlin` with:

`Unresolved reference: ExposedDropdownMenu`

**File:**

`app/src/main/java/com/gym/app/ui/screens/OnboardingBasicProfileScreen.kt`

**Root Cause:** The current Material3 setup does not expose `androidx.compose.material3.ExposedDropdownMenu` as used by the screen.

**Fix:** Replaced `ExposedDropdownMenu` with `DropdownMenu` while preserving the existing `ExposedDropdownMenuBox`, anchor behavior, menu items, Arabic UI, RTL, and onboarding logic.

**Do not repeat:** Do not import or use `androidx.compose.material3.ExposedDropdownMenu` unless the project's actual Material3 version is first verified to support it.

**Validation status:** Pending GitHub Actions validation.

## KNOWN FIX #2A — OVER-BROAD STRING REPLACEMENT DURING FIX1

**Problem:** FIX1 caused additional Kotlin compilation errors for `ExposedDropdownMenuBox`, `ExposedDropdownMenuDefaults`, and `menuAnchor`.

**Root Cause:** The automated text replacement changed the substring `ExposedDropdownMenu` inside valid API names:
- `ExposedDropdownMenuBox` became `DropdownMenuBox`
- `ExposedDropdownMenuDefaults` became `DropdownMenuDefaults`

**Fix:** Restored the valid Material3 imports:
- `androidx.compose.material3.ExposedDropdownMenuBox`
- `androidx.compose.material3.ExposedDropdownMenuDefaults`

The menu itself remains `DropdownMenu`, which is the intended compatibility fix.

**Do not repeat:** Use exact-token or AST-aware replacements for API migrations. Do not use broad substring replacement when related API names share the same prefix.

**Validation status:** Pending GitHub Actions validation.

