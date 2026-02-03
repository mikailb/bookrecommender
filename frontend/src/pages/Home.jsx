import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import bookService from '../services/bookService';
import BookCard from '../components/books/BookCard.jsx';

const Home = () => {
  const { isAuthenticated } = useAuth();
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        const data = await bookService.getAllBooks(0, 8);
        setBooks(data.content || []);
      } catch (err) {
        console.error('Failed to load books:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchBooks();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-white">
      {/* Hero Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div className="text-center">
          <h1 className="text-5xl md:text-6xl font-extrabold text-gray-900 mb-6">
            Discover Your Next
            <span className="text-primary-600"> Great Read</span>
          </h1>
          <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
            Get personalized book recommendations based on your reading history and preferences.
            Join thousands of readers finding their perfect books.
          </p>
          <div className="flex justify-center gap-4">
            {isAuthenticated ? (
              <>
                <Link
                  to="/books"
                  className="px-8 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 font-medium text-lg transition-colors shadow-lg"
                >
                  Browse Books
                </Link>
                <Link
                  to="/recommendations"
                  className="px-8 py-3 bg-white text-primary-600 border-2 border-primary-600 rounded-lg hover:bg-primary-50 font-medium text-lg transition-colors shadow-lg"
                >
                  Get Recommendations
                </Link>
              </>
            ) : (
              <>
                <Link
                  to="/register"
                  className="px-8 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 font-medium text-lg transition-colors shadow-lg"
                >
                  Get Started
                </Link>
                <Link
                  to="/login"
                  className="px-8 py-3 bg-white text-primary-600 border-2 border-primary-600 rounded-lg hover:bg-primary-50 font-medium text-lg transition-colors shadow-lg"
                >
                  Sign In
                </Link>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Featured Books Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-3xl font-bold text-gray-900">
            Featured Books
          </h2>
          <Link
            to="/books"
            className="text-primary-600 hover:text-primary-700 font-medium"
          >
            View All →
          </Link>
        </div>
        
        {loading ? (
          <div className="text-center py-8">
            <p className="text-gray-600">Loading books...</p>
          </div>
        ) : books.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {books.map((book) => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>
        ) : (
          <div className="text-center py-8">
            <p className="text-gray-600">No books available yet. Check back soon!</p>
          </div>
        )}
      </div>

      {/* Features Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
          Why Choose BookRec?
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="bg-white rounded-lg shadow-lg p-8 text-center transform hover:scale-105 transition-transform">
            <div className="text-5xl mb-4">🎯</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-3">
              Personalized Recommendations
            </h3>
            <p className="text-gray-600">
              Get book suggestions tailored to your taste based on your reading history and ratings.
            </p>
          </div>
          <div className="bg-white rounded-lg shadow-lg p-8 text-center transform hover:scale-105 transition-transform">
            <div className="text-5xl mb-4">📚</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-3">Vast Library</h3>
            <p className="text-gray-600">
              Explore thousands of books across all genres. Find classics, bestsellers, and hidden gems.
            </p>
          </div>
          <div className="bg-white rounded-lg shadow-lg p-8 text-center transform hover:scale-105 transition-transform">
            <div className="text-5xl mb-4">⭐</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-3">Rate & Track</h3>
            <p className="text-gray-600">
              Rate books you've read and keep track of your reading journey all in one place.
            </p>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      {!isAuthenticated && (
        <div className="bg-primary-600 py-16">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <h2 className="text-3xl font-bold text-white mb-4">
              Ready to find your next favorite book?
            </h2>
            <p className="text-primary-100 text-lg mb-8">
              Join our community of readers today and start your personalized reading journey.
            </p>
            <Link
              to="/register"
              className="inline-block px-8 py-3 bg-white text-primary-600 rounded-lg hover:bg-gray-100 font-medium text-lg transition-colors shadow-lg"
            >
              Create Free Account
            </Link>
          </div>
        </div>
      )}

      {/* Stats Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
          <div>
            <p className="text-4xl font-bold text-primary-600 mb-2">10,000+</p>
            <p className="text-gray-600">Books Available</p>
          </div>
          <div>
            <p className="text-4xl font-bold text-primary-600 mb-2">5,000+</p>
            <p className="text-gray-600">Active Readers</p>
          </div>
          <div>
            <p className="text-4xl font-bold text-primary-600 mb-2">50,000+</p>
            <p className="text-gray-600">Ratings Submitted</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
