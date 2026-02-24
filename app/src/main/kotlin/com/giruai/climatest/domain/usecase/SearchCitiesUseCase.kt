package com.giruai.climatest.domain.usecase

import com.giruai.climatest.domain.model.City
import com.giruai.climatest.domain.repository.WeatherRepository
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(query: String): Result<List<City>> {
        if (query.length < 2) return Result.success(emptyList())
        return repository.searchCities(query)
    }
}
