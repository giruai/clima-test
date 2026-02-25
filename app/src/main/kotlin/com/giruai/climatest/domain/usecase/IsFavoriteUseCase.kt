package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.repository.FavoritesRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(cityId: Long): Boolean {
        return repository.isFavorite(cityId)
    }
}
