import React from 'react';
import { Link } from 'react-router-dom';

const BookCard = ({ book, userBook, onRemove, onToggleFavorite }) => {
  const [imageError, setImageError] = React.useState(false);
  
  const isInLibrary = !!userBook;
  const isFavorite = userBook?.isFavorite;
  const userRating = userBook?.rating;
  
  const getRatingStars = (rating) => {
    const stars = [];
    const roundedRating = Math.round(rating || 0);
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <span key={i} className={i <= roundedRating ? 'text-yellow-400' : 'text-gray-300'}>
          ★
        </span>
      );
    }
    return stars;
  };

  return (
    <Link to={`/books/${book.id}`} className="block h-full">
      <div className="bg-white rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300 overflow-hidden h-full flex flex-col relative">
        {/* Favorite heart icon - top right */}
        {isFavorite && (
          <div 
            className="absolute top-2 right-2 text-red-500 text-xl z-10" 
            aria-label="This book is in your favorites"
          >
            ❤️
          </div>
        )}
        
        <div className="bg-gradient-to-br from-primary-500 to-primary-700 h-48 flex items-center justify-center">
          {book.coverImageUrl && !imageError ? (
            <img 
              src={book.coverImageUrl} 
              alt={book.title}
              className="w-full h-full object-cover"
              onError={() => setImageError(true)}
            />
          ) : (
            <div className="text-white text-6xl">📖</div>
          )}
        </div>
        <div className="p-4 flex-1 flex flex-col">
          <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">{book.title}</h3>
          <p className="text-sm text-gray-600 mb-2">by {book.author}</p>
          <div className="flex items-center justify-between mb-3">
            <p className="text-xs text-gray-500 flex-1">{book.genre}</p>
            {book.publishYear && (
              <span className="text-xs text-gray-500">({book.publishYear})</span>
            )}
          </div>
          <div className="flex items-center justify-between mt-auto">
            <div className="flex items-center">
              {getRatingStars(book.averageRating)}
            </div>
            {book.averageRating && (
              <span className="text-sm text-gray-600">{book.averageRating.toFixed(1)}</span>
            )}
          </div>
          
          {/* User status indicators at bottom */}
          {isInLibrary && (
            <div className="flex items-center justify-between mt-2 pt-2 border-t border-gray-200">
              <div className="flex items-center gap-2">
                <span className="text-green-600 text-sm font-medium">✅ In Library</span>
                {userRating && (
                  <span className="text-yellow-500 text-sm font-medium">⭐ {userRating}</span>
                )}
              </div>
              
              {/* Quick actions */}
              <div className="flex gap-1">
                <button 
                  onClick={(e) => { 
                    e.preventDefault(); 
                    e.stopPropagation();
                    onToggleFavorite(book.id); 
                  }}
                  className="text-gray-400 hover:text-red-500 text-xl transition-colors"
                  title={isFavorite ? "Remove from favorites" : "Add to favorites"}
                  aria-label={isFavorite ? "Remove from favorites" : "Add to favorites"}
                >
                  {isFavorite ? '🤍' : '❤️'}
                </button>
                <button
                  onClick={(e) => { 
                    e.preventDefault(); 
                    e.stopPropagation();
                    onRemove(book.id); 
                  }}
                  className="text-gray-400 hover:text-red-500 text-lg font-bold transition-colors px-1"
                  title="Remove from library"
                  aria-label="Remove from library"
                >
                  ✕
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </Link>
  );
};

export default BookCard;
