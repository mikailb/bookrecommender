import api from './api';

const bookService = {
  getAllBooks: async (page = 0, size = 12) => {
    const response = await api.get(`/books?page=${page}&size=${size}`);
    return response.data;
  },

  getBookById: async (id) => {
    const response = await api.get(`/books/${id}`);
    return response.data;
  },

  searchBooks: async (query, page = 0, size = 12) => {
    const response = await api.get(`/books/search?query=${query}&page=${page}&size=${size}`);
    return response.data;
  },

  createBook: async (bookData) => {
    const response = await api.post('/books', bookData);
    return response.data;
  },

  updateBook: async (id, bookData) => {
    const response = await api.put(`/books/${id}`, bookData);
    return response.data;
  },

  deleteBook: async (id) => {
    const response = await api.delete(`/books/${id}`);
    return response.data;
  },
};

export default bookService;
