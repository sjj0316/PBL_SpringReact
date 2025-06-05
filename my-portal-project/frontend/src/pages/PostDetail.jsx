// src/pages/PostDetail.jsx
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { fetchPost } from "../api/postApi";
import CommentSection from "../components/CommentSection";

export default function PostDetail() {
    const { id } = useParams();
    const [post, setPost] = useState(null);

    useEffect(() => {
        fetchPost(id).then(setPost);
    }, [id]);

    if (!post) return <p>불러오는 중...</p>;

    return (
        <div>
            <h2>{post.title}</h2>
            <p>작성자: {post.author}</p>
            <p>{post.content}</p>
            <hr />
            <CommentSection postId={post.id} />
        </div>
    );
}
