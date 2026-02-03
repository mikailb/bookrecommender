import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-gray-800 text-white mt-auto">
      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div className="text-center">
          <p className="text-sm">
            © {new Date().getFullYear()} BookRec - Book Recommender System. Built with ❤️ using Spring Boot & React.
          </p>
          <p className="text-xs mt-2 text-gray-400">
            Discover your next favorite book with personalized recommendations
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
