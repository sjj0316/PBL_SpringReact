import { fetchComments, submitComment, updateComment, deleteComment } from "../api/commentApi";
import { useEffect, useState } from "react";

// 로그인한 사용자명 가져오기 (예: localStorage 저장)
function getUsername() {
    return localStorage.getItem("username"); // 로그인 시 저장했다고 가정
}

export default function CommentSection({ postId }) {
    const [comments, setComments] = useState([]);
    const [newContent, setNewContent] = useState("");
    const [editId, setEditId] = useState(null); // 현재 수정 중인 댓글 ID
    const [editContent, setEditContent] = useState("");

    const username = getUsername(); // 현재 로그인 사용자

    useEffect(() => {
        loadComments();
    }, [postId]);

    async function loadComments() {
        try {
            const data = await fetchComments(postId);
            setComments(data);
        } catch (err) {
            console.error("댓글 불러오기 실패", err);
        }
    }

    async function handleSubmit(e) {
        e.preventDefault();
        if (!newContent.trim()) return;
        try {
            await submitComment({ postId, content: newContent });
            setNewContent("");
            await loadComments();
        } catch (err) {
            console.error("댓글 등록 실패", err);
        }
    }

    async function handleUpdate(id) {
        try {
            await updateComment(id, editContent);
            setEditId(null);
            await loadComments();
        } catch (err) {
            console.error("댓글 수정 실패", err);
        }
    }

    async function handleDelete(id) {
        if (!window.confirm("정말로 댓글을 삭제하시겠습니까?")) return;
        try {
            await deleteComment(id);
            await loadComments();
        } catch (err) {
            console.error("댓글 삭제 실패", err);
        }
    }

    return (
        <div className="comment-section">
            <form onSubmit={handleSubmit}>
        <textarea
            value={newContent}
            onChange={(e) => setNewContent(e.target.value)}
            placeholder="댓글을 입력하세요"
        />
                <button type="submit">댓글 작성</button>
            </form>

            <ul>
                {comments.map((c) => (
                    <li key={c.id}>
                        <strong>{c.author}</strong> | <span>{c.createdAt}</span>
                        {editId === c.id ? (
                            <>
                <textarea
                    value={editContent}
                    onChange={(e) => setEditContent(e.target.value)}
                />
                                <button onClick={() => handleUpdate(c.id)}>저장</button>
                                <button onClick={() => setEditId(null)}>취소</button>
                            </>
                        ) : (
                            <>
                                <p>{c.content}</p>
                                {c.author === username && (
                                    <div>
                                        <button onClick={() => { setEditId(c.id); setEditContent(c.content); }}>✏ 수정</button>
                                        <button onClick={() => handleDelete(c.id)}>🗑 삭제</button>
                                    </div>
                                )}
                            </>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
}
