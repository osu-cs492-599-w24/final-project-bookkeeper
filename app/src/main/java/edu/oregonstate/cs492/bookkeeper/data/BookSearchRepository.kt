package edu.oregonstate.cs492.bookkeeper.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookSearchRepository(
    private val service: OpenLibraryService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadBookSearch(query: String): Result<BookSearch> =
        withContext(ioDispatcher) {
            try {
                val response = service.getBooks(query)
                val results = response.body()
                if (response.isSuccessful && results != null) {
                    Result.success(results)
                } else {
                    Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}