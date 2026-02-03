import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import userService from '../../services/userService';
import Loading from '../common/Loading.jsx';

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [userBooks, setUserBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('all');

  useEffect(() => {
    fetchProfileData();
  }, []);

  const fetchProfileData = async () => {
    setLoading(true);
    setError('');
    try {
      const [profileData, booksData] = await Promise.all([
        userService.getProfile(),
        userService.getUserBooks(),
      ]);
      setProfile(profileData);
      setUserBooks(booksData);
    } catch (err) {
      setError('Failed to load profile data. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const getRatingStars = (rating) => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <span key={i} className={i <= rating ? 'text-yellow-400' : 'text-gray-300'}>
          ★
        </span>
      );
    }
    return stars;
  };

  const filteredBooks = userBooks.filter(book => {
    switch (filter) {
      case 'favorites': return book.isFavorite;
      case 'rated': return book.rating > 0;
      case 'unrated': return !book.rating;
      default: return true;
    }
  });

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <Loading size="lg" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-red-50 border border-red-200 rounded-md p-4">
          <p className="text-red-800">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Profile Header */}
      <div className="bg-white rounded-lg shadow-md p-6 mb-8">
        <div className="flex items-center space-x-4">
          <div className="bg-primary-600 text-white rounded-full h-20 w-20 flex items-center justify-center text-3xl font-bold">
            {profile?.name?.charAt(0).toUpperCase()}
          </div>
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{profile?.name}</h1>
            <p className="text-gray-600">{profile?.email}</p>
            <p className="text-sm text-gray-500 mt-1">
              Member since {new Date(profile?.createdAt).toLocaleDateString()}
            </p>
          </div>
        </div>

        <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-primary-50 rounded-lg p-4 text-center">
            <p className="text-3xl font-bold text-primary-600">{userBooks.length}</p>
            <p className="text-gray-600">Books in Library</p>
          </div>
          <div className="bg-primary-50 rounded-lg p-4 text-center">
            <p className="text-3xl font-bold text-primary-600">
              {userBooks.filter((book) => book.rating).length}
            </p>
            <p className="text-gray-600">Books Rated</p>
          </div>
          <div className="bg-primary-50 rounded-lg p-4 text-center">
            <p className="text-3xl font-bold text-primary-600">
              {userBooks.filter((book) => book.isFavorite).length}
            </p>
            <p className="text-gray-600">Favorite Books</p>
          </div>
        </div>
      </div>

      {/* My Books Section */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-900">My Books</h2>
          <Link
            to="/books"
            className="text-primary-600 hover:text-primary-700 text-sm font-medium"
          >
            Browse More Books →
          </Link>
        </div>

        {/* Filter Tabs */}
        <div className="flex gap-2 mb-6 overflow-x-auto">
          <button
            onClick={() => setFilter('all')}
            className={`px-4 py-2 rounded-md font-medium transition-colors ${
              filter === 'all'
                ? 'bg-primary-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            All Books ({userBooks.length})
          </button>
          <button
            onClick={() => setFilter('favorites')}
            className={`px-4 py-2 rounded-md font-medium transition-colors ${
              filter === 'favorites'
                ? 'bg-primary-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Favorites ({userBooks.filter(b => b.isFavorite).length})
          </button>
          <button
            onClick={() => setFilter('rated')}
            className={`px-4 py-2 rounded-md font-medium transition-colors ${
              filter === 'rated'
                ? 'bg-primary-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Rated ({userBooks.filter(b => b.rating > 0).length})
          </button>
          <button
            onClick={() => setFilter('unrated')}
            className={`px-4 py-2 rounded-md font-medium transition-colors ${
              filter === 'unrated'
                ? 'bg-primary-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            Unrated ({userBooks.filter(b => !b.rating).length})
          </button>
        </div>

        {userBooks.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-600 mb-4">You haven't added any books yet.</p>
            <Link
              to="/books"
              className="inline-block px-6 py-3 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors"
            >
              Explore Books
            </Link>
          </div>
        ) : filteredBooks.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-600 mb-4">No books found with the selected filter.</p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredBooks.map((userBook) => (
              <div
                key={userBook.id}
                className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
              >
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <Link
                      to={`/books/${userBook.book.id}`}
                      className="text-lg font-semibold text-gray-900 hover:text-primary-600"
                    >
                      {userBook.book.title}
                    </Link>
                    <p className="text-gray-600">{userBook.book.author}</p>
                    <p className="text-sm text-gray-500">{userBook.book.genre}</p>
                    {userBook.readAt && (
                      <p className="text-xs text-gray-500 mt-2">
                        Read on {new Date(userBook.readAt).toLocaleDateString()}
                      </p>
                    )}
                  </div>
                  <div className="text-right">
                    {userBook.rating ? (
                      <div>
                        <p className="text-sm text-gray-600 mb-1">Your Rating:</p>
                        <div className="flex">{getRatingStars(userBook.rating)}</div>
                      </div>
                    ) : (
                      <Link
                        to={`/books/${userBook.book.id}`}
                        className="text-sm text-primary-600 hover:text-primary-700"
                      >
                        Rate this book
                      </Link>
                    )}
                    {userBook.isFavorite && (
                      <div className="mt-2">
                        <span className="inline-block bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded-full">
                          ❤️ Favorite
                        </span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Profile;
