import { Component } from '@angular/core';
import { PostCard } from '../post/post-card';
import { CursorPaginationResponse } from '../../shared/models/cursor-pagination-response';

@Component({
  selector: 'app-feed',
  imports: [PostCard],
  templateUrl: './feed.html',
  styleUrl: './feed.css',
})
export class Feed {
  posts: CursorPaginationResponse = {
    data: [
      {
        id: 14,
        userId: "57c72aef-a970-42a4-83b3-75018c74dcd5",
        content: "Just finished an amazing hike in the mountains! The views were absolutely breathtaking. Nature really has a way of putting everything into perspective. 🏔️",
        username: "emma-wilson",
        firstName: "Emma",
        lastName: "Wilson",
        profilePictureUrl: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop",
        privacy: "PUBLIC",
        mediaUrls: ["https://images.unsplash.com/photo-1617634667039-8e4cb277ab46?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"],
        isEdited: false,
        likeCount: 0,
        commentCount: 0,
        createdAt: "2025-12-23T13:30:10.418918",
        updatedAt: "2025-12-23T13:30:10.418918"
      },
      {
        id: 12,
        userId: "57c72aef-a970-42a4-83b3-75018c74dcd5",
        username: "james-rodriguez",
        firstName: "James",
        lastName: "Rodriguez",
        profilePictureUrl: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop",
        content: "Finally set up my dream workspace! Productivity is going to be through the roof 💻✨\n\nShoutout to everyone who helped me choose the perfect desk setup.",
        privacy: "PUBLIC",
        mediaUrls: ["https://images.unsplash.com/photo-1623715537851-8bc15aa8c145?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"],
        isEdited: true,
        likeCount: 0,
        commentCount: 0,
        createdAt: "2025-12-23T13:30:08.112312",
        updatedAt: "2025-12-23T13:30:08.112312"
      },
      {
        id: 11,
        userId: "57c72aef-a970-42a4-83b3-75018c74dcd5",
        username: "sophia-martinez",
        firstName: "Sophia",
        lastName: "Martinez",
        profilePictureUrl: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop",
        content: "Tried making homemade pasta for the first time and it actually turned out amazing! Who knew cooking could be this therapeutic? 🍝",
        privacy: "PUBLIC",
        mediaUrls: [
          "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
          "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
        ],
        isEdited: false,
        likeCount: 0,
        commentCount: 0,
        createdAt: "2025-12-23T13:30:06.906238",
        updatedAt: "2025-12-23T13:30:06.906238"
      },
      {
        id: 19,
        userId: "57c72aef-a970-42a4-83b3-75018c74dcd5",
        username: "daniel-park",
        firstName: "Daniel",
        lastName: "Park",
        profilePictureUrl: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop",
        content: "Exploring new destinations always fills my soul with joy. Travel is truly the only thing you buy that makes you richer. ✈️🌍",
        privacy: "PUBLIC",
        mediaUrls: [
          "https://images.unsplash.com/photo-1614088459293-5669fadc3448?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
          "https://images.unsplash.com/photo-1501785888041-af3ef285b470?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
          "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080",
        ],
        isEdited: false,
        likeCount: 2156,
        commentCount: 142,
        createdAt: "2025-12-23T13:30:06.906238",
        updatedAt: "2025-12-23T13:30:06.906238"
      }
    ],
    nextCursor: null,
    hasNext: false,
    pageSize: 5
  };


}
