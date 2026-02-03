import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { useSearchParams } from 'react-router-dom';
import bookService from '../../services/bookService';
import userService from '../../services/userService';
import { useAuth } from '../../context/AuthContext';
import BookCard from './BookCard.jsx';
import Loading from '../common/Loading.jsx';

const BookList = () => {
  const { isAuthenticated } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [totalPages, setTotalPages] = useState(0);
  const [searching, setSearching] = useState(false);
  const [userBooks, setUserBooks] = useState([]);
  
  // Get current page from URL query params, default to 0
  const currentPage = parseInt(searchParams.get('page') || '0', 10);

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await bookService.getAllBooks(currentPage, 12);
      setBooks(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError('Failed to load books. Please try again later.');
    } finally {
      setLoading(false);
    }
  }, [currentPage]);

  useEffect(() => {
    fetchBooks();
  }, [fetchBooks]);

  // Fetch user's books if authenticated
  useEffect(() => {
    const fetchUserBooks = async () => {
      if (isAuthenticated) {
        try {
          const data = await userService.getUserBooks();
          setUserBooks(data);
        } catch (err) {
          console.error('Failed to fetch user books:', err);
        }
      }
    };

    fetchUserBooks();
  }, [isAuthenticated]);

  // Create lookup map for quick access to user's books
  const userBookMap = useMemo(() => {
    const map = new Map();
    userBooks.forEach(ub => map.set(ub.book.id, ub));
    return map;
  }, [userBooks]);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) {
      fetchBooks();
      return;
    }

    setSearching(true);
    setError('');
    try {
      const data = await bookService.searchBooks(searchQuery, 0, 12);
      setBooks(data.content);
      setTotalPages(data.totalPages);
      setSearchParams({ page: '0' });
    } catch (err) {
      setError('Failed to search books. Please try again.');
    } finally {
      setSearching(false);
    }
  };

  const handlePageChange = (newPage) => {
    setSearchParams({ page: newPage.toString() });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const clearSearch = () => {
    setSearchQuery('');
    setSearchParams({ page: '0' });
  };

  const handleRemoveBook = async (bookId) => {
    try {
      await userService.removeBookFromList(bookId);
      setUserBooks(prev => prev.filter(ub => ub.book.id !== bookId));
    } catch (err) {
      console.error('Failed to remove book:', err);
    }
  };

  const handleToggleFavorite = async (bookId) => {
    try {
      await userService.toggleFavorite(bookId);
      setUserBooks(prev => prev.map(ub => 
        ub.book.id === bookId ? { ...ub, isFavorite: !ub.isFavorite } : ub
      ));
    } catch (err) {
      console.error('Failed to toggle favorite:', err);
    }
  };

  if (loading && currentPage === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <Loading size="lg" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Search Bar */}
      <div className="mb-8">
        <form onSubmit={handleSearch} className="flex gap-2">
          <input
            type="text"
            placeholder="Search books by title, author, or genre..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          />
          <button
            type="submit"
            disabled={searching}
            className="px-6 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50 transition-colors"
          >
            {searching ? 'Searching...' : 'Search'}
          </button>
          {searchQuery && (
            <button
              type="button"
              onClick={clearSearch}
              className="px-6 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400 transition-colors"
            >
              Clear
            </button>
          )}
        </form>
      </div>

      {error && (
        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-md">
          <p className="text-red-800">{error}</p>
        </div>
      )}

      {/* Books Grid */}
      {books.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600 text-lg">No books found.</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {books.map((book) => (
              <BookCard 
                key={book.id} 
                book={book}
                userBook={userBookMap.get(book.id)}
                onRemove={handleRemoveBook}
                onToggleFavorite={handleToggleFavorite}
              />
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="mt-8 flex justify-center items-center space-x-2">
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="px-4 py-2 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Previous
              </button>
              <span className="px-4 py-2 text-gray-700">
                Page {currentPage + 1} of {totalPages}
              </span>
              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
                className="px-4 py-2 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default BookList;
