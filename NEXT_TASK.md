PHASE 01G — DATASTORE FOUNDATION

Phase 01F (User Repositories) is complete:

- Domain repository interfaces added: UserProfileRepository, GoalRepository,
  UserPreferencesRepository (app/src/main/java/com/gym/app/domain/repository/)
- Data-layer implementations added, backed by the existing Phase 01E DAOs
  and mappers: UserProfileRepositoryImpl, GoalRepositoryImpl,
  UserPreferencesRepositoryImpl (app/src/main/java/com/gym/app/data/repository/)
- Hilt bindings added via RepositoryModule (@Binds), wiring each interface
  to its implementation (app/src/main/java/com/gym/app/core/di/RepositoryModule.kt)
- Repository unit tests added using in-memory fake DAOs (no Room/Mockito
  needed): app/src/test/java/com/gym/app/data/repository/ and
  app/src/test/java/com/gym/app/data/local/fake/

The next phase will add a DataStore-backed foundation, sitting alongside
(not replacing) the Room-backed repositories from Phase 01F. Typical scope
for this kind of phase:

- Preferences DataStore setup (e.g. for simple app-level settings that do
  not belong in the per-user Room tables — such as onboarding-completed
  flags or other lightweight app state), added incrementally without
  touching the existing Room entities, DAOs, mappers, or repositories.
- A small DataStore wrapper/manager class.
- Hilt bindings/provisions for the DataStore instance.
- Basic unit tests for the new DataStore code.

The next phase must not add:

- Onboarding UI
- Real feature logic (Workout, Nutrition, Recovery, Home, Smart Assistant)
- Login / cloud sync
- Any UI screens
- Use cases
- Changes to the existing domain models, Room entities, DAOs, mappers, or
  the Phase 01F repository interfaces/implementations, unless explicitly
  required to wire in the new DataStore code

Do not implement Phase 01G now.
