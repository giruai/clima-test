package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.repository.FavoritesRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(cityId: Long): Result<Unit> {
        return repository.removeFavorite(cityId)
    }
}
