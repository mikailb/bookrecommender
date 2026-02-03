# Book Recommender - React Frontend

A modern, responsive React frontend for the Book Recommender System.

## Features

### 🎨 User Interface
- Clean, modern design with Tailwind CSS
- Fully responsive (mobile, tablet, desktop)
- Smooth animations and transitions
- Loading states and error handling
- Intuitive navigation with mobile hamburger menu

### 🔐 Authentication
- User registration and login
- JWT token management with automatic refresh
- Protected routes for authenticated users
- Persistent login state

### 📚 Book Management
- Browse all books with pagination
- Search books by title, author, or genre
- View detailed book information
- Rate books (1-5 stars)
- Add books to personal reading list

### 🎯 Personalized Features
- Get AI-powered book recommendations
- View reading history
- Track rated books
- User profile with statistics

## Tech Stack

- **React** 19.2.4
- **React Router** 7.x - Client-side routing
- **Axios** - HTTP client for API calls
- **Tailwind CSS** - Utility-first CSS framework
- **Context API** - State management for authentication

## Getting Started

### Prerequisites

- Node.js 14+ and npm
- Backend API running at `http://localhost:8080`

### Installation

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

The app will open at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

This creates an optimized production build in the `build/` directory.

## Project Structure

```
frontend/
├── public/              # Static files
├── src/
│   ├── components/      # React components
│   │   ├── auth/        # Login, Register
│   │   ├── books/       # BookCard, BookList, BookDetails
│   │   ├── common/      # Navbar, Footer, Loading, ProtectedRoute
│   │   └── user/        # Profile
│   ├── context/         # Auth context for state management
│   ├── pages/           # Page components (Home, Recommendations)
│   ├── services/        # API service layer
│   │   ├── api.js           # Axios instance with interceptors
│   │   ├── authService.js   # Authentication API calls
│   │   ├── bookService.js   # Book API calls
│   │   └── userService.js   # User API calls
│   ├── App.js           # Main app component with routing
│   ├── index.js         # Entry point
│   └── index.css        # Global styles with Tailwind
├── package.json
└── tailwind.config.js   # Tailwind configuration
```

## API Integration

The frontend connects to the Spring Boot backend at `http://localhost:8080/api`

### Endpoints Used:

- **Authentication**
  - `POST /api/auth/register` - Register new user
  - `POST /api/auth/login` - Login user
  - `POST /api/auth/refresh` - Refresh JWT token

- **Books**
  - `GET /api/books?page=0&size=12` - Get books (paginated)
  - `GET /api/books/{id}` - Get book details
  - `GET /api/books/search?query=` - Search books

- **User**
  - `GET /api/users/profile` - Get user profile
  - `GET /api/users/books` - Get user's reading list
  - `POST /api/users/books/{bookId}` - Add book to list
  - `POST /api/users/books/{bookId}/rate` - Rate a book

- **Recommendations**
  - `GET /api/recommendations` - Get personalized recommendations

## Responsive Design

The application is fully responsive with breakpoints:
- **Mobile**: < 640px - Single column, hamburger menu
- **Tablet**: 640px - 1024px - 2-3 column grids
- **Desktop**: > 1024px - 4 column grids, full navigation

## Available Scripts

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

The page will reload when you make changes.\
You may also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can't go back!**

If you aren't satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

---

Built with ❤️ using React and Tailwind CSS
