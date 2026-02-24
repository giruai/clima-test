package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<City>> = repository.getAll()
}
