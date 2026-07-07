PHASE 01F — USER REPOSITORIES

The next phase will add repositories that sit between the domain layer and
the Room persistence layer added in Phase 01E:

- UserProfileRepository
- GoalRepository
- UserPreferencesRepository

The next phase may add:

- Repository interfaces (in domain or a repository contract package)
- Repository implementations backed by:
  - UserProfileDao / GoalDao / UserPreferencesDao (Phase 01E)
  - UserProfileMapper / GoalMapper / UserPreferencesMapper (Phase 01E)
- Hilt bindings (@Binds or @Provides) wiring interfaces to implementations
- Basic repository-level unit tests (with fake/in-memory DAOs or Room
  in-memory database)

The next phase must not add:

- DataStore
- Onboarding UI
- Real feature logic (Workout, Nutrition, Recovery, Home, Smart Assistant)
- Login / cloud sync
- Any UI screens

Do not implement Phase 01F now.
