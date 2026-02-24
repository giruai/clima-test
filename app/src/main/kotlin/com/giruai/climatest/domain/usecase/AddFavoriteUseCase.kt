package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.repository.FavoritesRepository
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(city: City): Result<Unit> {
        val count = repository.count()
        if (count >= MAX_FAVORITES) {
            return Result.failure(Exception("Maximum of $MAX_FAVORITES favorites reached"))
        }
        repository.add(city)
        return Result.success(Unit)
    }

    companion object {
        const val MAX_FAVORITES = 10
    }
}
