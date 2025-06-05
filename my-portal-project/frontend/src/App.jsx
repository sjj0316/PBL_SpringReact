import { BrowserRouter, Routes, Route } from "react-router-dom";
import PostList from "./pages/PostList";
import PostDetail from "./pages/PostDetail";
import PostWrite from "./pages/PostWrite";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<PostList />} />
                <Route path="/posts/:id" element={<PostDetail />} />
                <Route path="/write" element={<PostWrite />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
