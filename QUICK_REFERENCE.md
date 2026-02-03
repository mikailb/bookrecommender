# Quick Reference Guide

## Development Commands

### Backend
```bash
# Start with H2 (no database setup needed)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Start with PostgreSQL (requires DB setup)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build JAR
./mvnw clean package
```

### Frontend
```bash
cd frontend

# Install dependencies
npm install

# Start dev server (http://localhost:3000)
npm start

# Run tests
npm test

# Build for production
npm run build

# Lint code
npm run lint
```

## Environment Variables

### Backend (Optional)
```bash
# For PostgreSQL
export DB_PASSWORD=your_password

# For JWT secret (production)
export JWT_SECRET=your_secret_key_here
```

### Frontend
No environment variables needed for development. Backend URL is hardcoded to `http://localhost:8080/api`

## API Endpoints Quick Reference

### Authentication (No Auth Required)
```
POST /api/auth/register    - Register new user
POST /api/auth/login       - Login user
POST /api/auth/refresh     - Refresh JWT token
```

### Books (Auth Required)
```
GET    /api/books                      - Get all books (paginated)
GET    /api/books/{id}                 - Get book by ID
POST   /api/books                      - Create new book
PUT    /api/books/{id}                 - Update book
DELETE /api/books/{id}                 - Delete book
GET    /api/books/search?query={q}    - Search books
```

### User (Auth Required)
```
GET  /api/users/profile               - Get user profile
GET  /api/users/books                 - Get user's book list
POST /api/users/books/{bookId}        - Add book to user's list
POST /api/users/books/{bookId}/rate   - Rate a book (1-5)
```

### Recommendations (Auth Required)
```
GET /api/recommendations - Get personalized recommendations
```

## Common Issues & Solutions

### Backend won't start
**Issue**: PostgreSQL connection error  
**Solution**: Use dev profile with H2: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

### Frontend can't connect to backend
**Issue**: CORS error  
**Solution**: Make sure backend is running on port 8080 and CORS is configured

### Port 3000 already in use
**Solution**: Kill the process: `lsof -ti:3000 | xargs kill -9`

### Port 8080 already in use
**Solution**: Kill the process: `lsof -ti:8080 | xargs kill -9`

### npm install fails
**Solution**: Clear cache and try again:
```bash
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

## Project Structure

```
bookrecommender/
├── src/                          # Backend source code
│   └── main/
│       ├── java/                # Java source files
│       └── resources/           # Configuration files
├── frontend/                    # React frontend
│   ├── public/                 # Static files
│   ├── src/                    # React source code
│   │   ├── components/        # React components
│   │   ├── context/           # Context providers
│   │   ├── pages/             # Page components
│   │   ├── services/          # API services
│   │   └── App.js             # Main app component
│   └── package.json           # Frontend dependencies
├── pom.xml                     # Backend dependencies
└── README.md                   # Main documentation
```

## Testing Users

After starting the app, create a test user:
```
Name: John Doe
Email: john.doe@example.com
Password: password123
```

## Default Ports

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console` (dev profile)

## Tech Stack Versions

### Backend
- Java 17+
- Spring Boot 3.2.2
- Maven 3.6+

### Frontend
- Node.js 14+
- React 19.2.4
- React Router 7.x
- Tailwind CSS 3.x

## Useful Links

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [React Documentation](https://react.dev)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- [Axios Documentation](https://axios-http.com/docs/intro)

## Git Workflow

```bash
# See changes
git status

# Add files
git add .

# Commit
git commit -m "Your message"

# Push
git push origin your-branch

# Pull latest changes
git pull origin main
```

## Build for Production

### Backend
```bash
./mvnw clean package
java -jar target/bookrecommender-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
# Deploy the 'build' directory to your hosting service
```

## Need Help?

1. Check the main README.md
2. Review IMPLEMENTATION_SUMMARY.md
3. Check frontend/README.md for frontend-specific info
4. Review the code comments
5. Check the API documentation section in README
