export type CommentDto = {
  id: number;
  postId: number;
  userId: string;
  firstname: string;
  lastname: string;
  username: string;
  profilePictureUrl?: string;
  content: string;
  parentCommentId: number | null;
  isEdited: boolean;
  likeCount: number;
  replyCount: number;
  createdAt: string;
  updatedAt: string;
};
