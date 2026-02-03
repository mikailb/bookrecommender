import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import bookService from '../../services/bookService';
import userService from '../../services/userService';
import Loading from '../common/Loading.jsx';

const BookDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [submittingRating, setSubmittingRating] = useState(false);
  const [addingToList, setAddingToList] = useState(false);
  const [isBookInList, setIsBookInList] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);
  const [togglingFavorite, setTogglingFavorite] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [imageError, setImageError] = useState(false);

  useEffect(() => {
    const fetchBook = async () => {
      setLoading(true);
      setError('');
      try {
        const data = await bookService.getBookById(id);
        setBook(data);
        
        // Check if book is in user's list
        if (isAuthenticated) {
          const userBooks = await userService.getUserBooks();
          const bookInList = userBooks.some(ub => ub.book.id === parseInt(id));
          setIsBookInList(bookInList);
          
          // Set current rating and favorite status if exists
          const userBookEntry = userBooks.find(ub => ub.book.id === parseInt(id));
          if (userBookEntry) {
            if (userBookEntry.rating) {
              setRating(userBookEntry.rating);
            }
            setIsFavorite(userBookEntry.isFavorite || false);
          }
        }
      } catch (err) {
        setError('Failed to load book details. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchBook();
  }, [id, isAuthenticated]);

  const handleAddToList = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    
    setAddingToList(true);
    setError('');
    setSuccessMessage('');
    try {
      await userService.addBookToList(id);
      setIsBookInList(true);
      setSuccessMessage('Book added to your reading list!');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      if (err.response?.status === 409) {
        setError('This book is already in your reading list.');
        setIsBookInList(true);
      } else {
        setError('Failed to add book to your list. Please try again.');
      }
    } finally {
      setAddingToList(false);
    }
  };

  const handleRemoveFromList = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    
    setAddingToList(true);
    setError('');
    setSuccessMessage('');
    try {
      await userService.removeBookFromList(id);
      setIsBookInList(false);
      setRating(0); // Clear rating when removing from list
      setIsFavorite(false); // Clear favorite when removing from list
      setSuccessMessage('Book removed from your reading list!');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      setError('Failed to remove book from your list. Please try again.');
    } finally {
      setAddingToList(false);
    }
  };

  const handleToggleFavorite = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    
    // If book is not in list, add it first and then toggle favorite
    if (!isBookInList) {
      setAddingToList(true);
      setError('');
      setSuccessMessage('');
      try {
        await userService.addBookToList(id);
        setIsBookInList(true);
        // Now toggle favorite
        try {
          await userService.toggleFavorite(id);
          setIsFavorite(true);
          setSuccessMessage('Book added to your list and marked as favorite!');
          setTimeout(() => setSuccessMessage(''), 3000);
        } catch (favErr) {
          // Book was added but favorite toggle failed
          setSuccessMessage('Book added to your list!');
          setError('Failed to mark as favorite. Please try again.');
          setTimeout(() => {
            setSuccessMessage('');
            setError('');
          }, 3000);
        }
      } catch (err) {
        setError('Failed to add book. Please try again.');
      } finally {
        setAddingToList(false);
      }
      return;
    }
    
    setTogglingFavorite(true);
    setError('');
    setSuccessMessage('');
    try {
      await userService.toggleFavorite(id);
      setIsFavorite(!isFavorite);
      setSuccessMessage(isFavorite ? 'Removed from favorites!' : 'Added to favorites!');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to update favorite status.';
      setError(errorMessage);
    } finally {
      setTogglingFavorite(false);
    }
  };

  const handleRating = async (selectedRating) => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    
    // If book is not in list, add it first and then rate
    if (!isBookInList) {
      setAddingToList(true);
      setError('');
      setSuccessMessage('');
      try {
        await userService.addBookToList(id);
        setIsBookInList(true);
        // Now rate the book
        try {
          await userService.rateBook(id, selectedRating);
          setRating(selectedRating);
          setSuccessMessage('Book added to your list and rated successfully!');
          setTimeout(() => setSuccessMessage(''), 3000);
          // Refresh book to get updated average rating
          const data = await bookService.getBookById(id);
          setBook(data);
        } catch (rateErr) {
          // Book was added but rating failed
          setSuccessMessage('Book added to your list!');
          setError('Rating failed. Please try again.');
          setTimeout(() => {
            setSuccessMessage('');
            setError('');
          }, 3000);
        }
      } catch (err) {
        setError('Failed to add book. Please try again.');
      } finally {
        setAddingToList(false);
      }
      return;
    }
    
    setSubmittingRating(true);
    setError('');
    setSuccessMessage('');
    try {
      await userService.rateBook(id, selectedRating);
      setRating(selectedRating);
      setSuccessMessage('Rating submitted successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);
      // Refresh book to get updated average rating
      const data = await bookService.getBookById(id);
      setBook(data);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to submit rating. Please try again.';
      setError(errorMessage);
    } finally {
      setSubmittingRating(false);
    }
  };

  const handleRemoveRating = async () => {
    setSubmittingRating(true);
    setError('');
    setSuccessMessage('');
    try {
      await userService.removeRating(id);
      setRating(0);
      setSuccessMessage('Rating removed successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);
      // Refresh book to get updated average rating
      const data = await bookService.getBookById(id);
      setBook(data);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to remove rating. Please try again.';
      setError(errorMessage);
    } finally {
      setSubmittingRating(false);
    }
  };

  const getRatingStars = (bookRating) => {
    const stars = [];
    const roundedRating = Math.round(bookRating || 0);
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <span key={i} className={i <= roundedRating ? 'text-yellow-400' : 'text-gray-300'}>
          ★
        </span>
      );
    }
    return stars;
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <Loading size="lg" />
      </div>
    );
  }

  if (error && !book) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-red-50 border border-red-200 rounded-md p-4">
          <p className="text-red-800">{error}</p>
        </div>
        <button
          onClick={() => navigate(-1)}
          className="mt-4 text-primary-600 hover:text-primary-700"
        >
          ← Back
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <button
        onClick={() => navigate(-1)}
        className="mb-4 text-primary-600 hover:text-primary-700 flex items-center"
      >
        ← Back
      </button>

      <div className="bg-white rounded-lg shadow-lg overflow-hidden">
        <div className="md:flex">
          <div className="md:w-1/3 bg-gradient-to-br from-primary-500 to-primary-700 p-12 flex items-center justify-center">
            {book.coverImageUrl && !imageError ? (
              <img 
                src={book.coverImageUrl} 
                alt={book.title}
                className="max-w-full max-h-96 object-contain"
                onError={() => setImageError(true)}
              />
            ) : (
              <div className="text-white text-9xl">📖</div>
            )}
          </div>
          <div className="md:w-2/3 p-8">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">{book.title}</h1>
            <p className="text-xl text-gray-600 mb-4">by {book.author}</p>
            
            <div className="flex items-center mb-4">
              <span className="text-sm font-medium text-gray-700 mr-2">Average Rating:</span>
              <div className="flex items-center">
                {getRatingStars(book.averageRating)}
                {book.averageRating && (
                  <span className="ml-2 text-sm text-gray-600">
                    {book.averageRating.toFixed(1)} / 5.0
                  </span>
                )}
              </div>
            </div>

            <div className="mb-4 flex items-center gap-2">
              <span className="inline-block bg-primary-100 text-primary-800 px-3 py-1 rounded-full text-sm font-medium">
                {book.genre}
              </span>
              {book.publishYear && (
                <span className="text-sm text-gray-600">
                  Published: {book.publishYear}
                </span>
              )}
            </div>

            {book.isbn && (
              <p className="text-sm text-gray-600 mb-4">
                <span className="font-medium">ISBN:</span> {book.isbn}
              </p>
            )}

            <div className="mb-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Description</h3>
              <p className="text-gray-700 leading-relaxed">
                {book.description || 'No description available.'}
              </p>
            </div>

            {/* Favorite count */}
            {book.favoriteCount > 0 && (
              <div className="mb-4 text-gray-600">
                ❤️ {book.favoriteCount} {book.favoriteCount === 1 ? 'person' : 'people'} favorited this
              </div>
            )}

            {/* Reviews section */}
            {book.reviews && book.reviews.length > 0 && (
              <div className="mb-6">
                <h3 className="text-xl font-bold mb-4">Reviews ({book.reviews.length})</h3>
                <div className="space-y-4">
                  {book.reviews.map(review => (
                    <div key={review.id} className="border-b pb-4">
                      <div className="flex items-center gap-2">
                        <span className="font-semibold">{review.userName}</span>
                        <span className="text-yellow-500">{'⭐'.repeat(review.rating)}</span>
                      </div>
                      <p className="text-gray-500 text-sm">
                        Rated on {new Date(review.ratedAt).toLocaleDateString('en-US', { 
                          year: 'numeric', 
                          month: 'long', 
                          day: 'numeric' 
                        })}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {successMessage && (
              <div className="mb-4 p-4 bg-green-50 border border-green-200 rounded-md">
                <p className="text-green-800">{successMessage}</p>
              </div>
            )}

            {error && book && (
              <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-md">
                <p className="text-red-800">{error}</p>
              </div>
            )}

            <div className="space-y-4">
              {isAuthenticated ? (
                <>
                  <div className="flex gap-2">
                    <button
                      onClick={isBookInList ? handleRemoveFromList : handleAddToList}
                      disabled={addingToList}
                      className={`flex-1 px-6 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:cursor-not-allowed transition-colors ${
                        isBookInList 
                          ? 'bg-red-600 text-white hover:bg-red-700 disabled:opacity-50' 
                          : 'bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-50'
                      }`}
                    >
                      {addingToList ? (isBookInList ? 'Removing...' : 'Adding...') : (isBookInList ? 'Remove from My Books' : 'Add to My Books')}
                    </button>
                    
                    <button
                      onClick={handleToggleFavorite}
                      disabled={togglingFavorite || addingToList}
                      className={`px-6 py-3 rounded-md focus:outline-none focus:ring-2 focus:ring-yellow-500 disabled:cursor-not-allowed transition-colors ${
                        isFavorite 
                          ? 'bg-yellow-500 text-white hover:bg-yellow-600 disabled:opacity-50' 
                          : 'bg-gray-200 text-gray-700 hover:bg-gray-300 disabled:opacity-50'
                      }`}
                      title={isFavorite ? 'Remove from favorites' : 'Add to favorites'}
                    >
                      {togglingFavorite || addingToList ? '...' : (isFavorite ? '❤️' : '🤍')}
                    </button>
                  </div>

                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      Rate this book
                      {!isBookInList && (
                        <span className="text-sm font-normal text-gray-500 ml-2">(Will be added to your list)</span>
                      )}
                    </h3>
                    <div className="flex items-center space-x-2">
                      {[1, 2, 3, 4, 5].map((star) => (
                        <button
                          key={star}
                          onClick={() => handleRating(star)}
                          onMouseEnter={() => setHoverRating(star)}
                          onMouseLeave={() => setHoverRating(0)}
                          disabled={submittingRating || addingToList}
                          className="text-3xl focus:outline-none disabled:cursor-not-allowed transition-colors"
                        >
                          <span
                            className={
                              star <= (hoverRating || rating)
                                ? 'text-yellow-400'
                                : 'text-gray-300'
                            }
                          >
                            ★
                          </span>
                        </button>
                      ))}
                      {rating > 0 && (
                        <>
                          <span className="ml-2 text-sm text-gray-600">
                            You rated: {rating} / 5
                          </span>
                          <button
                            onClick={handleRemoveRating}
                            disabled={submittingRating}
                            className="ml-2 text-sm text-red-600 hover:text-red-700 disabled:opacity-50"
                          >
                            Remove Rating
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </>
              ) : (
                <div className="space-y-4">
                  <div className="bg-yellow-50 border border-yellow-200 rounded-md p-4">
                    <p className="text-yellow-800 mb-2">
                      <Link to="/login" className="font-semibold hover:underline">Log in</Link> to add this book to your reading list and rate it!
                    </p>
                    <Link
                      to="/login"
                      className="inline-block px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors"
                    >
                      Sign In
                    </Link>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookDetails;
