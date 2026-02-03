import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import userService from '../services/userService';
import Loading from '../components/common/Loading.jsx';
import BookCard from '../components/books/BookCard.jsx';

const Recommendations = () => {
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchRecommendations();
  }, []);

  const fetchRecommendations = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await userService.getRecommendations();
      setRecommendations(data);
    } catch (err) {
      setError('Failed to load recommendations. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <Loading size="lg" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Your Personalized Recommendations</h1>
        <p className="text-gray-600">
          Based on your reading history and ratings, we think you'll love these books.
        </p>
      </div>

      {error && (
        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-md">
          <p className="text-red-800">{error}</p>
        </div>
      )}

      {recommendations.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg shadow-md">
          <div className="text-6xl mb-4">📚</div>
          <h2 className="text-2xl font-bold text-gray-900 mb-4">No Recommendations Yet</h2>
          <p className="text-gray-600 mb-6 max-w-md mx-auto">
            Start rating books in your reading list to get personalized recommendations!
          </p>
          <div className="space-x-4">
            <Link
              to="/books"
              className="inline-block px-6 py-3 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors"
            >
              Browse Books
            </Link>
            <Link
              to="/profile"
              className="inline-block px-6 py-3 bg-white text-primary-600 border-2 border-primary-600 rounded-md hover:bg-primary-50 transition-colors"
            >
              View My Books
            </Link>
          </div>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {recommendations.map((book) => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>
          <div className="mt-8 text-center">
            <p className="text-gray-600 mb-4">Want more recommendations?</p>
            <Link
              to="/books"
              className="text-primary-600 hover:text-primary-700 font-medium"
            >
              Rate more books to improve your recommendations →
            </Link>
          </div>
        </>
      )}
    </div>
  );
};

export default Recommendations;
