// src/pages/PostWrite.jsx
import { useState } from "react";
import { createPost } from "../api/postApi";
import { useNavigate } from "react-router-dom";

export default function PostWrite() {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        try {
            await createPost({ title, content });
            alert("게시글 등록 완료");
            navigate("/"); // 글 목록으로 이동
        } catch (err) {
            alert("게시글 등록 실패");
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>✏ 게시글 작성</h2>
            <input
                type="text"
                placeholder="제목"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
            />
            <textarea
                placeholder="내용"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                required
            />
            <button type="submit">등록</button>
        </form>
    );
}
