import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import FileUploadPage from './pages/FileUploadPage';
import FileManagementPage from './pages/FileManagementPage';

const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/upload" element={<FileUploadPage />} />
        <Route path="/files" element={<FileManagementPage />} />
      </Routes>
    </Router>
  );
};

export default App; 