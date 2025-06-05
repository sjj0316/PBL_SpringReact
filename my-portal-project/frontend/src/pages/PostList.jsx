// src/pages/PostList.jsx
import { useEffect, useState } from "react";
import { fetchPosts } from "../api/postApi";
import { Link } from "react-router-dom";

export default function PostList() {
    const [posts, setPosts] = useState([]);

    useEffect(() => {
        fetchPosts().then(setPosts);
    }, []);

    return (
        <div>
            <h2>📃 게시글 목록</h2>
            <Link to="/write">✏ 새 글 작성</Link>
            <ul>
                {posts.map((p) => (
                    <li key={p.id}>
                        <Link to={`/posts/${p.id}`}>
                            <strong>{p.title}</strong> by {p.author}
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
}
