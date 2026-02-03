import api from './api';

const userService = {
  getProfile: async () => {
    const response = await api.get('/users/profile');
    return response.data;
  },

  getUserBooks: async () => {
    const response = await api.get('/users/books');
    return response.data;
  },

  addBookToList: async (bookId) => {
    const response = await api.post(`/users/books/${bookId}`);
    return response.data;
  },

  rateBook: async (bookId, rating) => {
    const response = await api.post(`/users/books/${bookId}/rate`, { rating });
    return response.data;
  },

  removeBookFromList: async (bookId) => {
    const response = await api.delete(`/users/books/${bookId}`);
    return response.data;
  },

  removeRating: async (bookId) => {
    const response = await api.delete(`/users/books/${bookId}/rate`);
    return response.data;
  },

  toggleFavorite: async (bookId) => {
    const response = await api.post(`/users/books/${bookId}/favorite`);
    return response.data;
  },

  getRecommendations: async () => {
    const response = await api.get('/recommendations');
    return response.data;
  },
};

export default userService;
