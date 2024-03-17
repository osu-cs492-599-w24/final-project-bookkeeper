package edu.oregonstate.cs492.bookkeeper.data

class LibraryRepository (
    private val dao: LibraryBookDao
) {

    suspend fun insertBook(book: LibraryBook) = dao.insert(book)

    suspend fun deleteBook(title: String, author: String) = dao.delete(title, author)

    suspend fun deleteAllBooks() = dao.deleteAll()

    fun getBook(title: String, author: String) = dao.getBook(title, author)

    fun getAllBooks() = dao.getAllBooks()
    fun getBookByTitleOrAuthor(query: String) = dao.getBookByTitleOrAuthor(query)
}