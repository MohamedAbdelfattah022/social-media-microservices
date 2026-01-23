export type PostData = {
  id: number;
  userId: string;
  username: string;
  firstName: string | null;
  lastName: string | null;
  profilePictureUrl?: string;
  content: string;
  privacy: string;
  mediaUrls?: string[];
  isEdited: boolean;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt: string;
};
