package com.gym.app.data.mapper

import com.gym.app.data.local.entity.UserProfileEntity
import com.gym.app.domain.model.ActivityLevel
import com.gym.app.domain.model.ExperienceLevel
import com.gym.app.domain.model.Gender
import com.gym.app.domain.model.UserProfile

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Pure mapping functions between [UserProfile] (domain) and
 * [UserProfileEntity] (Room persistence). Kept outside both layers so
 * neither the domain model nor the entity needs to know about the other.
 */

/** Converts this domain [UserProfile] into its Room [UserProfileEntity] representation. */
fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    userId = userId,
    name = name,
    age = age,
    gender = gender?.name,
    heightCm = heightCm,
    currentWeightKg = currentWeightKg,
    experienceLevel = experienceLevel.name,
    activityLevel = activityLevel.name
)

/** Converts this Room [UserProfileEntity] back into the domain [UserProfile] model. */
fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    userId = userId,
    name = name,
    age = age,
    gender = gender?.let { Gender.valueOf(it) },
    heightCm = heightCm,
    currentWeightKg = currentWeightKg,
    experienceLevel = ExperienceLevel.valueOf(experienceLevel),
    activityLevel = ActivityLevel.valueOf(activityLevel)
)
